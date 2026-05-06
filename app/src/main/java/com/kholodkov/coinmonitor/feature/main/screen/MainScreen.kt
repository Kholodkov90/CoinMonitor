package com.kholodkov.coinmonitor.feature.main.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kholodkov.coinmonitor.R
import com.kholodkov.coinmonitor.feature.main.MainViewModel
import com.kholodkov.coinmonitor.feature.main.model.ui.BudgetState
import com.kholodkov.coinmonitor.feature.main.model.ui.DayState
import com.kholodkov.coinmonitor.feature.main.model.ui.TransactionItem
import com.kholodkov.coinmonitor.feature.main.state.MainUiEvent
import com.kholodkov.coinmonitor.feature.main.state.MainUiIntent
import com.kholodkov.coinmonitor.feature.main.state.MainUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun MainScreenRoute(
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    MainScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onIntent = { viewModel.onIntent(it) }
    )

    val expenseDeletedMessage = stringResource(R.string.expense_deleted)
    val undoMessage = stringResource(R.string.undo)

    LaunchedEffect(Unit) {
        var snackbarJob: Job? = null
        viewModel.events.collect { event ->
            when (event) {
                is MainUiEvent.ShowRestoreTransactionSnackbar -> {
                    snackbarJob?.cancel()
                    snackbarJob = launch {
                        val result = snackbarHostState.showSnackbar(
                            message = expenseDeletedMessage,
                            actionLabel = undoMessage,
                            duration = SnackbarDuration.Short
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            viewModel.onIntent(MainUiIntent.Item.Restore(event.params))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MainScreen(
    uiState: MainUiState,
    snackbarHostState: SnackbarHostState,
    onIntent: (MainUiIntent) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onIntent(MainUiIntent.Item.AddNew)
            }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_expense)
                )
            }
        },
        contentWindowInsets = WindowInsets()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            DateHeader(
                modifier = Modifier.padding(8.dp),
                dayState = uiState.dayState,
                onIntent = onIntent
            )
            HorizontalDivider()
            BudgetCard(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .padding(top = 8.dp),
                budgetState = uiState.budgetState,
            )
            TransactionsList(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                transactionItems = uiState.transactions,
                onIntent = onIntent
            )
        }

        uiState.transactionState?.let {
            TransactionSheet(
                transactionState = it,
                onIntent = onIntent
            )
        }
    }
}

@Composable
private fun TransactionsList(
    modifier: Modifier,
    transactionItems: List<TransactionItem>,
    onIntent: (MainUiIntent.Item) -> Unit
) {
    val saveableStateHolder = rememberSaveableStateHolder()

    if (transactionItems.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.no_expenses_yet),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp)
    ) {
        items(
            items = transactionItems,
            key = { it.uid }
        ) { item ->
            DisposableEffect(Unit) {
                onDispose { saveableStateHolder.removeState(item.uid) }
            }

            saveableStateHolder.SaveableStateProvider(key = item.uid) {
                TransactionCard(
                    item = item,
                    onClick = { uid -> onIntent(MainUiIntent.Item.Edit(uid)) },
                    onSwipe = { uid -> onIntent(MainUiIntent.Item.Delete(uid)) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainScreenPreview() {
    MainScreen(
        uiState = MainUiState(
            dayState = DayState(
                date = "01.01.2026",
                dateDescriptionRes = null,
                isDatePickerVisible = false
            ),
            budgetState = BudgetState(
                balance = "27500 RSD",
                isBalancePositive = true,
                spent = "2500 RSD",
                remaining = "25000 RSD",
                isRemainingPositive = true,
            ),
            transactions = listOf(
                TransactionItem(
                    uid = "1",
                    amount = "400 RSD",
                    time = "09:21",
                    user = "Сережа"
                ),
                TransactionItem(
                    uid = "2",
                    amount = "287 RSD",
                    time = "11:57",
                    user = "Настя"
                ),
            )

        ),
        snackbarHostState = remember { SnackbarHostState() }
    ) { }
}
