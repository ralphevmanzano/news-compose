package com.ralphevmanzano.news.data.remote

import android.util.Log
import com.ralphevmanzano.news.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.util.Locale

/**
 * Interceptor class for providing api_key parameter and language parameter to NewsService
 */
class NewsApiInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val originalUrl = original.url

        val newUrl = originalUrl.newBuilder()
            .addQueryParameter("apiKey", BuildConfig.NEWS_API_KEY)
            .addQueryParameter("language", "en")
            .build()

        val newRequest = original.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }
}