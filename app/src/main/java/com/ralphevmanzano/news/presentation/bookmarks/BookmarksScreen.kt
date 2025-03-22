package com.ralphevmanzano.news.presentation.bookmarks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ralphevmanzano.news.presentation.model.NewsUi
import com.ralphevmanzano.news.presentation.news_list.COLUMN_COUNT
import com.ralphevmanzano.news.presentation.news_list.components.NewsListItem
import com.ralphevmanzano.news.presentation.news_list.previewNewsList
import com.ralphevmanzano.news.ui.theme.NewsTheme
import com.ralphevmanzano.news.ui.theme.Typography
import org.koin.androidx.compose.koinViewModel

@Composable
fun BookmarksScreen(
    modifier: Modifier = Modifier,
    viewModel: BookmarksViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToDetails: (NewsUi) -> Unit,
) {
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()

    Box(modifier = modifier) {
        BookmarksContent(
            modifier = modifier,
            bookmarks = bookmarks,
            onBookmarkClicked = { viewModel.toggleBookmark(it) },
            onNavigateBack = onNavigateBack,
            onNavigateToDetails = onNavigateToDetails
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksContent(
    modifier: Modifier = Modifier,
    bookmarks: List<NewsUi>,
    onBookmarkClicked: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToDetails: (NewsUi) -> Unit
) {
    val lazyGridState = rememberLazyGridState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Bookmarks",
                    style = Typography.headlineSmall,
                )
            },
            navigationIcon = {
                IconButton(onClick = { onNavigateBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            windowInsets = WindowInsets(top = 0, bottom = 0)
        )

        LazyVerticalGrid(
            modifier = modifier,
            state = lazyGridState,
            columns = GridCells.Fixed(COLUMN_COUNT),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = bookmarks,
                key = { it.id },
            ) { news ->
                NewsListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    newsUi = news,
                    onItemClicked = { onNavigateToDetails(news) },
                    onBookmarkClicked = { onBookmarkClicked(it) }
                )
            }
        }
    }
}

@Preview
@Composable
private fun BookmarksContentPreview() {
    NewsTheme {
        Surface {
            BookmarksContent(
                modifier = Modifier.fillMaxSize(),
                bookmarks = previewNewsList,
                onBookmarkClicked = {},
                onNavigateBack = {},
                onNavigateToDetails = {}
            )
        }
    }
}