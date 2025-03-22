package com.ralphevmanzano.news.presentation.model

import kotlinx.serialization.Serializable

sealed interface Screen {

    @Serializable
    data object FeaturedHeadlines : Screen

    @Serializable
    data object NewsDetails : Screen

    @Serializable
    data object BookmarksScreen : Screen
}

const val ARGS_NEWS = "news"