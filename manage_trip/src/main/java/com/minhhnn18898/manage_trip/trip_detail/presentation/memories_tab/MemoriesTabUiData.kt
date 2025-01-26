package com.minhhnn18898.manage_trip.trip_detail.presentation.memories_tab

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.minhhnn18898.manage_trip.R
import com.minhhnn18898.manage_trip.trip_detail.data.model.memories.TripPhotoInfo

data class PhotoItemUiState(
    val photoId: Long,
    val uri: String,
    val width: Int,
    val height: Int,
    @DrawableRes val backgroundRes: Int? = null,
    @DrawableRes val decorationRes: Int? = null
)

fun TripPhotoInfo.toPhotoItemUiState(
    backgroundRes: Int? = null,
    decorationRes: Int? = null
): PhotoItemUiState {
    return PhotoItemUiState(
        photoId = photoId,
        uri = photoUri,
        width = width,
        height = height,
        backgroundRes = backgroundRes,
        decorationRes = decorationRes
    )
}

data class PhotoFrameUiState(
    val photoFrameType: Int = 0,
    val isSelected: Boolean = false,
    @StringRes val photoFrameNameRes: Int = R.string.empty_string,
    @DrawableRes val backgroundRes: Int? = null,
    @DrawableRes val decorationRes: Int? = null
)