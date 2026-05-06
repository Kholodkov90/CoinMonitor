package com.kholodkov.coinmonitor.feature.purchase.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kholodkov.coinmonitor.R
import com.kholodkov.coinmonitor.core.ui.components.InfoRow

@Composable
fun PurchaseSummaryCard(
    modifier: Modifier = Modifier,
    plannedAmount: String
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {

        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            InfoRow(
                icon = Icons.Outlined.Wallet,
                label = stringResource(R.string.planned_amount),
                amount = plannedAmount,
                amountColor = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PurchaseSummaryCardPreview() {
    PurchaseSummaryCard(
        plannedAmount = "1 000 000 RSD"
    )
}
