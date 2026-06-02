package com.kholodkov.coinmonitor.feature.main.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kholodkov.coinmonitor.feature.main.R
import com.kholodkov.coinmonitor.core.ui.dialogs.DateSelectorDialog
import com.kholodkov.coinmonitor.feature.main.model.ui.DayState
import com.kholodkov.coinmonitor.feature.main.state.MainUiIntent


@Composable
fun DateHeader(
    modifier: Modifier = Modifier,
    dayState: DayState,
    onIntent: (MainUiIntent.DayNavigation) -> Unit
) {

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilledTonalIconButton(
            onClick = { onIntent(MainUiIntent.DayNavigation.PreviousDay) }
        ) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = stringResource(R.string.previous_day)
            )
        }
        Surface(
            onClick = { onIntent(MainUiIntent.DayNavigation.OpenDatePicker) },
            modifier = Modifier.weight(1f),
            color = Color.Transparent,
            shape = MaterialTheme.shapes.medium,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = dayState.date,
                    style = MaterialTheme.typography.titleLarge
                )
                dayState.dateDescriptionRes?.let { res ->
                    Text(
                        text = stringResource(res),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        FilledTonalIconButton(onClick = { onIntent(MainUiIntent.DayNavigation.NextDay) }) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = stringResource(R.string.next_day)
            )
        }
    }

    if (dayState.isDatePickerVisible) {
        DateSelectorDialog(
            onDateSelected = { date -> onIntent(MainUiIntent.DayNavigation.SelectDate(date)) },
            onDismiss = { onIntent(MainUiIntent.DayNavigation.DismissDatePicker) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DateHeaderPreview() {

    DateHeader(
        dayState = DayState(
            date = "01.01.2026",
            dateDescriptionRes = null,
            isDatePickerVisible = false
        ),
        onIntent = {}
    )
}