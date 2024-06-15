package com.minhhnn18898.letstravel.tripdetail

import androidx.annotation.DrawableRes
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.minhhnn18898.letstravel.R
import com.minhhnn18898.letstravel.ui.theme.typography
import com.minhhnn18898.letstravel.utils.DateTimeUtils

@Composable
fun EditFlightInfoPage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        val defaultModifier = Modifier.padding(horizontal = 16.dp)

        SectionHeader(
            iconRes = R.drawable.flight_takeoff_24,
            label = stringResource(id = R.string.flight_info),
            modifier = defaultModifier
        )
        Spacer(modifier = Modifier.height(8.dp))
        EditFlightInfo(modifier = defaultModifier)
    }
}

@Composable
fun EditFlightInfo(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {

        InputTextRow(R.drawable.airplane_ticket_24, stringResource(id = R.string.flight_number))

        Spacer(modifier = Modifier.height(8.dp))
        InputTextRow(R.drawable.airlines_24, stringResource(id = R.string.airlines))

        Spacer(modifier = Modifier.height(8.dp))
        InputTextRow(R.drawable.payments_24, stringResource(id = R.string.prices))

        Spacer(modifier = Modifier.height(12.dp))
        InputFlightDateTimeRow()

        Spacer(modifier = Modifier.height(8.dp))
        EditAirportInfo("From")

        Spacer(modifier = Modifier.height(8.dp))
        EditAirportInfo(label = "To")
    }
}

@Composable
fun EditAirportInfo(
    label: String,
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

            InputTextRow(R.drawable.pin_24, stringResource(id = R.string.airport_code))

            Spacer(modifier = Modifier.height(8.dp))
            InputTextRow(R.drawable.id_card_24, stringResource(id = R.string.airport_name))

            Spacer(modifier = Modifier.height(8.dp))
            InputTextRow(R.drawable.apartment_24, stringResource(id = R.string.city))
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
fun InputTextRow(
    @DrawableRes iconRes: Int,
    label: String,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf("") }

    Row(modifier = modifier,
        verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            leadingIcon = {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(iconRes),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            modifier = modifier.fillMaxWidth(),
            value = text,
            onValueChange = { text = it },
            label = { Text(label) }
        )
    }
}

@Composable
fun InputFlightDateTimeRow(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        DatePickerWithDialog()
        Spacer(modifier = Modifier.height(8.dp))
        InputFlightTime()
    }
}

@Composable
fun InputFlightTime(
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier,
        verticalAlignment = Alignment.CenterVertically) {
        TimePickerWithDialog()
        Icon(
            modifier = Modifier
                .size(32.dp)
                .padding(horizontal = 4.dp),
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.primary
        )
        TimePickerWithDialog()
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerWithDialog(
    modifier: Modifier = Modifier
) {
    val dateState = rememberDatePickerState()

    val millisToLocalDate = dateState.selectedDateMillis?.let {
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
                    onClick = { showDialog = false }
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
                state = dateState,
                showModeToggle = true
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerWithDialog(modifier: Modifier = Modifier) {
    var selectedHour by remember { mutableIntStateOf(0) }
    var selectedMinute by remember { mutableIntStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }
    val timeState = rememberTimePickerState(
        initialHour = selectedHour,
        initialMinute = selectedMinute
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
            text = DateTimeUtils().formatTime(timeState.hour, timeState.minute),
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
                        //.background(color = MaterialTheme.colorScheme.surface)
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
                            selectedHour = timeState.hour
                            selectedMinute = timeState.minute
                        }) {
                            Text(text = stringResource(id = R.string.confirm))
                        }
                    }
                }
            }
        }
    }
}