package com.example.core.navigation

// Общие маршруты для навигации между фичами
object NavigationRoutes {
    const val FEED = "feed"
    const val ADD = "add"
    const val ITEM_ID = "itemId"
    const val DETAILS = "details/{$ITEM_ID}"
    const val PROFILE = "profile"
    const val LOGIN = "login"
    const val ABOUT = "about"
    const val ASSISTANT = "assistant"

    fun details(itemId: String): String = "details/$itemId"
}
