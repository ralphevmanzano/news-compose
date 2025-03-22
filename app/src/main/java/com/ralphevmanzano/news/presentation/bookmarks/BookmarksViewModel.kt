package com.ralphevmanzano.news.presentation.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ralphevmanzano.news.domain.usecase.BookmarkNewsUseCase
import com.ralphevmanzano.news.domain.usecase.GetAllBookmarkedNewsUseCase
import com.ralphevmanzano.news.domain.usecase.UnBookmarkNewsUseCase
import com.ralphevmanzano.news.presentation.model.NewsUi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BookmarksViewModel(
    getAllBookmarkedNewsUseCase: GetAllBookmarkedNewsUseCase,
    private val bookmarkNewsUseCase: BookmarkNewsUseCase,
    private val unBookmarkNewsUseCase: UnBookmarkNewsUseCase,
): ViewModel() {

    val bookmarks: StateFlow<List<NewsUi>> = getAllBookmarkedNewsUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    fun toggleBookmark(id: String) {
        val list = bookmarks.value
        val index = list.indexOfFirst { it.id == id }
        if (index != -1) {
            val news = list[index]

            if (news.isBookmarked) {
                unBookMarkNews(id)
            } else {
                bookMarkNews(news)
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