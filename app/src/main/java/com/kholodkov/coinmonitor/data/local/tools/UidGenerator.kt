package com.kholodkov.coinmonitor.data.local.tools

import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

interface UidGenerator {
    fun generate(): String

    class Base @Inject constructor(
        private val firestore: FirebaseFirestore
    ) : UidGenerator {
        override fun generate() = firestore.collection("gen_uid").document().id
    }
}