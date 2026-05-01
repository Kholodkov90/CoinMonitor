package com.kholodkov.coinmonitor.feature.settings.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EuroSymbol
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek

private val weekStartOptions = listOf(DayOfWeek.MONDAY, DayOfWeek.SUNDAY)

@Composable
fun StartOfWeekCard(
    selectedDay: DayOfWeek,
    onDaySelected: (DayOfWeek) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Icon(
                modifier = Modifier.size(35.dp),
                imageVector = Icons.Default.EuroSymbol,
                contentDescription = null
            )
            Column(modifier = Modifier.weight(1f)) {
                Text("Начало недели")
                Text("Выберите день")
            }
        }
        Column(modifier = Modifier.selectableGroup()) {
            weekStartOptions.forEach { day ->
                RadioOptionRow(
                    name = if (day == DayOfWeek.MONDAY) "Понедельник" else "Воскресенье",
                    isSelected = day == selectedDay,
                    onClick = { onDaySelected(day) },
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun StratOfWeekCardPreview() {
    StartOfWeekCard(
        selectedDay = DayOfWeek.MONDAY,
        onDaySelected = {}
    )
}