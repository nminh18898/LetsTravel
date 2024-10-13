package com.minhhnn18898.discover.presentation.discover

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhhnn18898.account.domain.CheckValidSignedInUserUseCase
import com.minhhnn18898.account.domain.GetAuthStateUseCase
import com.minhhnn18898.architecture.ui.UiState
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.core.utils.BaseDateTimeFormatter
import com.minhhnn18898.discover.data.model.Article
import com.minhhnn18898.discover.domain.GetListArticlesDiscoveryUseCase
import com.minhhnn18898.discover.presentation.ui_models.ArticleDisplayInfo
import com.minhhnn18898.discover.presentation.ui_models.toDisplayInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val getListArticlesDiscoveryUseCase: GetListArticlesDiscoveryUseCase,
    private val baseDateTimeFormatter: BaseDateTimeFormatter,
    checkValidSignedInUserUseCase: CheckValidSignedInUserUseCase,
    private val getAuthStateUseCase: GetAuthStateUseCase
): ViewModel() {

    var verifiedUserState by mutableStateOf(checkValidSignedInUserUseCase.execute())
        private set

    var articlesContentState: UiState<List<ArticleDisplayInfo>> by mutableStateOf(UiState.Loading)
        private set

    init {
       observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            getAuthStateUseCase.execute().collect {
                verifiedUserState = it
                if(verifiedUserState) {
                    onUserAuthCheckSuccess()
                }
            }
        }
    }

    private fun onUserAuthCheckSuccess() {
        loadDiscoveryArticles()
    }

    private fun loadDiscoveryArticles() {
        viewModelScope.launch {
            getListArticlesDiscoveryUseCase.execute().collect {
                when(it) {
                    is Result.Loading -> articlesContentState = UiState.Loading
                    is Result.Success -> handleResultLoadArticles(it.data)
                    is Result.Error -> articlesContentState = UiState.Error()
                }
            }
        }
    }

    private fun handleResultLoadArticles(articles: List<Article>) {
       articlesContentState = UiState.Success(articles.map { it.toDisplayInfo(baseDateTimeFormatter) })
    }
}