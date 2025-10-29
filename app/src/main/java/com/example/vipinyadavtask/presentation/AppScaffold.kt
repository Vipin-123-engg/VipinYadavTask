package com.example.vipinyadavtask.presentation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(navController: NavController, content: @Composable () -> Unit) {
    var selectedIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { /* search */ }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Search", tint = Color.White)
                    }
                    Text("Portfolio", color = Color.White)
                } },
                actions = {
                    IconButton(onClick = { /* search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { innerPadding ->
        androidx.compose.foundation.layout.Column(modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(innerPadding)) {
            TabRow(selectedTabIndex = selectedIndex) {
                Tab(selected = selectedIndex == 0, onClick = { selectedIndex = 0; navController.navigate("holdings") }) { Text("HOLDINGS") }
                Tab(selected = selectedIndex == 1, onClick = { selectedIndex = 1; navController.navigate("positions") }) { Text("POSITIONS") }
            }
            content()
        }
    }
}


