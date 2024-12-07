package com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.manage_member

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.minhhnn18898.architecture.ui.UiState
import com.minhhnn18898.core.utils.StringUtils
import com.minhhnn18898.manage_trip.R
import com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.MemberInfoUiState
import com.minhhnn18898.ui_components.base_components.DefaultEmptyView
import com.minhhnn18898.ui_components.base_components.ErrorTextView
import com.minhhnn18898.ui_components.base_components.ProgressDialog
import com.minhhnn18898.ui_components.base_components.TopMessageBar
import com.minhhnn18898.ui_components.theme.typography


@Composable
fun BillSplitManageMemberView(
    modifier: Modifier = Modifier,
    viewModel: BillSplitManageMemberViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val memberInfoUiState by viewModel.memberInfoContentState.collectAsStateWithLifecycle()

    val updateMemberUiState = uiState.updateMemberUiState
    val deleteMemberUiState = uiState.deleteMemberUiState

    Column(modifier = modifier.padding(horizontal = 20.dp)) {
        AddNewMemberView(
            onClickAddButton = viewModel::onAddNewMember,
            memberName = uiState.newMemberName,
            onMemberNameChanged = viewModel::onNewMemberNameUpdated,
            allowAddMember = uiState.allowAddNewMember
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 20.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)
        )

        ManageMemberSection(
            memberInfoUiState = memberInfoUiState,
            onClickEditMember = { memberId, memberName, memberAvatar ->
                viewModel.onClickUpdateMemberInfo(memberId, memberName, memberAvatar)
            },
            onClickDeleteMember = { memberId, memberName, memberAvatar ->
                viewModel.onClickDeleteMember(memberId, memberName, memberAvatar)
            },
            onClickChangeDefaultBillOwner = {
                viewModel.onUpdateDefaultBillOwner(it)
            }
        )
    }

    AnimatedVisibility(visible = updateMemberUiState != null) {
        if(updateMemberUiState != null) {
            ChangeMemberNameConfirmationDialog(
                memberId = updateMemberUiState.memberId,
                currentMemberName = updateMemberUiState.currentMemberName,
                newMemberName = updateMemberUiState.newMemberName,
                memberAvatar = updateMemberUiState.memberAvatar,
                onMemberNameChanged = viewModel::onExistingMemberNameUpdated,
                allowUpdate = updateMemberUiState.allowUpdateExistingMemberInfo,
                onDismissRequest = viewModel::onCancelUpdateMemberInfo,
                onConfirmation = {
                    viewModel.onUpdateMemberInfo(it)
                }
            )
        }
    }

    AnimatedVisibility(visible = deleteMemberUiState != null) {
        if(deleteMemberUiState != null) {
            RemoveMemberConfirmationDialog(
                memberId = deleteMemberUiState.memberId,
                memberName = deleteMemberUiState.memberName,
                memberAvatar = deleteMemberUiState.memberAvatar,
                onDismissRequest = viewModel::onCancelDeleteMember,
                onConfirmation = {
                    viewModel.onDeleteMemberConfirmed(it)
                }
            )
        }
    }

    AnimatedVisibility(uiState.isLoading) {
        ProgressDialog()
    }

    TopMessageBar(
        shown = uiState.showError.isShow(),
        text = getMessageError(LocalContext.current, uiState.showError)
    )
}

@Composable
private fun AddNewMemberView(
    onClickAddButton: () -> Unit,
    memberName: String,
    onMemberNameChanged: (String) -> Unit,
    allowAddMember: Boolean,
    modifier: Modifier = Modifier
) {

    Row(modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedTextField(
            value = memberName,
            onValueChange = onMemberNameChanged,
            label = { Text(stringResource(id = R.string.add_new_member)) },
            modifier = Modifier.weight(1f),
            leadingIcon = {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(R.drawable.person_add_24),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )

        Spacer(modifier = Modifier.width(8.dp))

        val addButtonColor = MaterialTheme.colorScheme.tertiary
        IconButton(
            onClick = onClickAddButton,
            content = {
                Icon(
                    modifier = Modifier.size(36.dp),
                    painter = painterResource(id = R.drawable.check_circle_24),
                    contentDescription = "",
                    tint = if(allowAddMember) addButtonColor else addButtonColor.copy(alpha = 0.5f)
                )
            },
            enabled = allowAddMember
        )
    }
}

@Composable
private fun ManageMemberSection(
    memberInfoUiState: UiState<List<MemberInfoUiState>>,
    onClickEditMember: (memberId: Long, memberName: String, memberAvatar: Int) -> Unit,
    onClickDeleteMember: (memberId: Long, memberName: String, memberAvatar: Int) -> Unit,
    onClickChangeDefaultBillOwner: (Long) -> Unit,
    modifier: Modifier = Modifier) {

    when (memberInfoUiState) {
        is UiState.Loading -> {
            ManageMemberSectionLoading()
        }

        is UiState.Error -> {
            ErrorTextView(
                error = stringResource(id = com.minhhnn18898.core.R.string.can_not_load_info),
                modifier = modifier
            )
        }

        is UiState.Success -> {
            val listMember = memberInfoUiState.data

            if(listMember.isEmpty()) {
                DefaultEmptyView(
                    text = stringResource(id = R.string.add_member_to_your_trip),
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth(),
                    onClick = {
                        // no-op
                    }
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(memberInfoUiState.data) {
                        ManageMemberItemView(
                            memberInfo = it,
                            onClickEditMember = onClickEditMember,
                            onClickDeleteMember = onClickDeleteMember,
                            onClickChangeDefaultBillOwner = onClickChangeDefaultBillOwner
                        )
                    }
                }
            }
        }
    }

}

@Composable
private fun ManageMemberSectionLoading(modifier: Modifier = Modifier) {
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

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(10) {
            ManageMemberSkeletonItem(
                alpha = alpha,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun ManageMemberSkeletonItem(alpha: Float, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = alpha))
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .height(20.dp)
                    .width(160.dp)
                    .background(Color.LightGray.copy(alpha = alpha))
            )
        }

        Row(
            modifier = Modifier
                .padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = alpha))
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = alpha))
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = alpha))
            )
        }
    }
}

