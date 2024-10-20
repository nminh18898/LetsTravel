package com.minhhnn18898.letstravel.app.appbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import com.minhhnn18898.app_navigation.destination.HomeScreenDestination
import com.minhhnn18898.app_navigation.destination.PhotoViewFullDestination
import com.minhhnn18898.core.R
import com.minhhnn18898.ui_components.theme.typography
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppBar(
    backStackEntry: NavBackStackEntry?,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    title: String,
    actions: @Composable (RowScope.() -> Unit),
    coroutineScope: CoroutineScope,
    drawerState: DrawerState,
    modifier: Modifier = Modifier
) {
    val isViewFullPhotoScreen = backStackEntry?.isViewFullPhotoScreen() ?: false

    TopAppBar(
        title = {
            Text(
                color = MaterialTheme.colorScheme.primary,
                text = title,
                style = typography.titleMedium,
            )
        },
        modifier = modifier,
        navigationIcon = {
            if(backStackEntry == null || backStackEntry.isHomeScreen()) {
                IconButton(
                    onClick = {
                        coroutineScope.launch { drawerState.open() }
                    },
                    content = {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                )
            }
            else if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = if(isViewFullPhotoScreen) Color.LightGray else MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = if(isViewFullPhotoScreen) Color.Black.copy(0.9f) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
        )
    )
}

private fun NavBackStackEntry.isHomeScreen(): Boolean {
    return this.destination.hasRoute<HomeScreenDestination>()
}

private fun NavBackStackEntry.isViewFullPhotoScreen(): Boolean {
    return this.destination.hasRoute<PhotoViewFullDestination>()
}