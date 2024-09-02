package com.minhhnn18898.ui_components.base_components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.minhhnn18898.ui_components.R

@Composable
fun CreateNewDefaultButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    DefaultCtaButton(
        text = text,
        iconRes = R.drawable.add_24,
        onClick = onClick,
        modifier = modifier
    )
}