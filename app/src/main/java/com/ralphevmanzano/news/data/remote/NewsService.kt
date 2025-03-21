package com.ralphevmanzano.news.data.remote

import com.ralphevmanzano.news.data.remote.dto.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsService {

    @GET("latest")
    suspend fun getFeaturedHeadlines(
        @Query("page") nextPage: String? = null
    ): Response<NewsResponse>

    @GET("latest")
    suspend fun searchNews(
        @Query("q") q: String,
        @Query("page") nextPage: String? = null
    ): Response<NewsResponse>
}