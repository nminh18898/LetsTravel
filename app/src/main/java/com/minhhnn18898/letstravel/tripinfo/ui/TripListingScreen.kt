package com.minhhnn18898.letstravel.tripinfo.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.lifecycle.viewmodel.compose.viewModel
import com.minhhnn18898.letstravel.R
import com.minhhnn18898.letstravel.app.AppViewModelProvider
import com.minhhnn18898.letstravel.baseuicomponent.RoundedPolygonShape
import com.minhhnn18898.letstravel.ui.theme.typography

@Composable
fun TripListingScreen(
    onClickEmptyView: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TripListingViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    Column(
        modifier = modifier
    ) {
        viewModel.contentState.let { contentState ->
            TripListingScreenSection(icon = R.drawable.your_trips_24, title = R.string.your_trips) {
                if (contentState.isContentLoading()) {
                    ContentLoadingView(modifier)
                }

                if (contentState.hasResult()) {
                    val items = contentState.getResult()

                    if (items.isEmpty()) {
                        EmptyTripView(onClick = onClickEmptyView)
                    } else {
                        ContentListTripItem(modifier = modifier, listTripItemDisplay = items)
                    }
                }
            }
        }
    }
}

@Composable
private fun ContentLoadingView(modifier: Modifier) {
    Box(modifier = modifier
        .padding(vertical = 16.dp)
        .wrapContentHeight()
        .fillMaxWidth(),
        contentAlignment = Alignment.Center) {

        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun ContentListTripItem(
    modifier: Modifier,
    listTripItemDisplay: List<TripListingViewModel.TripItemDisplay>
) {
    LazyColumn(modifier = modifier.padding(start = 16.dp, end = 16.dp)) {
        items(listTripItemDisplay) { itemDisplay ->
            TripItemView(modifier = Modifier, itemDisplay = itemDisplay)
        }
    }
}

@Composable
private fun TripItemView(
    modifier: Modifier,
    itemDisplay: TripListingViewModel.TripItemDisplay
) {
    val hexagon = remember {
        RoundedPolygon(
            4,
            rounding = CornerRounding(0.15f)
        )
    }
    val thumbClip = remember(hexagon) {
        RoundedPolygonShape(polygon = hexagon)
    }

    val roundCornerBgShape = RoundedCornerShape(
        topStartPercent = 0,
        topEndPercent = 50,
        bottomStartPercent = 0,
        bottomEndPercent = 50,
    )

    val thumbSize = 100
    val titleBoxMargin = thumbSize * -0.5f
    val titleTextMargin = (thumbSize * 0.5f) + 12

    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .height(thumbSize.dp)) {
        val (thumbConstraint, titleConstraint) = createRefs()

        Box(
            modifier = Modifier
                .clip(thumbClip)
                .width(thumbSize.dp)
                .zIndex(2f)
                .constrainAs(thumbConstraint) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)

                    height = Dimension.fillToConstraints
                }
                .border(
                    shape = thumbClip,
                    border = BorderStroke(4.dp, MaterialTheme.colorScheme.surfaceContainerLowest)
                )
        ) {
            Image(
                painter = painterResource(itemDisplay.defaultCoverRes),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .graphicsLayer {
                        this.shape = thumbClip
                        this.clip = true
                    }
                    .size(thumbSize.dp)
            )
        }

        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    shape = roundCornerBgShape
                )
                .constrainAs(titleConstraint) {
                    centerVerticallyTo(parent)
                    start.linkTo(thumbConstraint.end, margin = titleBoxMargin.dp)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)

                    height = Dimension.fillToConstraints
                    width = Dimension.fillToConstraints
                },
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp)
                    .padding(end = 20.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerLowest,
                        shape = roundCornerBgShape
                    )
            )

            Text(
                modifier = Modifier
                    .padding(start = titleTextMargin.dp),
                text = itemDisplay.tripName,
                style = typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TripListingScreenSection(
    @DrawableRes icon: Int,
    @StringRes title: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)) {
            Icon(
                painter = painterResource(icon),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(title),
                style = typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1
            )
        }

        content()
    }
}

@Composable
private fun EmptyTripView(
    onClick: () -> Unit
) {
    val stroke = Stroke(width = 6f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
    )
    val color = MaterialTheme.colorScheme.primary

    Box(
        Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(horizontal = 24.dp, vertical = 4.dp)
            .drawBehind {
                drawRoundRect(color = color, style = stroke, cornerRadius = CornerRadius(16.dp.toPx()))
            }
            .clickable {
                onClick.invoke()
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(R.drawable.edit_note_24),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.secondary
            )

            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(R.string.create_your_first_trip),
                style = typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1
            )
        }
    }

}

private fun TripListingViewModel.ContentState.isContentLoading(): Boolean = this is TripListingViewModel.ContentLoading
private fun TripListingViewModel.ContentState.hasResult(): Boolean = this is TripListingViewModel.ContentResult
private fun TripListingViewModel.ContentState.getResult(): List<TripListingViewModel.TripItemDisplay> = (this as? TripListingViewModel.ContentResult)?.listTripItem ?: emptyList()
private fun TripListingViewModel.ContentState.hasError(): Boolean = this is TripListingViewModel.ContentError