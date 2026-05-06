package com.kholodkov.coinmonitor.core.ui.fields

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.kholodkov.coinmonitor.R

@Composable
fun EditAmountField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        textStyle = MaterialTheme.typography.titleLarge,
        onValueChange = onValueChange,
        label = {
            Text(
                text = stringResource(R.string.amount),
                style = MaterialTheme.typography.titleMedium
            )
        },
        placeholder = {
            Text(
                text = stringResource(R.string.enter_amount),
                style = MaterialTheme.typography.titleLarge
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { onDone() }
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun EditAmountFieldPreview() {
    EditAmountField(
        value = "10000000",
        onValueChange = {},
        onDone = {}
    )
}