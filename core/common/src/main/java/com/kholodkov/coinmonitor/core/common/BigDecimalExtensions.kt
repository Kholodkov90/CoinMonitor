package com.kholodkov.coinmonitor.core.common

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat

fun String.parseToBigDecimal() = this.replace(",", ".")
    .trim()
    .toBigDecimalOrNull()

fun BigDecimal.toDisplayString(): String {
    val format = NumberFormat.getNumberInstance()
    return if (stripTrailingZeros().scale() <= 0) {
        format.maximumFractionDigits = 0
        format.format(this)
    } else {
        format.minimumFractionDigits = 2
        format.maximumFractionDigits = 2
        format.format(this)
    }
}

fun BigDecimal.toDisplayStringFloored(): String {
    val floored = setScale(0, RoundingMode.FLOOR)
    val format = NumberFormat.getNumberInstance()
    format.maximumFractionDigits = 0
    return format.format(floored)
}

fun BigDecimal.toInputString(): String {
    return if (stripTrailingZeros().scale() <= 0) {
        toBigInteger().toString()
    } else {
        toPlainString()
    }
}