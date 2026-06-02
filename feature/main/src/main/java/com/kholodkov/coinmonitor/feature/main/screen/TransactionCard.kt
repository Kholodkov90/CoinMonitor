package com.kholodkov.coinmonitor.feature.main.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kholodkov.coinmonitor.core.ui.components.CircleIcon
import com.kholodkov.coinmonitor.feature.main.model.ui.TransactionItem

@Composable
fun TransactionCard(
    item: TransactionItem,
    onClick: (String) -> Unit,
    onSwipe: (String) -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState()

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
                    icon = Icons.Outlined.Payments,
                    backgroundColor = MaterialTheme.colorScheme.surfaceContainer
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    modifier = Modifier.weight(1f),
                    text = item.amount,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = item.time,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = item.user,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TransactionCardPreview() {
    TransactionCard(
        item = TransactionItem(
            uid = "1",
            amount = "400 RSD",
            time = "09:21",
            user = "Сережа"
        ),
        onClick = {},
        onSwipe = {}
    )
}