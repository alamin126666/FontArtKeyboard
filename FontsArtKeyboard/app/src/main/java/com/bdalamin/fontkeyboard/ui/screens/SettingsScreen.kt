package com.bdalamin.fontkeyboard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bdalamin.fontkeyboard.utils.PreferenceManager
import dagger.hilt.android.EntryPointAccessors
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit, onAbout: () -> Unit) {
    var darkMode by remember { mutableStateOf(false) }
    var soundEnabled by remember { mutableStateOf(true) }
    var vibrationEnabled by remember { mutableStateOf(true) }
    var autoSuggestion by remember { mutableStateOf(true) }
    var autocorrect by remember { mutableStateOf(true) }
    var unicodeFont by remember { mutableStateOf(false) }
    var keyHeight by remember { mutableFloatStateOf(1.0f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("⚙️ Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Appearance
            item { SettingsSectionHeader("🎨 Appearance") }
            item {
                SettingsSwitch(
                    title = "Dark Mode",
                    subtitle = "Switch between dark and light theme",
                    icon = Icons.Filled.DarkMode,
                    checked = darkMode,
                    onCheckedChange = { darkMode = it }
                )
            }

            // Keyboard
            item { SettingsSectionHeader("⌨️ Keyboard") }
            item {
                SettingsSwitch(
                    title = "Key Press Sound",
                    subtitle = "Play sound on each key press",
                    icon = Icons.Filled.VolumeUp,
                    checked = soundEnabled,
                    onCheckedChange = { soundEnabled = it }
                )
            }
            item {
                SettingsSwitch(
                    title = "Key Vibration",
                    subtitle = "Vibrate on key press",
                    icon = Icons.Filled.Vibration,
                    checked = vibrationEnabled,
                    onCheckedChange = { vibrationEnabled = it }
                )
            }
            item {
                SettingsSwitch(
                    title = "Auto Suggestion",
                    subtitle = "Show word suggestions while typing",
                    icon = Icons.Filled.AutoAwesome,
                    checked = autoSuggestion,
                    onCheckedChange = { autoSuggestion = it }
                )
            }
            item {
                SettingsSwitch(
                    title = "Auto Correct",
                    subtitle = "Automatically correct misspelled words",
                    icon = Icons.Filled.Spellcheck,
                    checked = autocorrect,
                    onCheckedChange = { autocorrect = it }
                )
            }
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Height, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Key Height", fontWeight = FontWeight.Medium)
                                Text(
                                    when {
                                        keyHeight < 0.9f -> "Compact"
                                        keyHeight < 1.1f -> "Normal"
                                        keyHeight < 1.3f -> "Large"
                                        else -> "Extra Large"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Slider(
                            value = keyHeight,
                            onValueChange = { keyHeight = it },
                            valueRange = 0.7f..1.5f,
                            steps = 3,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            // Fonts
            item { SettingsSectionHeader("✍️ Fonts") }
            item {
                SettingsSwitch(
                    title = "Unicode Font Mode",
                    subtitle = "Type directly in fancy Unicode fonts",
                    icon = Icons.Filled.TextFields,
                    checked = unicodeFont,
                    onCheckedChange = { unicodeFont = it }
                )
            }

            // Language
            item { SettingsSectionHeader("🌍 Language") }
            item {
                SettingsItem(
                    title = "Default Language",
                    subtitle = "English (US)",
                    icon = Icons.Filled.Language,
                    onClick = {}
                )
            }

            // Privacy
            item { SettingsSectionHeader("🔒 Privacy") }
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Security, null, tint = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("100% Offline & Private", fontWeight = FontWeight.SemiBold)
                            Text(
                                "This keyboard never reads or transmits your keystrokes. All processing happens on your device.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            // About
            item { SettingsSectionHeader("ℹ️ About") }
            item {
                SettingsItem(
                    title = "About App",
                    subtitle = "Version 1.0.0 · Developer info",
                    icon = Icons.Filled.Info,
                    onClick = onAbout
                )
            }
            item {
                SettingsItem(
                    title = "Rate Us",
                    subtitle = "Love the app? Give us 5 stars!",
                    icon = Icons.Filled.Star,
                    onClick = {}
                )
            }
            item {
                SettingsItem(
                    title = "Share App",
                    subtitle = "Share Fonts Art Keyboard with friends",
                    icon = Icons.Filled.Share,
                    onClick = {}
                )
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
private fun SettingsSwitch(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Medium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
private fun SettingsItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Medium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            Icon(Icons.Filled.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
        }
    }
}
