package com.minhhnn18898.ui_components.base_components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.minhhnn18898.ui_components.R

@Composable
fun DefaultErrorView(modifier: Modifier = Modifier) {
    Box(modifier = modifier
        .fillMaxWidth()
        .wrapContentSize()
        .padding(16.dp)) {

        Image(
            painter = painterResource(id = R.drawable.default_error_illus),
            contentDescription = "",
            modifier = modifier
                .width(200.dp)
                .height(200.dp))
    }
}