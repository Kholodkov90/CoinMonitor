package com.kholodkov.coinmonitor.feature.settings.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EuroSymbol
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kholodkov.coinmonitor.feature.settings.R
import com.kholodkov.coinmonitor.core.ui.components.CircleIcon
import com.kholodkov.coinmonitor.domain.model.currency.Currency

@Composable
fun CurrencyCard(
    selectedCurrency: Currency,
    onSelect: (Currency) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleIcon(
                icon = Icons.Default.EuroSymbol,
                backgroundColor = MaterialTheme.colorScheme.surfaceContainer
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.currency),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.select_currency),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Column(modifier = Modifier.selectableGroup()) {
            Currency.entries.forEach { currency ->
                RadioOptionRow(
                    name = currency.name,
                    isSelected = currency == selectedCurrency,
                    onClick = { onSelect(currency) }
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