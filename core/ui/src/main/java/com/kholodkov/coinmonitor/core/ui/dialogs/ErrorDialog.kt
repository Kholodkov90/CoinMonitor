package com.kholodkov.coinmonitor.core.ui.dialogs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.kholodkov.coinmonitor.core.ui.R

@Composable
fun ErrorDialog(
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.common_error_title),
                color = MaterialTheme.colorScheme.error
            )
        },
        text = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = message
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.common_ok))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun ErrorDialogPreview() {
    ErrorDialog(
        message = "Не удалось войти. Попробуйте ещё раз",
        onDismiss = {}
    )
}