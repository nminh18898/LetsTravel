package com.minhhnn18898.manage_trip.trip_detail.presentation.memories_tab

import androidx.annotation.StringRes
import com.minhhnn18898.manage_trip.R
import com.minhhnn18898.manage_trip.trip_detail.data.repo.memories.MemoriesConfigRepository

interface MemoriesTabResourceProvider {

    fun getPhotoFrameResources(photoFrameType: Int): Pair<Int?, Int?>

    @StringRes
    fun getPhotoFrameNameRes(photoFrameType: Int): Int
}

class MemoriesTabResourceProviderImpl: MemoriesTabResourceProvider {
    private val photoFrameBackgroundRes: Map<Int, Int> = mutableMapOf(
        MemoriesConfigRepository.PHOTO_FRAME_DEFAULT to R.drawable.memories_right_photo_background,
        MemoriesConfigRepository.PHOTO_FRAME_VINTAGE to R.drawable.memories_right_photo_background,
        MemoriesConfigRepository.PHOTO_FRAME_FLOWER to R.drawable.memories_right_photo_background,
        MemoriesConfigRepository.PHOTO_FRAME_COLORFUL to R.drawable.memories_right_photo_background
    )

    private val photoFrameDecorationRes: Map<Int, Int> = mutableMapOf(
        MemoriesConfigRepository.PHOTO_FRAME_DEFAULT to R.drawable.memories_right_photo_decoration,
        MemoriesConfigRepository.PHOTO_FRAME_VINTAGE to R.drawable.memories_right_photo_decoration,
        MemoriesConfigRepository.PHOTO_FRAME_FLOWER to R.drawable.memories_right_photo_decoration,
        MemoriesConfigRepository.PHOTO_FRAME_COLORFUL to R.drawable.memories_right_photo_decoration
    )


    override fun getPhotoFrameResources(photoFrameType: Int): Pair<Int?, Int?> {
       return Pair(photoFrameBackgroundRes[photoFrameType], photoFrameDecorationRes[photoFrameType])
    }

    @StringRes
    override fun getPhotoFrameNameRes(photoFrameType: Int): Int {
        return when(photoFrameType) {
            MemoriesConfigRepository.PHOTO_FRAME_DEFAULT -> R.string.photo_frame_default_name
            MemoriesConfigRepository.PHOTO_FRAME_VINTAGE -> R.string.photo_frame_vintage_name
            MemoriesConfigRepository.PHOTO_FRAME_FLOWER -> R.string.photo_frame_flower_name
            MemoriesConfigRepository.PHOTO_FRAME_COLORFUL -> R.string.photo_frame_colorful_name
            else -> R.string.empty_string
        }
    }

}