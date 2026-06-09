package com.example.feed

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.core.navigation.NavigationRoutes

fun NavGraphBuilder.feedScreen(
    onItemClick: (String) -> Unit,
    onAddClick: () -> Unit,
    onAssistantClick: (() -> Unit)?,
    viewModel: FeedViewModel
) {
    composable(NavigationRoutes.FEED) {
        FeedScreen(
            onItemClick = onItemClick,
            onAddClick = onAddClick,
            onAssistantClick = onAssistantClick,
            viewModel = viewModel
        )
    }
}
