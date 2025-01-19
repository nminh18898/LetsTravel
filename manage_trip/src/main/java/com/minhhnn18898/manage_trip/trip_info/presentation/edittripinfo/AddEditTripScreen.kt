package com.minhhnn18898.manage_trip.trip_info.presentation.edittripinfo

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.minhhnn18898.app_navigation.appbarstate.TopAppBarState
import com.minhhnn18898.app_navigation.destination.EditTripInfoDestination
import com.minhhnn18898.core.utils.StringUtils
import com.minhhnn18898.manage_trip.R
import com.minhhnn18898.ui_components.base_components.CreateNewDefaultButton
import com.minhhnn18898.ui_components.error_view.DefaultErrorView
import com.minhhnn18898.ui_components.base_components.DeleteConfirmationDialog
import com.minhhnn18898.ui_components.input.InputTextRow
import com.minhhnn18898.ui_components.loading_view.ProgressDialog
import com.minhhnn18898.ui_components.base_components.TopMessageBar
import com.minhhnn18898.ui_components.theme.typography
import com.minhhnn18898.core.R.string as CommonStringRes
import com.minhhnn18898.ui_components.R.drawable as CommonDrawableRes

@Composable
fun EditTripScreen(
    onComposedTopBarActions: (TopAppBarState) -> Unit,
    navigateUp: () -> Unit,
    onNavigateToTripDetailScreen: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddEditTripViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        onComposedTopBarActions(
            TopAppBarState(
                screenTitle = StringUtils.getString(context = context, id = EditTripInfoDestination.title),
                actions = {
                    if(uiState.canDeleteTrip) {
                        IconButton(
                            onClick = {
                                viewModel.onDeleteClick()
                            }
                        ) {
                            Icon(
                                painter = painterResource(CommonDrawableRes.delete_24),
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            viewModel.onSaveClick()
                        },
                        enabled = uiState.allowSaveContent
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.save_as_24),
                            contentDescription = "",
                            tint = if(uiState.allowSaveContent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(0.3f)
                        )
                    }
                }
            )
        )
    }

    LaunchedEffect(uiState.isTripDeleted) {
        if(uiState.isTripDeleted) {
            navigateUp()
        }
    }

    LaunchedEffect(uiState.isTripUpdated) {
        if(uiState.isTripUpdated) {
            navigateUp()
        }
    }

    LaunchedEffect(uiState.newCreatedTripUiState) {
        uiState.newCreatedTripUiState?.let {
            navigateUp()
            onNavigateToTripDetailScreen(it.tripId)
        }
    }

    if(uiState.isNotFound) {
        DefaultErrorView(modifier = modifier)
    } else {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            InputTextRow(
                iconRes = R.drawable.trip_24,
                label = stringResource(id = CommonStringRes.trip_name),
                inputText = uiState.tripTitle,
                onTextChanged = {
                    viewModel.onTripTitleUpdated(it)
                })

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text =  stringResource(id = CommonStringRes.choose_cover),
                style = typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(8.dp))

            DefaultCoverCollectionGrid(
                listCoverDefault = uiState.listCoverItems,
                onItemClick = {
                    viewModel.onCoverSelected(it)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            val imagePicker = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickVisualMedia(),
                onResult = { uri ->
                    uri?.let {
                        val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        context.contentResolver.takePersistableUriPermission(uri, flag)
                        viewModel.onNewPhotoPicked(uri.toString())
                    }
                }
            )

            CreateNewDefaultButton(
                text = stringResource(id = com.minhhnn18898.core.R.string.pick_your_photo),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                imagePicker.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }
        }
    }

    AnimatedVisibility(uiState.isLoading) {
        ProgressDialog()
    }

    AnimatedVisibility(uiState.isShowDeleteConfirmation) {
        DeleteConfirmationDialog(
            onConfirmation = viewModel::onDeleteConfirm,
            onDismissRequest = viewModel::onDeleteDismiss
        )
    }

    TopMessageBar(
        shown = uiState.showError.isShow(),
        text = getMessageError(LocalContext.current, uiState.showError)
    )
}

@Composable
fun DefaultCoverCollectionGrid(
    listCoverDefault: List<AddEditTripViewModel.CoverUIElement>,
    onItemClick: (AddEditTripViewModel.CoverUIElement) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyHorizontalGrid(
        rows = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.height(216.dp)
    ) {
        items(listCoverDefault) { item ->
            DefaultCoverCollectionCard(item, onItemClick, modifier)
        }
    }
}

@Composable
fun DefaultCoverCollectionCard(
    coverUIElement: AddEditTripViewModel.CoverUIElement,
    onClick: (AddEditTripViewModel.CoverUIElement) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        onClick = {
            onClick.invoke(coverUIElement)
        },
        modifier = modifier
            .height(100.dp)
            .width(200.dp)
    ) {
        if(coverUIElement is AddEditTripViewModel.DefaultCoverElement) {
            Image(
                painter = painterResource(coverUIElement.resId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )
        }
        else if(coverUIElement is AddEditTripViewModel.CustomCoverPhotoElement) {
            AsyncImage(
                model = coverUIElement.uri,
                contentDescription = "",
                contentScale = ContentScale.Crop,
                error = painterResource(id = CommonDrawableRes.empty_image_bg)
            )
        }

        val isShowChecked = coverUIElement.isSelected

        AnimatedVisibility(
            visible = isShowChecked,
            enter = fadeIn()
        ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f)))
        }

        AnimatedVisibility(
            visible = isShowChecked,
            enter = scaleIn(),
            exit = scaleOut()
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(36.dp),
                    painter = painterResource(R.drawable.priority_24),
                    contentDescription = "",
                    tint = Color.White
                )
            }
        }
    }
}

private fun getMessageError(context: Context, errorType: AddEditTripViewModel.ErrorType): String {
    return when(errorType) {
        AddEditTripViewModel.ErrorType.ERROR_MESSAGE_CAN_NOT_CREATE_TRIP_INFO -> StringUtils.getString(context, R.string.error_can_not_create_trip)
        AddEditTripViewModel.ErrorType.ERROR_MESSAGE_CAN_NOT_LOAD_TRIP_INFO -> StringUtils.getString(context, R.string.error_can_not_load_trip)
        AddEditTripViewModel.ErrorType.ERROR_MESSAGE_CAN_NOT_UPDATE_TRIP_INFO -> StringUtils.getString(context, R.string.error_can_not_update_trip)
        AddEditTripViewModel.ErrorType.ERROR_MESSAGE_CAN_NOT_DELETE_TRIP_INFO -> StringUtils.getString(context, R.string.error_can_not_delete_trip)
        else -> ""
    }
}

private fun AddEditTripViewModel.ErrorType.isShow(): Boolean {
    return this != AddEditTripViewModel.ErrorType.ERROR_MESSAGE_NONE
}