package com.ralphevmanzano.news

import android.app.Application
import com.ralphevmanzano.news.di.appModule
import com.ralphevmanzano.news.di.databaseModule
import com.ralphevmanzano.news.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class NewsApp: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@NewsApp)
            androidLogger()

            modules(appModule, networkModule, databaseModule)
        }
    }
}