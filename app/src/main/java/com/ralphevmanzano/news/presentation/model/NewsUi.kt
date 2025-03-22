package com.ralphevmanzano.news.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NewsUi(
    val id: String = "",
    val title: String = "",
    val sourceTitle: String = "",
    val sourceIcon: String = "",
    val description: String? = null,
    val formattedPublishedDate: String = "",
    val articleLink: String = "",
    val imageUrl: String? = null,
    var isBookmarked: Boolean = false,
    var isFeatured: Boolean = false
): Parcelable
