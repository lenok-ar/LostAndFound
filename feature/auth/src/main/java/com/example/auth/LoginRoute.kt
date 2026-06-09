package com.example.auth

import android.app.Activity
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.core.navigation.NavigationRoutes

fun NavGraphBuilder.loginScreen(
    activity: Activity,
    viewModel: LoginViewModel,
    onLoggedIn: () -> Unit
) {
    composable(NavigationRoutes.LOGIN) {
        LoginScreen(activity, viewModel, onLoggedIn)
    }
}
