package com.minhhnn18898.letstravel

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.minhhnn18898.letstravel.navigation.AppScreen
import com.minhhnn18898.letstravel.navigation.MainAppBar
import com.minhhnn18898.letstravel.triplisting.EditTripScreen
import com.minhhnn18898.letstravel.triplisting.TripListingScreen
import com.minhhnn18898.letstravel.ui.theme.LetsTravelTheme

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
    val currentScreen = AppScreen.valueOf(backStackEntry?.destination?.route ?: AppScreen.Home.name)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            MainAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = AppScreen.Home.name,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(route = AppScreen.Home.name) {
                TripListingScreen(onClickEmptyView = {
                    navController.navigate(AppScreen.EditTripInfo.name)
                })
            }

            composable(route = AppScreen.EditTripInfo.name) {
                EditTripScreen()
            }
        }
    }
}
