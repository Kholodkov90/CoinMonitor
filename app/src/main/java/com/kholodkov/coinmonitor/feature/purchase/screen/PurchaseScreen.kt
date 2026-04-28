package com.kholodkov.coinmonitor.feature.purchase.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kholodkov.coinmonitor.core.ui.InfoRow
import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.model.purchase.PurchaseStatus
import com.kholodkov.coinmonitor.feature.purchase.PurchaseViewModel
import com.kholodkov.coinmonitor.feature.purchase.model.PurchaseItem
import com.kholodkov.coinmonitor.feature.purchase.state.PurchaseUiEvent
import com.kholodkov.coinmonitor.feature.purchase.state.PurchaseUiIntent
import com.kholodkov.coinmonitor.feature.purchase.state.PurchaseUiState
import java.math.BigDecimal

@Composable
fun PurchaseScreenRoute(
    viewModel: PurchaseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    PurchaseScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        sendIntent = { viewModel.onIntent(it) }
    )

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is PurchaseUiEvent.ShowRestorePurchaseSnackbar -> {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    val result = snackbarHostState.showSnackbar(
                        message = "Запланированная покупка удалена",
                        actionLabel = "Отмена",
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onIntent(PurchaseUiIntent.RestorePurchase(event.params))
                    }
                }
            }
        }
    }
}

@Composable
fun PurchaseScreen(
    uiState: PurchaseUiState,
    snackbarHostState: SnackbarHostState,
    sendIntent: (PurchaseUiIntent) -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { sendIntent(PurchaseUiIntent.AddPurchase) }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "")
            }
        }
    ) { innerPadding ->

        if (uiState.isPurchaseSheetVisible) {
            PurchaseSheet(
                uid = uiState.inputUid,
                description = uiState.inputDescription,
                amount = uiState.inputAmount,
                currency = uiState.inputCurrency,
                date = uiState.inputDate,
                isDateSelectorOpened = uiState.isDateSelectorOpened,
                isButtonsEnabled = uiState.isButtonsEnabled,
                isBuyButtonVisible = uiState.isBuyButtonVisible,
                onDismiss = { sendIntent(PurchaseUiIntent.HidePurchaseSheet) },
                onEditAmount = { amount -> sendIntent(PurchaseUiIntent.EditAmount(amount)) },
                onEditDescription = { description ->
                    sendIntent(PurchaseUiIntent.EditDescription(description))
                },
                onEditCurrency = { currency -> sendIntent(PurchaseUiIntent.EditCurrency(currency)) },
                onShowDatePicker = { sendIntent(PurchaseUiIntent.ShowDatePicker) },
                onSetDate = { date -> sendIntent(PurchaseUiIntent.SetDate(date)) },
                onDismissDateSelector = { sendIntent(PurchaseUiIntent.DismissDateSelector) },
                onSavePurchase = { uid -> sendIntent(PurchaseUiIntent.SavePurchase(uid)) },
                onBuyPurchase = { uid -> sendIntent(PurchaseUiIntent.BuyPurchase(uid)) }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 8.dp)
        ) {
            Text("Планируемые покупки")
            Spacer(modifier = Modifier.height(8.dp))
            Spacer(modifier = Modifier.height(8.dp))
            InfoRow("Запланированная сумма", uiState.planedAmount)
            Spacer(modifier = Modifier.height(8.dp))
            PurchaseList(
                purchaseItems = uiState.purchases,
                onSwipe = { uid -> sendIntent(PurchaseUiIntent.DeletePurchase(uid)) },
                onClick = { uid -> sendIntent(PurchaseUiIntent.EditPurchase(uid)) }
            )
        }
    }
}

@Composable
private fun PurchaseList(
    purchaseItems: List<PurchaseItem>,
    onClick: (String) -> Unit,
    onSwipe: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = purchaseItems,
            key = { it.uid }
        ) { item ->
            PurchaseCard(
                item = item,
                onClick = onClick,
                onSwipe = onSwipe
            )
        }
    }
}


@Preview
@Composable
fun PurchaseScreenPreview() {
    PurchaseScreen(
        uiState = PurchaseUiState(
            planedAmount = "27000 RSD",
            purchases = listOf(
                PurchaseItem(
                    uid = "1",
                    amount = "27000 RSD",
                    description = "Оперативная память",
                    user = "Сергей",
                    status = PurchaseStatus.Pending(
                        BigDecimal("1000"),
                        Currency.RSD
                    ),
                    date = "30.04.2026"
                )
            ),
            isPurchaseSheetVisible = false,
            inputUid = "",
            inputDescription = "",
            inputAmount = "",
            inputCurrency = Currency.RSD
        ),
        snackbarHostState = SnackbarHostState()
    ) { }
}