package com.ralphevmanzano.news.domain.usecase

import com.ralphevmanzano.news.domain.model.PagedList
import com.ralphevmanzano.news.domain.model.networking.map
import com.ralphevmanzano.news.domain.repository.NewsRepository
import com.ralphevmanzano.news.presentation.model.mapper.toNewsUi
import kotlinx.coroutines.flow.map

class GetFeaturedHeadlinesUseCase(private val newsRepository: NewsRepository) {
    suspend operator fun invoke(nextCursor: String? = null) =
        newsRepository.getFeaturedHeadlines(nextCursor).map { pagedList ->
            PagedList(
                data = pagedList.data.map { it.toNewsUi() }.filter { it.title.isNotBlank() },
                nextPage = pagedList.nextPage
            )
        }
}