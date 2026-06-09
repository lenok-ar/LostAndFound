package com.example.core.navigation

sealed class NavigationDestination(val route: String) {
    object Feed : NavigationDestination(NavigationRoutes.FEED)
    object Add : NavigationDestination(NavigationRoutes.ADD)
    object Profile : NavigationDestination(NavigationRoutes.PROFILE)
    data class Details(val itemId: String) : NavigationDestination(NavigationRoutes.details(itemId))
}
