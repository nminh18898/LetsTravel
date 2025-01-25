package com.minhhnn18898.manage_trip.trip_detail.presentation.memories_tab

import com.minhhnn18898.manage_trip.trip_detail.data.model.memories.TripPhotoInfo

data class PhotoItemUiState(
    val photoId: Long,
    val uri: String,
    val width: Int,
    val height: Int
)

fun TripPhotoInfo.toPhotoItemUiState(): PhotoItemUiState {
    return PhotoItemUiState(
        photoId = photoId,
        uri = photoUri,
        width = width,
        height = height
    )
}