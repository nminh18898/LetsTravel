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
import com.minhhnn18898.manage_trip.R
import com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.memberAvatarList
import com.minhhnn18898.ui_components.theme.typography

@Composable
fun BillSplitManageMemberView(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 20.dp)) {
        AddNewMemberView()

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 20.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)
        )

        ManageMemberItemView()
    }
}

@Composable
private fun AddNewMemberView(modifier: Modifier = Modifier) {
    var memberNameText by remember { mutableStateOf("") }

    Row(modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedTextField(
            value = memberNameText,
            onValueChange = {
                memberNameText = it
            },
            label = { Text("Add new member") },
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

        IconButton(
            onClick = {

            },
            content = {
                Icon(
                    modifier = Modifier.size(36.dp),
                    painter = painterResource(id = R.drawable.check_circle_24),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }
        )
    }
}

@Composable
private fun ManageMemberItemView(modifier: Modifier = Modifier) {
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
                text = "Lorem ipsum dolor sit amet,",
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