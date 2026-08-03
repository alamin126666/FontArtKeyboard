package com.bdalamin.fontkeyboard.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bdalamin.fontkeyboard.data.model.EmojiData
import com.bdalamin.fontkeyboard.data.model.KaomojiData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmojiScreen(onBack: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedEmojiCategoryIdx by remember { mutableIntStateOf(0) }
    var selectedKaomojiCategoryIdx by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    fun copyToClipboard(text: String) {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("emoji", text))
        Toast.makeText(context, "Copied: $text", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("😊 Emoji & Kaomoji", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search emoji...") },
                leadingIcon = { Icon(Icons.Filled.Search, null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Filled.Clear, null)
                        }
                    }
                },
                shape = RoundedCornerShape(50),
                singleLine = true
            )

            // Tabs
            TabRow(selectedTabIndex = selectedTab) {
                listOf("Emoji", "Kaomoji").forEachIndexed { idx, title ->
                    Tab(
                        selected = selectedTab == idx,
                        onClick = { selectedTab = idx },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTab) {
                0 -> {
                    // Emoji categories
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        EmojiData.categories.forEachIndexed { idx, cat ->
                            val isSelected = idx == selectedEmojiCategoryIdx
                            Surface(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clickable { selectedEmojiCategoryIdx = idx },
                                shape = CircleShape,
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surface,
                                tonalElevation = if (isSelected) 4.dp else 1.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(cat.icon, fontSize = 20.sp)
                                }
                            }
                        }
                    }

                    val category = EmojiData.categories[selectedEmojiCategoryIdx]
                    val emojis = if (searchQuery.isBlank()) category.emojis
                    else EmojiData.categories.flatMap { it.emojis }
                        .filter { it.contains(searchQuery, ignoreCase = true) }

                    Text(
                        category.name,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(9),
                        modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(emojis) { emoji ->
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clickable { copyToClipboard(emoji) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(emoji, fontSize = 24.sp, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }

                1 -> {
                    // Kaomoji
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        KaomojiData.categories.forEachIndexed { idx, cat ->
                            FilterChip(
                                selected = idx == selectedKaomojiCategoryIdx,
                                onClick = { selectedKaomojiCategoryIdx = idx },
                                label = { Text(cat.name, fontSize = 12.sp) }
                            )
                        }
                    }

                    val kaoCat = KaomojiData.categories[selectedKaomojiCategoryIdx]
                    val kaomojis = if (searchQuery.isBlank()) kaoCat.kaomoji
                    else KaomojiData.categories.flatMap { it.kaomoji }
                        .filter { it.contains(searchQuery) }

                    Text(
                        "${kaoCat.name} Kaomoji",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(kaomojis) { kao ->
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable { copyToClipboard(kao) },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(kao, fontSize = 13.sp, modifier = Modifier.weight(1f), maxLines = 1)
                                    Icon(
                                        Icons.Filled.ContentCopy,
                                        null,
                                        modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
