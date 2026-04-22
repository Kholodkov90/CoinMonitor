package com.kholodkov.coinmonitor.core.tools

import java.math.BigDecimal

fun String.parseToBigDecimal() = this.replace(",", ".")
    .trim()
    .toBigDecimalOrNull()

fun BigDecimal.toDisplayString(): String {
    return if (stripTrailingZeros().scale() <= 0) {
        toPlainString().substringBefore(".")
    } else {
        "%.2f".format(this)
    }
}