package com.kholodkov.coinmonitor.feature.purchase.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kholodkov.coinmonitor.R
import com.kholodkov.coinmonitor.core.tools.toDisplayStringFloored
import com.kholodkov.coinmonitor.core.ui.components.CircleIcon
import com.kholodkov.coinmonitor.core.ui.theme.LocalExtendedColors
import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.model.purchase.PurchaseStatus
import com.kholodkov.coinmonitor.feature.purchase.model.ui.PurchaseItem
import java.math.BigDecimal

@Composable
fun PurchaseCard(
    item: PurchaseItem,
    onClick: (String) -> Unit,
    onSwipe: (String) -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState()
    val stateUiData = item.status.toUiData()

    SwipeToDismissBox(
        state = dismissState,
        onDismiss = { onSwipe(item.uid) },
        backgroundContent = { },
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
                    .clickable { onClick(item.uid) }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircleIcon(
                    icon = stateUiData.icon,
                    iconTint = stateUiData.contentColor,
                    backgroundColor = stateUiData.containerColor
                )

                Spacer(modifier = Modifier.width(12.dp))
                PurchaseCardContent(
                    modifier = Modifier.weight(1f),
                    item = item,
                    amountColor = stateUiData.amountColor
                )

                PurchaseCardStatus(
                    modifier = Modifier.align(Alignment.Top),
                    item = item,
                    stateUiData = stateUiData
                )
            }
        }
    }
}

@Composable
private fun PurchaseCardContent(
    modifier: Modifier = Modifier,
    item: PurchaseItem,
    amountColor: Color
) {
    Column(modifier = modifier) {
        Text(
            text = item.amount,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = amountColor
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(text = item.description)

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Text(
                text = item.date,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.width(4.dp))

            VerticalDivider(
                modifier = Modifier
                    .fillMaxHeight(0.6f)
                    .align(Alignment.CenterVertically),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = item.user,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun PurchaseCardStatus(
    modifier: Modifier = Modifier,
    item: PurchaseItem,
    stateUiData: StatusUiData
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraSmall,
            color = stateUiData.containerColor,
            border = BorderStroke(1.dp, stateUiData.contentColor)
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                text = stateUiData.label,
                color = stateUiData.contentColor
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        PurchaseCardStatusExtra(
            item = item,
            stateUiData = stateUiData
        )
    }
}

@Composable
private fun PurchaseCardStatusExtra(
    item: PurchaseItem,
    stateUiData: StatusUiData
) {
    when (val status = item.status) {
        is PurchaseStatus.Planned -> {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = stateUiData.amountColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(
                        R.string.daily_limit,
                        "${status.dailyLimit.toDisplayStringFloored()} ${status.currency.name}"
                    ),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = stateUiData.amountColor
                )
            }
        }

        is PurchaseStatus.Unreachable -> {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = stateUiData.amountColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${status.gap.toDisplayStringFloored()} ${status.currency.name}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = stateUiData.amountColor
                )
            }
        }

        else -> Unit
    }
}

@Composable
private fun PurchaseStatus.toUiData(): StatusUiData {
    val extendedColors = LocalExtendedColors.current

    return when (this) {
        PurchaseStatus.Available -> StatusUiData(
            icon = Icons.Default.Wallet,
            contentColor = extendedColors.availablePurchaseContent,
            containerColor = extendedColors.availablePurchaseContainer,
            label = stringResource(R.string.purchase_available),
            amountColor = extendedColors.positiveAmount
        )

        PurchaseStatus.Bought -> StatusUiData(
            icon = Icons.Outlined.CheckCircle,
            contentColor = extendedColors.boughtPurchaseContent,
            containerColor = extendedColors.boughtPurchaseContainer,
            label = stringResource(R.string.purchase_bought),
            amountColor = MaterialTheme.colorScheme.onSurface
        )

        PurchaseStatus.Overdue -> StatusUiData(
            icon = Icons.Default.Schedule,
            contentColor = extendedColors.overduePurchaseContent,
            containerColor = extendedColors.overduePurchaseContainer,
            label = stringResource(R.string.purchase_overdue),
            amountColor = extendedColors.negativeAmount
        )

        is PurchaseStatus.Planned -> StatusUiData(
            icon = Icons.Default.CalendarMonth,
            contentColor = extendedColors.plannedPurchaseContent,
            containerColor = extendedColors.plannedPurchaseContainer,
            label = stringResource(R.string.purchase_planned),
            amountColor = MaterialTheme.colorScheme.onSurface
        )

        is PurchaseStatus.Unreachable -> StatusUiData(
            icon = Icons.Outlined.Lock,
            contentColor = extendedColors.unreachablePurchaseContent,
            containerColor = extendedColors.unreachablePurchaseContainer,
            label = stringResource(R.string.purchase_unreachable),
            amountColor = extendedColors.negativeAmount
        )
    }
}

private data class StatusUiData(
    val icon: ImageVector,
    val contentColor: Color,
    val containerColor: Color,
    val label: String,
    val amountColor: Color
)

@Preview(showBackground = true)
@Composable
private fun PurchaseCardPreview() {
    PurchaseCard(
        item = PurchaseItem(
            uid = "1",
            amount = "13 213 RSD",
            description = "оперативная память",
            user = "Сергей",
            status = PurchaseStatus.Planned(
                BigDecimal("100135.12"),
                currency = Currency.RSD
            ),
            date = "01.01.2025"
        ),
        onClick = { },
        onSwipe = { }
    )
}