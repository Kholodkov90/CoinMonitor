package com.kholodkov.coinmonitor.core.common

fun String.isValidAmountInput(): Boolean {
    val cleaned = this.replace(',', '.')
    return Regex("^\\d*(\\.\\d{0,2})?$").matches(cleaned)
}