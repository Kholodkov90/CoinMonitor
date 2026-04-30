package com.kholodkov.coinmonitor.feature.statistic.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kholodkov.coinmonitor.feature.statistic.model.StatisticItem

@Composable
fun StatisticCard(item: StatisticItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                modifier = Modifier.size(40.dp),
                imageVector = Icons.Default.DateRange,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                modifier = Modifier.weight(1f),
                text = item.period,
            )

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text("Потрачено")
                Text(item.totalSpent)
            }
        }
        HorizontalDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text("Количество операций")
                Text(item.transactionCount.toString())
            }

            VerticalDivider()

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text("Средний расход в день")
                Text(item.average)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StatisticCardPreview() {
    StatisticCard(
        StatisticItem(
            period = "2026",
            totalSpent = "1500000 RSD",
            transactionCount = 75,
            average = "3000 RDS"
        )
    )
}