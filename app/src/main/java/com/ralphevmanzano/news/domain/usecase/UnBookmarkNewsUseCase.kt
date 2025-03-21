package com.ralphevmanzano.news.domain.usecase

import com.ralphevmanzano.news.domain.repository.NewsRepository

class UnBookmarkNewsUseCase(private val repository: NewsRepository) {
    suspend operator fun invoke(id: String) = repository.unBookmarkNews(id)
}