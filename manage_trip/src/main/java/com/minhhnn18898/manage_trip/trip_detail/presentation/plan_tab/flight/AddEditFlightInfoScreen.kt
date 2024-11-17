package com.minhhnn18898.manage_trip.trip_detail.presentation.plan_tab.flight

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.minhhnn18898.app_navigation.appbarstate.TopAppBarState
import com.minhhnn18898.app_navigation.destination.EditFlightInfoDestination
import com.minhhnn18898.core.utils.BaseDateTimeFormatterImpl
import com.minhhnn18898.core.utils.StringUtils
import com.minhhnn18898.manage_trip.R
import com.minhhnn18898.manage_trip.trip_detail.presentation.plan_tab.flight.AddEditFlightInfoViewModel.ItineraryType
import com.minhhnn18898.manage_trip.trip_detail.presentation.trip.TripDetailDateTimeFormatterImpl
import com.minhhnn18898.ui_components.base_components.DeleteConfirmationDialog
import com.minhhnn18898.ui_components.base_components.InputPriceRow
import com.minhhnn18898.ui_components.base_components.InputTextRow
import com.minhhnn18898.ui_components.base_components.ProgressDialog
import com.minhhnn18898.ui_components.base_components.TopMessageBar
import com.minhhnn18898.ui_components.theme.typography
import com.minhhnn18898.core.R.string as CommonStringRes

