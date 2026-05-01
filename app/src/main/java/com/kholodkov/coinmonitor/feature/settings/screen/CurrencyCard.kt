package com.kholodkov.coinmonitor.feature.settings.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EuroSymbol
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kholodkov.coinmonitor.domain.model.currency.Currency

@Composable
fun CurrencyCard(
    selectedCurrency: Currency,
    onSelect: (Currency) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Icon(
                modifier = Modifier.size(35.dp),
                imageVector = Icons.Default.EuroSymbol,
                contentDescription = null
            )
            Column(modifier = Modifier.weight(1f)) {
                Text("Валюта")
                Text("Выберите валюту")
            }
        }
        Column(modifier = Modifier.selectableGroup()) {
            Currency.entries.forEach { currency ->
                RadioOptionRow(
                    name = currency.name,
                    isSelected = currency == selectedCurrency,
                    onClick = { onSelect(currency) },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrencyCardPreview() {
    CurrencyCard(
        selectedCurrency = Currency.RSD,
        onSelect = {}
    )
}