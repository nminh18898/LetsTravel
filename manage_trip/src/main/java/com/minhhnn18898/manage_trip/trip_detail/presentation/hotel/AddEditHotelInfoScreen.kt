package com.minhhnn18898.manage_trip.trip_detail.presentation.hotel

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.minhhnn18898.app_navigation.appbarstate.AppBarActionsState
import com.minhhnn18898.core.utils.StringUtils
import com.minhhnn18898.manage_trip.R
import com.minhhnn18898.manage_trip.trip_detail.presentation.flight.DatePickerWithDialog
import com.minhhnn18898.ui_components.base_components.DeleteConfirmationDialog
import com.minhhnn18898.ui_components.base_components.InputPriceRow
import com.minhhnn18898.ui_components.base_components.InputTextRow
import com.minhhnn18898.ui_components.base_components.ProgressDialog
import com.minhhnn18898.ui_components.base_components.TopMessageBar
import com.minhhnn18898.ui_components.theme.typography
import com.minhhnn18898.core.R.string as CommonStringRes
import com.minhhnn18898.ui_components.R.drawable as CommonDrawableRes

@Composable
fun AddEditHotelInfoScreen(
    onComposedTopBarActions: (AppBarActionsState) -> Unit,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddEditHotelInfoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = true) {
        onComposedTopBarActions(
            AppBarActionsState(
                actions = {
                    if(uiState.canDelete) {
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

    LaunchedEffect(uiState.isDeleted) {
        if(uiState.isDeleted) {
            navigateUp()
        }
    }

    LaunchedEffect(uiState.isUpdated) {
        if(uiState.isUpdated) {
            navigateUp()
        }
    }

    LaunchedEffect(uiState.isCreated) {
        if(uiState.isCreated) {
            navigateUp()
        }
    }

    val defaultModifier = Modifier.padding(horizontal = 16.dp)

    Column(
        modifier = modifier
            .padding(vertical = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        EditHotelInfo(
            uiState = uiState.hotelUiState,
            onHotelNameUpdated = viewModel::onHotelNameUpdated,
            onAddressNameUpdated = viewModel::onAddressUpdated,
            onPricesUpdated = viewModel::onPricesUpdated,
            onCheckInDateUpdated = viewModel::onCheckInDateUpdated,
            onCheckOutDateUpdated = viewModel::onCheckOutDateUpdated,
            modifier = defaultModifier
        )
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
fun EditHotelInfo(
    uiState: HotelUiState,
    onHotelNameUpdated: (String) -> Unit,
    onAddressNameUpdated: (String) -> Unit,
    onPricesUpdated: (String) -> Unit,
    onCheckInDateUpdated: (Long?) -> Unit,
    onCheckOutDateUpdated: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {

    Column(modifier = modifier) {
        InputTextRow(
            iconRes = R.drawable.home_24,
            label = "${stringResource(id = R.string.hotel_name)} ${StringUtils.getRequiredFieldIndicator()}",
            inputText = uiState.hotelName,
            onTextChanged = onHotelNameUpdated
        )

        Spacer(modifier = Modifier.height(8.dp))

        InputTextRow(
            iconRes = R.drawable.home_pin_24,
            label = stringResource(id = CommonStringRes.address),
            inputText = uiState.address,
            onTextChanged = onAddressNameUpdated
        )

        Spacer(modifier = Modifier.height(8.dp))

        InputPriceRow(
            iconRes = R.drawable.payments_24,
            label = stringResource(id = CommonStringRes.prices),
            inputText = uiState.prices,
            onTextChanged = onPricesUpdated,
        )

        Spacer(modifier = Modifier.height(16.dp))

        InputDateTime(
            titleRes = CommonStringRes.check_in_date,
            date = uiState.checkInDate,
            onDateSelected = onCheckInDateUpdated,
        )

        Spacer(modifier = Modifier.height(8.dp))

        InputDateTime(
            titleRes = CommonStringRes.check_out_date,
            date = uiState.checkOutDate,
            onDateSelected = onCheckOutDateUpdated,
        )
    }
}

@Composable
fun InputDateTime(
    @StringRes titleRes: Int,
    date: Long?,
    onDateSelected: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {

        Text(
            text =  "${stringResource(id = titleRes)} ${StringUtils.getRequiredFieldIndicator()}",
            style = typography.bodyMedium,
            color = MaterialTheme.colorScheme.tertiary,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(4.dp))

        DatePickerWithDialog(
            date = date,
            onDateSelected = onDateSelected
        )
    }
}

private fun getMessageError(context: Context, errorType: AddEditHotelInfoViewModel.ErrorType): String {
    return when(errorType) {
        AddEditHotelInfoViewModel.ErrorType.ERROR_MESSAGE_CAN_NOT_ADD_HOTEL_INFO -> StringUtils.getString(context, R.string.error_can_not_create_hotel_info)
        AddEditHotelInfoViewModel.ErrorType.ERROR_MESSAGE_STAY_DURATION_IS_NOT_VALID -> StringUtils.getString(context, R.string.error_hotel_stay_duration_is_invalid)
        AddEditHotelInfoViewModel.ErrorType.ERROR_MESSAGE_CAN_NOT_LOAD_HOTEL_INFO -> StringUtils.getString(context, R.string.error_can_not_load_hotel_info)
        AddEditHotelInfoViewModel.ErrorType.ERROR_MESSAGE_CAN_NOT_UPDATE_HOTEL_INFO -> StringUtils.getString(context, R.string.error_can_not_update_hotel_info)
        AddEditHotelInfoViewModel.ErrorType.ERROR_MESSAGE_CAN_NOT_DELETE_HOTEL_INFO -> StringUtils.getString(context, R.string.error_can_not_delete_hotel_info)
        else -> ""
    }
}

private fun AddEditHotelInfoViewModel.ErrorType.isShow(): Boolean {
    return this != AddEditHotelInfoViewModel.ErrorType.ERROR_MESSAGE_NONE
}