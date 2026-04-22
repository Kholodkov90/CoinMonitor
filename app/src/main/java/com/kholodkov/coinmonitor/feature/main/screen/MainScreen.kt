package com.kholodkov.coinmonitor.feature.main.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kholodkov.coinmonitor.feature.main.MainViewModel
import com.kholodkov.coinmonitor.feature.main.model.TransactionItem
import com.kholodkov.coinmonitor.feature.main.state.MainUiEvent
import com.kholodkov.coinmonitor.feature.main.state.MainUiIntent
import com.kholodkov.coinmonitor.feature.main.state.MainUiState

@Composable
fun MainScreenRoute(
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    MainScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        sendIntent = { viewModel.onIntent(it) }
    )

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is MainUiEvent.ShowRestoreTransactionSnackbar -> {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    val result = snackbarHostState.showSnackbar(
                        message = "Транзакция удалена",
                        actionLabel = "Отмена",
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onIntent(MainUiIntent.RestoreTransaction(event.params))
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    uiState: MainUiState,
    snackbarHostState: SnackbarHostState,
    sendIntent: (MainUiIntent) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                sendIntent(MainUiIntent.AddNewTransaction)
            }) {
                Icon(Icons.Filled.Add, contentDescription = "")
            }
        },
        contentWindowInsets = WindowInsets()
    ) { innerPadding ->
        if (showDatePicker) {
            DateSelector(
                onDateSelected = { date -> sendIntent(MainUiIntent.SelectDate(date)) },
                onDismiss = { showDatePicker = false }
            )
        }

        if (uiState.isTransactionSheetVisible) {
            TransactionSheet(
                uid = uiState.editTransactionUid,
                amount = uiState.inputAmount,
                currency = uiState.inputCurrency,
                time = uiState.inputTime,
                isTimeSelectorOpened = uiState.isTimeSelectorOpened,
                isSaveAvailable = uiState.isSaveAvailable,
                onDismiss = { sendIntent(MainUiIntent.HideTransactionSheet) },
                onEditAmount = { amount -> sendIntent(MainUiIntent.EditAmount(amount)) },
                onEditCurrency = { currency -> sendIntent(MainUiIntent.EditCurrency(currency)) },
                onEditTime = { sendIntent(MainUiIntent.EditTime) },
                onSaveTransaction = { uid -> sendIntent(MainUiIntent.SaveTransaction(uid)) },
                onDismissTimeSelector = { sendIntent(MainUiIntent.DismissTimeSelector) },
                onSetTime = { time -> sendIntent(MainUiIntent.SetTime(time)) }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 8.dp)
        ) {
            DateHeader(
                date = uiState.date,
                dateDescription = uiState.dateDescription,
                onCalendarClick = { showDatePicker = true },
                onPreviousDayClick = { sendIntent(MainUiIntent.PreviousDay) },
                onNextDayClick = { sendIntent(MainUiIntent.NextDay) }
            )
            Spacer(modifier = Modifier.height(8.dp))
            InfoRow("Баланс", uiState.balance)
            Spacer(modifier = Modifier.height(8.dp))
            InfoRow("Потрачено", uiState.spent)
            Spacer(modifier = Modifier.height(8.dp))
            InfoRow("Остаток", uiState.remaining)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Операции")
            TransactionsList(
                transactionItems = uiState.transactions,
                onClick = { uid -> sendIntent(MainUiIntent.EditTransaction(uid)) },
                onDelete = { uid -> sendIntent(MainUiIntent.DeleteTransaction(uid)) }
            )
        }
    }

}

@Composable
private fun TransactionsList(
    transactionItems: List<TransactionItem>,
    onClick: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            items = transactionItems,
            key = { it.uid }
        ) { item ->
            TransactionCard(
                item = item,
                onClick = onClick,
                onDelete = onDelete
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen(
        uiState = MainUiState(
            date = "07.04.2026",
            dateDescription = "Сегодня",
            balance = "30231 RSD",
            spent = "1293 RSD",
            remaining = "28940 RSD",
            transactions = listOf(
                TransactionItem(
                    uid = "1",
                    amount = "-400 RSD",
                    time = "09:21",
                    user = "Сережа"
                ),
                TransactionItem(
                    uid = "2",
                    amount = "-287 RSD",
                    time = "11:57",
                    user = "Настя"
                ),
                TransactionItem(
                    uid = "3",
                    amount = "-412 EUR",
                    time = "14:30",
                    user = "Сережа"
                ),
                TransactionItem(
                    uid = "4",
                    amount = "-194 EUR",
                    time = "16:02",
                    user = "Сережа"
                ),
            )
        ),
        snackbarHostState = remember { SnackbarHostState() }
    ) { }
}
