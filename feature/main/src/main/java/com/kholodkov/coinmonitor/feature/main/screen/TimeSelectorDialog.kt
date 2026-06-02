package com.kholodkov.coinmonitor.feature.main.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kholodkov.coinmonitor.feature.main.R
import java.time.LocalTime
import com.kholodkov.coinmonitor.core.ui.R as CoreR

private val timeFieldWidth = 100.dp
private val timeFieldHeight = 70.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSelectorDialog(
    time: String,
    onDismiss: () -> Unit,
    onConfirm: (LocalTime) -> Unit
) {

    val (hours, minutes) = time.split(":")
    var hour by remember { mutableStateOf(hours) }
    var minute by remember { mutableStateOf(minutes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val h = hour.toIntOrNull() ?: 0
                val m = minute.toIntOrNull() ?: 0

                onConfirm(LocalTime.of(h, m))
            }) {
                Text(stringResource(CoreR.string.common_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(CoreR.string.common_cancel))
            }
        },
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.enter_time),
                textAlign = TextAlign.Center,
            )
        },
        text = {
            Clock(
                hour = hour,
                minute = minute,
                onHourChange = { hour = it },
                onMinuteChange = { minute = it }
            )
        }
    )
}

@Composable
private fun Clock(
    hour: String,
    minute: String,
    onHourChange: (String) -> Unit,
    onMinuteChange: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val hourFocus = remember { FocusRequester() }
    val minuteFocus = remember { FocusRequester() }

    var moveFocusToMinutes by remember { mutableStateOf(false) }

    LaunchedEffect(moveFocusToMinutes) {
        if (moveFocusToMinutes) {
            minuteFocus.requestFocus()
            moveFocusToMinutes = false
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TimeField(
            value = hour,
            onValueChange = { newValue ->
                val intValue = newValue.toIntOrNull()
                if (intValue == null) {
                    onHourChange("")
                    return@TimeField
                }

                if (intValue > 23) return@TimeField

                val normalized = if (newValue.length > 2) {
                    newValue.removePrefix("0")
                } else
                    newValue

                onHourChange(normalized)

                if (normalized.length >= 2) {
                    moveFocusToMinutes = true
                }
            },
            keyboardActions = KeyboardActions(
                onNext = { minuteFocus.requestFocus() }
            ),
            focusRequester = hourFocus,
            onBackspaceEmpty = {},
            onFocusLost = { current ->
                if (current.isEmpty()) onHourChange("00")
                if (current.length == 1) onHourChange("0${current}")
            }
        )
        Text(
            modifier = Modifier.padding(8.dp),
            text = ":",
            style = MaterialTheme.typography.displayMedium
        )
        TimeField(
            value = minute,
            onValueChange = { newValue ->
                val intValue = newValue.toIntOrNull()
                if (intValue == null) {
                    onMinuteChange("")
                    return@TimeField
                }

                if (intValue > 59) return@TimeField
                val normalized = if (newValue.length > 2) {
                    newValue.removePrefix("0")
                } else
                    newValue
                onMinuteChange(normalized)
            },
            keyboardActions = KeyboardActions(
                onNext = { focusManager.clearFocus() }
            ),
            focusRequester = minuteFocus,
            onBackspaceEmpty = { hourFocus.requestFocus() },
            onFocusLost = { current ->
                if (current.isEmpty()) onMinuteChange("00")
                if (current.length == 1) onMinuteChange("0${current}")
            }
        )
    }
}

@Composable
private fun TimeField(
    value: String,
    onValueChange: (String) -> Unit,
    keyboardActions: KeyboardActions,
    focusRequester: FocusRequester,
    onBackspaceEmpty: () -> Unit,
    onFocusLost: (String) -> Unit
) {

    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val borderColor = if (isFocused) MaterialTheme.colorScheme.primary else Color.Transparent
    val backgroundColor =
        if (isFocused) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant

    BasicTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        singleLine = true,
        textStyle = MaterialTheme.typography.displayMedium.copy(
            textAlign = TextAlign.Center
        ),
        interactionSource = interactionSource,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        keyboardActions = keyboardActions,
        modifier = Modifier
            .width(timeFieldWidth)
            .height(timeFieldHeight)
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                if (!focusState.isFocused) {
                    onFocusLost(value)
                }
            }
            .onKeyEvent {
                if (it.key == Key.Backspace && value.isEmpty()) {
                    onBackspaceEmpty()
                    true
                } else {
                    false
                }
            },
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = backgroundColor,
                        shape = MaterialTheme.shapes.small
                    )
                    .border(
                        width = 1.dp,
                        color = borderColor,
                        shape = MaterialTheme.shapes.small
                    ),
                contentAlignment = Alignment.Center
            ) {
                innerTextField()
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun TimeSelectorDialogPreview() {
    TimeSelectorDialog(
        time = "11:23",
        onDismiss = {},
        onConfirm = {}
    )
}