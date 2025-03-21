package com.ralphevmanzano.news.presentation.featured_headlines

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ralphevmanzano.news.ui.theme.NewsTheme
import com.ralphevmanzano.news.utils.ObserveAsEvents
import com.ralphevmanzano.news.utils.toString
import org.koin.androidx.compose.koinViewModel

@Composable
fun FeaturedHeadlinesScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    viewModel: FeaturedHeadlinesViewModel = koinViewModel(),
    onNavigateToDetails: (String) -> Unit,
) {
    val context = LocalContext.current

    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(events = viewModel.errorEvents) {
        snackbarHostState.showSnackbar(
            message = it.toString(context),
            duration = SnackbarDuration.Short
        )
    }

    Box(modifier = modifier) {
        FeaturedHeadlinesContent(
            modifier = modifier,
            state = state,
            onBookmarkClicked = { viewModel.toggleBookmark(it) },
            onNavigateToDetails = onNavigateToDetails,
            onFetchNewPage = { viewModel.getFeaturedHeadlines() }
        )
    }
}

const val COLUMN_COUNT = 2

@Composable
fun FeaturedHeadlinesContent(
    modifier: Modifier = Modifier,
    state: FeaturedHeadlinesState,
    onBookmarkClicked: (String) -> Unit,
    onNavigateToDetails: (String) -> Unit,
    onFetchNewPage: () -> Unit,
) {
    val lazyState = rememberLazyGridState()

    val shouldFetchMore by remember {
        derivedStateOf {
            val currentLastVisible = lazyState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val currentItemCount = lazyState.layoutInfo.totalItemsCount

            currentItemCount > 0 &&
                    currentLastVisible >= currentItemCount - 2 &&
                    !state.isFetchingNext &&
                    !state.isEndReached
        }
    }

    LaunchedEffect(shouldFetchMore) {
        if (shouldFetchMore) {
            onFetchNewPage()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (state.isLoading && state.featuredHeadlines.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        if (state.featuredHeadlines.isNotEmpty()) {
            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                state = lazyState,
                columns = GridCells.Fixed(COLUMN_COUNT),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    items = state.featuredHeadlines,
                    key = { it.id },
                ) { news ->
                    NewsListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                        newsUi = news,
                        onItemClicked = { onNavigateToDetails(it) },
                        onBookmarkClicked = { onBookmarkClicked(it) }
                    )
                }
                if (state.isFetchingNext) {
                    item(span = { GridItemSpan(COLUMN_COUNT) }) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    }
                }
            }
        }
    }
}