package com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.mange_bill

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.minhhnn18898.core.utils.StringUtils
import com.minhhnn18898.manage_trip.R
import com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.main.MemberInfoSelectionUiState
import com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.main.MemberInfoUiState
import com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.main.ReceiptPayerInfoUiState
import com.minhhnn18898.ui_components.base_components.CreateNewDefaultButton
import com.minhhnn18898.ui_components.base_components.DeleteConfirmationDialog
import com.minhhnn18898.ui_components.base_components.NumberCommaTransformation
import com.minhhnn18898.ui_components.base_components.drawWithoutRect
import com.minhhnn18898.ui_components.theme.LetsTravelTheme
import com.minhhnn18898.ui_components.theme.typography

@Composable
fun ManageReceiptView(
    modifier: Modifier = Modifier,
    viewModel: ManageReceiptViewModel = hiltViewModel()
) {
    val receiptInfoUiState by viewModel.receiptInfoUiState.collectAsStateWithLifecycle()
    val receiptUiState = receiptInfoUiState.receiptUiState
    val receiptSplittingUiState by viewModel.receiptSplittingUiState.collectAsStateWithLifecycle()
    val addMoreMemberUiState by viewModel.manageMembersUiState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        BillInfo(
            receiptName = receiptUiState.name,
            onReceiptNameUpdate = viewModel::onNameUpdated,
            formattedDate = receiptUiState.formattedDate,
            date = receiptUiState.dateCreated,
            onDateUpdated = viewModel::onDateUpdated,
            time = receiptUiState.timeCreated,
            onTimeUpdated = viewModel::onTimeUpdated,
            receiptPrice = receiptUiState.prices,
            onReceiptPriceUpdate = viewModel::onPricesUpdated,
            receiptDescription = receiptUiState.description,
            onReceiptDescriptionUpdate = viewModel::onDescriptionUpdated,
            receiptOwner = receiptInfoUiState.receiptOwner,
            receiptOwnerMemberSelection = receiptInfoUiState.updateReceiptOwnerUiState.listMemberReceiptOwnerSelection,
            onSelectReceiptOwnerMember = viewModel::onSelectNewReceiptOwner,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        BillSplitSectionDescription()

        Spacer(modifier = Modifier.height(8.dp))

        BillSplitOptions(
            selectedSplittingMode = receiptSplittingUiState.splittingMode,
            onOptionSelected = viewModel::onUpdateReceiptSplittingOption
        )

        Spacer(modifier = Modifier.height(8.dp))

        AddNewMemberButton(
            items = addMoreMemberUiState.listMembers,
            onSelectMember = viewModel::onAddMemberToReceipt
        )

        Spacer(modifier = Modifier.height(20.dp))

        ReceiptPayersInfoView(
            listPayersInfo = receiptSplittingUiState.payers,
            enableChangeDueAmount = receiptSplittingUiState.splittingMode.canChangeAmount(),
            onChangeDueAmount = viewModel::onUpdateCustomAmount,
            onRemoveMemberFromReceipt = viewModel::onRemoveMemberFromReceipt
        )
    }
}

@Composable
private fun BillInfo(
    receiptName: String,
    onReceiptNameUpdate: (String) -> Unit,
    formattedDate: String,
    date: Long?,
    onDateUpdated: (Long?) -> Unit,
    time: Pair<Int, Int>,
    onTimeUpdated: (Pair<Int, Int>) -> Unit,
    receiptPrice: String,
    onReceiptPriceUpdate: (String) -> Unit,
    receiptDescription: String,
    onReceiptDescriptionUpdate: (String) -> Unit,
    receiptOwner: MemberInfoUiState?,
    receiptOwnerMemberSelection: List<MemberInfoSelectionUiState>,
    onSelectReceiptOwnerMember: (MemberInfoUiState) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BillHeaderIcon()

            Spacer(modifier = Modifier.height(4.dp))

            BillNameAndDate(
                receiptName = receiptName,
                onReceiptNameUpdate = onReceiptNameUpdate,
                formattedDate = formattedDate,
                date = date,
                onDateUpdated = onDateUpdated,
                time = time,
                onTimeUpdated = onTimeUpdated,
            )

            Spacer(modifier = Modifier.height(16.dp))

            BillPriceAndDescription(
                receiptPrice = receiptPrice,
                onReceiptPriceUpdate = onReceiptPriceUpdate,
                receiptDescription = receiptDescription,
                onReceiptDescriptionUpdate = onReceiptDescriptionUpdate,
                modifier = modifier
            )

            Spacer(modifier = Modifier.height(20.dp))

            PaidByBadge(
                receiptOwner = receiptOwner,
                listMember = receiptOwnerMemberSelection,
                onSelectReceiptOwnerMember = onSelectReceiptOwnerMember
            )
        }
    }
}

