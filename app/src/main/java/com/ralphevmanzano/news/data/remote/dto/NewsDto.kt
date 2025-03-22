package com.ralphevmanzano.news.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewsDto(
    @SerialName("article_id")
    val id: String,
    val title: String? = null,
    val link: String,
    val keywords: List<String>? = emptyList(),
    val creator: List<String>? = emptyList(),
    val description: String? = null,
    val pubDate: String,
    @SerialName("source_name")
    val sourceName: String,
    @SerialName("source_icon")
    val sourceIcon: String? = null,
    val content: String,
    @SerialName("image_url")
    val imageUrl: String? = null,
    val language: String,
)