package com.kholodkov.coinmonitor.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kholodkov.coinmonitor.domain.model.currency.Currency

@Composable
fun PaymentDetailsRow(
    modifier: Modifier = Modifier,
    currency: Currency,
    periodIcon: ImageVector,
    periodLabel: String,
    onEditCurrency: (Currency) -> Unit,
    onEditPeriod: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CurrencySelector(
            modifier = Modifier.fillMaxHeight(),
            selected = currency,
            onSelect = { currency -> onEditCurrency(currency) }
        )
        EditPeriodButton(
            modifier = Modifier.fillMaxHeight(),
            icon = periodIcon,
            label = periodLabel,
            onClick = onEditPeriod
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrencySelector(
    modifier: Modifier = Modifier,
    selected: Currency,
    onSelect: (Currency) -> Unit
) {
    SingleChoiceSegmentedButtonRow {
        Currency.entries.forEachIndexed { index, currency ->
            SegmentedButton(
                modifier = modifier,
                selected = currency == selected,
                onClick = { onSelect(currency) },
                shape = SegmentedButtonDefaults.itemShape(index, Currency.entries.size),
                icon = {}
            ) {
                Text(currency.name)
            }
        }
    }
}

@Composable
private fun EditPeriodButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        modifier = modifier,
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label)
        Spacer(modifier = Modifier.width(8.dp))
        VerticalDivider(
            modifier = Modifier
                .fillMaxHeight(0.7f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = null
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PaymentDetailsRowPreview() {
    PaymentDetailsRow(
        currency = Currency.EUR,
        periodIcon = Icons.Default.Schedule,
        periodLabel = "11:11",
        onEditCurrency = {},
        onEditPeriod = {}
    )
}