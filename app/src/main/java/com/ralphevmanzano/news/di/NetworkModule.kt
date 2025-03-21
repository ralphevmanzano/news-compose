package com.ralphevmanzano.news.di

import android.content.Context
import android.telephony.TelephonyManager
import com.ralphevmanzano.news.BuildConfig
import com.ralphevmanzano.news.data.remote.NewsApiInterceptor
import com.ralphevmanzano.news.data.remote.NewsService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.Locale
import java.util.concurrent.TimeUnit

fun provideJson(): Json {
    return Json {
        coerceInputValues = true
        ignoreUnknownKeys = true
    }
}

fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
        .apply {
            if (BuildConfig.DEBUG) {
                this.addNetworkInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                )
            }
            readTimeout(60, TimeUnit.SECONDS)
            connectTimeout(60, TimeUnit.SECONDS)
            addNetworkInterceptor(NewsApiInterceptor())
        }.build()
}

fun provideNewsRetrofit(): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://newsdata.io/api/1/")
        .client(provideOkHttpClient())
        .addConverterFactory(provideJson().asConverterFactory("application/json".toMediaType()))
        .build()
}

fun provideNewsService(retrofit: Retrofit): NewsService {
    return retrofit.create(NewsService::class.java)
}

val networkModule = module {
    single { provideJson() }
    single { provideNewsRetrofit() }
    single { provideOkHttpClient() }
    single { provideNewsService(get()) }
}