@Composable
fun AddEditFlightInfoScreen(
    onComposedTopBarActions: (TopAppBarState) -> Unit,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddEditFlightInfoViewModel = hiltViewModel()) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        onComposedTopBarActions(
            TopAppBarState(
                screenTitle = StringUtils.getString(context, EditFlightInfoDestination.title),
                actions = {
                    if(uiState.canDelete) {
                        IconButton(
                            onClick = {
                                viewModel.onDeleteClick()
                            }
                        ) {
                            Icon(
                                painter = painterResource(com.minhhnn18898.ui_components.R.drawable.delete_24),
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

    Column(
        modifier = modifier
            .padding(vertical = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val defaultModifier = Modifier.padding(horizontal = 16.dp)

        SectionHeader(
            iconRes = R.drawable.travel_24,
            label = stringResource(id = CommonStringRes.flight_info),
            modifier = defaultModifier
        )
        Spacer(modifier = Modifier.height(8.dp))
        EditFlightInfo(
            modifier = defaultModifier,
            uiState = uiState.flightUiState,
            onFlightNumberUpdated = viewModel::onFlightNumberUpdated,
            onAirlinesUpdated = viewModel::onAirlinesUpdated,
            onPricesUpdated = viewModel::onPricesUpdated,
            onFlightDateUpdated = viewModel::onFlightDateUpdated,
            onFlightTimeUpdated = viewModel::onFlightTimeUpdated,
            onAirportCodeUpdated = viewModel::onAirportCodeUpdated,
            onAirportCityUpdated = viewModel::onAirportCityUpdated,
            onAirportNameUpdated = viewModel::onAirportNameUpdated
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
        text = getMessageError(context, uiState.showError)
    )
}

@Composable
fun EditFlightInfo(
    uiState: FlightInfoUiState,
    onFlightNumberUpdated: (String) -> Unit,
    onAirlinesUpdated: (String) -> Unit,
    onPricesUpdated: (String) -> Unit,
    onFlightDateUpdated: (itineraryType: ItineraryType, value: Long?) -> Unit,
    onFlightTimeUpdated: (itineraryType: ItineraryType, value: Pair<Int, Int>) -> Unit,
    onAirportCodeUpdated: (ItineraryType, String) -> Unit,
    onAirportNameUpdated: (ItineraryType, String) -> Unit,
    onAirportCityUpdated: (ItineraryType, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {

        InputTextRow(
            iconRes = R.drawable.airplane_ticket_24,
            label = "${stringResource(id = CommonStringRes.flight_number)} ${StringUtils.getRequiredFieldIndicator()}",
            inputText = uiState.flightNumber,
            onTextChanged = {
                onFlightNumberUpdated(it)
            })

        Spacer(modifier = Modifier.height(8.dp))

        InputTextRow(
            iconRes = R.drawable.airlines_24,
            label = stringResource(id = CommonStringRes.airlines),
            inputText = uiState.operatedAirlines,
            onTextChanged = {
                onAirlinesUpdated(it)
            })

        Spacer(modifier = Modifier.height(8.dp))

        InputPriceRow(
            iconRes = R.drawable.payments_24,
            label = stringResource(id = CommonStringRes.prices),
            inputText = uiState.prices,
            onTextChanged = {
               onPricesUpdated(it)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        InputFlightDateTimeSection(
            dateFrom = uiState.flightDate[ItineraryType.DEPARTURE],
            onDateFromSelected = {
                onFlightDateUpdated(ItineraryType.DEPARTURE, it)
            },
            timeFrom = uiState.flightTime[ItineraryType.DEPARTURE] ?: Pair(0, 0),
            onTimeFromSelected = {
                onFlightTimeUpdated(ItineraryType.DEPARTURE, it)
            },

            dateTo = uiState.flightDate[ItineraryType.ARRIVAL],
            onDateToSelected = {
                onFlightDateUpdated(ItineraryType.ARRIVAL, it)
            },
            timeTo =  uiState.flightTime[ItineraryType.ARRIVAL] ?: Pair(0, 0),
            onTimeToSelected = {
                onFlightTimeUpdated(ItineraryType.ARRIVAL, it)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))
        val departureType = ItineraryType.DEPARTURE
        EditAirportInfo(
            label = stringResource(id = CommonStringRes.from),
            airportCode = uiState.airportCodes[departureType] ?: "",
            onAirportCodeUpdated = {
                onAirportCodeUpdated(departureType, it)
            },
            airportName = uiState.airportNames[departureType] ?: "",
            onAirportNameUpdated = {
                onAirportNameUpdated(departureType, it)
            },
            city = uiState.airportCities[departureType] ?: "",
            onCityUpdated = {
                onAirportCityUpdated(departureType, it)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))
        val arrivalType = ItineraryType.ARRIVAL
        EditAirportInfo(
            label = stringResource(id = CommonStringRes.to),
            airportCode = uiState.airportCodes[arrivalType] ?: "",
            onAirportCodeUpdated = {
                onAirportCodeUpdated(arrivalType, it)
            },
            airportName = uiState.airportNames[arrivalType] ?: "",
            onAirportNameUpdated = {
                onAirportNameUpdated(arrivalType, it)
            },
            city = uiState.airportCities[arrivalType] ?: "",
            onCityUpdated = {
                onAirportCityUpdated(arrivalType, it)
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
                label = "${stringResource(id = CommonStringRes.airport_code)} ${StringUtils.getRequiredFieldIndicator()}",
                inputText = airportCode,
                onTextChanged = onAirportCodeUpdated
            )

            Spacer(modifier = Modifier.height(8.dp))
            InputTextRow(
                iconRes = R.drawable.id_card_24,
                label = stringResource(id = CommonStringRes.airport_name),
                inputText = airportName,
                onTextChanged = onAirportNameUpdated
            )

            Spacer(modifier = Modifier.height(8.dp))
            InputTextRow(
                iconRes = R.drawable.apartment_24,
                label = stringResource(id = CommonStringRes.city),
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
            titleRes = CommonStringRes.departure_time,
            date = dateFrom,
            onDateSelected = onDateFromSelected,
            time = timeFrom,
            onTimeSelected = onTimeFromSelected
        )

        Spacer(modifier = Modifier.height(16.dp))
        
        InputFlightTime(
            iconRes = R.drawable.flight_land_24,
            titleRes = CommonStringRes.arrival_time,
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
        BaseDateTimeFormatterImpl().millisToLocalDate(it)
    }

    val dateToString = millisToLocalDate?.let {
        TripDetailDateTimeFormatterImpl(BaseDateTimeFormatterImpl()).getFormattedFlightDateString(millisToLocalDate)
    } ?: stringResource(id = CommonStringRes.choose_date)

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
                    Text(text = stringResource(id = CommonStringRes.confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text(
                        text = stringResource(id = CommonStringRes.cancel),
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
            text = BaseDateTimeFormatterImpl().formatHourMinute(time.first, time.second),
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
                                text = stringResource(id = CommonStringRes.cancel),
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))

                        TextButton(onClick = {
                            showDialog = false
                            onTimeSelected.invoke(Pair(timeState.hour, timeState.minute))
                        }) {
                            Text(text = stringResource(id = CommonStringRes.confirm))
                        }
                    }
                }
            }
        }
    }
}

private fun getMessageError(context: Context, errorType: AddEditFlightInfoViewModel.ErrorType): String {
    return when(errorType) {
        AddEditFlightInfoViewModel.ErrorType.ERROR_MESSAGE_CAN_NOT_ADD_FLIGHT_INFO -> StringUtils.getString(context, R.string.error_can_not_create_flight_info)
        AddEditFlightInfoViewModel.ErrorType.ERROR_MESSAGE_FLIGHT_TIME_IS_NOT_VALID -> StringUtils.getString(context, R.string.error_flight_time_is_invalid)
        AddEditFlightInfoViewModel.ErrorType.ERROR_MESSAGE_CAN_NOT_LOAD_FLIGHT_INFO -> StringUtils.getString(context, R.string.error_can_not_load_flight_info)
        AddEditFlightInfoViewModel.ErrorType.ERROR_MESSAGE_CAN_NOT_UPDATE_FLIGHT_INFO -> StringUtils.getString(context, R.string.error_can_not_update_flight_info)
        AddEditFlightInfoViewModel.ErrorType.ERROR_MESSAGE_CAN_NOT_DELETE_FLIGHT_INFO -> StringUtils.getString(context, R.string.error_can_not_delete_flight_info)
        else -> ""
    }
}

private fun AddEditFlightInfoViewModel.ErrorType.isShow(): Boolean {
    return this != AddEditFlightInfoViewModel.ErrorType.ERROR_MESSAGE_NONE
}