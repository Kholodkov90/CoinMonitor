package com.kholodkov.coinmonitor.feature.purchase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kholodkov.coinmonitor.core.tools.parseToBigDecimal
import com.kholodkov.coinmonitor.core.tools.toDisplayString
import com.kholodkov.coinmonitor.core.tools.toInputString
import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.model.purchase.EditPurchaseParams
import com.kholodkov.coinmonitor.domain.model.purchase.RestorePurchaseParams
import com.kholodkov.coinmonitor.domain.model.purchase.SavePurchaseParams
import com.kholodkov.coinmonitor.domain.usecase.currency.ObserveInputCurrencyUseCase
import com.kholodkov.coinmonitor.domain.usecase.currency.SetInputCurrencyUseCase
import com.kholodkov.coinmonitor.domain.usecase.purchase.BuyPurchaseUseCase
import com.kholodkov.coinmonitor.domain.usecase.purchase.DeletePurchaseUseCase
import com.kholodkov.coinmonitor.domain.usecase.purchase.ObservePurchaseSummaryUseCase
import com.kholodkov.coinmonitor.domain.usecase.purchase.ObservePurchasesUseCase
import com.kholodkov.coinmonitor.domain.usecase.purchase.RestorePurchaseUseCase
import com.kholodkov.coinmonitor.domain.usecase.purchase.SavePurchaseUseCase
import com.kholodkov.coinmonitor.feature.purchase.mapper.toPurchaseItemList
import com.kholodkov.coinmonitor.feature.purchase.mapper.toPurchaseState
import com.kholodkov.coinmonitor.feature.purchase.mapper.toRestorePurchaseParams
import com.kholodkov.coinmonitor.feature.purchase.model.raw.MainData
import com.kholodkov.coinmonitor.feature.purchase.model.raw.PurchaseData
import com.kholodkov.coinmonitor.feature.purchase.state.PurchaseUiEvent
import com.kholodkov.coinmonitor.feature.purchase.state.PurchaseUiIntent
import com.kholodkov.coinmonitor.feature.purchase.state.PurchaseUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class PurchaseViewModel @Inject constructor(
    observePurchaseSummaryUseCase: ObservePurchaseSummaryUseCase,
    observePurchasesUseCase: ObservePurchasesUseCase,
    observeInputCurrencyUseCase: ObserveInputCurrencyUseCase,
    private val setInputCurrencyUseCase: SetInputCurrencyUseCase,
    private val savePurchaseUseCase: SavePurchaseUseCase,
    private val deletePurchaseUseCase: DeletePurchaseUseCase,
    private val restorePurchaseUseCase: RestorePurchaseUseCase,
    private val buyPurchaseUseCase: BuyPurchaseUseCase,
) : ViewModel() {

    private val inputCurrency = observeInputCurrencyUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = Currency.RSD
    )

    private val mainData = combine(
        observePurchaseSummaryUseCase(),
        observePurchasesUseCase()
    ) { summary, purchases ->
        MainData(
            plannedAmount = summary.totalAmount,
            plannedCurrency = summary.currency,
            purchases = purchases
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = MainData()
    )


    private val activePurchase = MutableStateFlow<PurchaseData?>(null)

    val uiState: StateFlow<PurchaseUiState> = combine(
        mainData,
        activePurchase,
        inputCurrency,
    ) { mainInfo,
        purchase,
        currency ->
        PurchaseUiState(
            plannedAmount = "${mainInfo.plannedAmount.toDisplayString()} ${mainInfo.plannedCurrency.name}",
            purchases = mainInfo.purchases.toPurchaseItemList(),
            purchaseState = purchase?.toPurchaseState(currency)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PurchaseUiState()
    )

    private val _events = MutableSharedFlow<PurchaseUiEvent>()
    val events = _events.asSharedFlow()

    fun onIntent(intent: PurchaseUiIntent) = when (intent) {
        is PurchaseUiIntent.Item.AddNew -> handleAddNewPurchase()
        is PurchaseUiIntent.Item.Delete -> handleDeletePurchase(intent.uid)
        is PurchaseUiIntent.Item.Edit -> handleEditPurchase(intent.uid)
        is PurchaseUiIntent.Item.Restore -> handleRestorePurchase(intent.params)

        is PurchaseUiIntent.Sheet.AmountChanged -> handleAmountChanged(intent.amount)
        is PurchaseUiIntent.Sheet.DescriptionChanged -> handleDescriptionChanged(intent.description)
        is PurchaseUiIntent.Sheet.CurrencyChanged -> handleCurrencyChanged(intent.currency)
        is PurchaseUiIntent.Sheet.OpenDatePicker -> handleOpenDatePicker()
        is PurchaseUiIntent.Sheet.DismissDatePicker -> handleDismissDatePicker()
        is PurchaseUiIntent.Sheet.SetDate -> handleSetDate(intent.date)
        is PurchaseUiIntent.Sheet.Save -> handleSavePurchase(intent.uid)
        is PurchaseUiIntent.Sheet.Buy -> handleBuyPurchase(intent.uid)
        is PurchaseUiIntent.Sheet.Dismiss -> handleDismissActivePurchase()
    }

    //Item intents

    private fun handleAddNewPurchase() {
        activePurchase.value = PurchaseData(
            uid = null,
            amount = "",
            description = "",
            date = LocalDate.now(),
            transactionUid = null,
            isDatePickerVisible = false
        )
    }

    private fun handleDeletePurchase(uid: String) {
        viewModelScope.launch {
            val purchase = mainData.value.purchases.firstOrNull { it.uid == uid }

            purchase?.let {
                deletePurchaseUseCase(purchase.uid)
                _events.emit(
                    PurchaseUiEvent.ShowRestorePurchaseSnackbar(purchase.toRestorePurchaseParams())
                )
            }
        }
    }

    private fun handleEditPurchase(uid: String) {
        mainData.value.purchases.firstOrNull { it.uid == uid }?.let { purchase ->
            viewModelScope.launch {
                setInputCurrencyUseCase(purchase.currency)
            }
            activePurchase.value = PurchaseData(
                uid = purchase.uid,
                amount = purchase.amount.toInputString(),
                description = purchase.description,
                date = purchase.date,
                transactionUid = purchase.transactionUid,
                isDatePickerVisible = false
            )
        }
    }

    private fun handleRestorePurchase(params: RestorePurchaseParams) {
        viewModelScope.launch { restorePurchaseUseCase(params) }
    }


    //Sheet intents

    private fun handleAmountChanged(amount: String) = activePurchase.update {
        it?.copy(amount = amount.replace(',', '.'))
    }

    private fun handleDescriptionChanged(description: String) = activePurchase.update {
        it?.copy(description = description)
    }

    private fun handleOpenDatePicker() = activePurchase.update {
        it?.copy(isDatePickerVisible = true)
    }

    private fun handleCurrencyChanged(currency: Currency) {
        viewModelScope.launch { setInputCurrencyUseCase(currency) }
    }

    private fun handleSetDate(date: LocalDate) = activePurchase.update {
        it?.copy(date = date)
    }

    private fun handleSavePurchase(uid: String?) {
        val state = activePurchase.value ?: return
        val amount = state.amount.parseToBigDecimal() ?: return
        val date = state.date
        val description = state.description

        viewModelScope.launch {
            savePurchaseUseCase(
                SavePurchaseParams(
                    uid = uid,
                    date = date,
                    amount = amount,
                    currency = inputCurrency.value,
                    description = description
                )
            )

            activePurchase.update { null }
        }
    }

    private fun handleBuyPurchase(uid: String) {
        val state = activePurchase.value ?: return
        val amount = state.amount.parseToBigDecimal() ?: return
        val date = state.date
        val description = state.description

        viewModelScope.launch {
            buyPurchaseUseCase(
                EditPurchaseParams(
                    uid = uid,
                    date = date,
                    amount = amount,
                    currency = inputCurrency.value,
                    description = description
                )
            )

            activePurchase.update { null }
        }
    }

    private fun handleDismissActivePurchase() {
        activePurchase.value = null
    }

    private fun handleDismissDatePicker() = activePurchase.update {
        it?.copy(isDatePickerVisible = false)
    }
}
