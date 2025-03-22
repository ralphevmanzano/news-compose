package com.ralphevmanzano.news.data.repository.datasource

import android.util.Log
import com.ralphevmanzano.news.data.remote.NewsService
import com.ralphevmanzano.news.data.remote.dto.mappers.toNews
import com.ralphevmanzano.news.domain.model.News
import com.ralphevmanzano.news.domain.model.PagedList
import com.ralphevmanzano.news.domain.model.networking.NetworkError
import com.ralphevmanzano.news.domain.model.networking.Result
import com.ralphevmanzano.news.domain.model.networking.map
import com.ralphevmanzano.news.utils.safeCall

class NewsRemoteDataSource(private val newsService: NewsService) {
    private var prevSearchQuery = ""

    suspend fun getFeaturedHeadlines(nextCursor: String? = null): Result<PagedList<News>, NetworkError> {
        return safeCall {
            Log.e("NewsRemoteDataSource", "nextCursor: $nextCursor", )
            newsService.getFeaturedHeadlines(nextPage = nextCursor)
        }.map { response ->
            PagedList(
                data = response.results.map { it.toNews() },
                nextPage = response.nextPage
            )
        }
    }

    suspend fun searchNews(q: String, nextPage: String? = null): Result<PagedList<News>, NetworkError> {
        if (q != prevSearchQuery) {
            prevSearchQuery = q
        }

        return safeCall {
            newsService.searchNews(q = q, nextPage = nextPage)
        }.map { response ->
            PagedList(
                data = response.results.map { it.toNews() },
                nextPage = response.nextPage
            )
        }
    }
}