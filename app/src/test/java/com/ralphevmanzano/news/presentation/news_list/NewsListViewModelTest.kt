package com.ralphevmanzano.news.presentation.news_list

import com.ralphevmanzano.news.domain.model.PagedList
import com.ralphevmanzano.news.domain.model.networking.NetworkError
import com.ralphevmanzano.news.domain.model.networking.Result
import com.ralphevmanzano.news.domain.repository.NewsRepository
import com.ralphevmanzano.news.domain.usecase.BookmarkNewsUseCase
import com.ralphevmanzano.news.domain.usecase.GetAllBookmarkedNewsUseCase
import com.ralphevmanzano.news.domain.usecase.GetFeaturedHeadlinesUseCase
import com.ralphevmanzano.news.domain.usecase.SearchNewsUseCase
import com.ralphevmanzano.news.domain.usecase.UnBookmarkNewsUseCase
import com.ralphevmanzano.news.presentation.model.NewsUi
import com.ralphevmanzano.news.presentation.model.mapper.toNews
import com.ralphevmanzano.news.presentation.model.mapper.toNewsUi
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test


class NewsListViewModelTest {

    @MockK
    private lateinit var newsRepository: NewsRepository

    private lateinit var viewModel: NewsListViewModel
    private lateinit var testDispatcher: TestDispatcher

    private lateinit var getFeaturedHeadlinesUseCase: GetFeaturedHeadlinesUseCase
    private lateinit var searchNewsUseCase: SearchNewsUseCase
    private lateinit var getAllBookmarkedNewsUseCase: GetAllBookmarkedNewsUseCase
    private lateinit var bookmarkNewsUseCase: BookmarkNewsUseCase
    private lateinit var unBookmarkNewsUseCase: UnBookmarkNewsUseCase

