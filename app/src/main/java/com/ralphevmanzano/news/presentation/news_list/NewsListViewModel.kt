package com.ralphevmanzano.news.presentation.news_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ralphevmanzano.news.domain.model.PagedList
import com.ralphevmanzano.news.domain.model.networking.NetworkError
import com.ralphevmanzano.news.domain.model.networking.onError
import com.ralphevmanzano.news.domain.model.networking.onSuccess
import com.ralphevmanzano.news.domain.usecase.BookmarkNewsUseCase
import com.ralphevmanzano.news.domain.usecase.GetAllBookmarkedNewsUseCase
import com.ralphevmanzano.news.domain.usecase.GetFeaturedHeadlinesUseCase
import com.ralphevmanzano.news.domain.usecase.SearchNewsUseCase
import com.ralphevmanzano.news.domain.usecase.UnBookmarkNewsUseCase
import com.ralphevmanzano.news.presentation.model.NewsUi
import com.ralphevmanzano.news.presentation.news_list.NewsListScreenState.FeaturedListState
import com.ralphevmanzano.news.presentation.news_list.NewsListScreenState.SearchListState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NewsListViewModel(
    private val getFeaturedHeadlinesUseCase: GetFeaturedHeadlinesUseCase,
    private val searchNewsUseCase: SearchNewsUseCase,
    private val getAllBookmarkedNewsUseCase: GetAllBookmarkedNewsUseCase,
    private val bookmarkNewsUseCase: BookmarkNewsUseCase,
    private val unBookmarkNewsUseCase: UnBookmarkNewsUseCase,
) : ViewModel() {
    private var featuredNextPage: String? = null
    private var searchNextPage: String? = null
    private var didInit = false

    private val _errorEvents = MutableSharedFlow<NetworkError>()
    val errorEvents = _errorEvents.asSharedFlow()

    private val bookmarkedNews = mutableListOf<NewsUi>()

    private val _featuredHeadlinesState = MutableStateFlow(FeaturedListState())
    val featuredHeadlinesState = _featuredHeadlinesState.onStart {
        performInitialLoad()
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        FeaturedListState()
    )

    private var prevQuery = ""
    val query = MutableStateFlow("")

    private val _searchedState = MutableStateFlow(SearchListState())
    val searchedNewsState: StateFlow<SearchListState> = _searchedState.asStateFlow()

    private fun performInitialLoad() {
        if (didInit) return

        didInit = true

        // Load bookmarks first, then headlines
        viewModelScope.launch {
            val bookmarksJob = launch { getAllBookmarkedNews() }
            bookmarksJob.join()
            getFeaturedHeadlines(true)
        }
    }

    fun getFeaturedHeadlines(isInit: Boolean = false) {
        val state = _featuredHeadlinesState.value

        // Checks needed to prevent multiple calls
        if (state.isLoading || state.isLoadingMore || state.isEndReached) {
            return
        }

        if (isInit) {
            featuredNextPage = null
        }

        val existingNews = state.news

        viewModelScope.launch {
            _featuredHeadlinesState.update {
                it.copy(
                    isLoading = isInit,
                    isLoadingMore = !isInit && existingNews.isNotEmpty()
                )
            }

            getFeaturedHeadlinesUseCase(featuredNextPage)
                .onSuccess { pagedList ->
                    featuredNextPage = pagedList.nextPage

                    val updatedList = processPagedList(pagedList, state, !isInit)

                    _featuredHeadlinesState.update {
                        it.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            news = updatedList,
                            isEndReached = featuredNextPage == null
                        )
                    }
                }.onError { error ->
                    _featuredHeadlinesState.update {
                        it.copy(isLoading = false, isLoadingMore = false)
                    }
                    _errorEvents.emit(error)
                }
        }
    }

    fun onSearchChanged(q: String) {
        query.value = q
        _searchedState.update {
            it.copy(query = q)
        }
    }

    fun searchNews() {
        val state = _searchedState.value
        val didQueryChanged = query.value != prevQuery

        // Checks needed to prevent multiple calls
        if (state.isLoading || state.isLoadingMore || (!didQueryChanged && state.isEndReached)) {
            return
        }

        val existingSearchedList = state.news

        _searchedState.update {
            it.copy(
                isLoading = didQueryChanged,
                isLoadingMore = !didQueryChanged && existingSearchedList.isNotEmpty()
            )
        }

        if (didQueryChanged) {
            searchNextPage = null
        }

        viewModelScope.launch {
            searchNewsUseCase(query.value, searchNextPage)
                .onSuccess { pagedList ->
                    searchNextPage = pagedList.nextPage

                    val updatedList = processPagedList(pagedList, state, !didQueryChanged)

                    _searchedState.update {
                        it.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            news = updatedList,
                            isEndReached = searchNextPage == null,
                            hasNoResults = updatedList.isEmpty()
                        )
                    }

                    prevQuery = query.value
                }.onError { error ->
                    _searchedState.update {
                        it.copy(isLoading = false, isLoadingMore = false)
                    }
                    _errorEvents.emit(error)
                }
        }
    }

    private fun processPagedList(
        pagedList: PagedList<NewsUi>,
        state: NewsListScreenState,
        isPaginating: Boolean
    ): List<NewsUi> {
        // Set bookmark state
        var currentPageList = pagedList.data.map {
            it.copy(
                isBookmarked = bookmarkedNews.any { bNews ->
                    bNews.id == it.id
                }
            )
        }

        // Set featured state on search results ONLY
        if (state is SearchListState) {
            currentPageList = currentPageList.map {
                it.copy(
                    isFeatured = featuredHeadlinesState.value.news.any { fNews ->
                        fNews.id == it.id
                    }
                )
            }
        }

        // Append if paginating
        return if (!isPaginating) {
            currentPageList.distinctBy { it.id }
        } else {
            (state.news + currentPageList).distinctBy { it.id }
        }
    }

    fun clearSearchResults() {
        searchNextPage = null
        _searchedState.update {
            it.copy(
                news = emptyList(),
                isLoading = false,
                isLoadingMore = false
            )
        }
    }

    private fun getAllBookmarkedNews() {
        viewModelScope.launch {
            getAllBookmarkedNewsUseCase().collect { news ->
                bookmarkedNews.clear()
                bookmarkedNews.addAll(news)
                _featuredHeadlinesState.update {
                    it.copy(news = updateNewsBookmarkState(it))
                }
                _searchedState.update {
                    it.copy(news = updateNewsBookmarkState(it))
                }
            }
        }
    }

    private fun updateNewsBookmarkState(state: NewsListScreenState): List<NewsUi> {
        val bookmarkedIds = bookmarkedNews.map { it.id }.toSet()

        return state.news.map { news ->
            news.copy(isBookmarked = bookmarkedIds.contains(news.id))
        }.toMutableList()
    }

    fun toggleBookmark(id: String, isSearchedList: Boolean = false) {
        val list = if (isSearchedList) _searchedState.value.news
        else _featuredHeadlinesState.value.news

        val index = list.indexOfFirst { it.id == id }
        if (index != -1) {
            val news = list[index]

            updateBookmarkState(index, !news.isBookmarked, isSearchedList)

            if (news.isBookmarked) {
                unBookMarkNews(id)
            } else {
                bookMarkNews(news)
            }
        }
    }

    private fun updateBookmarkState(index: Int, isBookmarked: Boolean, isSearchedList: Boolean) {
        val updatedList = if (isSearchedList) _searchedState.value.news.toMutableList()
        else _featuredHeadlinesState.value.news.toMutableList()

        updatedList[index] = updatedList[index].copy(isBookmarked = isBookmarked)

        if (isSearchedList) {
            _searchedState.update {
                it.copy(news = updatedList)
            }
        } else {
            _featuredHeadlinesState.update {
                it.copy(news = updatedList)
            }
        }
    }

    private fun bookMarkNews(news: NewsUi) {
        viewModelScope.launch {
            bookmarkNewsUseCase(news)
        }
    }

    private fun unBookMarkNews(id: String) {
        viewModelScope.launch {
            unBookmarkNewsUseCase(id)
        }
    }
}