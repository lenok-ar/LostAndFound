package com.example.lostandfound

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.add.AddViewModel
import com.example.add.addScreen
import com.example.about.aboutScreen
import com.example.assistant.AssistantViewModel
import com.example.assistant.assistantScreen
import com.example.auth.LoginViewModel
import com.example.auth.loginScreen
import com.example.core.navigation.NavigationManager
import com.example.core.navigation.NavigationRoutes
import com.example.details.DetailsViewModel
import com.example.details.detailsScreen
import com.example.domain.usecase.AddItemUseCase
import com.example.domain.usecase.GetItemByIdUseCase
import com.example.domain.usecase.GetItemsUseCase
import com.example.feed.FeedViewModel
import com.example.feed.feedScreen
import com.example.profile.ProfileViewModel
import com.example.profile.profileScreen
import com.example.lostandfound.ui.theme.LostAndFoundTheme
import com.example.services.analytics.AnalyticsService
import com.example.services.auth.AuthService
import com.example.services.auth.TokenStorage
import com.example.services.map.MapService
import com.example.services.profile.UserProfileService
import com.example.services.remoteconfig.RemoteConfigService
import com.example.services.crash.CrashReporter
import com.example.services.ai.AiAssistantService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var notificationTargetRoute by mutableStateOf<String?>(null)

    @Inject
    lateinit var navigationManager: NavigationManager

    @Inject
    lateinit var getItemsUseCase: GetItemsUseCase

    @Inject
    lateinit var getItemByIdUseCase: GetItemByIdUseCase

    @Inject
    lateinit var addItemUseCase: AddItemUseCase

    @Inject
    lateinit var analyticsService: AnalyticsService

    @Inject
    lateinit var authService: AuthService

    @Inject
    lateinit var tokenStorage: TokenStorage

    @Inject
    lateinit var mapService: MapService

    @Inject
    lateinit var remoteConfigService: RemoteConfigService

    @Inject
    lateinit var userProfileService: UserProfileService

    @Inject
    lateinit var crashReporter: CrashReporter

    @Inject
    lateinit var aiAssistantService: AiAssistantService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationTargetRoute = intent.getStringExtra(TARGET_ROUTE)
        setContent {
            LostAndFoundTheme {
                LostAndFoundApp()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        notificationTargetRoute = intent.getStringExtra(TARGET_ROUTE)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun LostAndFoundApp() {
        val navController = rememberNavController()
        var selectedItem by remember { mutableStateOf(0) }
        var welcomeMessage by remember { mutableStateOf(remoteConfigService.welcomeMessage()) }
        var experimentEnabled by remember { mutableStateOf(remoteConfigService.isExperimentalFeatureEnabled()) }
        val lifecycleOwner = LocalLifecycleOwner.current
        val startDestination = if (tokenStorage.isTokenValid())
            NavigationRoutes.FEED else NavigationRoutes.LOGIN
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        val notificationPermissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {}

        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    remoteConfigService.refresh {
                        welcomeMessage = remoteConfigService.welcomeMessage()
                        experimentEnabled = remoteConfigService.isExperimentalFeatureEnabled()
                    }
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
        }

        // Устанавливаем NavController в NavigationManager
        LaunchedEffect(Unit) {
            navigationManager.setNavController(navController)
            remoteConfigService.refresh {
                welcomeMessage = remoteConfigService.welcomeMessage()
                experimentEnabled = remoteConfigService.isExperimentalFeatureEnabled()
            }
            if (Build.VERSION.SDK_INT >= 33 &&
                ContextCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        LaunchedEffect(notificationTargetRoute) {
            val route = notificationTargetRoute
            if (tokenStorage.isTokenValid() && route != null && route in notificationRoutes) {
                navController.navigate(route)
                notificationTargetRoute = null
            }
        }

        val feedViewModel = remember { FeedViewModel(getItemsUseCase, crashReporter) }
        val addViewModel = remember { AddViewModel(addItemUseCase, crashReporter) }
        val profileViewModel = remember { ProfileViewModel(userProfileService, tokenStorage, crashReporter) }
        val loginViewModel = remember { LoginViewModel(authService, analyticsService) }
        val assistantViewModel = remember { AssistantViewModel(aiAssistantService, crashReporter) }

        LaunchedEffect(Unit) {
            if (startDestination == NavigationRoutes.FEED) {
                analyticsService.trackEvent("screen_viewed", mapOf("screen_name" to "feed"))
            }
        }

        Scaffold(
            topBar = {
                if (currentRoute == NavigationRoutes.FEED) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = if (experimentEnabled) "$welcomeMessage · Новая функция включена" else welcomeMessage,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            },
            bottomBar = {
                if (currentRoute != NavigationRoutes.LOGIN) NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Лента") },
                        label = { Text("Лента") },
                        selected = selectedItem == 0,
                        onClick = {
                            selectedItem = 0
                            navigationManager.navigateToFeed()
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Add, contentDescription = "Добавить") },
                        label = { Text("Добавить") },
                        selected = selectedItem == 1,
                        onClick = {
                            selectedItem = 1
                            navigationManager.navigateToAdd()
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = "Профиль") },
                        label = { Text("Профиль") },
                        selected = selectedItem == 2,
                        onClick = {
                            selectedItem = 2
                            navigationManager.navigateToProfile()
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Info, contentDescription = "О нас") },
                        label = { Text("О нас") },
                        selected = selectedItem == 3,
                        onClick = {
                            selectedItem = 3
                            navController.navigate(NavigationRoutes.ABOUT)
                        }
                    )
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(paddingValues)
            ) {
                feedScreen(
                    onItemClick = { itemId ->
                        navigationManager.navigateToDetails(itemId)
                    },
                    onAddClick = { navigationManager.navigateToAdd() },
                    onAssistantClick = if (BuildConfig.ENABLE_AI_ASSISTANT) {
                        { navController.navigate(NavigationRoutes.ASSISTANT) }
                    } else {
                        null
                    },
                    viewModel = feedViewModel
                )

                addScreen(
                    onBack = { navigationManager.navigateBack() },
                    onSuccess = {
                        feedViewModel.loadItems()
                        navigationManager.navigateBack()
                    },
                    viewModel = addViewModel
                )

                detailsScreen(
                    onBack = { navigationManager.navigateBack() },
                    viewModel = DetailsViewModel(getItemByIdUseCase, crashReporter)
                )

                profileScreen(
                    onBack = { navigationManager.navigateBack() },
                    viewModel = profileViewModel
                )

                loginScreen(
                    activity = this@MainActivity,
                    viewModel = loginViewModel,
                    onLoggedIn = {
                        analyticsService.trackEvent("screen_viewed", mapOf("screen_name" to "feed"))
                        profileViewModel.refreshAfterLogin()
                        tokenStorage.user()?.let {
                            userProfileService.saveCurrentProfile(it.name, "${it.provider}@lostandfound.local")
                        }
                        navController.navigate(NavigationRoutes.FEED) {
                            popUpTo(NavigationRoutes.LOGIN) { inclusive = true }
                        }
                    }
                )

                aboutScreen(
                    onBack = { navigationManager.navigateBack() },
                    mapService = mapService
                )

                assistantScreen(
                    onBack = { navigationManager.navigateBack() },
                    viewModel = assistantViewModel
                )
            }
        }
    }

    private companion object {
        const val TARGET_ROUTE = "target_route"
        val notificationRoutes = setOf(
            NavigationRoutes.FEED,
            NavigationRoutes.ADD,
            NavigationRoutes.PROFILE,
            NavigationRoutes.ABOUT,
            NavigationRoutes.ASSISTANT
        )
    }
}
