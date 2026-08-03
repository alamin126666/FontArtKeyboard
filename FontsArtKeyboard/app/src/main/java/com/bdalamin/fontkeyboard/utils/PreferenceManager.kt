package com.bdalamin.fontkeyboard.utils

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("fontkeyboard_prefs", Context.MODE_PRIVATE)

    var onboardingCompleted: Boolean
        get() = prefs.getBoolean("onboarding_completed", false)
        set(value) = prefs.edit().putBoolean("onboarding_completed", value).apply()

    var isDarkMode: Boolean
        get() = prefs.getBoolean("dark_mode", false)
        set(value) = prefs.edit().putBoolean("dark_mode", value).apply()

    var selectedThemeId: String
        get() = prefs.getString("selected_theme_id", "default") ?: "default"
        set(value) = prefs.edit().putString("selected_theme_id", value).apply()

    var selectedFontId: String
        get() = prefs.getString("selected_font_id", "normal") ?: "normal"
        set(value) = prefs.edit().putString("selected_font_id", value).apply()

    var soundEnabled: Boolean
        get() = prefs.getBoolean("sound_enabled", true)
        set(value) = prefs.edit().putBoolean("sound_enabled", value).apply()

    var vibrationEnabled: Boolean
        get() = prefs.getBoolean("vibration_enabled", true)
        set(value) = prefs.edit().putBoolean("vibration_enabled", value).apply()

    var currentLanguage: String
        get() = prefs.getString("current_language", "en") ?: "en"
        set(value) = prefs.edit().putString("current_language", value).apply()

    var autoSuggestionEnabled: Boolean
        get() = prefs.getBoolean("auto_suggestion_enabled", true)
        set(value) = prefs.edit().putBoolean("auto_suggestion_enabled", value).apply()

    var autocorrectEnabled: Boolean
        get() = prefs.getBoolean("autocorrect_enabled", true)
        set(value) = prefs.edit().putBoolean("autocorrect_enabled", value).apply()

    var unicodeFontEnabled: Boolean
        get() = prefs.getBoolean("unicode_font_enabled", false)
        set(value) = prefs.edit().putBoolean("unicode_font_enabled", value).apply()

    var keyHeightScale: Float
        get() = prefs.getFloat("key_height_scale", 1.0f)
        set(value) = prefs.edit().putFloat("key_height_scale", value).apply()
}
