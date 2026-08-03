package com.bdalamin.fontkeyboard.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bdalamin.fontkeyboard.data.model.KeyboardTheme
import com.bdalamin.fontkeyboard.data.model.KeyboardThemes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeStoreScreen(onBack: () -> Unit) {
    var selectedThemeId by remember { mutableStateOf("default") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All") + KeyboardThemes.all.map { it.category }.distinct()
    val displayThemes = if (selectedCategory == "All") KeyboardThemes.all
    else KeyboardThemes.all.filter { it.category == selectedCategory }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🎨 Theme Store", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    Button(
                        onClick = { /* Apply theme */ },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(Icons.Filled.Check, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Apply")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Keyboard preview
            item {
                KeyboardPreview(theme = KeyboardThemes.all.find { it.id == selectedThemeId })
            }

            // Category filter
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) }
                        )
                    }
                }
            }

            // Theme count
            item {
                Text(
                    "${displayThemes.size} Themes",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Themes grid (2 per row)
            items(displayThemes.chunked(2)) { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { theme ->
                        ThemeCard(
                            theme = theme,
                            isSelected = theme.id == selectedThemeId,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedThemeId = theme.id }
                        )
                    }
                    if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun KeyboardPreview(theme: KeyboardTheme?) {
    val bgColor = if (theme != null) Color(theme.bgColor) else Color(0xFFE6E0E9)
    val keyColor = if (theme != null) Color(theme.keyColor) else Color.White
    val keyTextColor = if (theme != null) Color(theme.keyTextColor) else Color(0xFF1C1B1F)
    val accentColor = if (theme != null) Color(theme.accentColor) else Color(0xFF6750A4)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .background(bgColor)
                .padding(12.dp),
        ) {
            Text(
                "Theme Preview",
                color = keyTextColor,
                fontSize = 11.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            // Key row preview
            listOf(
                listOf("Q","W","E","R","T","Y","U","I","O","P"),
                listOf("A","S","D","F","G","H","J","K","L"),
            ).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    row.forEach { key ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(keyColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(key, color = keyTextColor, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
            // Space bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(32.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(accentColor.copy(alpha = 0.25f))
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(keyColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text("space", color = keyTextColor.copy(alpha = 0.5f), fontSize = 10.sp)
                }
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(32.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(accentColor.copy(alpha = 0.25f))
                )
            }
        }
    }
}

@Composable
private fun ThemeCard(
    theme: KeyboardTheme,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .then(
                if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                else Modifier
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(if (isSelected) 6.dp else 2.dp)
    ) {
        Column {
            // Color preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(Color(theme.bgColor)),
                contentAlignment = Alignment.Center
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(20.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(theme.keyColor))
                        )
                    }
                }
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                }
                if (theme.isPremium) {
                    Surface(
                        modifier = Modifier.align(Alignment.TopStart).padding(6.dp),
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFFFB300)
                    ) {
                        Text("PRO", fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                    }
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(theme.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, maxLines = 1)
                Text(theme.category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                // Color swatches
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(top = 4.dp)) {
                    listOf(theme.bgColor, theme.keyColor, theme.accentColor).forEach { colorLong ->
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(Color(colorLong))
                                .border(0.5.dp, Color.Gray.copy(alpha = 0.3f), CircleShape)
                        )
                    }
                }
            }
        }
    }
}
