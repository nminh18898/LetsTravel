package com.minhhnn18898.letstravel

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.minhhnn18898.letstravel.tripdetail.EditFlightInfoPage
import com.minhhnn18898.letstravel.ui.theme.LetsTravelTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LetsTravelTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                    EditFlightInfoPage(modifier = Modifier.padding(paddingValues))
                }
            }
        }
    }
}
