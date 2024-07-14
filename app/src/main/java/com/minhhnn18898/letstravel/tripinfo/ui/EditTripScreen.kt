package com.minhhnn18898.letstravel.tripinfo.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.minhhnn18898.letstravel.R
import com.minhhnn18898.letstravel.app.AppViewModelProvider
import com.minhhnn18898.letstravel.baseuicomponent.InputTextRow
import com.minhhnn18898.letstravel.ui.theme.typography

@Composable
fun EditTripScreen(
    modifier: Modifier = Modifier,
    viewModel: EditTripViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {

        InputTextRow(
            iconRes = R.drawable.trip_24,
            label = stringResource(id = R.string.trip_name),
            inputText = viewModel.tripTitle,
            onTextChanged = {
                viewModel.onTripTitleUpdated(it)
            })

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text =  stringResource(id = R.string.choose_cover),
            style = typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(8.dp))
        DefaultCoverCollectionGrid(
            listCoverDefault = viewModel.listCoverDefault,
            onItemClick = {
                viewModel.onDefaultCoverSelected(it)
            }
        )
    }
}



@Composable
fun DefaultCoverCollectionGrid(
    listCoverDefault: List<EditTripViewModel.DefaultCoverUI>,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyHorizontalGrid(
        rows = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.height(216.dp)
    ) {
        items(listCoverDefault) { item ->
            DefaultCoverCollectionCard(item, onItemClick, modifier)
        }
    }
}

@Composable
fun DefaultCoverCollectionCard(
    coverUIElement: EditTripViewModel.DefaultCoverUI,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        onClick = {
            onClick.invoke(coverUIElement.coverId)
        },
        modifier = modifier
            .height(100.dp)
            .width(200.dp)
    ) {
        Image(
            painter = painterResource(coverUIElement.resId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )

        val isShowChecked = coverUIElement.isSelected

        AnimatedVisibility(
            visible = isShowChecked,
            enter = fadeIn()
        ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f)))
        }

        AnimatedVisibility(
            visible = isShowChecked,
            enter = scaleIn(),
            exit = scaleOut()
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(36.dp),
                    painter = painterResource(R.drawable.priority_24),
                    contentDescription = "",
                    tint = Color.White
                )
            }
        }
    }
}
