package com.kholodkov.coinmonitor.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kholodkov.coinmonitor.core.tools.parseToBigDecimal
import com.kholodkov.coinmonitor.core.tools.toDisplayString
import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.model.transaction.RestoreTransactionParams
import com.kholodkov.coinmonitor.domain.model.transaction.SaveTransactionParams
import com.kholodkov.coinmonitor.domain.model.transaction.Transaction
import com.kholodkov.coinmonitor.domain.usecase.transaction.DeleteTransactionUseCase
import com.kholodkov.coinmonitor.domain.usecase.transaction.ObserveDailySummary
import com.kholodkov.coinmonitor.domain.usecase.currency.ObserveInputCurrencyUseCase
import com.kholodkov.coinmonitor.domain.usecase.transaction.ObserveTransactionsByDateUseCase
import com.kholodkov.coinmonitor.domain.usecase.transaction.RestoreTransactionUseCase
import com.kholodkov.coinmonitor.domain.usecase.transaction.SaveTransactionUseCase
import com.kholodkov.coinmonitor.domain.usecase.currency.SetInputCurrencyUseCase
import com.kholodkov.coinmonitor.feature.main.mapper.toItemList
import com.kholodkov.coinmonitor.feature.main.mapper.toRestoreTransactionParams
import com.kholodkov.coinmonitor.feature.main.state.MainUiEvent
import com.kholodkov.coinmonitor.feature.main.state.MainUiIntent
import com.kholodkov.coinmonitor.feature.main.state.MainUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
@HiltViewModel
class MainViewModel @Inject constructor(
    private val observeTransactionsByDate: ObserveTransactionsByDateUseCase,
    observeInputCurrencyUseCase: ObserveInputCurrencyUseCase,
    private val observeDailySummary: ObserveDailySummary,
    private val setInputCurrencyUseCase: SetInputCurrencyUseCase,
    private val saveTransactionUseCase: SaveTransactionUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val restoreTransactionUseCase: RestoreTransactionUseCase
) : ViewModel() {

    private val selectedDate = MutableStateFlow(LocalDate.now())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val mainInfoFlow = selectedDate.flatMapLatest { date ->
        combine(
            observeTransactionsByDate(date),
            observeDailySummary(date)
        ) { transactions, dailySummary ->
            MainInfo(
                date = date,
                balance = dailySummary.budget,
                spent = dailySummary.spent,
                remaining = dailySummary.remaining,
                currency = dailySummary.currency,
                transactions = transactions
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = MainInfo()
    )

    private val inputCurrencyState = observeInputCurrencyUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = Currency.RSD
    )

    private val editTransactionState = MutableStateFlow(EditTransactionState())

    val uiState: StateFlow<MainUiState> = combine(
        mainInfoFlow,
        editTransactionState,
        inputCurrencyState,
    ) { mainInfo,
        editTransaction,
        currency->
        MainUiState(
            date = mainInfo.date.toDisplayString(),
            balance = "${mainInfo.balance.toDisplayString()} ${mainInfo.currency}",
            spent = "${mainInfo.spent.toDisplayString()} ${mainInfo.currency}",
            remaining = "${mainInfo.remaining.toDisplayString()} ${mainInfo.currency}",
            transactions = mainInfo.transactions.toItemList(),
            inputCurrency = currency,
            inputAmount = editTransaction.inputAmount,
            editTransactionUid = editTransaction.editTransactionUid,
            inputTime = editTransaction.inputTime,
            isTransactionSheetVisible = editTransaction.isVisible,
            isTimeSelectorOpened = editTransaction.isTimePickerVisible
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainUiState()
    )

    private val _events = MutableSharedFlow<MainUiEvent>()
    val events = _events.asSharedFlow()

    fun onIntent(intent: MainUiIntent) = when (intent) {
        // Main intents
        is MainUiIntent.SelectDate -> handleSelectDate(intent.date)
        is MainUiIntent.NextDay -> handleNextDay()
        is MainUiIntent.PreviousDay -> handlePreviousDay()
        is MainUiIntent.EditTransaction -> handleEditTransaction(intent.uid)
        is MainUiIntent.DeleteTransaction -> handleDeleteTransaction(intent.uid)
        is MainUiIntent.RestoreTransaction -> handleRestoreTransaction(intent.params)
        is MainUiIntent.AddNewTransaction -> handleAddNewTransaction()

        //Transaction sheet intents
        is MainUiIntent.HideTransactionSheet -> handleHideTransactionSheet()
        is MainUiIntent.EditTime -> handleEditTime()
        is MainUiIntent.SaveTransaction -> handleSaveTransaction(intent.uid)
        is MainUiIntent.EditAmount -> handleEditAmount(intent.amount)
        is MainUiIntent.EditCurrency -> handleEditCurrency(intent.currency)

        //Time selector intents
        is MainUiIntent.SetTime -> handleSetTime(intent.time)
        is MainUiIntent.DismissTimeSelector -> handleDismissTimeSelector()
    }

    // Main intents
    private fun handleSelectDate(date: LocalDate) {
        selectedDate.value = date
    }

    private fun handleNextDay() = selectedDate.update { it.plusDays(1) }

    private fun handlePreviousDay() = selectedDate.update { it.minusDays(1) }

    private fun handleEditTransaction(uid: String) {
        mainInfoFlow.value.transactions.firstOrNull { it.uid == uid }?.let { transaction ->
            viewModelScope.launch {
                setInputCurrencyUseCase(transaction.currency)
            }
            editTransactionState.update {
                it.copy(
                    isVisible = true,
                    inputAmount = transaction.amount.toDisplayString(),
                    inputTime = transaction.time.toDisplayString(),
                    editTransactionUid = uid,
                    isTimePickerVisible = false
                )
            }
        }
    }

    private fun handleDeleteTransaction(uid: String) {
        viewModelScope.launch {
            val transaction = mainInfoFlow.value.transactions.firstOrNull { it.uid == uid }

            transaction?.let {
                deleteTransactionUseCase(transaction.uid)
                _events.emit(MainUiEvent.ShowRestoreTransactionSnackbar(transaction.toRestoreTransactionParams()))
            }
        }
    }

    private fun handleRestoreTransaction(params: RestoreTransactionParams) {
        viewModelScope.launch { restoreTransactionUseCase(params) }
    }

    private fun handleAddNewTransaction() = editTransactionState.update {
        it.copy(
            isVisible = true,
            inputAmount = "",
            editTransactionUid = null,
            inputTime = LocalTime.now().toDisplayString(),
            isTimePickerVisible = false
        )
    }

    //Transaction sheet intents
    private fun handleHideTransactionSheet() = editTransactionState.update {
        it.copy(isVisible = false)
    }

    private fun handleEditTime() = editTransactionState.update { it.copy(isTimePickerVisible = true) }

    private fun handleSaveTransaction(uid: String?) {
        val amount = editTransactionState.value.inputAmount.parseToBigDecimal() ?: return
        val date = selectedDate.value
        val time = editTransactionState.value.inputTime
        viewModelScope.launch {
            saveTransactionUseCase(
                SaveTransactionParams(
                    uid = uid,
                    date = date,
                    amount = amount,
                    currency = inputCurrencyState.value,
                    time = LocalTime.parse(time)
                )
            )
        }
    }

    private fun handleEditAmount(amount: String) = editTransactionState.update {
        it.copy(inputAmount = amount.replace(',', '.'))
    }

    private fun handleEditCurrency(currency: Currency) {
        viewModelScope.launch {
            setInputCurrencyUseCase(currency)
        }
    }

    //Time selector intents
    private fun handleSetTime(time: LocalTime) = editTransactionState.update {
        it.copy(
            inputTime = time.toDisplayString(),
            isTimePickerVisible = false
        )
    }

    private fun handleDismissTimeSelector() =
        editTransactionState.update { it.copy(isTimePickerVisible = false) }

    data class MainInfo(
        val date: LocalDate = LocalDate.now(),
        val dateDescription: String = "",
        val balance: BigDecimal = BigDecimal.ZERO,
        val spent: BigDecimal = BigDecimal.ZERO,
        val remaining: BigDecimal = BigDecimal.ZERO,
        val currency: Currency = Currency.RSD,
        val isDatePickerVisible: Boolean = false,
        val transactions: List<Transaction> = listOf()
    )

    data class EditTransactionState(
        val isVisible: Boolean = false,
        val inputAmount: String = "",
        val inputTime: String = LocalTime.now().toDisplayString(),
        val editTransactionUid: String? = null,
        val isTimePickerVisible: Boolean = false
    )
}