package com.ralphevmanzano.news.data.remote.dto.mappers

import com.ralphevmanzano.news.data.remote.dto.NewsDto
import com.ralphevmanzano.news.domain.model.News

fun NewsDto.toNews(): News {
    return News(
        id = id,
        title = title.orEmpty(),
        sourceTitle = sourceName,
        sourceIcon = sourceIcon.orEmpty(),
        description = description.orEmpty(),
        pubDate = pubDate,
        articleLink = link,
        imageUrl = imageUrl.orEmpty()
    )
}