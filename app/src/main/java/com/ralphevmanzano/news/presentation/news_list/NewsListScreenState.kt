package com.ralphevmanzano.news.presentation.news_list

import androidx.compose.runtime.Immutable
import com.ralphevmanzano.news.presentation.model.NewsUi

sealed class NewsListScreenState {
    abstract val news: List<NewsUi>
    abstract val isLoading: Boolean
    abstract val isLoadingMore: Boolean
    abstract val isEndReached: Boolean

    @Immutable
    data class FeaturedListState(
        override val news: List<NewsUi> = emptyList(),
        override val isLoading: Boolean = false,
        override val isLoadingMore: Boolean = false,
        override val isEndReached: Boolean = false,
    ) : NewsListScreenState()

    @Immutable
    data class SearchListState(
        override val news: List<NewsUi> = emptyList(),
        override val isLoading: Boolean = false,
        override val isLoadingMore: Boolean = false,
        override val isEndReached: Boolean = false,
        val query: String = "",
        val hasNoResults: Boolean = false,
    ) : NewsListScreenState()
}

val previewNewsList = listOf(
    NewsUi(
        id = "1",
        title = "Sample News Title 1",
        description = "This is a sample description for the news article 1.",
        imageUrl = "https://example.com/sample-image1.jpg",
        formattedPublishedDate = "March 22, 2025",
        isBookmarked = false,
        articleLink = "",
        sourceTitle = "BBC News",
        sourceIcon = ""
    ),
    NewsUi(
        id = "2",
        title = "Sample News Title 2",
        description = "This is a sample description for the news article 2.",
        imageUrl = "https://example.com/sample-image2.jpg",
        formattedPublishedDate = "March 22, 2025",
        isBookmarked = true,
        articleLink = "",
        sourceTitle = "BBC News",
        sourceIcon = ""
    ),
    NewsUi(
        id = "3",
        title = "Sample News Title 3",
        description = "This is a sample description for the news article 3.",
        imageUrl = "https://example.com/sample-image3.jpg",
        formattedPublishedDate = "March 22, 2025",
        isBookmarked = false,
        articleLink = "",
        sourceTitle = "Washington Post",
        sourceIcon = ""
    ),
    NewsUi(
        id = "4",
        title = "Sample News Title 4",
        description = "This is a sample description for the news article 4.",
        imageUrl = "https://example.com/sample-image4.jpg",
        formattedPublishedDate = "March 22, 2025",
        isBookmarked = true,
        isFeatured = true,
        articleLink = "",
        sourceTitle = "ABS-CBN",
        sourceIcon = ""
    ),
    NewsUi(
        id = "5",
        title = "Sample News Title 5",
        description = "This is a sample description for the news article 5.",
        imageUrl = "https://example.com/sample-image5.jpg",
        formattedPublishedDate = "March 22, 2025",
        isBookmarked = false,
        isFeatured = true,
        articleLink = "",
        sourceTitle = "Rappler",
        sourceIcon = ""
    ),
    NewsUi(
        id = "6",
        title = "Sample News Title 6",
        description = "This is a sample description for the news article 6.",
        imageUrl = "https://example.com/sample-image6.jpg",
        formattedPublishedDate = "March 22, 2025",
        isBookmarked = true,
        articleLink = "",
        sourceTitle = "GMA News",
        sourceIcon = ""
    ),
    NewsUi(
        id = "7",
        title = "Sample News Title 7",
        description = "This is a sample description for the news article 7.",
        imageUrl = "https://example.com/sample-image7.jpg",
        formattedPublishedDate = "March 22, 2025",
        isBookmarked = false,
        articleLink = "",
        sourceTitle = "Rappler",
        sourceIcon = ""
    ),
    NewsUi(
        id = "8",
        title = "Sample News Title 8",
        description = "This is a sample description for the news article 8.",
        imageUrl = "https://example.com/sample-image8.jpg",
        formattedPublishedDate = "March 22, 2025",
        isBookmarked = true,
        articleLink = "",
        sourceTitle = "Rappler",
        sourceIcon = ""
    )
)

val previewFeatureListLoadingState = NewsListScreenState.FeaturedListState(
    news = emptyList(),
    isLoading = true,
    isLoadingMore = false,
    isEndReached = false,
)

val previewFeatureListState = NewsListScreenState.FeaturedListState(
    news = previewNewsList,
    isLoading = true,
    isLoadingMore = false,
    isEndReached = false,
)