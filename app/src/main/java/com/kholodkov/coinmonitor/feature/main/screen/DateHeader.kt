package com.kholodkov.coinmonitor.feature.main.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowBackIos
import androidx.compose.material.icons.automirrored.sharp.ArrowForwardIos
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier


@Composable
fun DateHeader(
    date: String,
    dateDescription: String,
    onCalendarClick: () -> Unit,
    onPreviousDayClick: () -> Unit,
    onNextDayClick: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousDayClick) {
            Icon(Icons.AutoMirrored.Sharp.ArrowBackIos, null)
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(date)
            Text(dateDescription)
        }

        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = onNextDayClick) {
            Icon(Icons.AutoMirrored.Sharp.ArrowForwardIos, null)
        }

        IconButton(onClick = onCalendarClick) {
            Icon(Icons.Filled.CalendarMonth, null)
        }
    }
}
