package com.kholodkov.coinmonitor.feature.main.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kholodkov.coinmonitor.core.tools.isValidAmountInput
import com.kholodkov.coinmonitor.domain.model.Currency
import java.time.LocalTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionSheet(
    uid: String?,
    amount: String,
    currency: Currency,
    time: String,
    isTimeSelectorOpened: Boolean,
    isSaveAvailable: Boolean,
    onDismiss: () -> Unit,
    onEditAmount: (String) -> Unit,
    onEditCurrency: (Currency) -> Unit,
    onEditTime: () -> Unit,
    onSaveTransaction: (String?) -> Unit,
    onDismissTimeSelector: () -> Unit,
    onSetTime: (LocalTime) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
    ) {
        val focusManager = LocalFocusManager.current
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(if (uid == null) "Новая трата" else "Редактировать")
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = amount,
                onValueChange = { value ->
                    if (value.isValidAmountInput()) {
                        onEditAmount(value)
                    }
                },
                label = { Text("Сумма оплаты") },
                placeholder = { Text("Введите сумму") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CurrencySelector(
                    selected = currency,
                    onSelect = { currency -> onEditCurrency(currency) }
                )
                TimeButton(
                    time = time,
                    onClick = onEditTime
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    focusManager.clearFocus()
                    onSaveTransaction(uid)
                    onDismiss()
                },
                modifier = Modifier.align(Alignment.End),
                enabled = isSaveAvailable
            ) {
                Text(text = "Внести")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (isTimeSelectorOpened) {
            TimeSelectorDialog(
                time = time,
                onDismiss = onDismissTimeSelector,
                onConfirm = { onSetTime(it) }
            )
        }
    }


}

@Composable
fun TimeButton(
    time: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick
    ) {
        Text(
            text = time,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.width(6.dp))
        Icon(imageVector = Icons.Default.Edit, contentDescription = "")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencySelector(
    selected: Currency,
    onSelect: (Currency) -> Unit
) {
    SingleChoiceSegmentedButtonRow {
        SegmentedButton(
            selected = selected == Currency.RSD,
            onClick = { onSelect(Currency.RSD) },
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
        ) {
            Text("RSD")
        }

        SegmentedButton(
            selected = selected == Currency.EUR,
            onClick = { onSelect(Currency.EUR) },
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
        ) {
            Text("EUR")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionSheetPreview() {
    TransactionSheet(
        uid = null,
        amount = "100",
        currency = Currency.RSD,
        time = "11:25",
        isTimeSelectorOpened = true,
        isSaveAvailable = true,
        onDismiss = {},
        onEditAmount = {},
        onEditCurrency = {},
        onEditTime = {},
        onSaveTransaction = {},
        onDismissTimeSelector = {},
        onSetTime = {}
    )
}