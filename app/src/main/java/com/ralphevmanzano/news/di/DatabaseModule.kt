package com.ralphevmanzano.news.di

import android.app.Application
import androidx.room.Room
import com.ralphevmanzano.news.data.local.NewsDatabase
import org.koin.dsl.module

fun provideNewsDatabase(application: Application): NewsDatabase {
    return Room.databaseBuilder(application, NewsDatabase::class.java, "table_news")
        .fallbackToDestructiveMigration()
        .build()
}

fun provideNewsDao(newsDatabase: NewsDatabase) = newsDatabase.newsDao()

val databaseModule = module {
    single { provideNewsDatabase(get()) }
    single { provideNewsDao(get()) }
}