package com.ralphevmanzano.news.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewsResponse(
    val nextPage: String,
    val totalResults: Int,
    val results: List<NewsDto>
)