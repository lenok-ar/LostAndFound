package com.example.core.navigation

import androidx.navigation.NavController
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationManager @Inject constructor() {

    private var navController: NavController? = null

    fun setNavController(controller: NavController) {
        navController = controller
    }

    fun navigateToFeed() {
        navController?.navigate(NavigationRoutes.FEED) {
            popUpTo(NavigationRoutes.FEED) { inclusive = true }
        }
    }

    fun navigateToAdd() {
        navController?.navigate(NavigationRoutes.ADD)
    }

    fun navigateToDetails(itemId: String) {
        navController?.navigate(NavigationRoutes.details(itemId))
    }

    fun navigateToProfile() {
        navController?.navigate(NavigationRoutes.PROFILE)
    }

    fun navigateBack() {
        navController?.popBackStack()
    }

    fun getCurrentDestination(): String? {
        return navController?.currentDestination?.route
    }
}
