package com.ralphevmanzano.news.domain.usecase

import com.ralphevmanzano.news.domain.repository.NewsRepository

class SearchNewsUseCase(private val newsRepository: NewsRepository) {
    suspend operator fun invoke(query: String) = newsRepository.searchNews(query)
}