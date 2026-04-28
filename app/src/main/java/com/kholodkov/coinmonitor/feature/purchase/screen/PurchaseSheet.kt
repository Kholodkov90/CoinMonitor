package com.kholodkov.coinmonitor.feature.purchase.screen

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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kholodkov.coinmonitor.core.tools.isValidAmountInput
import com.kholodkov.coinmonitor.core.ui.DateSelectorDialog
import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.feature.main.screen.CurrencySelector
import java.time.LocalDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseSheet(
    uid: String?,
    description: String,
    amount: String,
    currency: Currency,
    date: String,
    isDateSelectorOpened: Boolean,
    isButtonsEnabled: Boolean,
    isBuyButtonVisible: Boolean,
    onDismiss: () -> Unit,
    onDismissDateSelector: () -> Unit,
    onEditAmount: (String) -> Unit,
    onEditDescription: (String) -> Unit,
    onEditCurrency: (Currency) -> Unit,
    onShowDatePicker: () -> Unit,
    onSetDate: (LocalDate) -> Unit,
    onSavePurchase: (String?) -> Unit,
    onBuyPurchase: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {

        val descriptionFocus = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current

        if (isDateSelectorOpened) {
            DateSelectorDialog(
                onDateSelected = { onSetDate(it) },
                onDismiss = onDismissDateSelector
            )
        }
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
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        descriptionFocus.requestFocus()
                    }
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(descriptionFocus),
                value = description,
                onValueChange = { value -> onEditDescription(value) },
                label = { Text("Описание") },
                placeholder = { Text("Введите описание") },
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
                DateButton(
                    date = date,
                    onClick = onShowDatePicker
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    focusManager.clearFocus()
                    onSavePurchase(uid)
                    onDismiss()
                },
                modifier = Modifier.align(Alignment.End),
                enabled = isButtonsEnabled
            ) {
                Text(text = "Внести")
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (isBuyButtonVisible) {
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        onBuyPurchase(uid?: error ("Purchase uid mustn't be null"))
                        onDismiss()
                    },
                    modifier = Modifier.align(Alignment.End),
                    enabled = isButtonsEnabled
                ) {
                    Text(text = "Купить")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

        }
    }
}


@Composable
fun DateButton(
    date: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick
    ) {
        Text(
            text = date,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.width(6.dp))
        Icon(imageVector = Icons.Default.Edit, contentDescription = "")
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C1B1F)
@Composable
fun PurchaseSheetPreview() {
    PurchaseSheet(
        uid = "1",
        description = "Купить всякое",
        amount = "15000",
        currency = Currency.RSD,
        date = "01.01.0001",
        isDateSelectorOpened = false,
        isButtonsEnabled = false,
        isBuyButtonVisible = true,
        onDismiss = {},
        onBuyPurchase = {},
        onEditAmount = {},
        onEditDescription = {},
        onEditCurrency = {},
        onShowDatePicker = {},
        onSavePurchase = {},
        onDismissDateSelector = {},
        onSetDate = {},
    )
}