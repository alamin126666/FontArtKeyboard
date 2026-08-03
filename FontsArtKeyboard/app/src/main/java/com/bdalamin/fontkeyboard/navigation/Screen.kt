package com.bdalamin.fontkeyboard.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object EnableKeyboard : Screen("enable_keyboard")
    object SelectKeyboard : Screen("select_keyboard")
    object Home : Screen("home")
    object ThemeStore : Screen("theme_store")
    object FontStore : Screen("font_store")
    object Emoji : Screen("emoji")
    object Settings : Screen("settings")
    object About : Screen("about")
}
