package com.ralphevmanzano.news.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class NewsResponse(
    val nextPage: String? = null,
    val totalResults: Int,
    val results: List<NewsDto>
)