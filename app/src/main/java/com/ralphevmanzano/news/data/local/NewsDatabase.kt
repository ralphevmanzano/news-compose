package com.ralphevmanzano.news.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ralphevmanzano.news.data.local.entity.NewsEntity

@Database(entities = [NewsEntity::class], version = 1, exportSchema = false)
abstract class NewsDatabase: RoomDatabase() {
    abstract fun newsDao(): NewsDao
}