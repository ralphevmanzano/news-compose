package com.ralphevmanzano.news.domain.repository

import com.ralphevmanzano.news.domain.model.News
import com.ralphevmanzano.news.domain.model.PagedList
import com.ralphevmanzano.news.domain.model.networking.NetworkError
import com.ralphevmanzano.news.domain.model.networking.Result
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    suspend fun getFeaturedHeadlines(
        nextCursor: String? = null
    ): Result<PagedList<News>, NetworkError>

    suspend fun searchNews(
        q: String,
        nextCursor: String? = null
    ): Result<PagedList<News>, NetworkError>

    suspend fun bookmarkNews(news: News)

    suspend fun unBookmarkNews(id: String)

    fun getAllBookmarkedNews(): Flow<List<News>>
}