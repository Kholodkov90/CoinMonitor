package com.kholodkov.coinmonitor.feature.settings.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
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
import com.kholodkov.coinmonitor.R
import com.kholodkov.coinmonitor.core.ui.components.CircleIcon
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

private val weekStartOptions = listOf(DayOfWeek.MONDAY, DayOfWeek.SUNDAY)

@Composable
fun StartOfWeekCard(
    selectedDay: DayOfWeek,
    onDaySelected: (DayOfWeek) -> Unit
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
                icon = Icons.Default.CalendarMonth,
                backgroundColor = MaterialTheme.colorScheme.surfaceContainer
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.start_of_week),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.select_day),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Column(modifier = Modifier.selectableGroup()) {
            weekStartOptions.forEach { day ->
                RadioOptionRow(
                    name = day.getDisplayName(TextStyle.FULL, Locale.getDefault()),
                    isSelected = day == selectedDay,
                    onClick = { onDaySelected(day) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StartOfWeekCardPreview() {
    StartOfWeekCard(
        selectedDay = DayOfWeek.MONDAY,
        onDaySelected = {}
    )
}