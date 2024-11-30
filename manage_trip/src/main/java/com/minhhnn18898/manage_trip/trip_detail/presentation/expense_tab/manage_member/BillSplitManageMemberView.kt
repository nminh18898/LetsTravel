package com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.manage_member

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.minhhnn18898.architecture.ui.UiState
import com.minhhnn18898.manage_trip.R
import com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.MemberInfoUiState
import com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.memberAvatarList
import com.minhhnn18898.ui_components.base_components.DefaultEmptyView
import com.minhhnn18898.ui_components.base_components.ErrorTextView
import com.minhhnn18898.ui_components.theme.typography


@Composable
fun BillSplitManageMemberView(
    modifier: Modifier = Modifier,
    viewModel: BillSplitManageMemberViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val memberInfoUiState by viewModel.memberInfoContentState.collectAsStateWithLifecycle()

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

        ManageMemberSection(memberInfoUiState)
    }
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
                LazyColumn {
                    items(memberInfoUiState.data) {
                        ManageMemberItemView(it)
                    }
                }
            }
        }
    }

}

@Composable
private fun ManageMemberSectionLoading(modifier: Modifier = Modifier) {

}

@Composable
private fun ManageMemberItemView(
    memberInfo: MemberInfoUiState,
    modifier: Modifier = Modifier
) {
    val openRemoveMemberConfirmationDialog = remember { mutableStateOf(false) }
    val openChangeMemberNameDialog = remember { mutableStateOf(false) }

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
                painter = painterResource(memberAvatarList.first()),
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
                    openRemoveMemberConfirmationDialog.value = true
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
                    openChangeMemberNameDialog.value = true
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

                },
                content = {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.editor_choice_24),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            )
        }
    }

    if(openRemoveMemberConfirmationDialog.value) {
        RemoveMemberConfirmationDialog(
            onDismissRequest = {
                openRemoveMemberConfirmationDialog.value = false
            },
            onConfirmation = {
                openRemoveMemberConfirmationDialog.value = false
            }
        )
    }

    if(openChangeMemberNameDialog.value) {
        ChangeMemberNameConfirmationDialog(
            onDismissRequest = {
                openChangeMemberNameDialog.value = false
            },
            onConfirmation = {
                openChangeMemberNameDialog.value = false
            }
        )
    }
}

@Composable
private fun RemoveMemberConfirmationDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit
) {
    AlertDialog(
        icon = {
            Image(
                painter = painterResource(memberAvatarList.first()),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
        },
        title = {
            Text(text = "Remove [member] from bill")
        },
        text = {
            Text(text = "Are you sure you want to remove [Member Name] from the bill? This will recalculate the total amount due for the remaining members.")
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
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
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit
) {
    var memberNameText by remember { mutableStateOf("") }

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
                    painter = painterResource(memberAvatarList.first()),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Change member name",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = typography.titleLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Please enter new name for [Current member name]:",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = memberNameText,
                    onValueChange = {
                        memberNameText = it
                    },
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
                        onClick = { onConfirmation() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text(
                            text = stringResource(id = com.minhhnn18898.core.R.string.confirm),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}