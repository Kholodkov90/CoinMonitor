package com.kholodkov.coinmonitor.domain.scheduler

interface LoadExchangeRateScheduler {
    fun loadRates()
}