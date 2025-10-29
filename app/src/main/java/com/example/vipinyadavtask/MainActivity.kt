package com.example.vipinyadavtask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vipinyadavtask.presentation.AppScaffold
import com.example.vipinyadavtask.presentation.holdings.HoldingsScreen
import com.example.vipinyadavtask.presentation.positions.PositionsScreen
import com.example.vipinyadavtask.ui.theme.VipinYadavTaskTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            VipinYadavTaskTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    AppScaffold(navController = navController) {
                        NavHost(navController = navController, startDestination = "holdings") {
                            composable("holdings") { HoldingsScreen() }
                            composable("positions") { PositionsScreen() }
                        }
                    }
                }
            }
        }
    }
}