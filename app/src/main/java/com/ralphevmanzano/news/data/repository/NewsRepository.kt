package com.ralphevmanzano.news.data.repository

import com.ralphevmanzano.news.data.repository.datasource.NewsLocalDataSource
import com.ralphevmanzano.news.data.repository.datasource.NewsRemoteDataSource
import com.ralphevmanzano.news.domain.model.News
import com.ralphevmanzano.news.domain.model.PagedList
import com.ralphevmanzano.news.domain.model.networking.NetworkError
import com.ralphevmanzano.news.domain.model.networking.Result
import com.ralphevmanzano.news.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow

class NewsRepositoryImpl(
    private val remoteDataSource: NewsRemoteDataSource,
    private val localDataSource: NewsLocalDataSource
): NewsRepository {

    override suspend fun getFeaturedHeadlines(nextCursor: String?): Result<PagedList<News>, NetworkError> {
        return remoteDataSource.getFeaturedHeadlines(nextCursor)
    }

    override suspend fun searchNews(q: String, nextPage: String?): Result<PagedList<News>, NetworkError> {
        return remoteDataSource.searchNews(q, nextPage)
    }

    override suspend fun bookmarkNews(news: News) {
        localDataSource.insertNews(news)
    }

    override suspend fun unBookmarkNews(id: String) {
        localDataSource.deleteNews(id)
    }

    override fun getAllBookmarkedNews(): Flow<List<News>> {
        return localDataSource.getAllNews()
    }
}