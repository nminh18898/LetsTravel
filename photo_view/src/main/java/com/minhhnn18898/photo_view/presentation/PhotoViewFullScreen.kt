package com.minhhnn18898.photo_view.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.minhhnn18898.ui_components.R

@Composable
fun PhotoViewFullScreen(
    url: String,
    modifier: Modifier
) {
    Box(modifier = modifier
        .fillMaxSize()
        .background(Color.Black.copy(0.9f)),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = url,
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth(),
            contentScale = ContentScale.Fit,
            placeholder = painterResource(id = R.drawable.image_placeholder),
            error = painterResource(id = R.drawable.empty_image_bg)
        )
    }
}