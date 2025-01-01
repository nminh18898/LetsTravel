package com.minhhnn18898.manage_trip.trip_detail.presentation.trip

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.minhhnn18898.app_navigation.appbarstate.TopAppBarState
import com.minhhnn18898.app_navigation.destination.ExpenseTabDestination
import com.minhhnn18898.app_navigation.destination.MemoryTabDestination
import com.minhhnn18898.app_navigation.destination.TripDetailPlanTabDestination
import com.minhhnn18898.app_navigation.destination.isExpenseTab
import com.minhhnn18898.app_navigation.destination.isPlanTab
import com.minhhnn18898.core.utils.StringUtils
import com.minhhnn18898.manage_trip.navigation.TripDetailTabRow
import com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.main.renderExpenseTabScreen
import com.minhhnn18898.manage_trip.trip_detail.presentation.plan_tab.main.renderPlanTabUI
import com.minhhnn18898.manage_trip.trip_info.presentation.base.TripCustomCoverDisplay
import com.minhhnn18898.manage_trip.trip_info.presentation.base.TripDefaultCoverDisplay
import com.minhhnn18898.manage_trip.trip_info.presentation.base.UserTripDisplay
import com.minhhnn18898.ui_components.base_components.SectionCtaData
import com.minhhnn18898.ui_components.theme.typography
import com.minhhnn18898.core.R.string as CommonStringRes
import com.minhhnn18898.ui_components.R.drawable as CommonDrawableRes

