package com.ralphevmanzano.news.presentation.featured_headlines

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.ralphevmanzano.news.R
import com.ralphevmanzano.news.presentation.model.NewsUi
import com.ralphevmanzano.news.ui.theme.Bookmark
import com.ralphevmanzano.news.ui.theme.NewsTheme
import com.ralphevmanzano.news.ui.theme.Pink80
import com.ralphevmanzano.news.ui.theme.Purple80
import com.ralphevmanzano.news.ui.theme.TransparentGray
import com.ralphevmanzano.news.ui.theme.Typography

const val IMAGE_SIZE = 300
const val TITLE_MAX_LINES = 4

@Composable
fun NewsListItem(
    modifier: Modifier = Modifier,
    newsUi: NewsUi,
    onItemClicked: (String) -> Unit,
    onBookmarkClicked: (String) -> Unit,
) {
    val context = LocalContext.current

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = { onItemClicked(newsUi.id) }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            SubcomposeAsyncImage(
                modifier = Modifier
                    .fillMaxSize(),
                model = ImageRequest.Builder(context)
                    .data(newsUi.imageUrl)
                    .size(IMAGE_SIZE)
                    .crossfade(true)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .build(),
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            modifier = Modifier.size(48.dp),
                            painter = painterResource(id = R.drawable.ic_placeholder),
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                    }
                },
                contentDescription = newsUi.title,
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = TransparentGray)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Spacer(modifier = Modifier.weight(1f))
                if (newsUi.isFeatured) {
                    Icon(
                        painter = painterResource(R.drawable.ic_featured),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                Text(
                    text = newsUi.title,
                    style = Typography.titleSmall.copy(color = Color.White),
                    maxLines = TITLE_MAX_LINES,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = newsUi.sourceTitle,
                    style = Typography.bodySmall.copy(
                        color = Color.White,
                        textDecoration = TextDecoration.Underline,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            IconButton(
                modifier = Modifier.align(Alignment.TopEnd),
                onClick = { onBookmarkClicked(newsUi.id) },
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color.White, shape = CircleShape)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = if (newsUi.isBookmarked) painterResource(R.drawable.ic_bookmark_filled)
                        else painterResource(R.drawable.ic_bookmark),
                        contentDescription = stringResource(R.string.bookmark),
                        tint = if (newsUi.isBookmarked) Bookmark else Color.Gray
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun NewsListItemPreview() {
    NewsTheme {
        NewsListItem(
            modifier = Modifier.aspectRatio(1f),
            newsUi = NewsUi(
                id = "c4d6bd24-4b4a-4ce1-a4d3-0000f8242beb",
                title = "What to know about a legal dispute over one Ohio school district's pronoun policy",
                sourceTitle = "The Washington Times",
                articleLink = "https://www.washingtontimes.com/news/2025/mar/19/legal-dispute-one-ohio-school-districts-pronoun-policy-know/",
                formattedPublishedDate = "March 19, 2025",
                description = "",
            ),
            onItemClicked = {},
            onBookmarkClicked = {}
        )
    }
}