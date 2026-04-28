package com.kholodkov.coinmonitor.domain.tools

import com.kholodkov.coinmonitor.domain.model.currency.Currency
import com.kholodkov.coinmonitor.domain.model.currency.ExchangeRate
import com.kholodkov.coinmonitor.domain.model.transaction.Transaction

fun List<Transaction>.calculateSpentByDate(
    exchangeRates: List<ExchangeRate>,
    currency: Currency
) = groupBy { it.date }
    .mapValues { (date, dayTransactions) ->
        dayTransactions
            .groupBy { it.currency }
            .map { (transactionCurrency, transactions) ->
                transactions.sumOf { it.amount }
                    .convertTo(
                        from = transactionCurrency,
                        to = currency,
                        rate = exchangeRates.rateFor(date)
                    )
            }
            .sumOf { it }
    }


