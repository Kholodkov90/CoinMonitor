package com.kholodkov.coinmonitor.feature.main.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kholodkov.coinmonitor.core.ui.theme.Purple40
import com.kholodkov.coinmonitor.core.ui.theme.Purple80
import java.time.LocalTime

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
        onDismissRequest = { },
        confirmButton = {
            TextButton(onClick = {
                val h = hour.toIntOrNull() ?: 0
                val m = minute.toIntOrNull() ?: 0

                onConfirm(LocalTime.of(h, m))
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        },
        text = {
            Column(Modifier.fillMaxWidth()) {
                Text("Введите время")
                Clock(
                    hour = hour,
                    minute = minute,
                    onHourChange = { hour = it },
                    onMinuteChange = { minute = it }
                )
            }
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

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TimerField(
            value = hour,
            onValueChange = { newValue ->
                val intValue = newValue.toIntOrNull()
                if (intValue == null) {
                    onHourChange("")
                    return@TimerField
                }

                if (intValue > 23) return@TimerField

                val normalized = if (newValue.length > 2) {
                    newValue.removePrefix("0")
                } else
                    newValue

                onHourChange(normalized)

                if (normalized.length >= 2) {
                    minuteFocus.requestFocus()
                }
            },
            keyboardActions = KeyboardActions(
                onNext = { minuteFocus.requestFocus() }
            ),
            focusRequester = hourFocus,
            onBackspaceEmpty = {},
            onFocusLost = {
                if (hour.isEmpty()) onHourChange("00")
                if (hour.length == 1) onHourChange("0${hour}")
            }
        )
        Text(
            modifier = Modifier.padding(8.dp),
            text = ":",
            style = textStyle
        )
        TimerField(
            value = minute,
            onValueChange = { newValue ->
                val intValue = newValue.toIntOrNull()
                if (intValue == null) {
                    onMinuteChange("")
                    return@TimerField
                }

                if (intValue > 59) return@TimerField
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
            onFocusLost = {
                if (minute.isEmpty()) onMinuteChange("00")
                if (minute.length == 1) onMinuteChange("0${minute}")
            }
        )

    }
}

@Composable
private fun TimerField(
    value: String,
    onValueChange: (String) -> Unit,
    keyboardActions: KeyboardActions,
    focusRequester: FocusRequester,
    onBackspaceEmpty: () -> Unit,
    onFocusLost: () -> Unit
) {

    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val borderColor = if (isFocused) Purple40 else Color.LightGray
    val backgroundColor = if (isFocused) Purple80 else Color.Transparent

    BasicTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        singleLine = true,
        textStyle = textStyle,
        interactionSource = interactionSource,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        keyboardActions = keyboardActions,
        modifier = Modifier
            .width(100.dp)
            .height(70.dp)
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                if (!focusState.isFocused) {
                    onFocusLost()
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
                    .background(backgroundColor, RoundedCornerShape(8.dp))
                    .border(2.dp, borderColor, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                innerTextField()
            }
        },
    )
}

private val textStyle: TextStyle
    get() = TextStyle(
        fontSize = 42.sp,
        textAlign = TextAlign.Center
    )

@Preview(showBackground = true)
@Composable
fun TimeSelectorDialogPreview() {
    TimeSelectorDialog(
        "11:23",
        {}
    ) { }
}