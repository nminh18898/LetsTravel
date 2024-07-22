package com.minhhnn18898.letstravel.tripdetail.ui.flight

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.minhhnn18898.letstravel.R
import com.minhhnn18898.letstravel.app.AppViewModelProvider
import com.minhhnn18898.letstravel.app.navigation.AppBarActionsState
import com.minhhnn18898.letstravel.baseuicomponent.InputTextRow
import com.minhhnn18898.letstravel.baseuicomponent.ProgressDialog
import com.minhhnn18898.letstravel.baseuicomponent.TopMessageBar
import com.minhhnn18898.letstravel.ui.theme.typography
import com.minhhnn18898.letstravel.utils.DateTimeUtils
import com.minhhnn18898.letstravel.utils.StringUtils

@Composable
fun EditFlightInfoScreen(
    onComposedTopBarActions: (AppBarActionsState) -> Unit,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditFlightInfoViewModel = viewModel(factory = AppViewModelProvider.Factory)) {

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
                if(event == EditFlightInfoViewModel.Event.CloseScreen) {
                    navigateUp.invoke()
                }
            }
        }
    }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        val defaultModifier = Modifier.padding(horizontal = 16.dp)

        SectionHeader(
            iconRes = R.drawable.travel_24,
            label = stringResource(id = R.string.flight_info),
            modifier = defaultModifier
        )
        Spacer(modifier = Modifier.height(8.dp))
        EditFlightInfo(
            modifier = defaultModifier,
            viewModel = viewModel
        )
    }

    AnimatedVisibility(viewModel.onShowSaveLoadingState) {
        ProgressDialog()
    }

    TopMessageBar(
        shown = viewModel.errorType.isShow(),
        text = getMessageError(LocalContext.current, viewModel.errorType)
    )
}

@Composable
fun EditFlightInfo(
    viewModel: EditFlightInfoViewModel,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {

        InputTextRow(
            iconRes = R.drawable.airplane_ticket_24,
            label = "${stringResource(id = R.string.flight_number)} ${StringUtils.getRequiredFieldIndicator()}",
            inputText = viewModel.flightNumber,
            onTextChanged = {
                viewModel.onFlightNumberUpdated(it)
            })

        Spacer(modifier = Modifier.height(8.dp))
        InputTextRow(
            iconRes = R.drawable.airlines_24,
            label = stringResource(id = R.string.airlines),
            inputText = viewModel.operatedAirlines,
            onTextChanged = {
                viewModel.onAirlinesUpdated(it)
            })

        Spacer(modifier = Modifier.height(8.dp))
        InputTextRow(
            iconRes = R.drawable.payments_24,
            label = stringResource(id = R.string.prices),
            inputText = viewModel.prices,
            onTextChanged = {
                viewModel.onPricesUpdated(it)
            })

        Spacer(modifier = Modifier.height(12.dp))
        InputFlightDateTimeSection(
            dateFrom = viewModel.getFlightDate(EditFlightInfoViewModel.ItineraryType.DEPARTURE),
            onDateFromSelected = {
                viewModel.onFlightDateUpdated(EditFlightInfoViewModel.ItineraryType.DEPARTURE, it)
            },
            timeFrom = viewModel.getFlightTime(EditFlightInfoViewModel.ItineraryType.DEPARTURE),
            onTimeFromSelected = {
                viewModel.onFlightTimeUpdated(EditFlightInfoViewModel.ItineraryType.DEPARTURE, it)
            },

            dateTo = viewModel.getFlightDate(EditFlightInfoViewModel.ItineraryType.ARRIVAL),
            onDateToSelected = {
                viewModel.onFlightDateUpdated(EditFlightInfoViewModel.ItineraryType.ARRIVAL, it)
            },
            timeTo = viewModel.getFlightTime(EditFlightInfoViewModel.ItineraryType.ARRIVAL),
            onTimeToSelected = {
                viewModel.onFlightTimeUpdated(EditFlightInfoViewModel.ItineraryType.ARRIVAL, it)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))
        val departureType = EditFlightInfoViewModel.ItineraryType.DEPARTURE
        EditAirportInfo(
            label = stringResource(id = R.string.from),
            airportCode = viewModel.getAirportCode(departureType),
            onAirportCodeUpdated = {
                viewModel.onAirportCodeUpdated(departureType, it)
            },
            airportName = viewModel.getAirportName(departureType),
            onAirportNameUpdated = {
                viewModel.onAirportNameUpdated(departureType, it)
            },
            city = viewModel.getAirportCity(departureType),
            onCityUpdated = {
                viewModel.onAirportCityUpdated(departureType, it)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))
        val arrivalType = EditFlightInfoViewModel.ItineraryType.ARRIVAL
        EditAirportInfo(
            label = stringResource(id = R.string.to),
            airportCode = viewModel.getAirportCode(arrivalType),
            onAirportCodeUpdated = {
                viewModel.onAirportCodeUpdated(arrivalType, it)
            },
            airportName = viewModel.getAirportName(arrivalType),
            onAirportNameUpdated = {
                viewModel.onAirportNameUpdated(arrivalType, it)
            },
            city = viewModel.getAirportCity(arrivalType),
            onCityUpdated = {
                viewModel.onAirportCityUpdated(arrivalType, it)
            }
        )
    }
}

