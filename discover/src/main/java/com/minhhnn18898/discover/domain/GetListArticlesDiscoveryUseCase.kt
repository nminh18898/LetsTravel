package com.minhhnn18898.discover.domain

import com.minhhnn18898.architecture.usecase.NoParamUseCase
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.discover.data.model.ArticlePreview
import com.minhhnn18898.discover.data.repo.DiscoverRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetListArticlesDiscoveryUseCase @Inject constructor(private val discoverRepository: DiscoverRepository): NoParamUseCase<Flow<Result<List<ArticlePreview>>>>() {

    override fun run(): Flow<Result<List<ArticlePreview>>> = flow {
        emit(Result.Loading)
        val result = discoverRepository.getArticlesPreview()
        emit(Result.Success(result))
    }.catch {
        emit(Result.Error(it))
    }

}