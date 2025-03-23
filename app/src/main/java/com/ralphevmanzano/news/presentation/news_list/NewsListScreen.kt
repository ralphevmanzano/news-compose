package com.ralphevmanzano.news.presentation.news_list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ralphevmanzano.news.R
import com.ralphevmanzano.news.presentation.model.NewsUi
import com.ralphevmanzano.news.presentation.news_list.NewsListScreenState.FeaturedListState
import com.ralphevmanzano.news.presentation.news_list.NewsListScreenState.SearchListState
import com.ralphevmanzano.news.presentation.news_list.components.NewsFeed
import com.ralphevmanzano.news.ui.theme.NewsTheme
import com.ralphevmanzano.news.ui.theme.Typography
import com.ralphevmanzano.news.utils.ObserveAsEvents
import com.ralphevmanzano.news.utils.toString
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.androidx.compose.koinViewModel

private const val DEBOUNCE_TIME = 300L
private const val MINIMUM_SEARCH_CHAR_LENGTH = 3
const val COLUMN_COUNT = 2

@OptIn(FlowPreview::class)
@Composable
fun NewsListScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    viewModel: NewsListViewModel = koinViewModel(),
    onNavigateToDetails: (NewsUi) -> Unit,
    onNavigateToBookmarks: () -> Unit,
) {
    val context = LocalContext.current

    val featuredHeadlinesState by viewModel.featuredHeadlinesState.collectAsStateWithLifecycle()
    val searchNewsState by viewModel.searchedNewsState.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()

    ObserveAsEvents(events = viewModel.errorEvents) {
        snackbarHostState.showSnackbar(
            message = it.toString(context),
            duration = SnackbarDuration.Short
        )
    }

    LaunchedEffect(Unit) {
        snapshotFlow { query }
            .debounce(DEBOUNCE_TIME)
            .distinctUntilChanged()
            .collectLatest { q ->
                if (q.isNotBlank() && q.length >= MINIMUM_SEARCH_CHAR_LENGTH) {
                    viewModel.searchNews()
                } else if (q.isBlank()) {
                    viewModel.clearSearchResults()
                }
            }
    }

    Box(modifier = modifier) {
        NewsListContent(
            modifier = modifier,
            featuredHeadlinesState = featuredHeadlinesState,
            searchNewsState = searchNewsState,
            onBookmarkClicked = { id, isSearchedList ->
                viewModel.toggleBookmark(id, isSearchedList)
            },
            onNavigateToDetails = onNavigateToDetails,
            onFeaturedHeadlinesLoadMore = { viewModel.getFeaturedHeadlines() },
            onSearchedLoadMore = { viewModel.searchNews() },
            onQueryChanged = { viewModel.onSearchChanged(it) },
            onSearch = { viewModel.searchNews() },
            onNavigateToBookmarks = { onNavigateToBookmarks() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsListContent(
    modifier: Modifier = Modifier,
    featuredHeadlinesState: FeaturedListState,
    searchNewsState: SearchListState,
    onBookmarkClicked: (String, Boolean) -> Unit,
    onFeaturedHeadlinesLoadMore: () -> Unit,
    onSearchedLoadMore: () -> Unit,
    onQueryChanged: (String) -> Unit,
    onSearch: () -> Unit,
    onNavigateToDetails: (NewsUi) -> Unit,
    onNavigateToBookmarks: () -> Unit
) {
    val featuredListLazyState = rememberLazyGridState()
    val searchedListLazyState = rememberLazyGridState()

    val featuredShouldFetchMore by remember {
        derivedStateOf {
            shouldFetchMoreItems(featuredListLazyState, featuredHeadlinesState)
        }
    }

    val searchShouldFetchMore by remember {
        derivedStateOf {
            shouldFetchMoreItems(searchedListLazyState, searchNewsState)
        }
    }

    var expanded by remember { mutableStateOf(false) }
    var appBarHeightDp by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    val horizontalPadding by animateDpAsState(
        targetValue = if (expanded) 0.dp else 16.dp,
        label = "search_bar_padding"
    )

    LaunchedEffect(featuredShouldFetchMore) {
        if (featuredShouldFetchMore) {
            onFeaturedHeadlinesLoadMore()
        }
    }

    LaunchedEffect(searchShouldFetchMore) {
        if (searchShouldFetchMore) {
            onSearchedLoadMore()
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        AnimatedVisibility(!expanded) {
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned {
                        val appBarHeight = it.size.height
                        appBarHeightDp = with(density) { appBarHeight.toDp() }
                    },
                title = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Featured Headlines",
                        style = Typography.headlineMedium,
                    )
                },
                windowInsets = WindowInsets(top = 0, bottom = 0)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = horizontalPadding,
                    end = horizontalPadding,
                    bottom = 16.dp,
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SearchBar(
                modifier = Modifier.weight(1f),
                expanded = expanded,
                onExpandedChange = {
                    expanded = it
                },
                inputField = {
                    SearchBarDefaults.InputField(
                        onSearch = { onSearch() },
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        placeholder = {
                            Text(stringResource(R.string.search_for_news))
                        },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (expanded) {
                                IconButton(
                                    onClick = {
                                        expanded = false
                                        onQueryChanged("")
                                    },
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = null)
                                }
                            }
                        },
                        query = searchNewsState.query,
                        onQueryChange = {
                            onQueryChanged(it)
                        }
                    )
                },
                windowInsets = WindowInsets(top = if (expanded) appBarHeightDp else 0.dp)
            ) {
                if (searchNewsState.isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                if (searchNewsState.news.isNotEmpty()) {
                    NewsFeed(
                        modifier = Modifier.weight(1f),
                        state = searchNewsState,
                        lazyGridState = searchedListLazyState,
                        onNewsClicked = { onNavigateToDetails(it) },
                        onBookmarkClicked = { id ->
                            onBookmarkClicked(id, true)
                        }
                    )
                }

                if (searchNewsState.hasNoResults) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp)
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = stringResource(R.string.no_results),
                            style = Typography.titleSmall
                        )
                    }
                }
            }

            AnimatedVisibility(!expanded) {
                IconButton(onClick = { onNavigateToBookmarks() }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_collection_bookmarks),
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            }
        }
        if (featuredHeadlinesState.isLoading && featuredHeadlinesState.news.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }

        if (featuredHeadlinesState.news.isNotEmpty()) {
            // Featured Headlines
            NewsFeed(
                modifier = Modifier.weight(1f),
                state = featuredHeadlinesState,
                lazyGridState = featuredListLazyState,
                onNewsClicked = { onNavigateToDetails(it) },
                onBookmarkClicked = { id ->
                    onBookmarkClicked(id, false)
                }
            )
        }
    }
}

