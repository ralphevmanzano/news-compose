package com.ralphevmanzano.news.presentation.news_details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.SubcomposeAsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.ralphevmanzano.news.R
import com.ralphevmanzano.news.presentation.model.NewsUi
import com.ralphevmanzano.news.presentation.news_details.components.ImagePlaceHolder
import com.ralphevmanzano.news.presentation.news_details.components.NewsDetailsSource
import com.ralphevmanzano.news.ui.theme.NewsTheme
import com.ralphevmanzano.news.ui.theme.Typography
import org.koin.androidx.compose.koinViewModel

private const val IMAGE_SIZE = 300

@Composable
fun NewsDetailsScreen(
    modifier: Modifier = Modifier,
    newsArgs: NewsUi?,
    viewModel: NewsDetailsViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
) {

    LaunchedEffect(newsArgs) {
        if (newsArgs != null) {
            viewModel.setNews(newsArgs)
        }
    }

    val news by viewModel.news.collectAsStateWithLifecycle()

    NewsDetailsContent(
        modifier = modifier,
        news = news,
        onBookmarkClick = { viewModel.onToggleBookmark() },
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailsContent(
    modifier: Modifier = Modifier,
    news: NewsUi?,
    onBookmarkClick: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current

    Column(modifier = modifier) {
        TopAppBar(
            modifier = Modifier.fillMaxWidth(),
            title = {},
            navigationIcon = {
                IconButton(onClick = { onNavigateBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            },
            windowInsets = WindowInsets(top = 0, bottom = 0)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        ) {
            if (news != null) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = news.title,
                    style = Typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                NewsDetailsSource(
                    modifier = Modifier.fillMaxWidth(),
                    news = news,
                    onBookmarkClick = onBookmarkClick,
                )
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = news.formattedPublishedDate,
                    style = Typography.bodySmall.copy(fontSize = 14.sp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .background(color = Color.DarkGray, shape = RoundedCornerShape(32.dp))
                        .aspectRatio(5 / 3f)
                ) {
                    SubcomposeAsyncImage(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(32.dp)),
                        model = ImageRequest.Builder(context)
                            .data(news.imageUrl)
                            .size(IMAGE_SIZE)
                            .crossfade(true)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .build(),
                        loading = {
                            ImagePlaceHolder(modifier = Modifier.fillMaxSize())
                        },
                        error = {
                            ImagePlaceHolder(modifier = Modifier.fillMaxSize())
                        },
                        contentDescription = news.title,
                        contentScale = ContentScale.Crop,
                        clipToBounds = true
                    )
                }
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = news.description.orEmpty(),
                    style = Typography.bodyMedium
                )
            }
        }
    }
}

@Preview
@Composable
private fun NewsDetailsPreview() {
    NewsTheme {
        Surface {
            NewsDetailsContent(
                modifier = Modifier.fillMaxSize(),
                news = NewsUi(
                    id = "1",
                    title = "Sample News Title 1",
                    description = "This is a sample description for the news article 1.",
                    imageUrl = "https://example.com/sample-image1.jpg",
                    formattedPublishedDate = "March 22, 2025",
                    isBookmarked = false,
                    articleLink = "",
                    sourceTitle = "BBC News",
                    sourceIcon = ""
                ),
                onBookmarkClick = {},
                onNavigateBack = {}
            )
        }
    }
}