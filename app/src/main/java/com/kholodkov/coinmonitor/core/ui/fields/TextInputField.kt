package com.kholodkov.coinmonitor.core.ui.fields

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun TextInputField(
    modifier: Modifier = Modifier,
    value: String,
    labelText: String,
    placeholderText: String,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        textStyle = MaterialTheme.typography.titleLarge,
        onValueChange = onValueChange,
        label = {
            Text(
                text = labelText,
                style = MaterialTheme.typography.titleMedium
            )
        },
        placeholder = {
            Text(
                text = placeholderText,
                style = MaterialTheme.typography.titleLarge
            )
        },
        singleLine = true
    )
}

@Preview(showBackground = true)
@Composable
private fun TextInputFieldPreview() {
    TextInputField(
        value = "",
        labelText = "label",
        placeholderText = "placeholder",
        onValueChange = {},
    )
}