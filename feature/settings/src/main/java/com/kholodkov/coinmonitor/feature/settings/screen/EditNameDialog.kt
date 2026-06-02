package com.kholodkov.coinmonitor.feature.settings.screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.kholodkov.coinmonitor.feature.settings.R
import com.kholodkov.coinmonitor.core.ui.fields.TextInputField
import com.kholodkov.coinmonitor.core.ui.R as CoreR

@Composable
fun EditNameDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by rememberSaveable { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.edit_name)) },
        text = {
            TextInputField(
                modifier = Modifier.fillMaxWidth(),
                value = name,
                labelText = stringResource(R.string.name),
                placeholderText = stringResource(R.string.enter_name),
                onValueChange = { name = it }
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name) },
                enabled = name.isNotBlank()
            ) {
                Text(stringResource(CoreR.string.common_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(CoreR.string.common_cancel))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun EditNameDialogPreview() {
    EditNameDialog(
        currentName = "Сергей",
        onDismiss = {},
        onConfirm = {}
    )
}