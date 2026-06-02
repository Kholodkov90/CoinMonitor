package com.kholodkov.coinmonitor.data.di

import com.kholodkov.coinmonitor.data.BuildConfig
import com.kholodkov.coinmonitor.data.remote.exchange.ExchangeApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(logging)
        }

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideExchangeApi(client: OkHttpClient): ExchangeApi =
        Retrofit.Builder()
            .baseUrl(BuildConfig.EXCHANGE_API_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ExchangeApi::class.java)
}