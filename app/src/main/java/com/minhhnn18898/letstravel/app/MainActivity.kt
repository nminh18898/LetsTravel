package com.minhhnn18898.letstravel.app

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.minhhnn18898.app_navigation.appbarstate.TopAppBarState
import com.minhhnn18898.app_navigation.destination.SignInScreenDestination
import com.minhhnn18898.letstravel.app.appbar.AppDrawer
import com.minhhnn18898.letstravel.app.appbar.AppNavHost
import com.minhhnn18898.letstravel.app.appbar.MainAppBar
import com.minhhnn18898.ui_components.theme.LetsTravelTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LetsTravelTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()

    var topAppBarState by remember { mutableStateOf(TopAppBarState()) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                onNavigateToSignInScreen = {
                    navController.navigate(SignInScreenDestination.route)
                    scope.launch { drawerState.close() }
                }
            )
        },
    ){
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                MainAppBar(
                    backStackEntry = backStackEntry,
                    canNavigateBack = navController.previousBackStackEntry != null,
                    navigateUp = { navController.navigateUp() },
                    title = topAppBarState.screenTitle,
                    actions = {
                        topAppBarState.actions?.invoke(this)
                    },
                    drawerState = drawerState,
                    coroutineScope = scope
                )
            }
        ) { innerPadding ->

            AppNavHost(
                navController = navController,
                appBarOnScreenDisplay = {
                    topAppBarState = it
                },
                modifier = modifier.padding(innerPadding)
            )
        }
    }
}
