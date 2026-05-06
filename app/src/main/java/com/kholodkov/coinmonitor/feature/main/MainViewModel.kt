package com.kholodkov.coinmonitor.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kholodkov.coinmonitor.core.tools.parseToBigDecimal
import com.kholodkov.coinmonitor.core.tools.toDisplayString
import com.kholodkov.coinmonitor.core.tools.toInputString
import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.model.transaction.RestoreTransactionParams
import com.kholodkov.coinmonitor.domain.model.transaction.SaveTransactionParams
import com.kholodkov.coinmonitor.domain.usecase.currency.ObserveInputCurrencyUseCase
import com.kholodkov.coinmonitor.domain.usecase.currency.SetInputCurrencyUseCase
import com.kholodkov.coinmonitor.domain.usecase.transaction.DeleteTransactionUseCase
import com.kholodkov.coinmonitor.domain.usecase.transaction.ObserveDailySummary
import com.kholodkov.coinmonitor.domain.usecase.transaction.ObserveTransactionsByDateUseCase
import com.kholodkov.coinmonitor.domain.usecase.transaction.RestoreTransactionUseCase
import com.kholodkov.coinmonitor.domain.usecase.transaction.SaveTransactionUseCase
import com.kholodkov.coinmonitor.feature.main.mapper.toBudgetState
import com.kholodkov.coinmonitor.feature.main.mapper.toDayState
import com.kholodkov.coinmonitor.feature.main.mapper.toRestoreTransactionParams
import com.kholodkov.coinmonitor.feature.main.mapper.toTransactionItemList
import com.kholodkov.coinmonitor.feature.main.mapper.toTransactionState
import com.kholodkov.coinmonitor.feature.main.model.raw.MainData
import com.kholodkov.coinmonitor.feature.main.model.raw.TransactionData
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

    private val isDatePickerVisible = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val mainData = selectedDate.flatMapLatest { date ->
        combine(
            observeTransactionsByDate(date),
            observeDailySummary(date),
        ) { transactions, dailySummary ->
            MainData(
                date = date,
                balance = dailySummary.budget,
                spent = dailySummary.spent,
                remaining = dailySummary.remaining,
                currency = dailySummary.currency,
                transactions = transactions,
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = MainData()
    )

    private val inputCurrency = observeInputCurrencyUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = Currency.RSD
    )

    private val activeTransaction = MutableStateFlow<TransactionData?>(null)

    val uiState: StateFlow<MainUiState> = combine(
        mainData,
        activeTransaction,
        inputCurrency,
        isDatePickerVisible
    ) { mainInfo,
        transaction,
        currency,
        isDatePickerVisible ->

        MainUiState(
            dayState = mainInfo.toDayState(isDatePickerVisible),
            budgetState = mainInfo.toBudgetState(),
            transactions = mainInfo.transactions.toTransactionItemList(),
            transactionState = transaction?.toTransactionState(currency)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainUiState()
    )

    private val _events = MutableSharedFlow<MainUiEvent>()
    val events = _events.asSharedFlow()

    fun onIntent(intent: MainUiIntent) = when (intent) {
        is MainUiIntent.DayNavigation.PreviousDay -> handlePreviousDay()
        is MainUiIntent.DayNavigation.NextDay -> handleNextDay()
        is MainUiIntent.DayNavigation.OpenDatePicker -> handleOpenDatePicker()
        is MainUiIntent.DayNavigation.DismissDatePicker -> handleDismissDatePicker()
        is MainUiIntent.DayNavigation.SelectDate -> handleSelectDate(intent.date)

        is MainUiIntent.Item.AddNew -> handleAddNewTransaction()
        is MainUiIntent.Item.Edit -> handleEditTransaction(intent.uid)
        is MainUiIntent.Item.Delete -> handleDeleteTransaction(intent.uid)
        is MainUiIntent.Item.Restore -> handleRestoreTransaction(intent.params)

        is MainUiIntent.Sheet.AmountChanged -> handleAmountChanged(intent.amount)
        is MainUiIntent.Sheet.CurrencyChanged -> handleCurrencyChanged(intent.currency)
        is MainUiIntent.Sheet.OpenTimeSelector -> handleOpenTimeSelector()
        is MainUiIntent.Sheet.DismissTimeSelector -> handleDismissTimeSelector()
        is MainUiIntent.Sheet.SetTime -> handleSetTime(intent.time)
        is MainUiIntent.Sheet.Save -> handleSaveTransaction(intent.uid)
        is MainUiIntent.Sheet.Dismiss -> handleDismissActiveTransaction()
    }

    //DayNavigation intents
    private fun handleSelectDate(date: LocalDate) {
        selectedDate.value = date
    }

    private fun handleNextDay() = selectedDate.update { it.plusDays(1) }

    private fun handleOpenDatePicker() = isDatePickerVisible.update { true }

    private fun handleDismissDatePicker() = isDatePickerVisible.update { false }

    private fun handlePreviousDay() = selectedDate.update { it.minusDays(1) }

    //Item intents

    private fun handleEditTransaction(uid: String) {
        mainData.value.transactions.firstOrNull { it.uid == uid }?.let { transaction ->
            viewModelScope.launch {
                setInputCurrencyUseCase(transaction.currency)
            }
            activeTransaction.value = TransactionData(
                uid = uid,
                amount = transaction.amount.toInputString(),
                time = transaction.time.toDisplayString(),
                isTimePickerVisible = false
            )
        }
    }

    private fun handleDeleteTransaction(uid: String) {
        viewModelScope.launch {
            val transaction = mainData.value.transactions.firstOrNull { it.uid == uid }

            transaction?.let {
                deleteTransactionUseCase(transaction.uid)
                _events.emit(
                    MainUiEvent.ShowRestoreTransactionSnackbar(transaction.toRestoreTransactionParams())
                )
            }
        }
    }

    private fun handleRestoreTransaction(params: RestoreTransactionParams) {
        viewModelScope.launch { restoreTransactionUseCase(params) }
    }

    private fun handleAddNewTransaction() {
        activeTransaction.value = TransactionData(
            uid = null,
            amount = "",
            time = LocalTime.now().toDisplayString(),
            isTimePickerVisible = false
        )
    }

    //Sheet intents

    private fun handleDismissActiveTransaction() {
        activeTransaction.value = null
    }

    private fun handleOpenTimeSelector() = activeTransaction.update {
        it?.copy(isTimePickerVisible = true)
    }

    private fun handleSaveTransaction(uid: String?) {
        val state = activeTransaction.value ?: return
        val amount = state.amount.parseToBigDecimal() ?: return
        val date = selectedDate.value
        viewModelScope.launch {
            saveTransactionUseCase(
                SaveTransactionParams(
                    uid = uid,
                    date = date,
                    amount = amount,
                    currency = inputCurrency.value,
                    time = LocalTime.parse(state.time)
                )
            )

            activeTransaction.update { null }
        }
    }

    private fun handleAmountChanged(amount: String) = activeTransaction.update {
        it?.copy(amount = amount.replace(',', '.'))
    }

    private fun handleCurrencyChanged(currency: Currency) {
        viewModelScope.launch {
            setInputCurrencyUseCase(currency)
        }
    }

    private fun handleSetTime(time: LocalTime) = activeTransaction.update {
        it?.copy(
            time = time.toDisplayString(),
            isTimePickerVisible = false
        )
    }

    private fun handleDismissTimeSelector() =
        activeTransaction.update { it?.copy(isTimePickerVisible = false) }
}