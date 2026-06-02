package com.kholodkov.coinmonitor.feature.main.state

import com.kholodkov.coinmonitor.domain.model.transaction.RestoreTransactionParams

sealed interface MainUiEvent {
    data class ShowRestoreTransactionSnackbar(val params: RestoreTransactionParams) : MainUiEvent
}