package com.minhhnn18898.manage_trip.trip_detail.presentation.memories_tab

import android.content.Context
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.minhhnn18898.architecture.ui.UiState
import com.minhhnn18898.core.utils.StringUtils
import com.minhhnn18898.manage_trip.R
import com.minhhnn18898.ui_components.base_components.DefaultEmptyView
import com.minhhnn18898.ui_components.base_components.TopMessageBar
import com.minhhnn18898.ui_components.error_view.DefaultErrorView
import com.minhhnn18898.ui_components.theme.typography

fun LazyListScope.renderMemoriesTabScreen(
    photoInfoContentState: UiState<List<PhotoItemUiState>>,
    memoriesTabMainUiState: MemoriesTabMainUiState,
    photoFrameOptionsUiState: List<PhotoFrameUiState>,
    onClickAddPhoto: () -> Unit,
    onClickRemovePhoto: (Long) -> Unit,
    onClickViewPhoto: (String) -> Unit,
    onChangePhotoFrameLayout: (Int) -> Unit
) {
    if(memoriesTabMainUiState.showError.isShow()) {
        item {
            TopMessageBar(
                shown = memoriesTabMainUiState.showError.isShow(),
                text = getMessageError(LocalContext.current, memoriesTabMainUiState.showError)
            )
        }
    }

    when(photoInfoContentState) {
        is UiState.Loading -> {
            item {
                GridPhotoSectionLoading()
            }
        }

        is UiState.Error -> {
            item {
                DefaultErrorView()
            }
        }

        is UiState.Success -> {
            renderPhotoContent(
                photoInfoUiState = photoInfoContentState.data,
                photoFrameOptionsUiState = photoFrameOptionsUiState,
                onClickAddPhoto = onClickAddPhoto,
                onClickRemovePhoto = onClickRemovePhoto,
                onClickViewPhoto = onClickViewPhoto,
                onChangePhotoFrameLayout = onChangePhotoFrameLayout
            )
        }
    }
}

private fun LazyListScope.renderPhotoContent(
    photoInfoUiState: List<PhotoItemUiState>,
    photoFrameOptionsUiState: List<PhotoFrameUiState>,
    onClickAddPhoto: () -> Unit,
    onClickRemovePhoto: (Long) -> Unit,
    onClickViewPhoto: (String) -> Unit,
    onChangePhotoFrameLayout: (Int) -> Unit
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
        item {
            ChangePhotoFrameCta(
                photoFrameOptionsUiState = photoFrameOptionsUiState,
                onChangePhotoFrameLayout = onChangePhotoFrameLayout
            )
        }

        renderPhotoGrid(
            photoInfoUiState = photoInfoUiState,
            onClickRemovePhoto = onClickRemovePhoto,
            onClickViewPhoto = onClickViewPhoto
        )

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

@Composable
private fun ChangePhotoFrameCta(
    photoFrameOptionsUiState: List<PhotoFrameUiState>,
    onChangePhotoFrameLayout: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var displaySelectionSheet by rememberSaveable { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.TopEnd,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = modifier
                .padding(horizontal = 16.dp)
                .clickable {
                    displaySelectionSheet = true
                },
            verticalAlignment = Alignment.CenterVertically) {

            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.wall_art_24),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.tertiary
            )

            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = stringResource(id = R.string.change_photo_frame),
                style = typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    if(displaySelectionSheet) {
        PhotoFrameOptionsSheet(
            photoFrameOptionsUiState = photoFrameOptionsUiState,
            onSelected = {
                onChangePhotoFrameLayout(it)
                displaySelectionSheet = false
            },
            onDismissSheet = {
                displaySelectionSheet = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhotoFrameOptionsSheet(
    photoFrameOptionsUiState: List<PhotoFrameUiState>,
    onSelected: (Int) -> Unit,
    onDismissSheet: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissSheet
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                photoFrameOptionsUiState.forEach { frameItem ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            onSelected(frameItem.photoFrameType)
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(if (frameItem.isSelected) Color.Black.copy(alpha = 0.6f) else Color.Unspecified),
                            contentAlignment = Alignment.Center
                        ) {
                            if(frameItem.backgroundRes != null) {
                                Image(
                                    painter = painterResource(id = frameItem.backgroundRes),
                                    contentDescription = "",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            if(frameItem.decorationRes != null) {
                                Image(
                                    painter = painterResource(id = frameItem.decorationRes),
                                    contentDescription = "",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            if(frameItem.isSelected) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.6f))
                                )

                                Icon(
                                    modifier = Modifier.size(24.dp),
                                    painter = painterResource(R.drawable.priority_24),
                                    contentDescription = "",
                                    tint = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(id = frameItem.photoFrameNameRes),
                            style = typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.renderPhotoGrid(
    photoInfoUiState: List<PhotoItemUiState>,
    onClickRemovePhoto: (Long) -> Unit,
    onClickViewPhoto: (String) -> Unit,
) {
    val rowItems = photoInfoUiState.chunked(3)
    items(rowItems) { row ->
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        val padding = 8.dp
        val itemWidth = (screenWidth - padding * 4) / 3

        val haptics = LocalHapticFeedback.current
        var contextMenuPhotoId by rememberSaveable { mutableStateOf<Long?>(null) }

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
                        .combinedClickable(
                            onClick = {
                                onClickViewPhoto(photoItemUiState.uri)
                            },
                            onLongClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                contextMenuPhotoId = photoItemUiState.photoId
                            }
                        )
                ) {

                    if(photoItemUiState.backgroundRes != null) {
                        Image(
                            painter = painterResource(id = photoItemUiState.backgroundRes),
                            contentDescription = "",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

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

                    if(photoItemUiState.decorationRes != null) {
                        Image(
                            painter = painterResource(id = photoItemUiState.decorationRes),
                            contentDescription = "",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        if (contextMenuPhotoId != null) {
            PhotoActionsSheet(
                photoId = contextMenuPhotoId ?: 0L,
                onRemovePhoto = {
                    onClickRemovePhoto(it)
                    contextMenuPhotoId = null
                },
                onDismissSheet = {
                    contextMenuPhotoId = null
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhotoActionsSheet(
    photoId: Long,
    onRemovePhoto: (Long) -> Unit,
    onDismissSheet: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissSheet
    ) {
        ListItem(
            headlineContent = { Text("Remove") },
            leadingContent = {
                Icon(Icons.Default.Delete, null)
            },
            modifier = Modifier.clickable {
                onRemovePhoto(photoId)
            }
        )
    }
}

private fun getMessageError(context: Context, errorType: MemoriesTabController.ErrorType): String {
    return when(errorType) {
        MemoriesTabController.ErrorType.ERROR_CAN_NOT_CHANGE_FRAME_LAYOUT -> StringUtils.getString(context, R.string.error_can_not_change_photo_frame)
        else -> ""
    }
}

private fun MemoriesTabController.ErrorType.isShow(): Boolean {
    return this != MemoriesTabController.ErrorType.ERROR_MESSAGE_NONE
}

@Composable
private fun GridPhotoSectionLoading() {
    val infiniteTransition = rememberInfiniteTransition(label = "infinite loading")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
                0.7f at 500
            },
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Column(
        modifier = Modifier.padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repeat(4) {
            GridPhotoSkeletonRow(alpha = alpha)
        }
    }
}

@Composable
private fun GridPhotoSkeletonRow(alpha: Float) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val padding = 8.dp
    val itemWidth = (screenWidth - padding * 4) / 3

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(3) {
            Box(
                modifier = Modifier
                    .size(itemWidth)
                    .background(Color.LightGray.copy(alpha = alpha))
            )
        }
    }
}