@Composable
private fun BillHeaderIcon() {
    Box(modifier = Modifier
        .size(48.dp)
        .clip(CircleShape)
        .background(color = Color(0xB300AB41)),
        contentAlignment = Alignment.Center
    ) {

        Icon(
            modifier = Modifier.size(28.dp),
            painter = painterResource(id = R.drawable.receipt_long_24),
            contentDescription = "",
            tint = Color.White
        )
    }
}

@Composable
private fun BillNameAndDate(
    receiptName: String,
    onReceiptNameUpdate: (String) -> Unit,
    formattedDate: String,
    date: Long?,
    onDateUpdated: (Long?) -> Unit,
    time: Pair<Int, Int>,
    onTimeUpdated: (Pair<Int, Int>) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialogOptionsChangeDateOrTime by remember { mutableStateOf(false) }
    var showDatePickerDialog by remember { mutableStateOf(false) }
    var showTimePickerDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            singleLine = true,
            modifier = modifier.fillMaxWidth(),
            value = receiptName,
            onValueChange = onReceiptNameUpdate,
            label = { Text(stringResource(com.minhhnn18898.core.R.string.receipt_name)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.receipt_24),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = formattedDate,
                style = typography.titleSmall,
                color = MaterialTheme.colorScheme.secondary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                modifier = Modifier.clickable {
                    showDialogOptionsChangeDateOrTime = true
                },
                painter = painterResource(id = R.drawable.edit_calendar_24),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.secondary
            )
        }

    }

    if(showDialogOptionsChangeDateOrTime) {
        SelectDateTimeOptionsDialog(
            onChangedDateSelected = {
                showDialogOptionsChangeDateOrTime = false
                showDatePickerDialog = true
            },
            onChangedTimeSelected = {
                showDialogOptionsChangeDateOrTime = false
                showTimePickerDialog = true
            },
            onDismissRequest = {
                showDialogOptionsChangeDateOrTime = false
            }
        )
    }

    if (showDatePickerDialog) {
        DatePickerWithDialog(
            date = date,
            onDateSelected = {
                onDateUpdated(it)
                showDatePickerDialog = false
            },
            onDismissRequest = {
                showDatePickerDialog = false
            }
        )
    }

    if(showTimePickerDialog) {
        TimePickerWithDialog(
            time = time,
            onTimeSelected = {
                onTimeUpdated(it)
                showTimePickerDialog = false
            },
            onDismissRequest = {
                showTimePickerDialog = false
            }
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerWithDialog(
    date: Long?,
    onDateSelected: (Long?) -> Unit,
    onDismissRequest: () -> Unit
) {
    val dateState = rememberDatePickerState(
        initialSelectedDateMillis = date
    )

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected.invoke(dateState.selectedDateMillis)
                }
            ) {
                Text(text = stringResource(id = com.minhhnn18898.core.R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(
                    text = stringResource(id = com.minhhnn18898.core.R.string.cancel),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerWithDialog(
    time: Pair<Int, Int>,
    onTimeSelected: (Pair<Int, Int>) -> Unit,
    onDismissRequest: () -> Unit
) {

    val timeState = rememberTimePickerState(
        initialHour = time.first,
        initialMinute = time.second
    )

    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
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

                    TextButton(onClick = onDismissRequest) {
                        Text(
                            text = stringResource(id = com.minhhnn18898.core.R.string.cancel),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    TextButton(
                        onClick = {
                            onTimeSelected(Pair(timeState.hour, timeState.minute))
                        }
                    ) {
                        Text(text = stringResource(id = com.minhhnn18898.core.R.string.confirm))
                    }
                }
            }
        }
    }
}

@Composable
private fun BillPriceAndDescription(
    receiptPrice: String,
    onReceiptPriceUpdate: (String) -> Unit,
    receiptDescription: String,
    onReceiptDescriptionUpdate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "${stringResource(id = R.string.pricing_and_details_of_the_receipt)}:",
            style = typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )

        TextField(
            value = receiptPrice,
            onValueChange = onReceiptPriceUpdate,
            modifier = modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = com.minhhnn18898.ui_components.R.drawable.attach_money_24),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.secondary
                )
            },
            placeholder = {
                Text(
                    text= "00.00",
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
            ),
            visualTransformation = NumberCommaTransformation()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = receiptDescription,
            onValueChange = onReceiptDescriptionUpdate,
            modifier = modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.text_snippet_24),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.secondary
                )
            },
            placeholder = {
                Text(
                    text= "${stringResource(id = R.string.add_description_here)}...",
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                    style = typography.bodyMedium,
                )
            }
        )
    }
}

