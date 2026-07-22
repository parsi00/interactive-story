package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*
import com.example.ui.theme.InteractiveStoryTheme
import com.example.ui.viewmodels.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InteractiveStoryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val gameViewModel: GameViewModel = viewModel()

                    val profile by gameViewModel.characterProfile.collectAsStateWithLifecycle()
                    val chapter by gameViewModel.currentChapter.collectAsStateWithLifecycle()
                    val inventory by gameViewModel.inventoryItems.collectAsStateWithLifecycle()
                    val achievements by gameViewModel.achievements.collectAsStateWithLifecycle()
                    val toastEvent by gameViewModel.toastEvent.collectAsStateWithLifecycle()
                    val notificationBanner by gameViewModel.notificationBanner.collectAsStateWithLifecycle()
                    val soundEnabled by gameViewModel.soundEnabled.collectAsStateWithLifecycle()
                    val isGeneratingAi by gameViewModel.isGeneratingAiStory.collectAsStateWithLifecycle()

                    // Toast Feedback Listener
                    LaunchedEffect(toastEvent) {
                        toastEvent?.let { msg ->
                            Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                            gameViewModel.clearToast()
                        }
                    }

                    NavHost(
                        navController = navController,
                        startDestination = "splash"
                    ) {
                        composable("splash") {
                            SplashScreen(
                                onNavigateToHome = {
                                    navController.navigate("home") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("home") {
                            HomeScreen(
                                profile = profile,
                                onNavigateToNewStory = { navController.navigate("character_creation") },
                                onNavigateToContinue = { navController.navigate("story") },
                                onNavigateToAiStory = { navController.navigate("ai_story") },
                                onNavigateToProfile = { navController.navigate("profile") },
                                onNavigateToSettings = { navController.navigate("settings") }
                            )
                        }

                        composable("character_creation") {
                            CharacterCreationScreen(
                                onCharacterCreated = { name, gender, avatarIdx, classType ->
                                    gameViewModel.createCharacter(name, gender, avatarIdx, classType)
                                    navController.navigate("story") {
                                        popUpTo("character_creation") { inclusive = true }
                                    }
                                },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("story") {
                            StoryScreen(
                                profile = profile,
                                chapter = chapter,
                                inventory = inventory,
                                notificationBanner = notificationBanner,
                                onDismissNotification = { gameViewModel.dismissNotification() },
                                onChoiceMade = { choice -> gameViewModel.makeChoice(choice) },
                                onCustomChoiceSubmitted = { prompt -> gameViewModel.submitCustomChoice(prompt) },
                                onUseItem = { item -> gameViewModel.useItem(item) },
                                onToggleEquip = { item -> gameViewModel.toggleEquipItem(item) },
                                onUseSpecialAbility = { gameViewModel.useClassSpecialAbility() },
                                onNavigateHome = { navController.navigate("home") }
                            )
                        }

                        composable("profile") {
                            ProfileScreen(
                                profile = profile,
                                inventory = inventory,
                                achievements = achievements,
                                onToggleEquip = { item -> gameViewModel.toggleEquipItem(item) },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("settings") {
                            SettingsScreen(
                                soundEnabled = soundEnabled,
                                notifyStoryEvents = profile?.notifyStoryEvents ?: true,
                                notifyMilestones = profile?.notifyMilestones ?: true,
                                notifyAchievements = profile?.notifyAchievements ?: true,
                                onToggleSound = { gameViewModel.toggleSound() },
                                onUpdateNotificationPrefs = { s, m, a -> gameViewModel.updateNotificationPreferences(s, m, a) },
                                onResetGame = {
                                    gameViewModel.resetGame()
                                    navController.navigate("home") {
                                        popUpTo("settings") { inclusive = true }
                                    }
                                },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("ai_story") {
                            AiStoryGeneratorScreen(
                                isGenerating = isGeneratingAi,
                                onGenerateRequested = { prompt ->
                                    gameViewModel.generateAiStory(prompt) {
                                        navController.navigate("story")
                                    }
                                },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}

