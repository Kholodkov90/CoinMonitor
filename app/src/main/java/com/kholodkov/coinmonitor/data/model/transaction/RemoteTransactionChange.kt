package com.kholodkov.coinmonitor.data.model.transaction

sealed class RemoteTransactionChange {
    data class Upsert(val transaction: RemoteTransaction) : RemoteTransactionChange()
    data class Delete(val uid: String) : RemoteTransactionChange()
}