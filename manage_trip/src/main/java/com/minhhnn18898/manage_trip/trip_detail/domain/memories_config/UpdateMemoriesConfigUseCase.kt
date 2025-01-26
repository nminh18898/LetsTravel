package com.minhhnn18898.manage_trip.trip_detail.domain.memories_config

import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.manage_trip.trip_detail.data.model.memories.TripMemoriesConfigInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.memories.MemoriesConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateMemoriesConfigUseCase @Inject constructor(private val repository: MemoriesConfigRepository) {

    fun execute(tripId: Long, memoriesConfig: TripMemoriesConfigInfo): Flow<Result<Long>> = flow {
        emit(Result.Loading)
        val result = repository.upsertConfig(
            tripId = tripId,
            config = memoriesConfig
        )
        emit(Result.Success(result))
    }.catch {
        emit(Result.Error(it))
    }
}