@Composable
fun EditAirportInfo(
    label: String,
    airportCode: String,
    onAirportCodeUpdated: (String) -> Unit,
    airportName: String,
    onAirportNameUpdated: (String) -> Unit,
    city: String,
    onCityUpdated: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text =  label,
                style = typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1
            )

            InputTextRow(
                iconRes = R.drawable.pin_24,
                label = "${stringResource(id = R.string.airport_code)} ${StringUtils.getRequiredFieldIndicator()}",
                inputText = airportCode,
                onTextChanged = onAirportCodeUpdated
            )

            Spacer(modifier = Modifier.height(8.dp))
            InputTextRow(
                iconRes = R.drawable.id_card_24,
                label = stringResource(id = R.string.airport_name),
                inputText = airportName,
                onTextChanged = onAirportNameUpdated
            )

            Spacer(modifier = Modifier.height(8.dp))
            InputTextRow(
                iconRes = R.drawable.apartment_24,
                label = stringResource(id = R.string.city),
                inputText = city,
                onTextChanged = onCityUpdated
            )
        }
    }
}

@Composable
fun SectionHeader(
    iconRes: Int,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {

            Icon(
                modifier = Modifier.size(40.dp),
                painter = painterResource(id = iconRes),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text =  label,
                style = typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1
            )
        }
    }
}

@Composable
fun InputFlightDateTimeSection(
    dateFrom: Long?,
    onDateFromSelected: (Long?) -> Unit,
    timeFrom: Pair<Int, Int>,
    onTimeFromSelected: (Pair<Int, Int>) -> Unit,
    dateTo: Long?,
    onDateToSelected: (Long?) -> Unit,
    timeTo: Pair<Int, Int>,
    onTimeToSelected: (Pair<Int, Int>) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InputFlightTime(
            iconRes = R.drawable.flight_takeoff_24,
            titleRes = R.string.departure_time,
            date = dateFrom,
            onDateSelected = onDateFromSelected,
            time = timeFrom,
            onTimeSelected = onTimeFromSelected
        )

        Spacer(modifier = Modifier.height(16.dp))
        
        InputFlightTime(
            iconRes = R.drawable.flight_land_24,
            titleRes = R.string.arrival_time,
            date = dateTo,
            onDateSelected = onDateToSelected,
            time = timeTo,
            onTimeSelected = onTimeToSelected
        )
    }
}

@Composable
fun InputFlightTime(
    @DrawableRes iconRes: Int,
    @StringRes titleRes: Int,
    date: Long?,
    onDateSelected: (Long?) -> Unit,
    time: Pair<Int, Int>,
    onTimeSelected: (Pair<Int, Int>) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = iconRes),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.tertiary
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text =  "${stringResource(id = titleRes)} ${StringUtils.getRequiredFieldIndicator()}",
                style = typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary,
                maxLines = 1
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            DatePickerWithDialog(
                date = date,
                onDateSelected = onDateSelected
            )
            TimePickerWithDialog(time, onTimeSelected)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerWithDialog(
    date: Long?,
    onDateSelected: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    val state = rememberDatePickerState(
        initialSelectedDateMillis = date
    )

    val millisToLocalDate = date?.let {
        DateTimeUtils().convertMillisToLocalDate(it)
    }

    val dateToString = millisToLocalDate?.let {
        DateTimeUtils().dateToString(millisToLocalDate)
    } ?: stringResource(id = R.string.choose_date)

    var showDialog by remember { mutableStateOf(false) }

    TextButton(
        onClick = {
            showDialog = true
        },
        modifier = modifier
    ) {
        Icon(
            modifier = Modifier.size(32.dp),
            painter = painterResource(R.drawable.edit_calendar_24),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = dateToString,
            style = MaterialTheme.typography.bodyLarge
        )
    }

    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        onDateSelected.invoke(state.selectedDateMillis)
                    }
                ) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text(
                        text = stringResource(id = R.string.cancel),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        ) {
            DatePicker(
                state = state,
                showModeToggle = true
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerWithDialog(
    time: Pair<Int, Int>,
    onTimeSelected: (Pair<Int, Int>) -> Unit,
    modifier: Modifier = Modifier
) {

    var showDialog by remember { mutableStateOf(false) }

    val timeState = rememberTimePickerState(
        initialHour = time.first,
        initialMinute = time.second
    )

    TextButton(
        onClick = {
            showDialog = true
        },
        modifier = modifier
    ) {
        Icon(
            modifier = Modifier.size(32.dp),
            painter = painterResource(R.drawable.more_time_24),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = DateTimeUtils().formatTime(time.first, time.second),
            style = MaterialTheme.typography.bodyLarge
        )
    }

    if (showDialog) {
        BasicAlertDialog(
            onDismissRequest = { showDialog = false },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 28.dp, start = 20.dp, end = 20.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    TimePicker(state = timeState)

                    Row(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .fillMaxWidth(), horizontalArrangement = Arrangement.End
                    ) {

                        TextButton(onClick = { showDialog = false }) {
                            Text(
                                text = stringResource(id = R.string.cancel),
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))

                        TextButton(onClick = {
                            showDialog = false
                            onTimeSelected.invoke(Pair(timeState.hour, timeState.minute))
                        }) {
                            Text(text = stringResource(id = R.string.confirm))
                        }
                    }
                }
            }
        }
    }
}

private fun getMessageError(context: Context, errorType: EditFlightInfoViewModel.ErrorType): String {
    return when(errorType) {
        EditFlightInfoViewModel.ErrorType.ERROR_MESSAGE_CAN_NOT_ADD_FLIGHT_INFO -> StringUtils.getString(context, R.string.error_can_not_create_trip)
        EditFlightInfoViewModel.ErrorType.ERROR_MESSAGE_FLIGHT_TIME_IS_NOT_VALID -> StringUtils.getString(context, R.string.error_flight_time_is_invalid)
        else -> ""
    }
}

private fun EditFlightInfoViewModel.ErrorType.isShow(): Boolean {
    return this != EditFlightInfoViewModel.ErrorType.ERROR_MESSAGE_NONE
}