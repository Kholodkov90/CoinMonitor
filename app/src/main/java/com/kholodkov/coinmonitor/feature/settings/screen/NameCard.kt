package com.kholodkov.coinmonitor.feature.settings.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowForwardIos
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun NameCard(
    name: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Icon(
                modifier = Modifier.size(35.dp),
                imageVector = Icons.Default.Person,
                contentDescription = null
            )
            Column(modifier = Modifier.weight(1f)) {
                Text("Имя")
                Text(name)
            }
            Icon(
                imageVector = Icons.AutoMirrored.Sharp.ArrowForwardIos,
                contentDescription = null
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NameCardPreview() {
    NameCard(
        name = "Сергей",
        onClick = {}
    )
}