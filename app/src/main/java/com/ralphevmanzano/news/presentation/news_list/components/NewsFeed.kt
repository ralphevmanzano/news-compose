package com.ralphevmanzano.news.presentation.news_list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ralphevmanzano.news.presentation.model.NewsUi
import com.ralphevmanzano.news.presentation.news_list.COLUMN_COUNT
import com.ralphevmanzano.news.presentation.news_list.NewsListScreenState
import com.ralphevmanzano.news.ui.theme.NewsTheme

@Composable
fun NewsFeed(
    modifier: Modifier = Modifier,
    state: NewsListScreenState,
    lazyGridState: LazyGridState,
    onNewsClicked: (NewsUi) -> Unit,
    onBookmarkClicked: (String) -> Unit,
) {
    LazyVerticalGrid(
        modifier = modifier,
        state = lazyGridState,
        columns = GridCells.Fixed(COLUMN_COUNT),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = state.news,
            key = { it.id },
        ) { news ->
            NewsListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                newsUi = news,
                onItemClicked = { onNewsClicked(news) },
                onBookmarkClicked = { onBookmarkClicked(it) }
            )
        }
        if (state.isLoadingMore) {
            item(span = { GridItemSpan(COLUMN_COUNT) }) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(
                            Alignment.Center
                        )
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun NewsFeedPreview() {
    NewsTheme {
        NewsFeed(
            modifier = Modifier.fillMaxSize(),
            state = NewsListScreenState.FeaturedListState(
                news = listOf(
                    NewsUi(
                        id = "1",
                        title = "A Sample Title",
                        sourceTitle = "BBC News",
                        description = "A quick brown fox jumped over",
                        formattedPublishedDate = "March 22, 2025",
                        imageUrl = null,
                        articleLink = "",
                        sourceIcon = ""
                    ),
                    NewsUi(
                        id = "2",
                        title = "A Sample Title",
                        sourceTitle = "BBC News",
                        description = "A quick brown fox jumped over",
                        formattedPublishedDate = "March 22, 2025",
                        imageUrl = null,
                        isBookmarked = true,
                        isFeatured = true,
                        articleLink = "",
                        sourceIcon = ""
                    ),
                )
            ),
            lazyGridState = LazyGridState(),
            onNewsClicked = {},
            onBookmarkClicked = {}
        )
    }
}