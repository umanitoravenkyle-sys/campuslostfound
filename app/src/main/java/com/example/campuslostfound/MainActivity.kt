package com.example.campuslostfound

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.campuslostfound.ui.theme.CampusLostFoundTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val authViewModel: AuthViewModel = viewModel()
            val isDark by remember { mutableStateOf(authViewModel.isDarkMode()) }
            var currentDarkTheme by remember { mutableStateOf(isDark) }

            CampusLostFoundTheme(darkTheme = currentDarkTheme) {

                val itemViewModel: ItemViewModel = viewModel()
                val chatViewModel: ChatViewModel = viewModel()
                val notifViewModel: NotificationViewModel = viewModel()
                val isUserAuthenticated by authViewModel.isUserAuthenticated.collectAsState()
                val navController = rememberNavController()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    NavHost(
                        navController = navController,
                        startDestination = if (isUserAuthenticated) "home" else "login"
                    ) {

                        // Login Screen]
                        composable("login") {
                            LoginScreen(
                                viewModel = authViewModel,
                                onLoginClick = {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onCreateAccountClick = {
                                    navController.navigate("signup")
                                }
                            )
                        }

                        // Sign Up Screen
                        composable("signup") {
                            SignUpScreen(
                                viewModel = authViewModel,
                                onBackToLoginClick = {
                                    navController.popBackStack()
                                },
                                onCreateAccountClick = {
                                    navController.navigate("home") {
                                        popUpTo("signup") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // Home Screen
                        composable("home") {
                            HomeScreen(
                                authViewModel = authViewModel,
                                itemViewModel = itemViewModel,
                                notifViewModel = notifViewModel,
                                onSearchClick = {
                                    navController.navigate("search")
                                },
                                onLostItemsClick = {
                                    navController.navigate("lost_items")
                                },
                                onFoundItemsClick = {
                                    navController.navigate("found_items")
                                },
                                onReportLostClick = {
                                    navController.navigate("report_lost")
                                },
                                onReportFoundClick = {
                                    navController.navigate("report_found")
                                },
                                onMyReportsClick = {
                                    navController.navigate("my_reports")
                                },
                                onChatClick = {
                                    navController.navigate("chat_list")
                                },
                                onNotificationsClick = {
                                    navController.navigate("notifications")
                                },
                                onProfileClick = {
                                    navController.navigate("profile")
                                },
                                onSettingsClick = {
                                    navController.navigate("settings")
                                },
                                onLogoutClick = {
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                },
                                onMapClick = {
                                    navController.navigate("map_screen")
                                },
                                onItemClick = { itemId ->
                                    navController.navigate("item_detail/$itemId")
                                }
                            )
                        }

                        // Chat List
                        composable("chat_list") {
                            ChatListScreen(
                                authViewModel = authViewModel,
                                chatViewModel = chatViewModel,
                                notifViewModel = notifViewModel,
                                onBackClick = { navController.popBackStack() },
                                onChatClick = { chatId, name ->
                                    navController.navigate("chat_detail/$chatId/$name")
                                },
                                onStartChattingClick = {
                                    navController.navigate("lost_items")
                                }
                            )
                        }

                        // Chat Detail
                        composable("chat_detail/{chatId}/{name}") { backStackEntry ->
                            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
                            val name = backStackEntry.arguments?.getString("name") ?: ""
                            ChatDetailScreen(
                                chatId = chatId,
                                otherName = name,
                                authViewModel = authViewModel,
                                chatViewModel = chatViewModel,
                                notifViewModel = notifViewModel,
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        // Item Detail
                        composable("item_detail/{itemId}") { backStackEntry ->
                            val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
                            ItemDetailScreen(
                                itemId = itemId,
                                authViewModel = authViewModel,
                                itemViewModel = itemViewModel,
                                chatViewModel = chatViewModel,
                                onBackClick = { navController.popBackStack() },
                                onMessageClick = { chatId, name ->
                                    navController.navigate("chat_detail/$chatId/$name")
                                }
                            )
                        }

                        // Search
                        composable("search") {
                            SearchLostItemsScreen(
                                itemViewModel = itemViewModel,
                                onBack = {
                                    navController.popBackStack()
                                },
                                onItemClick = { itemId ->
                                    navController.navigate("item_detail/$itemId")
                                }
                            )
                        }

                        // Profile
                        composable("profile") {
                            ProfileScreen(
                                viewModel = authViewModel,
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onEditProfileClick = {
                                    navController.navigate("personal_info")
                                },
                                onNotificationsClick = {
                                    navController.navigate("notifications")
                                },
                                onSettingsClick = {
                                    navController.navigate("settings")
                                }
                            )
                        }

                        // Personal Information
                        composable("personal_info") {
                            PersonalInformationScreen(
                                viewModel = authViewModel,
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onSaveClick = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // Notifications
                        composable("notifications") {
                            NotificationsScreen(
                                authViewModel = authViewModel,
                                viewModel = notifViewModel,
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onNotificationClick = { type, id, name ->
                                    if (type == "Message" && id != null) {
                                        val displayName = name ?: "User"
                                        navController.navigate("chat_detail/$id/$displayName")
                                    } else if (id != null) {
                                        navController.navigate("item_detail/$id")
                                    }
                                }
                            )
                        }

                        // Settings
                        composable("settings") {
                            SettingsScreen(
                                viewModel = authViewModel,
                                onThemeChange = { currentDarkTheme = it },
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // My Reports
                        composable("my_reports") {
                            MyReportsScreen(
                                authViewModel = authViewModel,
                                itemViewModel = itemViewModel,
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onReportClick = { itemId ->
                                    navController.navigate("item_detail/$itemId")
                                }
                            )
                        }

                        // Report Lost
                        composable("report_lost") {
                            ReportLostItemScreen(
                                authViewModel = authViewModel,
                                itemViewModel = itemViewModel,
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onSubmitClick = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // Report Found
                        composable("report_found") {
                            ReportFoundItemScreen(
                                authViewModel = authViewModel,
                                itemViewModel = itemViewModel,
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onSubmitClick = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // Lost Items
                        composable("lost_items") {
                            LostItemsScreen(
                                authViewModel = authViewModel,
                                chatViewModel = chatViewModel,
                                itemViewModel = itemViewModel,
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onItemClick = { itemId ->
                                    navController.navigate("item_detail/$itemId")
                                },
                                onMessageClick = { chatId, name ->
                                    navController.navigate("chat_detail/$chatId/$name")
                                }
                            )
                        }

                        // Found Items
                        composable("found_items") {
                            FoundItemsScreen(
                                authViewModel = authViewModel,
                                chatViewModel = chatViewModel,
                                itemViewModel = itemViewModel,
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onItemClick = { itemId ->
                                    navController.navigate("item_detail/$itemId")
                                },
                                onMessageClick = { chatId, name ->
                                    navController.navigate("chat_detail/$chatId/$name")
                                }
                            )
                        }

                        // Live Map
                        composable("map_screen") {
                            MapScreen(
                                onBackClick = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}