package com.kholodkov.coinmonitor.feature.purchase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kholodkov.coinmonitor.core.tools.parseToBigDecimal
import com.kholodkov.coinmonitor.core.tools.toDisplayString
import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.model.purchase.EditPurchaseParams
import com.kholodkov.coinmonitor.domain.model.purchase.PurchaseProjection
import com.kholodkov.coinmonitor.domain.model.purchase.RestorePurchaseParams
import com.kholodkov.coinmonitor.domain.model.purchase.SavePurchaseParams
import com.kholodkov.coinmonitor.domain.usecase.purchase.BuyPurchaseUseCase
import com.kholodkov.coinmonitor.domain.usecase.purchase.DeletePurchaseUseCase
import com.kholodkov.coinmonitor.domain.usecase.currency.ObserveInputCurrencyUseCase
import com.kholodkov.coinmonitor.domain.usecase.purchase.ObservePurchaseTotalAmountUseCase
import com.kholodkov.coinmonitor.domain.usecase.purchase.ObservePurchasesUseCase
import com.kholodkov.coinmonitor.domain.usecase.purchase.RestorePurchaseUseCase
import com.kholodkov.coinmonitor.domain.usecase.purchase.SavePurchaseUseCase
import com.kholodkov.coinmonitor.domain.usecase.currency.SetInputCurrencyUseCase
import com.kholodkov.coinmonitor.feature.purchase.mapper.toItemList
import com.kholodkov.coinmonitor.feature.purchase.mapper.toRestorePurchaseParams
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
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class PurchaseViewModel @Inject constructor(
    observePurchaseTotalAmountUseCase: ObservePurchaseTotalAmountUseCase,
    observePurchasesUseCase: ObservePurchasesUseCase,
    observeInputCurrencyUseCase: ObserveInputCurrencyUseCase,
    private val setInputCurrencyUseCase: SetInputCurrencyUseCase,
    private val savePurchaseUseCase: SavePurchaseUseCase,
    private val deletePurchaseUseCase: DeletePurchaseUseCase,
    private val restorePurchaseUseCase: RestorePurchaseUseCase,
    private val buyPurchaseUseCase: BuyPurchaseUseCase,
) : ViewModel() {

    private val inputCurrencyState = observeInputCurrencyUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = Currency.RSD
    )

    val mainState = combine(
        observePurchaseTotalAmountUseCase(),
        observePurchasesUseCase()
    ) { plannedAmount, purchases ->
        MainInfo(
            plannedAmount = plannedAmount,
            purchases = purchases
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = MainInfo()
    )


    private val editPurchaseState = MutableStateFlow(EditPurchaseState())

    val uiState: StateFlow<PurchaseUiState> = combine(
        mainState,
        editPurchaseState,
        inputCurrencyState,
    ) { mainInfo,
        editPurchase,
        currency ->
        val isBuyButtonVisible = editPurchase.uid != null && editPurchase.transactionUid == null
        PurchaseUiState(
            planedAmount = mainInfo.plannedAmount.toDisplayString(),
            purchases = mainInfo.purchases.toItemList(),
            isPurchaseSheetVisible = editPurchase.isVisible,
            inputUid = editPurchase.uid,
            inputDescription = editPurchase.description,
            inputAmount = editPurchase.amount,
            inputDate = editPurchase.date.toDisplayString(),
            inputCurrency = currency,
            isDateSelectorOpened = editPurchase.isDatePickerVisible,
            isBuyButtonVisible = isBuyButtonVisible
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PurchaseUiState()
    )

    private val _events = MutableSharedFlow<PurchaseUiEvent>()
    val events = _events.asSharedFlow()

    fun onIntent(intent: PurchaseUiIntent) = when (intent) {
        //Main intents
        is PurchaseUiIntent.AddPurchase -> handleAddPurchase()
        is PurchaseUiIntent.DeletePurchase -> handleDeletePurchase(intent.uid)
        is PurchaseUiIntent.EditPurchase -> handleEditPurchase(intent.uid)
        is PurchaseUiIntent.RestorePurchase -> handleRestorePurchase(intent.params)

        //Purchase sheet intents
        is PurchaseUiIntent.EditAmount -> handleEditAmount(intent.amount)
        is PurchaseUiIntent.EditDescription -> handleEditDescription(intent.description)
        is PurchaseUiIntent.EditCurrency -> handleEditCurrency(intent.currency)
        is PurchaseUiIntent.ShowDatePicker -> handleShowDatePicker()
        is PurchaseUiIntent.SetDate -> handleSetDate(intent.date)
        is PurchaseUiIntent.SavePurchase -> handleSavePurchase(intent.uid)
        is PurchaseUiIntent.BuyPurchase -> handleBuyPurchase(intent.uid)
        is PurchaseUiIntent.HidePurchaseSheet -> handleHidePurchaseSheet()
        is PurchaseUiIntent.DismissDateSelector -> handleDismissDateSelector()
    }

    private fun handleAddPurchase() = editPurchaseState.update {
        it.copy(
            isVisible = true,
            amount = "",
            description = "",
            date = LocalDate.now(),
            uid = null,
            transactionUid = null,
            isDatePickerVisible = false
        )
    }

    private fun handleDeletePurchase(uid: String) {
        viewModelScope.launch {
            val purchase = mainState.value.purchases.firstOrNull { it.uid == uid }

            purchase?.let {
                deletePurchaseUseCase(purchase.uid)
                _events.emit(
                    PurchaseUiEvent.ShowRestorePurchaseSnackbar(
                        purchase.toRestorePurchaseParams()
                    )
                )
            }
        }
    }

    private fun handleEditPurchase(uid: String) {
        mainState.value.purchases.firstOrNull { it.uid == uid }?.let { purchase ->
            viewModelScope.launch {
                setInputCurrencyUseCase(purchase.currency)
            }
            editPurchaseState.update {
                it.copy(
                    isVisible = true,
                    amount = purchase.amount.toDisplayString(),
                    description = purchase.description,
                    date = purchase.date,
                    uid = purchase.uid,
                    transactionUid = purchase.transactionUid,
                    isDatePickerVisible = false
                )
            }
        }
    }

    private fun handleRestorePurchase(params: RestorePurchaseParams) {
        viewModelScope.launch { restorePurchaseUseCase(params) }
    }

    private fun handleEditAmount(amount: String) = editPurchaseState.update {
        it.copy(amount = amount.replace(',', '.'))
    }

    private fun handleEditDescription(description: String) = editPurchaseState.update {
        it.copy(description = description)
    }

    private fun handleShowDatePicker() = editPurchaseState.update {
        it.copy(isDatePickerVisible = true)
    }

    private fun handleEditCurrency(currency: Currency) {
        viewModelScope.launch { setInputCurrencyUseCase(currency) }
    }

    private fun handleSetDate(date: LocalDate) = editPurchaseState.update {
        it.copy(date = date)
    }

    private fun handleSavePurchase(uid: String?) {
        val state = editPurchaseState.value
        val amount = state.amount.parseToBigDecimal() ?: return
        val date = state.date
        val description = state.description

        viewModelScope.launch {
            savePurchaseUseCase(
                SavePurchaseParams(
                    uid = uid,
                    date = date,
                    amount = amount,
                    currency = inputCurrencyState.value,
                    description = description
                )
            )
        }
    }

    private fun handleBuyPurchase(uid: String) {
        val state = editPurchaseState.value
        val amount = state.amount.parseToBigDecimal() ?: return
        val date = state.date
        val description = state.description

        viewModelScope.launch {
            buyPurchaseUseCase(
                EditPurchaseParams(
                    uid = uid,
                    date = date,
                    amount = amount,
                    currency = inputCurrencyState.value,
                    description = description
                )
            )
        }
    }

    private fun handleHidePurchaseSheet() = editPurchaseState.update {
        it.copy(isVisible = false)
    }

    private fun handleDismissDateSelector() = editPurchaseState.update {
        it.copy(isDatePickerVisible = false)
    }

    data class MainInfo(
        val plannedAmount: BigDecimal = BigDecimal.ZERO,
        val purchases: List<PurchaseProjection> = listOf()
    )

    data class EditPurchaseState(
        val isVisible: Boolean = false,
        val amount: String = "",
        val description: String = "",
        val date: LocalDate = LocalDate.now(),
        val uid: String? = null,
        val transactionUid: String? = null,
        val isDatePickerVisible: Boolean = false
    )
}
