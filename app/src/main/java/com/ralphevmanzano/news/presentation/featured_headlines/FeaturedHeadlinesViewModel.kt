package com.ralphevmanzano.news.presentation.featured_headlines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ralphevmanzano.news.domain.model.News
import com.ralphevmanzano.news.domain.model.networking.NetworkError
import com.ralphevmanzano.news.domain.model.networking.onError
import com.ralphevmanzano.news.domain.model.networking.onSuccess
import com.ralphevmanzano.news.domain.usecase.BookmarkNewsUseCase
import com.ralphevmanzano.news.domain.usecase.GetAllBookmarkedNewsUseCase
import com.ralphevmanzano.news.domain.usecase.GetFeaturedHeadlinesUseCase
import com.ralphevmanzano.news.domain.usecase.UnBookmarkNewsUseCase
import com.ralphevmanzano.news.presentation.model.NewsUi
import com.ralphevmanzano.news.utils.OnetimeWhileSubscribed
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeaturedHeadlinesViewModel(
    private val getFeaturedHeadlinesUseCase: GetFeaturedHeadlinesUseCase,
    private val getAllBookmarkedNewsUseCase: GetAllBookmarkedNewsUseCase,
    private val bookmarkNewsUseCase: BookmarkNewsUseCase,
    private val unBookmarkNewsUseCase: UnBookmarkNewsUseCase
) : ViewModel() {
    private var nextCursor: String? = null

    private val _errorEvents = MutableSharedFlow<NetworkError>()
    val errorEvents = _errorEvents.asSharedFlow()

    private val sharingStarted = OnetimeWhileSubscribed(5000L)

    private val bookmarkedNews = mutableListOf<NewsUi>()

    private val _state = MutableStateFlow(FeaturedHeadlinesState())
    val state = _state
        .onStart {
            // Load bookmarks first, then headlines
            viewModelScope.launch {
                val bookmarksJob = launch { getAllBookmarkedNews() }
                bookmarksJob.join()
                getFeaturedHeadlines(true)
            }
        }
        .stateIn(
            viewModelScope,
            sharingStarted,
            FeaturedHeadlinesState()
        )

    fun getFeaturedHeadlines(isRefresh: Boolean = false) {
        if (_state.value.isLoading || _state.value.isFetchingNext) {
            return
        }

        if (isRefresh) {
            nextCursor = null
            sharingStarted.reset()
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = isRefresh && _state.value.featuredHeadlines.isEmpty(),
                    isFetchingNext = !isRefresh && _state.value.featuredHeadlines.isNotEmpty()
                )
            }

            getFeaturedHeadlinesUseCase(nextCursor)
                .onSuccess { pagedList ->
                    nextCursor = pagedList.nextPage

                    // Check if the news is bookmarked
                    val currentPageList = pagedList.data.map {
                        it.copy(isBookmarked = bookmarkedNews.contains(it))
                    }

                    val updatedList = if (isRefresh) currentPageList
                    else {
                        (_state.value.featuredHeadlines + currentPageList)
                    }

                    _state.update {
                        it.copy(
                            isLoading = false,
                            isFetchingNext = false,
                            featuredHeadlines = updatedList,
                        )
                    }
                }.onError { error ->
                    _state.update {
                        it.copy(isLoading = false, isFetchingNext = false)
                    }
                    _errorEvents.emit(error)
                }
        }
    }

    private fun getAllBookmarkedNews() {
        viewModelScope.launch {
            getAllBookmarkedNewsUseCase().collect { news ->
                bookmarkedNews.clear()
                bookmarkedNews.addAll(news)
            }
        }
    }

    fun toggleBookmark(id: String) {
        val index = _state.value.featuredHeadlines.indexOfFirst { it.id == id }
        if (index != -1) {
            val news = _state.value.featuredHeadlines[index]
            updateBookmarkState(index, !news.isBookmarked)

            if (news.isBookmarked) {
                unBookMarkNews(id)
            } else {
                bookMarkNews(news)
            }
        }
    }

    private fun updateBookmarkState(index: Int, isBookmarked: Boolean) {
        val updatedHeadlines = _state.value.featuredHeadlines.toMutableList()
        updatedHeadlines[index] = updatedHeadlines[index].copy(isBookmarked = isBookmarked)
        _state.update {
            it.copy(featuredHeadlines = updatedHeadlines)
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