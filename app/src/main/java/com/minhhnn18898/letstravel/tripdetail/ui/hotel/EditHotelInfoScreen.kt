package com.minhhnn18898.letstravel.tripdetail.ui.hotel

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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.minhhnn18898.app_navigation.appbarstate.AppBarActionsState
import com.minhhnn18898.core.utils.StringUtils
import com.minhhnn18898.letstravel.R
import com.minhhnn18898.letstravel.tripdetail.ui.flight.DatePickerWithDialog
import com.minhhnn18898.ui_components.base_components.InputPriceRow
import com.minhhnn18898.ui_components.base_components.InputTextRow
import com.minhhnn18898.ui_components.base_components.ProgressDialog
import com.minhhnn18898.ui_components.base_components.TopMessageBar
import com.minhhnn18898.ui_components.theme.typography
import com.minhhnn18898.core.R.string as CommonStringRes

@Composable
fun EditHotelInfoScreen(
    onComposedTopBarActions: (AppBarActionsState) -> Unit,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditHotelInfoViewModel = hiltViewModel()
) {

    LaunchedEffect(key1 = true) {
        onComposedTopBarActions(
            AppBarActionsState(
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.onSaveClick()
                        },
                        enabled = viewModel.allowSaveContent
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.save_as_24),
                            contentDescription = "",
                            tint = if(viewModel.allowSaveContent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(0.3f)
                        )
                    }
                }
            )
        )
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.eventTriggerer.collect { event ->
                if(event == EditHotelInfoViewModel.Event.CloseScreen) {
                    navigateUp.invoke()
                }
            }
        }
    }

    val defaultModifier = Modifier.padding(horizontal = 16.dp)

    Column(
        modifier = modifier
            .padding(vertical = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        EditHotelInfo(
            viewModel = viewModel,
            modifier = defaultModifier
        )
    }

    AnimatedVisibility(viewModel.onShowLoadingState) {
        ProgressDialog()
    }

    TopMessageBar(
        shown = viewModel.errorType.isShow(),
        text = getMessageError(LocalContext.current, viewModel.errorType)
    )
}

@Composable
fun EditHotelInfo(
    viewModel: EditHotelInfoViewModel,
    modifier: Modifier = Modifier
) {

    val uiState by viewModel.uiState

    Column(modifier = modifier) {
        InputTextRow(
            iconRes = R.drawable.home_24,
            label = "${stringResource(id = R.string.hotel_name)} ${StringUtils.getRequiredFieldIndicator()}",
            inputText = uiState.hotelName,
            onTextChanged = viewModel::onHotelNameUpdated
        )

        Spacer(modifier = Modifier.height(8.dp))

        InputTextRow(
            iconRes = R.drawable.home_pin_24,
            label = stringResource(id = CommonStringRes.address),
            inputText = uiState.address,
            onTextChanged = viewModel::onAddressUpdated
        )

        Spacer(modifier = Modifier.height(8.dp))

        InputPriceRow(
            iconRes = R.drawable.payments_24,
            label = stringResource(id = CommonStringRes.prices),
            inputText = uiState.prices,
            onTextChanged = viewModel::onPricesUpdated,
        )

        Spacer(modifier = Modifier.height(16.dp))

        InputDateTime(
            titleRes = CommonStringRes.check_in_date,
            date = uiState.checkInDate,
            onDateSelected = viewModel::onCheckInDateUpdated,
        )

        Spacer(modifier = Modifier.height(8.dp))

        InputDateTime(
            titleRes = CommonStringRes.check_out_date,
            date = uiState.checkOutDate,
            onDateSelected = viewModel::onCheckOutDateUpdated,
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

private fun getMessageError(context: Context, errorType: EditHotelInfoViewModel.ErrorType): String {
    return when(errorType) {
        EditHotelInfoViewModel.ErrorType.ERROR_MESSAGE_CAN_NOT_ADD_HOTEL_INFO -> StringUtils.getString(context, R.string.error_can_not_create_hotel_info)
        EditHotelInfoViewModel.ErrorType.ERROR_MESSAGE_STAY_DURATION_IS_NOT_VALID -> StringUtils.getString(context, R.string.error_hotel_stay_duration_is_invalid)
        EditHotelInfoViewModel.ErrorType.ERROR_MESSAGE_CAN_NOT_LOAD_HOTEL_INFO -> StringUtils.getString(context, R.string.error_can_not_load_hotel_info)
        EditHotelInfoViewModel.ErrorType.ERROR_MESSAGE_CAN_NOT_UPDATE_HOTEL_INFO -> StringUtils.getString(context, R.string.error_can_not_update_hotel_info)
        else -> ""
    }
}

private fun EditHotelInfoViewModel.ErrorType.isShow(): Boolean {
    return this != EditHotelInfoViewModel.ErrorType.ERROR_MESSAGE_NONE
}