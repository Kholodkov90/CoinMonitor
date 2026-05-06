package com.kholodkov.coinmonitor.feature.main.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kholodkov.coinmonitor.R
import com.kholodkov.coinmonitor.core.tools.isValidAmountInput
import com.kholodkov.coinmonitor.core.ui.components.SheetButton
import com.kholodkov.coinmonitor.core.ui.fields.EditAmountField
import com.kholodkov.coinmonitor.core.ui.components.PaymentDetailsRow
import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.feature.main.model.ui.TransactionState
import com.kholodkov.coinmonitor.feature.main.state.MainUiIntent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionSheet(
    transactionState: TransactionState,
    onIntent: (MainUiIntent.Sheet) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = { onIntent(MainUiIntent.Sheet.Dismiss) },
    ) {
        val focusManager = LocalFocusManager.current
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            val title = if (transactionState.uid == null) {
                stringResource(R.string.new_expense)
            } else {
                stringResource(R.string.edit_expense)
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            EditAmountField(
                modifier = Modifier.fillMaxWidth(),
                value = transactionState.amount,
                onValueChange = { value ->
                    if (value.isValidAmountInput()) onIntent(MainUiIntent.Sheet.AmountChanged(value))
                },
                onDone = { focusManager.clearFocus() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PaymentDetailsRow(
                currency = transactionState.currency,
                periodIcon = Icons.Default.Schedule,
                periodLabel = transactionState.time,
                onEditCurrency = { currency ->
                    onIntent(MainUiIntent.Sheet.CurrencyChanged(currency))
                },
                onEditPeriod = { onIntent(MainUiIntent.Sheet.OpenTimeSelector) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SheetButton(
                text = stringResource(R.string.save),
                enabled = transactionState.isSaveEnabled,
                onClick = {
                    focusManager.clearFocus()
                    onIntent(MainUiIntent.Sheet.Save(transactionState.uid))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (transactionState.isTimePickerVisible) {
        TimeSelectorDialog(
            time = transactionState.time,
            onDismiss = { onIntent(MainUiIntent.Sheet.DismissTimeSelector) },
            onConfirm = { time -> onIntent(MainUiIntent.Sheet.SetTime(time)) }
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun TransactionSheetPreview() {
    TransactionSheet(
        transactionState = TransactionState(
            uid = null,
            amount = "1020",
            currency = Currency.RSD,
            time = "11:25",
            isTimePickerVisible = false,
            isSaveEnabled = true
        ),
        onIntent = {},
    )
}