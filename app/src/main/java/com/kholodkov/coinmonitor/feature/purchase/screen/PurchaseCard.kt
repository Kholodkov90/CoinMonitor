package com.kholodkov.coinmonitor.feature.purchase.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxDefaults
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kholodkov.coinmonitor.core.tools.toDisplayString
import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.model.purchase.PurchaseStatus
import com.kholodkov.coinmonitor.feature.purchase.model.PurchaseItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal

@Composable
fun PurchaseCard(
    item: PurchaseItem,
    onClick: (String) -> Unit,
    onSwipe: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val dismissState = rememberSwipeToDismissBoxState(
        positionalThreshold = SwipeToDismissBoxDefaults.positionalThreshold,
    )
    SwipeToDismissBox(
        state = dismissState,
        onDismiss = {
            scope.launch {
                onSwipe(item.uid)
                //FIXME: After purchase restore, SwipeToDismissBox still has EndToStart state
                // and immediately removes again. delay + snapTo(Settled) used as a workaround,
                // but only partially fixes the issue
                delay(20)
                dismissState.snapTo(SwipeToDismissBoxValue.Settled)
            }
        },
        backgroundContent = { },
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onClick(item.uid) }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
            ) {
                Icon(
                    modifier = Modifier
                        .size(35.dp)
                        .align(Alignment.CenterVertically),
                    imageVector = Icons.Default.Preview,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(4.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.amount)
                    Text(item.description)
                    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                        Text(item.date)
                        Spacer(modifier = Modifier.width(4.dp))
                        VerticalDivider(
                            thickness = 1.dp,
                            color = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(item.user)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color.Transparent,
                        border = BorderStroke(1.dp, Color.DarkGray)
                    ) {
                        val statusText = when (item.status) {
                            PurchaseStatus.Completed -> "Куплено"
                            PurchaseStatus.Overdue -> "Просрочено"
                            is PurchaseStatus.Pending -> "Активно"
                            is PurchaseStatus.Unreachable -> "Невозможно"
                            is PurchaseStatus.Available -> "Доступно"
                        }
                        Text(modifier = Modifier.padding(horizontal = 4.dp), text = statusText)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    when (val status = item.status) {
                        is PurchaseStatus.Pending -> {
                            Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${status.dailyLimit.toDisplayString()} ${status.currency.name}/день")
                            }
                        }

                        is PurchaseStatus.Unreachable -> {
                            Text("-${status.gap.toDisplayString()} ${status.currency.name}")
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PurchaseCardPreview() {
    PurchaseCard(
        item = PurchaseItem(
            uid = "1",
            amount = "13213 rds",
            description = "оперативкая память",
            user = "Сергей",
            status = PurchaseStatus.Unreachable(
                BigDecimal("1001"),
                currency = Currency.RSD
            ),
            date = "01.01.2025"
        ),
        onClick = { },
        onSwipe = { }
    )
}