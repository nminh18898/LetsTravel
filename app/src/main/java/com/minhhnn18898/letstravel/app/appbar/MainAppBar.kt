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
import androidx.compose.ui.res.stringResource
import com.minhhnn18898.app_navigation.destination.AppScreenDestination
import com.minhhnn18898.app_navigation.destination.HomeScreenDestination
import com.minhhnn18898.core.R
import com.minhhnn18898.ui_components.theme.typography
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppBar(
    currentScreen: AppScreenDestination,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    actions: @Composable (RowScope.() -> Unit),
    coroutineScope: CoroutineScope,
    drawerState: DrawerState,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                color = MaterialTheme.colorScheme.primary,
                text = stringResource(currentScreen.title),
                style = typography.titleMedium,
            )
        },
        modifier = modifier,
        navigationIcon = {
            if(currentScreen.isHomeScreen()) {
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
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    )
}

private fun AppScreenDestination.isHomeScreen(): Boolean {
    return this.route == HomeScreenDestination.route
}