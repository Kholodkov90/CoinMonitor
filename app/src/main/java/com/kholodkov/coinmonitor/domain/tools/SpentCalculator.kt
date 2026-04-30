package com.kholodkov.coinmonitor.domain.tools

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.model.currency.ExchangeRates
import com.kholodkov.coinmonitor.domain.model.transaction.Transaction

fun List<Transaction>.calculateSpentByDate(
    exchangeRates: ExchangeRates,
    currency: Currency
) = groupBy { it.date }
    .mapValues { (date, dayTransactions) ->
        dayTransactions
            .groupBy { it.currency }
            .map { (transactionCurrency, transactions) ->
                val totalAmount = transactions.sumOf { it.amount }
                exchangeRates.convert(
                    amount = totalAmount,
                    from = transactionCurrency,
                    to = currency,
                    date = date
                )
            }
            .sumOf { it }
    }
