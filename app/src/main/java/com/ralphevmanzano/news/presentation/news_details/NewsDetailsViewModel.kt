package com.ralphevmanzano.news.presentation.news_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ralphevmanzano.news.domain.usecase.BookmarkNewsUseCase
import com.ralphevmanzano.news.domain.usecase.UnBookmarkNewsUseCase
import com.ralphevmanzano.news.presentation.model.NewsUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NewsDetailsViewModel(
    private val bookmarkNewsUseCase: BookmarkNewsUseCase,
    private val unBookmarkNewsUseCase: UnBookmarkNewsUseCase,
) : ViewModel() {

    private val _news = MutableStateFlow(NewsUi())
    val news: StateFlow<NewsUi> = _news.asStateFlow()

    fun setNews(news: NewsUi) {
        _news.value = news
    }

    fun onToggleBookmark() {
        val isBookmarked = _news.value.isBookmarked

        _news.update {
            it.copy(isBookmarked = !isBookmarked)
        }

        viewModelScope.launch {
            if (!isBookmarked) {
                bookmarkNewsUseCase(_news.value)
            } else {
                unBookmarkNewsUseCase(_news.value.id)
            }
        }
    }
}