@Composable
private fun ManageMemberItemView(
    memberInfo: MemberInfoUiState,
    onClickEditMember: (memberId: Long, memberName: String, memberAvatar: Int) -> Unit,
    onClickDeleteMember: (memberId: Long, memberName: String, memberAvatar: Int) -> Unit,
    onClickChangeDefaultBillOwner: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(memberInfo.avatarRes),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = memberInfo.memberName,
                style = typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
        }

        Row(
            modifier = Modifier
                .padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                   onClickDeleteMember(memberInfo.memberId, memberInfo.memberName, memberInfo.avatarRes)
                },
                content = {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.person_remove_24),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            )

            IconButton(
                onClick = {
                    onClickEditMember(memberInfo.memberId, memberInfo.memberName, memberInfo.avatarRes)
                },
                content = {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.edit_document_24),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            )

            IconButton(
                onClick = {
                    onClickChangeDefaultBillOwner(memberInfo.memberId)
                },
                content = {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.editor_choice_24),
                        contentDescription = "",
                        tint = if(memberInfo.isDefaultBillOwner) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
                    )
                }
            )
        }
    }
}

@Composable
private fun RemoveMemberConfirmationDialog(
    memberId: Long,
    memberName: String,
    @DrawableRes memberAvatar: Int,
    onDismissRequest: () -> Unit,
    onConfirmation: (Long) -> Unit
) {
    AlertDialog(
        icon = {
            Image(
                painter = painterResource(memberAvatar),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
        },
        title = {
            Text(text = stringResource(id = R.string.delete_member_dialog_title, memberName))
        },
        text = {
            Text(text = stringResource(id = R.string.delete_member_dialog_content, memberName))
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation(memberId)
                }
            ) {
                Text(
                    text = stringResource(id = com.minhhnn18898.core.R.string.confirm),
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(
                    text = stringResource(id = com.minhhnn18898.core.R.string.cancel),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    )
}

@Composable
private fun ChangeMemberNameConfirmationDialog(
    memberId: Long,
    @DrawableRes memberAvatar: Int,
    currentMemberName: String,
    newMemberName: String,
    onMemberNameChanged: (String) -> Unit,
    allowUpdate: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmation: (Long) -> Unit
) {
    Dialog(
        onDismissRequest = { onDismissRequest() }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(375.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(memberAvatar),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(id = R.string.update_member_dialog_title),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = typography.titleLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(id = R.string.update_member_dialog_content, currentMemberName),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = newMemberName,
                    onValueChange = onMemberNameChanged,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {

                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text(
                            text = stringResource(id = com.minhhnn18898.core.R.string.cancel),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    TextButton(
                        onClick = { onConfirmation(memberId) },
                        modifier = Modifier.padding(8.dp),
                        enabled = allowUpdate
                    ) {
                        Text(
                            text = stringResource(id = com.minhhnn18898.core.R.string.confirm),
                            color = if(allowUpdate) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

private fun BillSplitManageMemberViewModel.ErrorType.isShow(): Boolean {
    return this != BillSplitManageMemberViewModel.ErrorType.ERROR_MESSAGE_NONE
}

private fun getMessageError(context: Context, errorType: BillSplitManageMemberViewModel.ErrorType): String {
    return when(errorType) {
        BillSplitManageMemberViewModel.ErrorType.ERROR_MESSAGE_LIMIT_MEMBER_REACH -> StringUtils.getString(context, R.string.manage_member_error_limit_reach)
        BillSplitManageMemberViewModel.ErrorType.ERROR_MESSAGE_CAN_NOT_ADD_MEMBER -> StringUtils.getString(context, R.string.error_can_not_add_member)
        BillSplitManageMemberViewModel.ErrorType.ERROR_MESSAGE_CAN_NOT_UPDATE_MEMBER -> StringUtils.getString(context, R.string.error_can_not_update_member_info)
        BillSplitManageMemberViewModel.ErrorType.ERROR_MESSAGE_CAN_NOT_DELETE_MEMBER -> StringUtils.getString(context, R.string.error_can_not_delete_member)
        else -> ""
    }
}