@Composable
private fun PaidByBadge(
    receiptOwner: MemberInfoUiState?,
    listMember: List<MemberInfoSelectionUiState>,
    onSelectReceiptOwnerMember: (MemberInfoUiState) -> Unit,
) {
    val color = Color(0xFF00AB41)

    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopEnd
    ) {
        Row(
            modifier = Modifier
                .border(
                    width = 2.dp,
                    shape = RoundedCornerShape(corner = CornerSize(16.dp)),
                    color = color
                )
                .clickable {
                    if (listMember.isNotEmpty()) {
                        expanded = true
                    }
                }
                .padding(vertical = 4.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.paid_by),
                style = typography.titleSmall,
                color = color
            )

            Spacer(modifier = Modifier.width(8.dp))

            if(receiptOwner != null) {
                Image(
                    painter = painterResource(receiptOwner.avatarRes),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                )
            } else {
                Image(
                    painter = painterResource(com.minhhnn18898.ui_components.R.drawable.person_24),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(color)
                        .padding(2.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(id = com.minhhnn18898.ui_components.R.drawable.arrow_drop_down_24),
                contentDescription = "",
                tint = color
            )
        }
    }

    if(expanded) {
        SelectMemberDialog(
            items = listMember,
            onItemSelected = {
                onSelectReceiptOwnerMember(it)
                expanded = false
            },
            onDismissRequest = {
                expanded = false
            }
        )
    }

}

@Composable
fun BillSplitSectionDescription() {
    Text(
        text = stringResource(id = R.string.splitting_mode_option_prompt),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.secondary
    )
}

@Composable
fun BillSplitOptions(
    selectedSplittingMode: ManageReceiptViewModel.SplittingMode,
    onOptionSelected: (ManageReceiptViewModel.SplittingMode)-> Unit,
) {
    val radioOptions = listOf(
        ManageReceiptViewModel.SplittingMode.EVENLY,
        ManageReceiptViewModel.SplittingMode.CUSTOM,
        ManageReceiptViewModel.SplittingMode.NO_SPLIT
    )

    Column(Modifier.selectableGroup()) {
        radioOptions.forEach { option ->
            val isSelected = option == selectedSplittingMode

            Row(
                Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .selectable(
                        selected = isSelected,
                        onClick = { onOptionSelected(option) },
                        role = Role.RadioButton
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = null,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.primary,
                        unselectedColor = MaterialTheme.colorScheme.secondary
                    )
                )
                Text(
                    text = getSplittingOptionsText(context = LocalContext.current, splittingMode = option),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp),
                    color = if(isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
private fun ReceiptPayersInfoView(
    listPayersInfo: List<ReceiptPayerInfoUiState>,
    enableChangeDueAmount: Boolean,
    onChangeDueAmount: (memberId: Long, newAmount: String) -> Unit,
    onRemoveMemberFromReceipt: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        listPayersInfo.forEach {
            MemberPaymentItemView(
                payerInfo = it,
                enableChangeDueAmount = enableChangeDueAmount,
                onChangeDueAmount = onChangeDueAmount,
                onRemoveMemberFromReceipt = onRemoveMemberFromReceipt
            )
        }
    }
}

@Composable
private fun MemberPaymentItemView(
    payerInfo: ReceiptPayerInfoUiState,
    enableChangeDueAmount: Boolean,
    onChangeDueAmount: (memberId: Long, newAmount: String) -> Unit,
    onRemoveMemberFromReceipt: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialogConfirmRemoveMember by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        var textCoordinates by remember { mutableStateOf<Rect?>(null) }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawWithoutRect(textCoordinates)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    enabled = enableChangeDueAmount,
                    value = payerInfo.payAmount,
                    onValueChange = {
                        onChangeDueAmount(payerInfo.memberInfo.memberId, it)
                    },
                    modifier = modifier
                        .fillMaxWidth()
                        .weight(1f),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = com.minhhnn18898.ui_components.R.drawable.attach_money_24),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    },
                    placeholder = {
                        Text(
                            text= "00.00",
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent,
                    ),
                    visualTransformation = NumberCommaTransformation()
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        showDialogConfirmRemoveMember = true
                    },
                    content = {
                        Icon(
                            painter = painterResource(id = R.drawable.receipt_long_off_24),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                )
            }
        }

        Row(
            modifier = Modifier
                .offset(x = 20.dp, y = (-12).dp)
                .onGloballyPositioned {
                    textCoordinates = it.boundsInParent()
                }
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(payerInfo.memberInfo.avatarRes),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = payerInfo.memberInfo.memberName,
                style = typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
        }
    }


    if(showDialogConfirmRemoveMember) {
        DeleteConfirmationDialog(
            title = stringResource(id = R.string.remove_member_from_receipt_dialog_title, payerInfo.memberInfo.memberName),
            description = stringResource(id = R.string.remove_member_from_receipt_dialog_content),
            onConfirmation = {
                showDialogConfirmRemoveMember = false
                onRemoveMemberFromReceipt(payerInfo.memberInfo.memberId)
            },
            onDismissRequest = {
                showDialogConfirmRemoveMember = false
            }
        )
    }
}