@Composable
fun TripDetailScreen(
    onComposedTopBarActions: (TopAppBarState) -> Unit,
    navigateUp: () -> Unit,
    onNavigateToEditFlightInfoScreen: (tripId: Long, flightId: Long) -> Unit,
    onNavigateEditHotelScreen: (tripId: Long, flightId: Long) -> Unit,
    onNavigateToEditTripScreen: (Long) -> Unit,
    onNavigateEditTripActivityScreen: (tripId: Long, activityId: Long) -> Unit,
    onNavigateToBillSlitMemberScreen: (tripId: Long, tripName: String) -> Unit,
    onNavigateToManageBillScreen: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TripDetailScreenViewModel = hiltViewModel()
) {

    val flightContentState by viewModel.planTabUIController.flightInfoContentState.collectAsStateWithLifecycle()
    val hotelContentState by viewModel.planTabUIController.hotelInfoContentState.collectAsStateWithLifecycle()
    val activityContentState by viewModel.planTabUIController.activityInfoContentState.collectAsStateWithLifecycle()

    val expenseTabMemberContentState by viewModel.expenseTabController.memberInfoContentState.collectAsStateWithLifecycle()
    val expenseTabReceiptContentState by viewModel.expenseTabController.receiptInfoContentState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        onComposedTopBarActions(
            TopAppBarState(
                screenTitle = StringUtils.getString(context, com.minhhnn18898.core.R.string.trip_detail),
                actions = {
                    IconButton(
                        onClick = {
                            onNavigateToEditTripScreen.invoke(viewModel.tripId)
                        }
                    ) {
                        Icon(
                            painter = painterResource(CommonDrawableRes.edit_square_24),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        )
    }

    val tripInfoContentState by viewModel.tripInfoContentState.collectAsStateWithLifecycle()

    val currentTab = viewModel.currentTabSelected

    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            TripDetailHeader(
                modifier = modifier,
                uiState = tripInfoContentState,
                navigateUp = navigateUp
            )
        }

        item {
            TripDetailTabRow(
                allScreens = mutableListOf(TripDetailPlanTabDestination, ExpenseTabDestination, MemoryTabDestination),
                onTabSelected = {
                    viewModel.onChangeTab(tab = it)
                },
                currentScreen = currentTab
            )
        }

        if(currentTab.isPlanTab()) {
            renderPlanTabUI(
                flightContentState = flightContentState,
                hotelContentState = hotelContentState,
                activityContentState = activityContentState,
                budgetDisplay = viewModel.planTabUIController.budgetDisplay,
                tripId = viewModel.tripId,
                onNavigateToEditFlightInfoScreen = onNavigateToEditFlightInfoScreen,
                onNavigateEditHotelScreen = onNavigateEditHotelScreen,
                onNavigateEditTripActivityScreen = onNavigateEditTripActivityScreen
            )
        } else if(currentTab.isExpenseTab()) {
            renderExpenseTabScreen(
                memberInfoContentState = expenseTabMemberContentState,
                receiptInfoUiState = expenseTabReceiptContentState,
                onNavigateManageMemberScreen = {
                    onNavigateToBillSlitMemberScreen(viewModel.tripId, viewModel.tripName)
                },
                onNavigateManageBillScreen = {
                    onNavigateToManageBillScreen(viewModel.tripId)
                }
            )
        }
    }
}

@Composable
private fun TripDetailHeader(
    modifier: Modifier = Modifier,
    uiState: TripDetailScreenTripInfoUiState,
    navigateUp: () -> Unit) {

    if(uiState.isLoading) {
        TripDetailHeaderLoading()
    } else if(uiState.tripDisplay != null) {
        TripDetailHeaderContent(tripInfoItemDisplay = uiState.tripDisplay, modifier = modifier)
    }

    LaunchedEffect(uiState.isNotFound) {
        if(uiState.isNotFound) {
            navigateUp()
        }
    }
}

@Composable
private fun TripDetailHeaderLoading() {
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


    Box(
        modifier = Modifier
            .height(180.dp)
            .fillMaxWidth()
            .background(Color.LightGray.copy(alpha = alpha))
    )
}

@Composable
private fun TripDetailHeaderContent(
    tripInfoItemDisplay: UserTripDisplay,
    modifier: Modifier = Modifier) {

    val shape =  RoundedCornerShape(8.dp)
    val height = 180.dp

    Box(
        modifier = modifier
            .height(height)
            .fillMaxWidth()
            .background(White, shape = shape),
    ) {

        val coverDisplay = tripInfoItemDisplay.coverDisplay
        if(coverDisplay is TripDefaultCoverDisplay) {
            Image(
                painter = painterResource(coverDisplay.defaultCoverRes),
                contentDescription = stringResource(id = CommonStringRes.trip_detail_header_cover_content_desc),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
            )
        } else if(coverDisplay is TripCustomCoverDisplay) {
            AsyncImage(
                model = coverDisplay.coverPath,
                contentDescription = stringResource(id = CommonStringRes.trip_detail_header_cover_content_desc),
                contentScale = ContentScale.Crop,
                error = painterResource(id =CommonDrawableRes.empty_image_bg),
                modifier = Modifier
                    .fillMaxSize()
            )
        }

        Text(
            text = tripInfoItemDisplay.tripName,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 24.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun DetailSection(
    @DrawableRes icon: Int,
    @StringRes title: Int,
    modifier: Modifier = Modifier,
    sectionCtaData: SectionCtaData? = null
) {
    DetailSection(icon, title, modifier, sectionCtaData) {
        // empty content
    }
}

@Composable
fun DetailSection(
    @DrawableRes icon: Int,
    @StringRes title: Int,
    modifier: Modifier = Modifier,
    sectionCtaData: SectionCtaData? = null,
    content: @Composable () -> Unit
) {
    Column(modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement  =  Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
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

            if(sectionCtaData != null) {
                Row(
                    modifier = Modifier.clickable {
                        sectionCtaData.onClick.invoke()
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        modifier = Modifier,
                        text = stringResource(sectionCtaData.title),
                        style = typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                        maxLines = 1
                    )

                    Icon(
                        modifier = Modifier
                            .size(20.dp),
                        painter = painterResource(sectionCtaData.icon),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

        }

        content()
    }
}