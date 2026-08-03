package com.bdalamin.fontkeyboard.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bdalamin.fontkeyboard.navigation.Screen
import com.bdalamin.fontkeyboard.utils.FontConverter
import com.bdalamin.fontkeyboard.utils.TextDecorator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    var inputText by remember { mutableStateOf("Hello World") }
    var selectedFontId by remember { mutableStateOf("normal") }
    var convertedText by remember { mutableStateOf("Hello World") }
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(inputText, selectedFontId) {
        val style = FontConverter.allFontStyles.find { it.id == selectedFontId }
        convertedText = style?.converter?.invoke(inputText) ?: inputText
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⌨️", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Fonts Art",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigate(Screen.Settings.route) }) {
                        Icon(Icons.Filled.Settings, "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Filled.Home, null) },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigate(Screen.ThemeStore.route) },
                    icon = { Icon(Icons.Filled.Palette, null) },
                    label = { Text("Themes") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigate(Screen.FontStore.route) },
                    icon = { Icon(Icons.Filled.TextFields, null) },
                    label = { Text("Fonts") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigate(Screen.Emoji.route) },
                    icon = { Icon(Icons.Filled.EmojiEmotions, null) },
                    label = { Text("Emoji") }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Text converter card
            item {
                TextConverterCard(
                    inputText = inputText,
                    convertedText = convertedText,
                    onInputChange = { inputText = it }
                )
            }

            // Tab selector
            item {
                TabRow(selectedTabIndex = selectedTab) {
                    listOf("Fonts", "Decorators", "Kaomoji").forEachIndexed { idx, label ->
                        Tab(
                            selected = selectedTab == idx,
                            onClick = { selectedTab = idx },
                            text = { Text(label) }
                        )
                    }
                }
            }

            // Content by tab
            when (selectedTab) {
                0 -> {
                    item {
                        FontStylesGrid(
                            inputText = inputText,
                            selectedFontId = selectedFontId,
                            onSelect = { selectedFontId = it }
                        )
                    }
                }
                1 -> {
                    item { TextDecoratorGrid(inputText = inputText) }
                }
                2 -> {
                    item { QuickKaomojiSection() }
                }
            }

            // Quick access
            item { QuickAccessSection(onNavigate = onNavigate) }

            // Features section
            item { FeaturesSection() }
        }
    }
}

@Composable
private fun TextConverterCard(
    inputText: String,
    convertedText: String,
    onInputChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "✍️ Text Converter",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Type something...") },
                shape = RoundedCornerShape(12.dp),
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (convertedText != inputText) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            convertedText,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 18.sp
                        )
                        IconButton(onClick = {}) {
                            Icon(Icons.Filled.ContentCopy, "Copy", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FontStylesGrid(
    inputText: String,
    selectedFontId: String,
    onSelect: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FontConverter.allFontStyles.chunked(1).forEach { row ->
            row.forEach { style ->
                val isSelected = style.id == selectedFontId
                val converted = style.converter(inputText.ifBlank { "The quick brown fox" })
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(style.id) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surface
                    ),
                    border = if (isSelected) CardDefaults.outlinedCardBorder() else null
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                style.name,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                converted.take(30),
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1
                            )
                        }
                        if (isSelected) {
                            Icon(Icons.Filled.CheckCircle, null, tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = {}) {
                            Icon(Icons.Filled.ContentCopy, "Copy", modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TextDecoratorGrid(inputText: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TextDecorator.allStyles.forEach { style ->
            val decorated = try {
                style.decorator(inputText.ifBlank { "Hello" })
            } catch (e: Exception) { inputText }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            style.name,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            decorated.take(60),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.ContentCopy, "Copy", modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickKaomojiSection() {
    val popular = listOf(
        "(＾▽＾)", "( ´ ▽ ` )ﾉ", "(╯°□°）╯︵ ┻━┻",
        "¯\\_(ツ)_/¯", "( ͡° ͜ʖ ͡°)", "(づ￣ ³￣)づ",
        "ʕ•ᴥ•ʔ", "(♥ω♥*)", "Σ(°ロ°)"
    )
    Column {
        Text(
            "Popular Kaomoji",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        popular.chunked(3).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                row.forEach { kao ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { },
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Text(
                            kao,
                            modifier = Modifier.padding(8.dp),
                            fontSize = 10.sp,
                            maxLines = 1
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

@Composable
private fun QuickAccessSection(onNavigate: (String) -> Unit) {
    val items = listOf(
        Triple("🎨", "50+ Themes", Screen.ThemeStore.route),
        Triple("✍️", "Font Store", Screen.FontStore.route),
        Triple("😊", "Emoji", Screen.Emoji.route),
        Triple("⚙️", "Settings", Screen.Settings.route),
    )
    Column {
        Text(
            "Quick Access",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items.forEach { (emoji, label, route) ->
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigate(route) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(emoji, fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(label, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                    }
                }
            }
        }
    }
}

@Composable
private fun FeaturesSection() {
    val features = listOf(
        "🔤 Unicode Fonts" to "15+ beautiful font styles",
        "😊 Emoji Keyboard" to "1000+ emoji in categories",
        "😎 Kaomoji" to "150+ cute face art",
        "📋 Clipboard" to "Smart clipboard manager",
        "🌙 Dark/Light" to "Auto theme support",
        "🌍 Bangla" to "Full phonetic Bangla input",
        "🔮 AI Suggestions" to "Smart word predictions",
        "📳 Haptic Feedback" to "Key press vibration & sound",
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "✨ Features",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(12.dp))
            features.chunked(2).forEach { row ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { (title, desc) ->
                        Column(modifier = Modifier.weight(1f).padding(vertical = 4.dp)) {
                            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text(desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f))
                        }
                    }
                    if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
