package com.example.details

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.core.navigation.NavigationRoutes

fun NavGraphBuilder.detailsScreen(
    onBack: () -> Unit,
    viewModel: DetailsViewModel
) {
    composable(
        route = NavigationRoutes.DETAILS,
        arguments = listOf(
            navArgument(NavigationRoutes.ITEM_ID) { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val itemId = backStackEntry.arguments?.getString(NavigationRoutes.ITEM_ID) ?: return@composable
        DetailsScreen(
            itemId = itemId,
            onBack = onBack,
            viewModel = viewModel
        )
    }
}