    private val newsList = listOf(
        NewsUi(
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
        NewsUi(
            id = "2",
            title = "Sample News Title 2",
            description = "This is a sample description for the news article 2.",
            imageUrl = "https://example.com/sample-image2.jpg",
            formattedPublishedDate = "March 22, 2025",
            isBookmarked = false,
            articleLink = "",
            sourceTitle = "BBC News",
            sourceIcon = ""
        ),
        NewsUi(
            id = "3",
            title = "Sample News Title 3",
            description = "This is a sample description for the news article 3.",
            imageUrl = "https://example.com/sample-image3.jpg",
            formattedPublishedDate = "March 22, 2025",
            isBookmarked = false,
            articleLink = "",
            sourceTitle = "Washington Post",
            sourceIcon = ""
        )
    )

    private val bookmarkedList = listOf(
        NewsUi(
            id = "2",
            title = "Sample News Title 2",
            description = "This is a sample description for the news article 2.",
            imageUrl = "https://example.com/sample-image2.jpg",
            formattedPublishedDate = "March 22, 2025",
            isBookmarked = true,
            articleLink = "",
            sourceTitle = "BBC News",
            sourceIcon = ""
        )
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        getFeaturedHeadlinesUseCase = GetFeaturedHeadlinesUseCase(newsRepository)
        searchNewsUseCase = SearchNewsUseCase(newsRepository)
        getAllBookmarkedNewsUseCase = GetAllBookmarkedNewsUseCase(newsRepository)
        bookmarkNewsUseCase = BookmarkNewsUseCase(newsRepository)
        unBookmarkNewsUseCase = UnBookmarkNewsUseCase(newsRepository)

        viewModel = NewsListViewModel(
            getFeaturedHeadlinesUseCase,
            searchNewsUseCase,
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
    fun `featuredHeadlinesState should call performInitialLoad and update state success`() =
        runTest {
            // Given
            val resultHeadlines = Result.Success(PagedList(newsList.map { it.toNews() }, "123"))
            val expectedNews = newsList.map { if (it.id == "2") it.copy(isBookmarked = true) else it }
            coEvery { newsRepository.getFeaturedHeadlines(any()) } returns resultHeadlines
            val bookmarkFlow = flow {
                emit(bookmarkedList.map { it.toNews() })
            }
            coEvery { newsRepository.getAllBookmarkedNews() } returns bookmarkFlow

            // When
            val featuredCollector = mutableListOf<NewsListScreenState>()
            val featuredJob = launch {
                viewModel.featuredHeadlinesState.collect {
                    featuredCollector.add(it)
                }
            }

            testScheduler.advanceUntilIdle()
            featuredJob.cancel()

            // Then
            assertEquals(2, featuredCollector.size)
            assertEquals(NewsListScreenState.FeaturedListState(), featuredCollector[0])
            assertEquals(expectedNews, featuredCollector[1].news)
            coVerify { newsRepository.getFeaturedHeadlines(any()) }
            coVerify { newsRepository.getAllBookmarkedNews() }
        }

    @Test
    fun `featuredHeadlinesState should call performInitialLoad and update state error`() =
        runTest {
            // Given
            val resultHeadlines = Result.Error(NetworkError.InternalServerError)
            coEvery { newsRepository.getFeaturedHeadlines(any()) } returns resultHeadlines
            val bookmarkFlow = flow {
                emit(bookmarkedList.map { it.toNews() })
            }
            coEvery { newsRepository.getAllBookmarkedNews() } returns bookmarkFlow

            // When
            val featuredCollector = mutableListOf<NewsListScreenState>()
            val featuredJob = launch {
                viewModel.featuredHeadlinesState.collect {
                    featuredCollector.add(it)
                }
            }

            val errorCollector = mutableListOf<NetworkError>()
            val errorJob = launch {
                viewModel.errorEvents.collect {
                    errorCollector.add(it)
                }
            }

            testScheduler.advanceUntilIdle()
            featuredJob.cancel()
            errorJob.cancel()

            // Then
            assertEquals(1, featuredCollector.size)
            assertEquals(NewsListScreenState.FeaturedListState(), featuredCollector[0])

            assertEquals(1, errorCollector.size)
            assertEquals(NetworkError.InternalServerError, errorCollector[0])

            coVerify { newsRepository.getFeaturedHeadlines(any()) }
            coVerify { newsRepository.getAllBookmarkedNews() }
        }

    @Test
    fun `onSearchChanged should update query state`() = runTest {
        // Given
        val query = "new query"

        // When
        viewModel.onSearchChanged(query)

        // Then
        assertEquals(query, viewModel.query.value)
        assertEquals(query, viewModel.searchedNewsState.value.query)
    }

    @Test
    fun `searchNews should update state on success`() = runTest {
        // Given
        val q = "query"
        val nextPage = "123"
        val resultSearch = Result.Success(PagedList(newsList.map { it.toNews() }, nextPage))
        coEvery { newsRepository.searchNews(q, any()) } returns resultSearch

        // When
        val searchCollector = mutableListOf<NewsListScreenState.SearchListState>()
        val searchJob = launch {
            viewModel.searchedNewsState.collect {
                searchCollector.add(it)
            }
        }

        // When
        // Collect initial state
        testScheduler.advanceUntilIdle()

        viewModel.onSearchChanged(q)
        viewModel.searchNews()
        testScheduler.advanceUntilIdle()

        searchJob.cancel()

        // Then
        // Initial state, loading state, and success state
        assertEquals(3, searchCollector.size)
        assertEquals(NewsListScreenState.SearchListState(), searchCollector.first())
        assertEquals(NewsListScreenState.SearchListState(isLoading = true, query = q), searchCollector[1])
        assertEquals(
            resultSearch.data.data.map { it.toNewsUi() },
            searchCollector[2].news
        )
        coVerify { newsRepository.searchNews(q, any()) }
    }

    @Test
    fun `searchNews should update state on error`() = runTest {
        // Given
        val q = "query"
        val resultSearch = Result.Error(NetworkError.InternalServerError)
        coEvery { newsRepository.searchNews(q, any()) } returns resultSearch

        // When
        val searchCollector = mutableListOf<NewsListScreenState.SearchListState>()
        val searchJob = launch {
            viewModel.searchedNewsState.collect {
                searchCollector.add(it)
            }
        }

        // When
        // Collect initial state
        testScheduler.advanceUntilIdle()

        val errorCollector = mutableListOf<NetworkError>()
        val errorJob = launch {
            viewModel.errorEvents.collect {
                errorCollector.add(it)
            }
        }

        viewModel.onSearchChanged(q)
        viewModel.searchNews()

        testScheduler.advanceUntilIdle()
        searchJob.cancel()
        errorJob.cancel()

        // Then
        // Initial state and error state
        assertEquals(3, searchCollector.size)
        assertEquals(NewsListScreenState.SearchListState(), searchCollector.first())
        assertEquals(NewsListScreenState.SearchListState(isLoading = true, query = q), searchCollector[1])

        assertEquals(1, errorCollector.size)
        assertEquals(NetworkError.InternalServerError, errorCollector.first())

        coVerify { newsRepository.searchNews(q, any()) }
    }

    @Test
    fun `clearSearchResults should clear search results`() = runTest {
        // Given
        val q = "query"
        val resultSearch = Result.Success(PagedList(newsList.map { it.toNews() }, "123"))
        coEvery { newsRepository.searchNews(q, any()) } returns resultSearch

        // When
        val searchCollector = mutableListOf<NewsListScreenState.SearchListState>()
        val searchJob = launch {
            viewModel.searchedNewsState.collect {
                searchCollector.add(it)
            }
        }

        // When
        // Collect initial state
        testScheduler.advanceUntilIdle()

        viewModel.onSearchChanged(q)
        viewModel.searchNews()
        testScheduler.advanceUntilIdle()

        // Then
        // Initial state, loading state, and success state
        assertEquals(3, searchCollector.size)
        assertEquals(NewsListScreenState.SearchListState(), searchCollector.first())
        assertEquals(NewsListScreenState.SearchListState(isLoading = true, query = q), searchCollector[1])
        assertEquals(
            resultSearch.data.data.map { it.toNewsUi() },
            searchCollector[2].news
        )
        coVerify { newsRepository.searchNews(q, any()) }

        // Now clear search results
        viewModel.clearSearchResults()
        testScheduler.advanceUntilIdle()

        searchJob.cancel()

        // Then
        assertTrue(searchCollector.last().news.isEmpty())
    }

    @Test
    fun `toggleBookmark should bookmark news when not bookmarked`() = runTest {
        // Given
        val resultHeadlines = Result.Success(PagedList(newsList.map { it.toNews() }, "123"))
        val expectedNews = newsList.map { if (it.id == "2") it.copy(isBookmarked = true) else it }
        coEvery { newsRepository.getFeaturedHeadlines(any()) } returns resultHeadlines
        val bookmarkFlow = flow {
            emit(bookmarkedList.map { it.toNews() })
        }
        coEvery { newsRepository.getAllBookmarkedNews() } returns bookmarkFlow
        coEvery { newsRepository.bookmarkNews(any()) } returns Unit

        // When
        val featuredCollector = mutableListOf<NewsListScreenState>()
        val featuredJob = launch {
            viewModel.featuredHeadlinesState.collect {
                featuredCollector.add(it)
            }
        }
        testScheduler.advanceUntilIdle()

        // Then
        assertEquals(2, featuredCollector.size)
        assertEquals(NewsListScreenState.FeaturedListState(), featuredCollector[0])
        assertEquals(expectedNews, featuredCollector[1].news)
        coVerify { newsRepository.getFeaturedHeadlines(any()) }
        coVerify { newsRepository.getAllBookmarkedNews() }

        // Bookmark first news
        viewModel.toggleBookmark(newsList.first().id)
        testScheduler.advanceUntilIdle()

        featuredJob.cancel()

        // Then
        assertEquals(true, featuredCollector.last().news.first().isBookmarked)
        coVerify { bookmarkNewsUseCase(newsList.first()) }
    }

    @Test
    fun `toggleBookmark should unbookmark news when bookmarked`() = runTest {
        // Given
        val resultHeadlines = Result.Success(PagedList(newsList.map { it.toNews() }, "123"))
        val expectedNews = newsList.map { if (it.id == "2") it.copy(isBookmarked = true) else it }
        coEvery { newsRepository.getFeaturedHeadlines(any()) } returns resultHeadlines
        val bookmarkFlow = flow {
            emit(bookmarkedList.map { it.toNews() })
        }
        coEvery { newsRepository.getAllBookmarkedNews() } returns bookmarkFlow
        coEvery { newsRepository.unBookmarkNews(any()) } returns Unit

        // When
        val featuredCollector = mutableListOf<NewsListScreenState>()
        val featuredJob = launch {
            viewModel.featuredHeadlinesState.collect {
                featuredCollector.add(it)
            }
        }
        testScheduler.advanceUntilIdle()

        // Then
        assertEquals(2, featuredCollector.size)
        assertEquals(NewsListScreenState.FeaturedListState(), featuredCollector[0])
        assertEquals(expectedNews, featuredCollector[1].news)
        coVerify { newsRepository.getFeaturedHeadlines(any()) }
        coVerify { newsRepository.getAllBookmarkedNews() }

        // Unbookmark news
        viewModel.toggleBookmark(bookmarkedList.first().id)
        testScheduler.advanceUntilIdle()

        featuredJob.cancel()

        // Then
        val unbookMarkedNews = featuredCollector.last().news.first { it.id == bookmarkedList.first().id }
        assertEquals(false, unbookMarkedNews.isBookmarked)
        coVerify { unBookmarkNewsUseCase(bookmarkedList.first().id) }
    }
}