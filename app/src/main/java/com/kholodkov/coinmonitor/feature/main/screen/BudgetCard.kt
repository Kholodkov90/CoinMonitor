package com.kholodkov.coinmonitor.feature.main.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kholodkov.coinmonitor.R
import com.kholodkov.coinmonitor.core.ui.components.InfoRow
import com.kholodkov.coinmonitor.core.ui.theme.LocalExtendedColors
import com.kholodkov.coinmonitor.feature.main.model.ui.BudgetState

@Composable
fun BudgetCard(
    modifier: Modifier = Modifier,
    budgetState: BudgetState
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {

        val extendedColors = LocalExtendedColors.current

        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {

            val balanceAmountColor = if (budgetState.isBalancePositive)
                extendedColors.positiveAmount
            else
                extendedColors.negativeAmount

            val remainingAmountColor = if (budgetState.isRemainingPositive)
                extendedColors.positiveAmount
            else
                extendedColors.negativeAmount

            InfoRow(
                icon = Icons.Outlined.Savings,
                label = stringResource(R.string.balance),
                amount = budgetState.balance,
                amountColor = balanceAmountColor
            )
            HorizontalDivider()
            InfoRow(
                icon = Icons.Default.Payments,
                label = stringResource(R.string.spent),
                amount = budgetState.spent,
                amountColor = MaterialTheme.colorScheme.onSurface
            )
            HorizontalDivider()
            InfoRow(
                icon = Icons.Outlined.AccountBalanceWallet,
                label = stringResource(R.string.remaining),
                amount = budgetState.remaining,
                amountColor = remainingAmountColor
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BudgetCardPreview() {
    BudgetCard(
        budgetState = BudgetState(
            balance = "27500 RSD",
            isBalancePositive = true,
            spent = "2500 RSD",
            remaining = "25000 RSD",
            isRemainingPositive = true,
        )
    )
}