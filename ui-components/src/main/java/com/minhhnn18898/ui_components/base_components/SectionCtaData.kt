package com.minhhnn18898.ui_components.base_components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class SectionCtaData (
    @DrawableRes val icon: Int,
    @StringRes val title: Int,
    val onClick: () -> Unit
)