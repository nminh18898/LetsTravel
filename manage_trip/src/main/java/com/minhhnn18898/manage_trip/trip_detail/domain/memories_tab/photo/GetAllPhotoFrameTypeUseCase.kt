package com.minhhnn18898.manage_trip.trip_detail.domain.memories_tab.photo

import com.minhhnn18898.trip_data.repo.memories.MemoriesConfigRepository
import javax.inject.Inject


class GetAllPhotoFrameTypeUseCase @Inject constructor() {

    fun execute() : List<Int> = mutableListOf(
        MemoriesConfigRepository.PHOTO_FRAME_DEFAULT,
        MemoriesConfigRepository.PHOTO_FRAME_VINTAGE,
        MemoriesConfigRepository.PHOTO_FRAME_FLOWER,
        MemoriesConfigRepository.PHOTO_FRAME_COLORFUL
    )
}