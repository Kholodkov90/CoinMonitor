package com.kholodkov.coinmonitor.feature.purchase.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import com.kholodkov.coinmonitor.feature.purchase.R
import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.model.purchase.PurchaseStatus
import com.kholodkov.coinmonitor.feature.purchase.PurchaseViewModel
import com.kholodkov.coinmonitor.feature.purchase.model.ui.PurchaseItem
import com.kholodkov.coinmonitor.feature.purchase.state.PurchaseUiEvent
import com.kholodkov.coinmonitor.feature.purchase.state.PurchaseUiIntent
import com.kholodkov.coinmonitor.feature.purchase.state.PurchaseUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.math.BigDecimal
import com.kholodkov.coinmonitor.core.ui.R as CoreR

@Composable
fun PurchaseScreenRoute(
    viewModel: PurchaseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    PurchaseScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onIntent = { viewModel.onIntent(it) }
    )

    val purchaseDeletedMessage = stringResource(R.string.purchase_deleted)
    val undoMessage = stringResource(CoreR.string.common_undo)

    LaunchedEffect(Unit) {
        var snackbarJob: Job? = null
        viewModel.events.collect { event ->
            when (event) {
                is PurchaseUiEvent.ShowRestorePurchaseSnackbar -> {
                    snackbarJob?.cancel()
                    snackbarJob = launch {
                        val result = snackbarHostState.showSnackbar(
                            message = purchaseDeletedMessage,
                            actionLabel = undoMessage,
                            duration = SnackbarDuration.Short
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            viewModel.onIntent(PurchaseUiIntent.Item.Restore(event.params))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PurchaseScreen(
    uiState: PurchaseUiState,
    snackbarHostState: SnackbarHostState,
    onIntent: (PurchaseUiIntent) -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onIntent(PurchaseUiIntent.Item.AddNew) }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_purchase)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            PurchaseSummaryCard(plannedAmount = uiState.plannedAmount)
            PurchaseList(
                modifier = Modifier.fillMaxSize(),
                purchaseItems = uiState.purchases,
                onIntent = onIntent
            )
        }
    }

    uiState.purchaseState?.let {
        PurchaseSheet(
            purchaseState = uiState.purchaseState,
            onIntent = onIntent
        )
    }
}

@Composable
private fun PurchaseList(
    modifier: Modifier = Modifier,
    purchaseItems: List<PurchaseItem>,
    onIntent: (PurchaseUiIntent.Item) -> Unit
) {
    val saveableStateHolder = rememberSaveableStateHolder()
    if (purchaseItems.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.no_planned_purchases_yet),
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
            items = purchaseItems,
            key = { it.uid }
        ) { item ->
            DisposableEffect(Unit) {
                onDispose { saveableStateHolder.removeState(item.uid) }
            }

            saveableStateHolder.SaveableStateProvider(key = item.uid) {
                PurchaseCard(
                    item = item,
                    onClick = { uid -> onIntent(PurchaseUiIntent.Item.Edit(uid)) },
                    onSwipe = { uid -> onIntent(PurchaseUiIntent.Item.Delete(uid)) }
                )
            }
        }
    }
}


@Preview
@Composable
private fun PurchaseScreenPreview() {
    PurchaseScreen(
        uiState = PurchaseUiState(
            plannedAmount = "27000 RSD",
            purchases = listOf(
                PurchaseItem(
                    uid = "1",
                    amount = "27000 RSD",
                    description = "Оперативная память",
                    user = "Сергей",
                    status = PurchaseStatus.Planned(
                        dailyLimit = BigDecimal("1000"),
                        currency = Currency.RSD
                    ),
                    date = "30.04.2026"
                ),
                PurchaseItem(
                    uid = "2",
                    amount = "27000 RSD",
                    description = "Оперативная память",
                    user = "Сергей",
                    status = PurchaseStatus.Unreachable(
                        gap = BigDecimal("1000"),
                        currency = Currency.RSD
                    ),
                    date = "30.04.2026"
                ),
                PurchaseItem(
                    uid = "3",
                    amount = "27000 RSD",
                    description = "Оперативная память",
                    user = "Сергей",
                    status = PurchaseStatus.Overdue,
                    date = "30.04.2026"
                ),
                PurchaseItem(
                    uid = "4",
                    amount = "27000 RSD",
                    description = "Оперативная память",
                    user = "Сергей",
                    status = PurchaseStatus.Bought,
                    date = "30.04.2026"
                ),
                PurchaseItem(
                    uid = "5",
                    amount = "27000 RSD",
                    description = "Оперативная память",
                    user = "Сергей",
                    status = PurchaseStatus.Available,
                    date = "30.04.2026"
                )
            ),
            purchaseState = null
        ),
        snackbarHostState = SnackbarHostState()
    ) { }
}