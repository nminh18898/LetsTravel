package com.minhhnn18898.letstravel.tripdetail.presentation.activity

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.minhhnn18898.app_navigation.appbarstate.AppBarActionsState
import com.minhhnn18898.letstravel.R
import com.minhhnn18898.letstravel.tripdetail.presentation.flight.DatePickerWithDialog
import com.minhhnn18898.letstravel.tripdetail.presentation.flight.TimePickerWithDialog
import com.minhhnn18898.ui_components.base_components.InputTextRow
import com.minhhnn18898.ui_components.theme.typography
import com.minhhnn18898.core.R.string as CommonStringRes
import com.minhhnn18898.ui_components.R.drawable as CommonDrawableRes

@Composable
fun EditTripActivityScreen(
    onComposedTopBarActions: (AppBarActionsState) -> Unit,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditTripActivityViewModel = hiltViewModel()
) {

    LaunchedEffect(key1 = true) {
        onComposedTopBarActions(
            AppBarActionsState(
                actions = {
                }
            )
        )
    }

    val defaultModifier = Modifier.padding(horizontal = 16.dp)

    val context = LocalContext.current
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, flag)
                viewModel.onPhotoUpdated(uri.toString())
            }
        }
    )

    Column(
        Modifier.verticalScroll(rememberScrollState())
    ) {
        ActivityPhoto(
            photoPath = viewModel.uiState.value.photo,
            onClick = {
                imagePicker.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        InputBasicActivityInfo(
            name = viewModel.uiState.value.name,
            onNameUpdated = viewModel::onNameUpdated,
            description = viewModel.uiState.value.description,
            onDescriptionUpdated = viewModel::onDescriptionUpdated,
            prices = viewModel.uiState.value.prices,
            onPricesUpdated = viewModel::onPricesUpdated,
            modifier = defaultModifier
        )

        Spacer(modifier = Modifier.height(16.dp))

        InputDateTime(
            titleRes = CommonStringRes.schedule,
            date = viewModel.uiState.value.date,
            onDateSelected = viewModel::onDateUpdated,
            timeFrom = viewModel.uiState.value.timeFrom,
            onTimeFromSelected = viewModel::onTimeFromUpdated,
            timeTo = viewModel.uiState.value.timeTo,
            onTimeToSelected = viewModel::onTimeToUpdated,
            modifier = defaultModifier
        )
    }

}

@Composable
private fun InputBasicActivityInfo(
    name: String,
    onNameUpdated: (String) -> Unit,
    description: String,
    onDescriptionUpdated: (String) -> Unit,
    prices: String,
    onPricesUpdated: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        InputTextRow(
            iconRes = R.drawable.tour_24,
            label = stringResource(id = R.string.activity_name),
            inputText = name,
            onTextChanged = onNameUpdated
        )

        Spacer(modifier = Modifier.height(16.dp))

        InputTextRow(
            iconRes = R.drawable.nordic_walking_24,
            label = stringResource(id = CommonStringRes.information),
            inputText = description,
            onTextChanged = onDescriptionUpdated
        )

        Spacer(modifier = Modifier.height(16.dp))

        InputTextRow(
            iconRes = R.drawable.payments_24,
            label = stringResource(id = CommonStringRes.prices),
            inputText = prices,
            onTextChanged = onPricesUpdated
        )
    }
}


@Composable
fun InputDateTime(
    @StringRes titleRes: Int,
    date: Long?,
    onDateSelected: (Long?) -> Unit,
    timeFrom: Pair<Int, Int>,
    onTimeFromSelected: (Pair<Int, Int>) -> Unit,
    timeTo: Pair<Int, Int>,
    onTimeToSelected: (Pair<Int, Int>) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {

        Text(
            text = stringResource(id = titleRes),
            style = typography.bodyMedium,
            color = MaterialTheme.colorScheme.tertiary,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(4.dp))

        DatePickerWithDialog(
            date = date,
            onDateSelected = onDateSelected
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TimePickerWithDialog(timeFrom, onTimeFromSelected)

            Icon(
                modifier = Modifier.size(32.dp),
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary
            )

            TimePickerWithDialog(timeTo, onTimeToSelected)
        }
    }
}

@Composable
private fun ActivityPhoto(
    photoPath: String,
    onClick: () -> Unit,
) {

    if(photoPath.isEmpty()) {
        Box(modifier = Modifier
            .clickable {
                onClick()
            }
        ) {
            Image(
                painter = painterResource(R.drawable.default_activity_photo),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f)
            )

           ChangePhotoButton(
               onClick = onClick,
               modifier = Modifier
                   .align(Alignment.BottomEnd)
           )
        }

    } else {
        AsyncImage(
            model = photoPath,
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.5f)
                .clickable {
                    onClick()
                },
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = CommonDrawableRes.image_placeholder),
            error = painterResource(id = CommonDrawableRes.empty_image_bg)
        )
    }
}

@Composable
private fun ChangePhotoButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .background(
                color = Color.Black.copy(alpha = 0.5f),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = CommonStringRes.change_photo),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            color = Color.White
        )

        Spacer(modifier = Modifier.width(4.dp))

        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(id = CommonDrawableRes.edit_24),
            contentDescription = "",
            tint = Color.White
        )
    }
}