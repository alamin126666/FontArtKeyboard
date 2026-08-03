package com.bdalamin.fontkeyboard.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bdalamin.fontkeyboard.ui.screens.*
import com.bdalamin.fontkeyboard.utils.PreferenceManager

@Composable
fun AppNavigation(preferenceManager: PreferenceManager) {
    val navController = rememberNavController()

    val startDestination = when {
        !preferenceManager.onboardingCompleted -> Screen.Onboarding.route
        else -> Screen.Home.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                tween(300)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                tween(300)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                tween(300)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                tween(300)
            )
        }
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    preferenceManager.onboardingCompleted = true
                    navController.navigate(Screen.EnableKeyboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.EnableKeyboard.route) {
            EnableKeyboardScreen(
                onNext = {
                    navController.navigate(Screen.SelectKeyboard.route)
                }
            )
        }
        composable(Screen.SelectKeyboard.route) {
            SelectKeyboardScreen(
                onNext = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.EnableKeyboard.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigate = { route -> navController.navigate(route) }
            )
        }
        composable(Screen.ThemeStore.route) {
            ThemeStoreScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.FontStore.route) {
            FontStoreScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Emoji.route) {
            EmojiScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onAbout = { navController.navigate(Screen.About.route) }
            )
        }
        composable(Screen.About.route) {
            AboutScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
