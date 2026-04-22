package com.kholodkov.coinmonitor.data.model.purchase

sealed class RemotePurchaseChange {
    data class Upsert(val purchase: RemotePurchase) : RemotePurchaseChange()
    data class Delete(val uid: String) : RemotePurchaseChange()
}

