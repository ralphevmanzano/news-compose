package com.ralphevmanzano.news.presentation.model.mapper

import com.ralphevmanzano.news.domain.model.News
import com.ralphevmanzano.news.presentation.model.NewsUi
import com.ralphevmanzano.news.utils.DateFormatterUtil

fun News.toNewsUi(): NewsUi {
    val formattedDate = DateFormatterUtil.formatDate(pubDate)

    return NewsUi(
        id = id,
        title = title,
        sourceTitle = sourceTitle,
        sourceIcon = sourceIcon,
        description = description,
        formattedPublishedDate = formattedDate,
        articleLink = articleLink,
        imageUrl = imageUrl
    )
}

fun NewsUi.toNews(): News {
    val pubDate = DateFormatterUtil.parseDate(formattedPublishedDate)
    return News(
        id = id,
        title = title,
        sourceTitle = sourceTitle,
        sourceIcon = sourceIcon,
        description = description.orEmpty(),
        pubDate = pubDate,
        articleLink = articleLink,
        imageUrl = imageUrl.orEmpty()
    )
}