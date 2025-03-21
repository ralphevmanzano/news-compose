package com.ralphevmanzano.news.presentation.featured_headlines

import androidx.compose.runtime.Immutable
import com.ralphevmanzano.news.presentation.model.NewsUi

@Immutable
data class FeaturedHeadlinesState(
    val isLoading: Boolean = false,
    val featuredHeadlines: List<NewsUi> = emptyList(),
    val isFetchingNext: Boolean = false,
    val isEndReached: Boolean = false
)