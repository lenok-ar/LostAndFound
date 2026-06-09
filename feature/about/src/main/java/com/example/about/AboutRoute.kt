package com.example.about

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.core.navigation.NavigationRoutes
import com.example.services.map.MapService

fun NavGraphBuilder.aboutScreen(onBack: () -> Unit, mapService: MapService) {
    composable(NavigationRoutes.ABOUT) {
        AboutScreen(onBack, mapService)
    }
}
