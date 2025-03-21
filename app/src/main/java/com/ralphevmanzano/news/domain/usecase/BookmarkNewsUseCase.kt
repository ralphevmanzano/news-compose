package com.ralphevmanzano.news.domain.usecase

import com.ralphevmanzano.news.domain.repository.NewsRepository
import com.ralphevmanzano.news.presentation.model.NewsUi
import com.ralphevmanzano.news.presentation.model.mapper.toNews

class BookmarkNewsUseCase(private val repository: NewsRepository) {
    suspend operator fun invoke(newsUi: NewsUi) {
        repository.bookmarkNews(newsUi.toNews())
    }
}