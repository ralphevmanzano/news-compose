package com.ralphevmanzano.news.presentation.model

import kotlinx.serialization.Serializable

sealed interface Screen {

    @Serializable
    data object FeaturedHeadlines : Screen

    @Serializable
    data class NewsDetails(val newsId: String) : Screen
}