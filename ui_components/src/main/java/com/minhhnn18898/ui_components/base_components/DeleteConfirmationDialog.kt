package com.minhhnn18898.ui_components.base_components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.minhhnn18898.core.utils.isNotBlankOrEmpty
import com.minhhnn18898.ui_components.theme.typography
import com.minhhnn18898.core.R.string as CommonStringRes

@Composable
fun DeleteConfirmationDialog(
    onConfirmation: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    DeleteConfirmationDialog(
        description = stringResource(id = CommonStringRes.delete_confirmation_prompt),
        onConfirmation = onConfirmation,
        onDismissRequest = onDismissRequest
    )
}

@Composable
fun DeleteConfirmationDialog(
    title: String = "",
    description: String = "",
    onConfirmation: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val titleComposable: @Composable (() -> Unit) = {
        Text(
            text = title,
            style = typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
    }

    val descriptionComposable: @Composable (() -> Unit) = {
        Text(
            text = description,
            style = typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }

    AlertDialog(
        title = if(title.isNotBlankOrEmpty()) { titleComposable } else null,
        text = if(description.isNotBlankOrEmpty()) descriptionComposable else null,
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
                    stringResource(id = CommonStringRes.confirm),
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
                    stringResource(id = CommonStringRes.cancel),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    )
}