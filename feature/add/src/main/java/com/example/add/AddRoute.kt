package com.example.add

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.core.navigation.NavigationRoutes

fun NavGraphBuilder.addScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: AddViewModel
) {
    composable(NavigationRoutes.ADD) {
        AddScreen(
            onBack = onBack,
            onSuccess = onSuccess,
            viewModel = viewModel
        )
    }
}