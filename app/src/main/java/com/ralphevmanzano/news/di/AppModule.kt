package com.ralphevmanzano.news.di

import com.ralphevmanzano.news.data.repository.NewsRepositoryImpl
import com.ralphevmanzano.news.data.repository.datasource.NewsLocalDataSource
import com.ralphevmanzano.news.data.repository.datasource.NewsRemoteDataSource
import com.ralphevmanzano.news.domain.repository.NewsRepository
import com.ralphevmanzano.news.domain.usecase.BookmarkNewsUseCase
import com.ralphevmanzano.news.domain.usecase.GetAllBookmarkedNewsUseCase
import com.ralphevmanzano.news.domain.usecase.GetFeaturedHeadlinesUseCase
import com.ralphevmanzano.news.domain.usecase.UnBookmarkNewsUseCase
import com.ralphevmanzano.news.presentation.featured_headlines.FeaturedHeadlinesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { NewsRemoteDataSource(get()) }
    single { NewsLocalDataSource(get()) }

    single<NewsRepository> { NewsRepositoryImpl(get(), get()) }

    single { GetFeaturedHeadlinesUseCase(get()) }
    single { BookmarkNewsUseCase(get()) }
    single { UnBookmarkNewsUseCase(get()) }
    single { GetAllBookmarkedNewsUseCase(get()) }

    viewModel { FeaturedHeadlinesViewModel(get(), get(), get(), get()) }
}