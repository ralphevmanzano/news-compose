package com.ralphevmanzano.news.data.local.entity.mappers

import com.ralphevmanzano.news.data.local.entity.NewsEntity
import com.ralphevmanzano.news.domain.model.News

fun NewsEntity.toNews(): News {
    return News(
        id = id,
        title = title,
        sourceTitle = sourceTitle,
        sourceIcon = sourceIcon,
        description = description.orEmpty(),
        pubDate = formattedPublishedDate,
        articleLink = articleLink,
        imageUrl = imageUrl.orEmpty()
    )
}

fun News.toNewsEntity(): NewsEntity {
    return NewsEntity(
        id = id,
        title = title,
        sourceTitle = sourceTitle,
        sourceIcon = sourceIcon,
        description = description,
        formattedPublishedDate = pubDate,
        articleLink = articleLink,
        imageUrl = imageUrl
    )
}