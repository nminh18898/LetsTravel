package com.minhhnn18898.manage_trip.trip_detail.presentation.memories_tab

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.minhhnn18898.architecture.ui.UiState
import com.minhhnn18898.manage_trip.R
import com.minhhnn18898.ui_components.base_components.DefaultEmptyView

fun LazyListScope.renderMemoriesTabScreen(
    photoInfoContentState: UiState<List<PhotoItemUiState>>,
    onClickAddPhoto: () -> Unit,
    modifier: Modifier = Modifier
) {
    when(photoInfoContentState) {
        is UiState.Loading -> {
            // Optional
        }

        is UiState.Error -> {
            // Optional
        }

        is UiState.Success -> {
            renderPhotoList(
                photoInfoUiState = photoInfoContentState.data,
                onClickAddPhoto = onClickAddPhoto
            )
        }
    }
}

private fun LazyListScope.renderPhotoList(
    photoInfoUiState: List<PhotoItemUiState>,
    onClickAddPhoto: () -> Unit,
) {
    if(photoInfoUiState.isEmpty()) {
        item {
            DefaultEmptyView(
                text = stringResource(id = R.string.add_photos_to_preserve_your_memories),
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth(),
                onClick = onClickAddPhoto
            )
        }
    } else {
        val rowItems = photoInfoUiState.chunked(3)

        items(rowItems) { row ->
            val screenWidth = LocalConfiguration.current.screenWidthDp.dp
            val padding = 8.dp
            val itemWidth = (screenWidth - padding * 4) / 3

            Row(
                modifier = Modifier.padding(horizontal = padding),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { photoItemUiState ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .width(itemWidth)
                            .height(itemWidth)
                    ) {

                        Image(
                            painter = painterResource(id = R.drawable.memories_right_photo_background),
                            contentDescription = "",
                            modifier = Modifier.fillMaxSize()
                        )

                        AsyncImage(
                            model = photoItemUiState.uri,
                            contentDescription = "",
                            modifier = Modifier
                                .width(itemWidth * 0.85f)
                                .height(itemWidth * 0.85f),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(id = com.minhhnn18898.ui_components.R.drawable.image_placeholder),
                            error = painterResource(id = com.minhhnn18898.ui_components.R.drawable.empty_image_bg)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomEnd) {

                FloatingActionButton(
                    onClick = onClickAddPhoto,
                    shape = CircleShape
                ) {
                    Icon(Icons.Filled.Add, "")
                }

            }
        }
    }
}