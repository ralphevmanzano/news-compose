package com.ralphevmanzano.news.data.repository.datasource

import com.ralphevmanzano.news.data.local.NewsDao
import com.ralphevmanzano.news.data.local.entity.NewsEntity
import com.ralphevmanzano.news.data.local.entity.mappers.toNews
import com.ralphevmanzano.news.data.local.entity.mappers.toNewsEntity
import com.ralphevmanzano.news.domain.model.News
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NewsLocalDataSource(private val newsDao: NewsDao) {

    suspend fun insertNews(news: News) {
        newsDao.insertNews(news.toNewsEntity())
    }

    fun getAllNews(): Flow<List<News>> {
        return newsDao.getAllNews().map { newsEntities ->
            newsEntities.map { it.toNews() }
        }
    }

    suspend fun deleteNews(id: String) {
        newsDao.deleteNews(id)
    }
}