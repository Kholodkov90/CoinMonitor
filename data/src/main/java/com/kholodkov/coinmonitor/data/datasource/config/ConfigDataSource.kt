package com.kholodkov.coinmonitor.data.datasource.config

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.kholodkov.coinmonitor.domain.model.config.AppConfig
import com.kholodkov.coinmonitor.domain.model.currency.Currency
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

class ConfigDataSource @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig,
) {

    suspend fun fetch() {
        remoteConfig.fetchAndActivate().await()
    }

    fun observeAppConfig(): Flow<AppConfig> = callbackFlow {
        trySend(getAppConfig())

        val listener = remoteConfig.addOnConfigUpdateListener(
            object : ConfigUpdateListener {
                override fun onUpdate(configUpdate: ConfigUpdate) {
                    remoteConfig.activate().addOnCompleteListener {
                        trySend(getAppConfig())
                    }
                }

                override fun onError(error: FirebaseRemoteConfigException) {
                    FirebaseCrashlytics.getInstance().recordException(error)
                }
            }
        )

        awaitClose {
            listener.remove()
        }
    }

    private fun getAppConfig(): AppConfig = AppConfig(
        dailyLimit = remoteConfig
            .getString(DAILY_LIMIT_AMOUNT)
            .toBigDecimalOrNull() ?: BigDecimal.ZERO,
        dailyLimitCurrency = remoteConfig
            .getString(DAILY_LIMIT_CURRENCY)
            .toCurrencyOrNull() ?: Currency.RSD,
        startDate = remoteConfig
            .getString(START_DATE)
            .toLocalDateOrNull() ?: LocalDate.now(),
        initialBalance = remoteConfig
            .getString(INITIAL_BALANCE_AMOUNT)
            .toBigDecimalOrNull() ?: BigDecimal.ZERO
    )

    private fun String.toCurrencyOrNull(): Currency? = try {
        Currency.valueOf(this)
    } catch (e: IllegalArgumentException) {
        FirebaseCrashlytics.getInstance().recordException(e)
        null
    }

    private fun String.toLocalDateOrNull(): LocalDate? = try {
        LocalDate.parse(this)
    } catch (e: Exception) {
        FirebaseCrashlytics.getInstance().recordException(e)
        null
    }

    companion object {
        private const val DAILY_LIMIT_AMOUNT = "daily_limit_amount"
        private const val DAILY_LIMIT_CURRENCY = "daily_limit_currency"
        private const val START_DATE = "start_date"
        private const val INITIAL_BALANCE_AMOUNT = "initial_balance_amount"
    }
}