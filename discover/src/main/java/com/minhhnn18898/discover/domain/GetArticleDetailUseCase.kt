package com.minhhnn18898.discover.domain

import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.discover.data.model.Article
import com.minhhnn18898.discover.data.repo.DiscoverRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetArticleDetailUseCase @Inject constructor(private val discoverRepository: DiscoverRepository): UseCase<GetArticleDetailUseCase.Param, Flow<Result<Article?>>>() {

    override fun run(params: Param): Flow<Result<Article?>> = flow {
        emit(Result.Loading)
        val result = discoverRepository.getArticle(params.id)
        emit(Result.Success(result))
    }.catch {
        emit(Result.Error(it))
    }

    data class Param(val id: String)
}