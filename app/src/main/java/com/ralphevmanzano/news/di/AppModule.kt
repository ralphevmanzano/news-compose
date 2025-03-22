package com.ralphevmanzano.news.di

import com.ralphevmanzano.news.data.repository.NewsRepositoryImpl
import com.ralphevmanzano.news.data.repository.datasource.NewsLocalDataSource
import com.ralphevmanzano.news.data.repository.datasource.NewsRemoteDataSource
import com.ralphevmanzano.news.domain.repository.NewsRepository
import com.ralphevmanzano.news.domain.usecase.BookmarkNewsUseCase
import com.ralphevmanzano.news.domain.usecase.GetAllBookmarkedNewsUseCase
import com.ralphevmanzano.news.domain.usecase.GetFeaturedHeadlinesUseCase
import com.ralphevmanzano.news.domain.usecase.SearchNewsUseCase
import com.ralphevmanzano.news.domain.usecase.UnBookmarkNewsUseCase
import com.ralphevmanzano.news.presentation.bookmarks.BookmarksViewModel
import com.ralphevmanzano.news.presentation.news_details.NewsDetailsViewModel
import com.ralphevmanzano.news.presentation.news_list.NewsListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single { NewsRemoteDataSource(get()) }
    single { NewsLocalDataSource(get()) }

    single<NewsRepository> { NewsRepositoryImpl(get(), get()) }

    single { GetFeaturedHeadlinesUseCase(get()) }
    single { BookmarkNewsUseCase(get()) }
    single { UnBookmarkNewsUseCase(get()) }
    single { GetAllBookmarkedNewsUseCase(get()) }
    single { SearchNewsUseCase(get()) }

    viewModelOf(::NewsDetailsViewModel)
    viewModelOf(::BookmarksViewModel)
    viewModelOf(::NewsListViewModel)
}