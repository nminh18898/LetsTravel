package com.minhhnn18898.discover.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhhnn18898.architecture.ui.UiState
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.core.utils.DateTimeUtils
import com.minhhnn18898.discover.data.model.Article
import com.minhhnn18898.discover.usecase.GetListArticlesDiscovery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val getListArticlesDiscovery: GetListArticlesDiscovery,
    private val dateTimeUtils: DateTimeUtils = DateTimeUtils()
): ViewModel() {

    var articlesContentState: UiState<List<ArticleDisplayInfo>, UiState.UndefinedError> by mutableStateOf(UiState.Loading)
        private set

    init {
        loadDiscoveryArticles()
    }

    private fun loadDiscoveryArticles() {
        viewModelScope.launch {
            getListArticlesDiscovery.execute(Unit)?.collect {
                when(it) {
                    is Result.Loading -> articlesContentState = UiState.Loading
                    is Result.Success -> handleResultLoadArticles(it.data)
                    is Result.Error -> articlesContentState = UiState.Error(UiState.UndefinedError)
                }
            }
        }
    }

    private fun handleResultLoadArticles(articles: List<Article>) {
       articlesContentState = UiState.Success(articles.map { it.toDisplayInfo() })
    }

    private fun Article.toDisplayInfo(): ArticleDisplayInfo {
        return ArticleDisplayInfo(
            this.title,
            this.content,
            this.thumbUrl,
            this.photoUrls,
            dateTimeUtils.dateToString(this.lastEdited ?: Date()),
            this.originalSrc,
            this.tag
        )
    }
}