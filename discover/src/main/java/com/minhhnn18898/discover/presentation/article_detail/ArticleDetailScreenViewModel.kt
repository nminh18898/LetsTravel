package com.minhhnn18898.discover.presentation.article_detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.minhhnn18898.account.domain.CheckValidSignedInUserUseCase
import com.minhhnn18898.account.domain.GetAuthStateUseCase
import com.minhhnn18898.app_navigation.destination.DiscoveryArticleDetailScreenDestination
import com.minhhnn18898.app_navigation.destination.DiscoveryArticleDetailScreenParameters
import com.minhhnn18898.app_navigation.destination.DiscoveryArticleDetailScreenParametersType
import com.minhhnn18898.architecture.ui.UiState
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.core.utils.BaseDateTimeFormatter
import com.minhhnn18898.core.utils.isNotBlankOrEmpty
import com.minhhnn18898.discover.data.model.Article
import com.minhhnn18898.discover.domain.GetArticleDetailUseCase
import com.minhhnn18898.discover.presentation.ui_models.ArticleDisplayInfo
import com.minhhnn18898.discover.presentation.ui_models.toDisplayInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.typeOf

@HiltViewModel
class ArticleDetailScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getArticlesDiscoveryUseCase: GetArticleDetailUseCase,
    private val baseDateTimeFormatter: BaseDateTimeFormatter,
    checkValidSignedInUserUseCase: CheckValidSignedInUserUseCase,
    private val getAuthStateUseCase: GetAuthStateUseCase
): ViewModel() {

    private val parameters = savedStateHandle.toRoute<DiscoveryArticleDetailScreenDestination>(
        mapOf(typeOf<DiscoveryArticleDetailScreenParameters>() to DiscoveryArticleDetailScreenParametersType)
    ).parameters

    private val articleId: String = parameters.articleId

    var verifiedUserState by mutableStateOf(checkValidSignedInUserUseCase.execute())
        private set

    var articlesContentState: UiState<ArticleDisplayInfo> by mutableStateOf(UiState.Loading)
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
        if(articleId.isNotBlankOrEmpty()) {
            loadDiscoveryArticle(articleId)
        }
    }

    private fun loadDiscoveryArticle(id: String) {
        viewModelScope.launch {
            getArticlesDiscoveryUseCase.execute(GetArticleDetailUseCase.Param(id)).collect {
                articlesContentState = when(it) {
                    is Result.Loading -> UiState.Loading
                    is Result.Success -> handleArticleResult(it.data)
                    is Result.Error -> UiState.Error()
                }
            }
        }
    }

    private fun handleArticleResult(article: Article?): UiState<ArticleDisplayInfo> {
        if(article == null) {
            return UiState.Error()
        }

        return UiState.Success(article.toDisplayInfo(baseDateTimeFormatter))
    }
}