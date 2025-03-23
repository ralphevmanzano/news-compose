package com.ralphevmanzano.news

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ralphevmanzano.news.presentation.bookmarks.BookmarksScreen
import com.ralphevmanzano.news.presentation.model.ARGS_NEWS
import com.ralphevmanzano.news.presentation.model.NewsUi
import com.ralphevmanzano.news.presentation.news_list.NewsListScreen
import com.ralphevmanzano.news.presentation.model.Screen
import com.ralphevmanzano.news.presentation.news_details.NewsDetailsScreen
import com.ralphevmanzano.news.ui.theme.NewsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewsTheme {
                val navHostController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    SetupNavGraph(
                        modifier = Modifier.padding(innerPadding),
                        navHostController = navHostController,
                        snackbarHostState = snackbarHostState
                    )
                }
            }
        }
    }
}

@Composable
fun SetupNavGraph(
    modifier: Modifier,
    navHostController: NavHostController,
    snackbarHostState: SnackbarHostState
) {
    NavHost(
        modifier = modifier,
        navController = navHostController,
        startDestination = Screen.FeaturedHeadlines
    ) {
        composable<Screen.FeaturedHeadlines> {
            NewsListScreen(
                modifier = Modifier.fillMaxSize(),
                snackbarHostState = snackbarHostState,
                onNavigateToDetails = { news ->
                    navHostController.navigate(Screen.NewsDetails)
                    navHostController.currentBackStackEntry?.savedStateHandle?.set(ARGS_NEWS, news)
                },
                onNavigateToBookmarks = {
                    navHostController.navigate(Screen.BookmarksScreen)
                }
            )
        }
        composable<Screen.NewsDetails> { backStackEntry ->
            val savedStateHandle = backStackEntry.savedStateHandle
            val newsArgs = savedStateHandle.get<NewsUi>(ARGS_NEWS)

            NewsDetailsScreen(
                modifier = Modifier.fillMaxSize(),
                onNavigateBack = {
                    navHostController.popBackStack()
                },
                newsArgs = newsArgs
            )
        }
        composable<Screen.BookmarksScreen> {
            BookmarksScreen(
                modifier = Modifier.fillMaxSize(),
                onNavigateBack = {
                    navHostController.popBackStack()
                },
                onNavigateToDetails = { news ->
                    navHostController.navigate(Screen.NewsDetails)
                    navHostController.currentBackStackEntry?.savedStateHandle?.set(ARGS_NEWS, news)
                }
            )
        }
    }
}