private fun shouldFetchMoreItems(lazyState: LazyGridState, state: NewsListScreenState): Boolean {
    val currentLastVisible = lazyState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
    val currentItemCount = lazyState.layoutInfo.totalItemsCount

    return currentItemCount > 0 && currentLastVisible >= currentItemCount - 2
            && !state.isLoadingMore && !state.isEndReached
}

@Preview
@Composable
private fun NewsListContentPreview() {
    NewsTheme {
        NewsListContent(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White),
            featuredHeadlinesState = previewFeatureListState,
            searchNewsState = SearchListState(),
            onBookmarkClicked = { _, _ -> },
            onFeaturedHeadlinesLoadMore = { },
            onSearchedLoadMore = { },
            onQueryChanged = { },
            onSearch = { },
            onNavigateToDetails = { },
            onNavigateToBookmarks = { }
        )
    }
}

@Preview
@Composable
private fun NewsListContentLoadingPreview() {
    NewsTheme {
        NewsListContent(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White),
            featuredHeadlinesState = previewFeatureListLoadingState,
            searchNewsState = SearchListState(),
            onBookmarkClicked = { _, _ -> },
            onFeaturedHeadlinesLoadMore = { },
            onSearchedLoadMore = { },
            onQueryChanged = { },
            onSearch = { },
            onNavigateToDetails = { },
            onNavigateToBookmarks = {}
        )
    }
}