@Composable
private fun AddNewMemberButton(
    items: List<MemberInfoSelectionUiState>,
    onSelectMember: (MemberInfoUiState) -> Unit,
) {
    var showDialogSelectMember by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopEnd) {
        CreateNewDefaultButton(
            text = stringResource(id = R.string.add_member),
            onClick = {
                showDialogSelectMember = true
            }
        )
    }

    if(showDialogSelectMember) {
        SelectMemberDialog(
            items = items,
            onItemSelected = {
                onSelectMember(it)
                showDialogSelectMember = false
            },
            onDismissRequest = {
                showDialogSelectMember = false
            }
        )
    }
}

@Composable
private fun SelectMemberDialog(
    items: List<MemberInfoSelectionUiState>,
    selectedIndex: Int = -1,
    onItemSelected: (MemberInfoUiState) -> Unit,
    onDismissRequest: () -> Unit,
    drawItem: @Composable (MemberInfoSelectionUiState) -> Unit = { item ->
        SelectMemberDialogItem(
            memberName = item.memberInfo.memberName,
            memberAvatar = item.memberInfo.avatarRes,
            selected = item.isSelected,
            onClick = {
                onItemSelected(item.memberInfo)
            },
        )
    },
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        LetsTravelTheme {
            Surface(
                shape = RoundedCornerShape(12.dp),
            ) {
                val listState = rememberLazyListState()
                if (selectedIndex > -1) {
                    LaunchedEffect("ScrollToSelected") {
                        listState.scrollToItem(index = selectedIndex)
                    }
                }

                LazyColumn(modifier = Modifier.fillMaxWidth(), state = listState) {
                    itemsIndexed(items) { index, item ->
                        drawItem(item)

                        if (index < items.lastIndex) {
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SelectMemberDialogItem(
    memberName: String,
    @DrawableRes memberAvatar: Int,
    selected: Boolean,
    onClick: () -> Unit,
) {

    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
        Row(
            modifier = Modifier
                .clickable(!selected) { onClick() }
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Image(
                    painter = painterResource(memberAvatar),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = memberName,
                    style = typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
            }

            if(selected) {
                Icon(
                    painter = painterResource(id = R.drawable.check_circle_24),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun SelectDateTimeOptionsDialog(
    onChangedDateSelected: () -> Unit,
    onChangedTimeSelected: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        LetsTravelTheme {
            Surface(
                shape = RoundedCornerShape(12.dp),
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onChangedDateSelected() },
                        text = stringResource(id = com.minhhnn18898.core.R.string.change_date),
                        style = typography.titleSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onChangedTimeSelected() },
                        text = stringResource(id = com.minhhnn18898.core.R.string.change_time),
                        style = typography.titleSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

private fun getSplittingOptionsText(context: Context, splittingMode: ManageReceiptViewModel.SplittingMode): String {
    return when(splittingMode) {
        ManageReceiptViewModel.SplittingMode.EVENLY -> StringUtils.getString(context, R.string.splitting_mode_option_evenly)
        ManageReceiptViewModel.SplittingMode.CUSTOM -> StringUtils.getString(context, R.string.splitting_mode_option_custom)
        ManageReceiptViewModel.SplittingMode.NO_SPLIT -> StringUtils.getString(context, R.string.splitting_mode_option_no_split)
    }
}