package com.wmc.eventplaner

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.wmc.eventplaner.feature.navgation.AppNavHost

@Composable
fun MyApp(innerPadding: PaddingValues? = null) {
    val navController = rememberNavController()
    AppNavHost(navController = navController)
}
