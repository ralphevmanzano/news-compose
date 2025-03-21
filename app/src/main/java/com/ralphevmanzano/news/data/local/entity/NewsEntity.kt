package com.ralphevmanzano.news.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news")
data class NewsEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val sourceTitle: String,
    val description: String? = null,
    val formattedPublishedDate: String,
    val articleLink: String,
    val imageUrl: String? = null,
)