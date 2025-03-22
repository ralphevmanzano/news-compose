package com.ralphevmanzano.news.domain.model

data class News(
    val id: String,
    val title: String,
    val sourceTitle: String,
    val sourceIcon: String,
    val description: String,
    val pubDate: String,
    val articleLink: String,
    val imageUrl: String,
)
