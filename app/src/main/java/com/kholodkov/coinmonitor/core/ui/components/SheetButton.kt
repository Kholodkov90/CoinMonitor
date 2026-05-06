package com.kholodkov.coinmonitor.core.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SheetButton(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        onClick = onClick,
        enabled = enabled
    ) {
        Text(text = text)
    }
}
@Preview(showBackground = true)
@Composable
private fun SheetButtonPreview(){
    SheetButton(
        text = "Сохранить",
        enabled = true,
        onClick = {}
    )
}