package com.example.assistant

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.core.navigation.NavigationRoutes

fun NavGraphBuilder.assistantScreen(onBack: () -> Unit, viewModel: AssistantViewModel) {
    composable(NavigationRoutes.ASSISTANT) {
        AssistantScreen(onBack, viewModel)
    }
}
