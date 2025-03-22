package com.ralphevmanzano.news.presentation.news_details.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.ralphevmanzano.news.R
import com.ralphevmanzano.news.presentation.model.NewsUi
import com.ralphevmanzano.news.ui.theme.Bookmark
import com.ralphevmanzano.news.ui.theme.Typography

@Composable
fun NewsDetailsSource(modifier: Modifier = Modifier, news: NewsUi, onBookmarkClick: () -> Unit) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            modifier = Modifier.size(32.dp),
            model = news.sourceIcon,
            contentDescription = news.sourceTitle,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = news.sourceTitle,
                style = Typography.bodyMedium
            )
        }
        IconButton(
            onClick = onBookmarkClick,
        ) {
            Icon(
                modifier = Modifier
                    .size(42.dp)
                    .padding(4.dp),
                painter = if (news.isBookmarked) painterResource(R.drawable.ic_bookmark_filled)
                else painterResource(R.drawable.ic_bookmark),
                contentDescription = stringResource(R.string.bookmark),
                tint = if (news.isBookmarked) Bookmark else Color.Gray
            )
        }
    }
}