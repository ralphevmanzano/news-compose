package com.ralphevmanzano.news.domain.model

data class PagedList<T>(
    val data: List<T>,
    val nextPage: String? = null
)