package com.kholodkov.coinmonitor.feature.main.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxDefaults
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kholodkov.coinmonitor.feature.main.model.TransactionItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun TransactionCard(
    item: TransactionItem,
    onClick: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val dismissState = rememberSwipeToDismissBoxState(
        positionalThreshold = SwipeToDismissBoxDefaults.positionalThreshold,
    )
    SwipeToDismissBox(
        state = dismissState,
        onDismiss = {
            scope.launch {
                onDelete(item.uid)
                //FIXME: After transaction restore, SwipeToDismissBox still has EndToStart state
                // and immediately removes again. delay + snapTo(Settled) used as a workaround,
                // but only partially fixes the issue
                delay(20)
                dismissState.snapTo(SwipeToDismissBoxValue.Settled)
            }
        },
        backgroundContent = { },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = {
                    onClick(item.uid)
                })
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = item.amount
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = item.time,
                    textAlign = TextAlign.Center
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = item.user,
                    textAlign = TextAlign.End
                )
            }
            Spacer(Modifier.height(4.dp))
            HorizontalDivider(
                thickness = 1.dp,
                color = Color.DarkGray
            )
        }
    }
}