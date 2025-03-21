package com.ralphevmanzano.news.presentation.model

data class NewsUi(
    val id: String,
    val title: String,
    val sourceTitle: String,
    val description: String? = null,
    val formattedPublishedDate: String,
    val articleLink: String,
    val imageUrl: String? = null,
    var isBookmarked: Boolean = false,
    var isFeatured: Boolean = false
)
