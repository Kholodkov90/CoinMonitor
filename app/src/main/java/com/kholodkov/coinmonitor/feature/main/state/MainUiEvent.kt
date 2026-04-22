package com.kholodkov.coinmonitor.feature.main.state

import com.kholodkov.coinmonitor.domain.model.RestoreTransactionParams

sealed interface MainUiEvent {
    data class ShowRestoreTransactionSnackbar(val params: RestoreTransactionParams) : MainUiEvent
}