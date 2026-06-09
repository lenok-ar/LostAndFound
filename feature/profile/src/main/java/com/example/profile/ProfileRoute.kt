package com.example.profile

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.core.navigation.NavigationRoutes

fun NavGraphBuilder.profileScreen(
    onBack: () -> Unit,
    viewModel: ProfileViewModel
) {
    composable(NavigationRoutes.PROFILE) {
        ProfileScreen(
            onBack = onBack,
            viewModel = viewModel
        )
    }
}