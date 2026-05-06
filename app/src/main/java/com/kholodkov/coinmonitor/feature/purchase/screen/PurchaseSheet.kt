package com.kholodkov.coinmonitor.feature.purchase.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kholodkov.coinmonitor.R
import com.kholodkov.coinmonitor.core.tools.isValidAmountInput
import com.kholodkov.coinmonitor.core.ui.components.SheetButton
import com.kholodkov.coinmonitor.core.ui.dialogs.DateSelectorDialog
import com.kholodkov.coinmonitor.core.ui.fields.EditAmountField
import com.kholodkov.coinmonitor.core.ui.components.PaymentDetailsRow
import com.kholodkov.coinmonitor.core.ui.fields.TextInputField
import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.feature.purchase.model.ui.PurchaseState
import com.kholodkov.coinmonitor.feature.purchase.state.PurchaseUiIntent


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseSheet(
    purchaseState: PurchaseState,
    onIntent: (PurchaseUiIntent.Sheet) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = { onIntent(PurchaseUiIntent.Sheet.Dismiss) },
        sheetState = sheetState,
    ) {

        val descriptionFocus = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current

        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            val title = if (purchaseState.uid == null) {
                stringResource(R.string.new_purchase)
            } else {
                stringResource(R.string.edit_purchase)
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            EditAmountField(
                modifier = Modifier.fillMaxWidth(),
                value = purchaseState.amount,
                onValueChange = { value ->
                    if (value.isValidAmountInput()) {
                        onIntent(PurchaseUiIntent.Sheet.AmountChanged(value))
                    }
                },
                onDone = { focusManager.clearFocus() }
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            TextInputField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(descriptionFocus),
                value = purchaseState.description,
                labelText = stringResource(R.string.description),
                placeholderText = stringResource(R.string.enter_description),
                onValueChange = { description ->
                    onIntent(
                        PurchaseUiIntent.Sheet.DescriptionChanged(description)
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PaymentDetailsRow(
                currency = purchaseState.currency,
                periodIcon = Icons.Default.CalendarMonth,
                periodLabel = purchaseState.date,
                onEditCurrency = { currency ->
                    onIntent(PurchaseUiIntent.Sheet.CurrencyChanged(currency))
                },
                onEditPeriod = { onIntent(PurchaseUiIntent.Sheet.OpenDatePicker) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (purchaseState.isBuyButtonVisible) {
                SheetButton(
                    text = stringResource(R.string.buy),
                    enabled = purchaseState.isButtonsEnabled,
                    onClick = {
                        focusManager.clearFocus()
                        val uid = purchaseState.uid ?: error("Purchase uid mustn't be null")
                        onIntent(PurchaseUiIntent.Sheet.Buy(uid))
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            SheetButton(
                text = stringResource(R.string.save),
                enabled = purchaseState.isButtonsEnabled,
                onClick = {
                    focusManager.clearFocus()
                    onIntent(PurchaseUiIntent.Sheet.Save(purchaseState.uid))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (purchaseState.isDateSelectorVisible) {
        DateSelectorDialog(
            onDateSelected = { date -> onIntent(PurchaseUiIntent.Sheet.SetDate(date)) },
            onDismiss = { onIntent(PurchaseUiIntent.Sheet.DismissDatePicker) }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PurchaseSheetPreview() {
    PurchaseSheet(
        purchaseState = PurchaseState(
            uid = null,
            description = "Ноутбук",
            amount = "15365",
            date = "05.05.2026",
            currency = Currency.RSD,
            isDateSelectorVisible = false,
            isBuyButtonVisible = true,
            isButtonsEnabled = false
        ),
        onIntent = {}
    )
}