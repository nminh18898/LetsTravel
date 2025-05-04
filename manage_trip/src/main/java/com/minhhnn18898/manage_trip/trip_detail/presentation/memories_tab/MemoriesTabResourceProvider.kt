package com.minhhnn18898.manage_trip.trip_detail.presentation.memories_tab

import androidx.annotation.StringRes
import com.minhhnn18898.manage_trip.R
import com.minhhnn18898.trip_data.repo.memories.MemoriesConfigRepository

interface MemoriesTabResourceProvider {

    fun getPhotoFrameResources(photoFrameType: Int, index: Int = 0): Pair<Int?, Int?>

    @StringRes
    fun getPhotoFrameNameRes(photoFrameType: Int): Int
}

class MemoriesTabResourceProviderImpl: MemoriesTabResourceProvider {
    private val photoFrameTypeDefault = mutableListOf(
        Pair<Int, Int?>(R.drawable.photo_frame_default_background, null)
    )

    private val photoFrameTypeFlower = mutableListOf(
        Pair<Int, Int?>(R.drawable.photo_frame_flower_blue_background, R.drawable.photo_frame_flower_blue_decoration),
        Pair<Int, Int?>(R.drawable.photo_frame_flower_red_background, R.drawable.photo_frame_flower_red_decoration),
        Pair<Int, Int?>(R.drawable.photo_frame_flower_yellow_background, R.drawable.photo_frame_flower_yellow_decoration),
        Pair<Int, Int?>(R.drawable.photo_frame_flower_purple_background, R.drawable.photo_frame_flower_purple_decoration)
    )

    private val photoFrameTypeVintage = mutableListOf(
        Pair<Int, Int?>(R.drawable.photo_frame_vintage_type_one, null),
        Pair<Int, Int?>(R.drawable.photo_frame_vintage_type_two, null)
    )

    private val photoFrameTypeColorful = mutableListOf(
        Pair<Int, Int?>(R.drawable.photo_frame_colorful_red, null),
        Pair<Int, Int?>(R.drawable.photo_frame_colorful_blue, null),
        Pair<Int, Int?>(R.drawable.photo_frame_colorful_yellow, null),
        Pair<Int, Int?>(R.drawable.photo_frame_colorful_green, null)
    )

    private val photoFrameMap: Map<Int, List<Pair<Int?, Int?>>> = mutableMapOf(
        MemoriesConfigRepository.PHOTO_FRAME_DEFAULT to photoFrameTypeDefault,
        MemoriesConfigRepository.PHOTO_FRAME_VINTAGE to photoFrameTypeVintage,
        MemoriesConfigRepository.PHOTO_FRAME_FLOWER to photoFrameTypeFlower,
        MemoriesConfigRepository.PHOTO_FRAME_COLORFUL to photoFrameTypeColorful
    )


    override fun getPhotoFrameResources(photoFrameType: Int, index: Int): Pair<Int?, Int?> {
        val photoFrame = photoFrameMap[photoFrameType] ?: return Pair(null, null)
        return photoFrame.getOrNull(index % photoFrame.size) ?: Pair(null, null)
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