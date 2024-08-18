package com.minhhnn18898.discover.domain

import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.discover.data.repo.DiscoverRepository
import com.minhhnn18898.discover.data.model.Article
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetListArticlesDiscovery @Inject constructor(private val discoverRepository: DiscoverRepository): UseCase<Unit, Flow<Result<List<Article>>>>() {

    override fun run(params: Unit): Flow<Result<List<Article>>> = flow {
        emit(Result.Loading)
        val result = discoverRepository.getArticles()
        emit(Result.Success(result))
    }.catch {
        emit(Result.Error(it))
    }

}