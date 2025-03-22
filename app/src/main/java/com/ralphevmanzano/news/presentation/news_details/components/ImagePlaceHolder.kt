package com.ralphevmanzano.news.presentation.news_details.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ralphevmanzano.news.R
import com.ralphevmanzano.news.ui.theme.NewsTheme

@Composable
fun ImagePlaceHolder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.size(64.dp),
            painter = painterResource(id = R.drawable.ic_placeholder),
            contentDescription = null,
            tint = Color.LightGray
        )
    }
}

@Preview
@Composable
private fun ImagePlaceHolderPreview() {
    NewsTheme {
        Surface {
            ImagePlaceHolder(modifier = Modifier.fillMaxWidth().aspectRatio(5/3f))
        }
    }
}