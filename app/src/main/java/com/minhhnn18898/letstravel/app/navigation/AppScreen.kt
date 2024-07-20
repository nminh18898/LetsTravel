package com.minhhnn18898.letstravel.app.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.minhhnn18898.letstravel.R

enum class AppScreen(@StringRes val title: Int) {
    Home(title = R.string.app_name),
    EditTripInfo(title = R.string.trip_info),
    SavedTripListingFull(title = R.string.saved_trips)
}

data class AppBarActionsState(
    val actions: (@Composable RowScope.() -> Unit)? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppBar(
    currentScreen: AppScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    actions: @Composable (RowScope.() -> Unit),
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                color = MaterialTheme.colorScheme.primary,
                text = stringResource(currentScreen.title)
            )
        },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        actions = actions
    )
}