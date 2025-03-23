package com.ralphevmanzano.news.presentation.news_details

import com.ralphevmanzano.news.domain.repository.NewsRepository
import com.ralphevmanzano.news.domain.usecase.BookmarkNewsUseCase
import com.ralphevmanzano.news.domain.usecase.UnBookmarkNewsUseCase
import com.ralphevmanzano.news.presentation.model.NewsUi
import com.ralphevmanzano.news.presentation.model.mapper.toNews
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class NewsDetailsViewModelTest {

    @MockK
    private lateinit var newsRepository: NewsRepository

    private lateinit var viewModel: NewsDetailsViewModel
    private lateinit var testDispatcher: TestDispatcher
    private lateinit var bookmarkNewsUseCase: BookmarkNewsUseCase
    private lateinit var unBookmarkNewsUseCase: UnBookmarkNewsUseCase

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        bookmarkNewsUseCase = BookmarkNewsUseCase(newsRepository)
        unBookmarkNewsUseCase = UnBookmarkNewsUseCase(newsRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `setNews() updates the news state`() = runTest {
        // Given
        val news = NewsUi(id = "1")
        viewModel = NewsDetailsViewModel(bookmarkNewsUseCase, unBookmarkNewsUseCase)

        // When
        viewModel.setNews(news)

        // Then
        assertEquals(viewModel.news.value, news)
        coVerify(exactly = 0) { newsRepository.bookmarkNews(any()) }
    }

    @Test
    fun `onToggleBookmark() calls bookmarkNewsUseCase when news is not bookmarked`() = runTest {
        // Given
        val news = NewsUi(id = "1", isBookmarked = false)
        coEvery { newsRepository.bookmarkNews(news.toNews()) } returns Unit

        viewModel = NewsDetailsViewModel(bookmarkNewsUseCase, unBookmarkNewsUseCase)

        // When
        viewModel.setNews(news)
        viewModel.onToggleBookmark()

        testScheduler.advanceUntilIdle()

        // Then
        assertEquals(viewModel.news.value.isBookmarked, true)
        coVerify { newsRepository.bookmarkNews(news.toNews()) }
    }

    @Test
    fun `onToggleBookmark() calls unBookmarkNewsUseCase when news is already bookmarked`() = runTest {
        // Given
        val news = NewsUi(id = "1", isBookmarked = true)
        coEvery { newsRepository.unBookmarkNews(news.id) } returns Unit

        viewModel = NewsDetailsViewModel(bookmarkNewsUseCase, unBookmarkNewsUseCase)

        // When
        viewModel.setNews(news)
        viewModel.onToggleBookmark()

        testScheduler.advanceUntilIdle()

        // Then
        assertEquals(viewModel.news.value.isBookmarked, false)
        coVerify { newsRepository.unBookmarkNews(news.id) }
    }
}