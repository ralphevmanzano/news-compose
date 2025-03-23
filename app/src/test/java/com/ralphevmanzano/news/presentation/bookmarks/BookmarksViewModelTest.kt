package com.ralphevmanzano.news.presentation.bookmarks

import com.ralphevmanzano.news.domain.repository.NewsRepository
import com.ralphevmanzano.news.domain.usecase.BookmarkNewsUseCase
import com.ralphevmanzano.news.domain.usecase.GetAllBookmarkedNewsUseCase
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class BookmarksViewModelTest {
    @MockK
    private lateinit var newsRepository: NewsRepository

    private lateinit var viewModel: BookmarksViewModel
    private lateinit var testDispatcher: TestDispatcher
    private lateinit var bookmarkNewsUseCase: BookmarkNewsUseCase
    private lateinit var unBookmarkNewsUseCase: UnBookmarkNewsUseCase
    private lateinit var getAllBookmarkedNewsUseCase: GetAllBookmarkedNewsUseCase

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        bookmarkNewsUseCase = BookmarkNewsUseCase(newsRepository)
        unBookmarkNewsUseCase = UnBookmarkNewsUseCase(newsRepository)
        getAllBookmarkedNewsUseCase = GetAllBookmarkedNewsUseCase(newsRepository)

        viewModel = BookmarksViewModel(
            getAllBookmarkedNewsUseCase,
            bookmarkNewsUseCase,
            unBookmarkNewsUseCase
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `getAllBookmarkedNewsUseCase updates the bookmarks StateFlow`() = runTest {
        // Given
        val bookmarks = listOf(
            NewsUi(id = "1"),
            NewsUi(id = "2"),
            NewsUi(id = "3")
        )
        coEvery { newsRepository.getAllBookmarkedNews() } returns flowOf(bookmarks.map { it.toNews() })

        // When
        val result = mutableListOf<NewsUi>()
        val job = launch {
            viewModel.bookmarks.collect {
                result.addAll(it)
            }
        }

        testScheduler.advanceUntilIdle()

        job.cancel()

        // Then
        assertEquals(bookmarks.size, result.size)
        assertEquals(bookmarks[0].id, result[0].id)
        assertEquals(bookmarks[1].id, result[1].id)
        assertEquals(bookmarks[2].id, result[2].id)
        coVerify { newsRepository.getAllBookmarkedNews() }
    }
}