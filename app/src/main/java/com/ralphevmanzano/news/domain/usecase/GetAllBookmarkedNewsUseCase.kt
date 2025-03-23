package com.ralphevmanzano.news.domain.usecase

import com.ralphevmanzano.news.domain.repository.NewsRepository
import com.ralphevmanzano.news.presentation.model.mapper.toNewsUi
import kotlinx.coroutines.flow.map

class GetAllBookmarkedNewsUseCase(private val newsRepository: NewsRepository) {
    operator fun invoke() = newsRepository.getAllBookmarkedNews().map { bookmarkedNews ->
        bookmarkedNews.map {
            // Set featured to false, as we only want to show it in Search results
            it.toNewsUi().copy(isBookmarked = true, isFeatured = false)
        }
    }
}