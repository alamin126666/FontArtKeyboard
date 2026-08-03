package com.bdalamin.fontkeyboard.service

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.inputmethodservice.InputMethodService
import android.media.AudioManager
import android.os.Build
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.bdalamin.fontkeyboard.data.model.EmojiData
import com.bdalamin.fontkeyboard.data.model.KaomojiData
import com.bdalamin.fontkeyboard.data.model.KeyboardThemes
import com.bdalamin.fontkeyboard.ui.theme.FontsArtKeyboardTheme
import com.bdalamin.fontkeyboard.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class KeyboardMode { ALPHA, NUMERIC, EMOJI, KAOMOJI, CLIPBOARD, FONT, VOICE }
enum class ShiftState { OFF, ON, CAPS_LOCK }
enum class KeyboardLanguage { ENGLISH, BANGLA }

@AndroidEntryPoint
class FontKeyboardService : InputMethodService(),
    LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    @Inject lateinit var preferenceManager: PreferenceManager

    private val lifecycleRegistry = LifecycleRegistry(this)
    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    private lateinit var soundFeedback: SoundFeedback
    private lateinit var vibrationFeedback: VibrationFeedback
    private lateinit var suggestionEngine: SuggestionEngine

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())

    // Keyboard state
    private var currentMode = mutableStateOf(KeyboardMode.ALPHA)
    private var shiftState = mutableStateOf(ShiftState.OFF)
    private var currentLanguage = mutableStateOf(KeyboardLanguage.ENGLISH)
    private var currentWord = mutableStateOf("")
    private var suggestions = mutableStateOf<List<String>>(emptyList())
    private var clipboardItems = mutableStateOf<List<String>>(emptyList())
    private var showCursorControl = mutableStateOf(false)

    private val clipboardManager by lazy {
        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    override fun onCreate() {
        savedStateRegistryController.performAttach()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        super.onCreate()
        soundFeedback = SoundFeedback(this)
        vibrationFeedback = VibrationFeedback(this)
        suggestionEngine = SuggestionEngine(this)
        serviceScope.launch {
            suggestionEngine.suggestions.collect { suggestions.value = it }
        }
    }

    override fun onCreateInputView(): View {
        val view = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@FontKeyboardService)
            setViewTreeViewModelStoreOwner(this@FontKeyboardService)
            setViewTreeSavedStateRegistryOwner(this@FontKeyboardService)
        }
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        view.setContent {
            val isDark = preferenceManager.isDarkMode
            val themeId = preferenceManager.selectedThemeId
            val theme = KeyboardThemes.all.find { it.id == themeId }

            FontsArtKeyboardTheme(darkTheme = isDark) {
                KeyboardLayout(
                    mode = currentMode.value,
                    shiftState = shiftState.value,
                    language = currentLanguage.value,
                    suggestions = suggestions.value,
                    clipboardItems = clipboardItems.value,
                    showCursorControl = showCursorControl.value,
                    theme = theme,
                    isDark = isDark,
                    onKeyPress = ::handleKeyPress,
                    onSuggestionClick = ::insertSuggestion,
                    onModeChange = { currentMode.value = it },
                    onShiftClick = ::handleShift,
                    onLanguageSwitch = ::switchLanguage,
                    onDeletePress = ::handleDelete,
                    onDeleteLongPress = ::handleDeleteAll,
                    onClipboardPaste = ::pasteFromClipboard,
                    onCursorControlToggle = {
                        showCursorControl.value = !showCursorControl.value
                    }
                )
            }
        }
        return view
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        currentWord.value = ""
        suggestions.value = emptyList()
        loadClipboardItems()
    }

    private fun loadClipboardItems() {
        // Load recent clipboard history
        try {
            val clip = clipboardManager.primaryClip
            if (clip != null && clip.itemCount > 0) {
                val text = clip.getItemAt(0).coerceToText(this).toString()
                if (text.isNotBlank()) {
                    clipboardItems.value = listOf(text)
                }
            }
        } catch (e: Exception) {
            clipboardItems.value = emptyList()
        }
    }

    private fun handleKeyPress(key: String) {
        if (preferenceManager.soundEnabled) soundFeedback.playKeyClick()
        if (preferenceManager.vibrationEnabled) vibrationFeedback.vibrateKeyPress()

        val ic = currentInputConnection ?: return
        val isShifted = shiftState.value == ShiftState.ON || shiftState.value == ShiftState.CAPS_LOCK
        val fontId = preferenceManager.selectedFontId

        var text = if (isShifted) key.uppercase() else key

        // Apply unicode font if not normal
        if (fontId != "normal" && text.all { it.isLetter() || it.isDigit() }) {
            val style = FontConverter.allFontStyles.find { it.id == fontId }
            text = style?.converter?.invoke(text) ?: text
        }

        ic.commitText(text, 1)

        // Auto turn off shift (not caps lock)
        if (shiftState.value == ShiftState.ON) {
            shiftState.value = ShiftState.OFF
        }

        // Update suggestion word
        currentWord.value += key
        suggestionEngine.onWordChanged(currentWord.value, currentLanguage.value == KeyboardLanguage.BANGLA)
    }

    private fun handleDelete() {
        if (preferenceManager.soundEnabled) soundFeedback.playDelete()
        if (preferenceManager.vibrationEnabled) vibrationFeedback.vibrateDelete()
        val ic = currentInputConnection ?: return
        val selected = ic.getSelectedText(0)
        if (selected != null && selected.isNotEmpty()) {
            ic.commitText("", 1)
        } else {
            ic.deleteSurroundingText(1, 0)
        }
        if (currentWord.value.isNotEmpty()) {
            currentWord.value = currentWord.value.dropLast(1)
            suggestionEngine.onWordChanged(currentWord.value, currentLanguage.value == KeyboardLanguage.BANGLA)
        }
    }

    private fun handleDeleteAll() {
        val ic = currentInputConnection ?: return
        ic.deleteSurroundingText(1000, 0)
        currentWord.value = ""
        suggestions.value = emptyList()
    }

    private fun handleShift() {
        shiftState.value = when (shiftState.value) {
            ShiftState.OFF -> ShiftState.ON
            ShiftState.ON -> ShiftState.CAPS_LOCK
            ShiftState.CAPS_LOCK -> ShiftState.OFF
        }
    }

    private fun switchLanguage() {
        currentLanguage.value = if (currentLanguage.value == KeyboardLanguage.ENGLISH)
            KeyboardLanguage.BANGLA else KeyboardLanguage.ENGLISH
        currentWord.value = ""
        suggestions.value = emptyList()
    }

    private fun insertSuggestion(word: String) {
        val ic = currentInputConnection ?: return
        val len = currentWord.value.length
        if (len > 0) ic.deleteSurroundingText(len, 0)
        ic.commitText("$word ", 1)
        currentWord.value = ""
        suggestions.value = emptyList()
    }

    private fun pasteFromClipboard(text: String) {
        currentInputConnection?.commitText(text, 1)
    }

    override fun onDestroy() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        soundFeedback.release()
        super.onDestroy()
    }
}

// ── Keyboard Layout Composable ────────────────────────────────────────────────

@Composable
private fun KeyboardLayout(
    mode: KeyboardMode,
    shiftState: ShiftState,
    language: KeyboardLanguage,
    suggestions: List<String>,
    clipboardItems: List<String>,
    showCursorControl: Boolean,
    theme: com.bdalamin.fontkeyboard.data.model.KeyboardTheme?,
    isDark: Boolean,
    onKeyPress: (String) -> Unit,
    onSuggestionClick: (String) -> Unit,
    onModeChange: (KeyboardMode) -> Unit,
    onShiftClick: () -> Unit,
    onLanguageSwitch: () -> Unit,
    onDeletePress: () -> Unit,
    onDeleteLongPress: () -> Unit,
    onClipboardPaste: (String) -> Unit,
    onCursorControlToggle: () -> Unit,
) {
    val bgColor = if (theme != null) Color(theme.bgColor) else
        if (isDark) Color(0xFF2B2930) else Color(0xFFE6E0E9)
    val keyColor = if (theme != null) Color(theme.keyColor) else
        if (isDark) Color(0xFF48464C) else Color(0xFFFFFFFF)
    val keyTextColor = if (theme != null) Color(theme.keyTextColor) else
        if (isDark) Color(0xFFE6E0E9) else Color(0xFF1C1B1F)
    val accentColor = if (theme != null) Color(theme.accentColor) else Color(0xFF6750A4)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        // Suggestion bar
        SuggestionBar(
            suggestions = suggestions,
            onSuggestionClick = onSuggestionClick,
            accentColor = accentColor,
            keyTextColor = keyTextColor
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Main keyboard area
        when (mode) {
            KeyboardMode.ALPHA -> AlphaKeyboard(
                shiftState = shiftState,
                language = language,
                bgColor = bgColor,
                keyColor = keyColor,
                keyTextColor = keyTextColor,
                accentColor = accentColor,
                onKeyPress = onKeyPress,
                onShiftClick = onShiftClick,
                onDeletePress = onDeletePress,
                onDeleteLongPress = onDeleteLongPress,
                onModeChange = onModeChange,
                onLanguageSwitch = onLanguageSwitch,
                onCursorControlToggle = onCursorControlToggle,
                showCursorControl = showCursorControl
            )
            KeyboardMode.NUMERIC -> NumericKeyboard(
                keyColor = keyColor,
                keyTextColor = keyTextColor,
                accentColor = accentColor,
                onKeyPress = onKeyPress,
                onDeletePress = onDeletePress,
                onModeChange = onModeChange
            )
            KeyboardMode.EMOJI -> EmojiKeyboard(
                keyTextColor = keyTextColor,
                onEmojiClick = onKeyPress,
                onModeChange = onModeChange
            )
            KeyboardMode.KAOMOJI -> KaomojiKeyboard(
                keyTextColor = keyTextColor,
                onKaomojiClick = onKeyPress,
                onModeChange = onModeChange
            )
            KeyboardMode.CLIPBOARD -> ClipboardKeyboard(
                items = clipboardItems,
                keyColor = keyColor,
                keyTextColor = keyTextColor,
                accentColor = accentColor,
                onPaste = onClipboardPaste,
                onModeChange = onModeChange
            )
            KeyboardMode.FONT -> FontKeyboard(
                keyColor = keyColor,
                keyTextColor = keyTextColor,
                accentColor = accentColor,
                onKeyPress = onKeyPress,
                onModeChange = onModeChange
            )
            KeyboardMode.VOICE -> {
                // Voice typing handled externally
                onModeChange(KeyboardMode.ALPHA)
            }
        }
    }
}

@Composable
private fun SuggestionBar(
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit,
    accentColor: Color,
    keyTextColor: Color
) {
    if (suggestions.isEmpty()) return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        suggestions.forEach { word ->
            SuggestionChip(
                onClick = { onSuggestionClick(word) },
                label = { Text(word, color = keyTextColor, fontSize = 13.sp) },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = accentColor.copy(alpha = 0.15f)
                ),
                border = SuggestionChipDefaults.suggestionChipBorder(
                    enabled = true,
                    borderColor = accentColor.copy(alpha = 0.3f)
                )
            )
        }
    }
}

@Composable
private fun KeyButton(
    text: String = "",
    modifier: Modifier = Modifier,
    keyColor: Color,
    keyTextColor: Color,
    fontSize: Int = 18,
    isSpecial: Boolean = false,
    accentColor: Color = Color(0xFF6750A4),
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    content: (@Composable () -> Unit)? = null
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSpecial) accentColor.copy(alpha = 0.2f) else keyColor)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                    onLongPress = { onLongClick?.invoke() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        if (content != null) content()
        else Text(
            text = text,
            color = keyTextColor,
            fontSize = fontSize.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AlphaKeyboard(
    shiftState: ShiftState,
    language: KeyboardLanguage,
    bgColor: Color,
    keyColor: Color,
    keyTextColor: Color,
    accentColor: Color,
    showCursorControl: Boolean,
    onKeyPress: (String) -> Unit,
    onShiftClick: () -> Unit,
    onDeletePress: () -> Unit,
    onDeleteLongPress: () -> Unit,
    onModeChange: (KeyboardMode) -> Unit,
    onLanguageSwitch: () -> Unit,
    onCursorControlToggle: () -> Unit,
) {
    val rows = if (language == KeyboardLanguage.ENGLISH) {
        listOf(
            listOf("q","w","e","r","t","y","u","i","o","p"),
            listOf("a","s","d","f","g","h","j","k","l"),
            listOf("z","x","c","v","b","n","m")
        )
    } else {
        // Bangla phonetic rows
        listOf(
            listOf("ক","খ","গ","ঘ","ঙ","চ","ছ","জ","ঝ","ঞ"),
            listOf("ট","ঠ","ড","ঢ","ণ","ত","থ","দ","ধ"),
            listOf("ন","প","ফ","ব","ভ","ম","য","র","ল")
        )
    }

    val shifted = shiftState == ShiftState.ON || shiftState == ShiftState.CAPS_LOCK

    if (showCursorControl) {
        CursorControlPanel(
            keyColor = keyColor,
            keyTextColor = keyTextColor,
            accentColor = accentColor,
            onDismiss = onCursorControlToggle
        )
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        // Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            rows[0].forEach { key ->
                val label = if (shifted) key.uppercase() else key
                KeyButton(
                    text = label,
                    modifier = Modifier.weight(1f),
                    keyColor = keyColor,
                    keyTextColor = keyTextColor,
                    accentColor = accentColor,
                    onClick = { onKeyPress(key) }
                )
            }
        }
        // Row 2
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            rows[1].forEach { key ->
                val label = if (shifted) key.uppercase() else key
                KeyButton(
                    text = label,
                    modifier = Modifier.weight(1f),
                    keyColor = keyColor,
                    keyTextColor = keyTextColor,
                    accentColor = accentColor,
                    onClick = { onKeyPress(key) }
                )
            }
        }
        // Row 3 with Shift and Delete
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Shift key
            KeyButton(
                modifier = Modifier.weight(1.5f),
                keyColor = keyColor,
                keyTextColor = keyTextColor,
                isSpecial = shiftState != ShiftState.OFF,
                accentColor = accentColor,
                onClick = onShiftClick
            ) {
                Icon(
                    imageVector = when (shiftState) {
                        ShiftState.OFF -> Icons.Filled.KeyboardArrowUp
                        ShiftState.ON -> Icons.Filled.KeyboardArrowUp
                        ShiftState.CAPS_LOCK -> Icons.Filled.Lock
                    },
                    contentDescription = "Shift",
                    tint = if (shiftState != ShiftState.OFF) accentColor else keyTextColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(2.dp))
            rows[2].forEach { key ->
                val label = if (shifted) key.uppercase() else key
                KeyButton(
                    text = label,
                    modifier = Modifier.weight(1f),
                    keyColor = keyColor,
                    keyTextColor = keyTextColor,
                    accentColor = accentColor,
                    onClick = { onKeyPress(key) }
                )
            }
            Spacer(modifier = Modifier.width(2.dp))
            // Delete
            KeyButton(
                modifier = Modifier.weight(1.5f),
                keyColor = keyColor,
                keyTextColor = keyTextColor,
                isSpecial = true,
                accentColor = accentColor,
                onClick = onDeletePress,
                onLongClick = onDeleteLongPress
            ) {
                Icon(
                    imageVector = Icons.Filled.Backspace,
                    contentDescription = "Delete",
                    tint = keyTextColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        // Bottom row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Language/Mode switch
            KeyButton(
                modifier = Modifier.weight(1.2f),
                keyColor = keyColor,
                keyTextColor = keyTextColor,
                isSpecial = true,
                accentColor = accentColor,
                onClick = onLanguageSwitch
            ) {
                Text(
                    if (language == KeyboardLanguage.ENGLISH) "বাং" else "EN",
                    color = accentColor,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Default
                )
            }
            // Symbol switch
            KeyButton(
                text = "?123",
                modifier = Modifier.weight(1.2f),
                keyColor = keyColor,
                keyTextColor = keyTextColor,
                fontSize = 12,
                isSpecial = true,
                accentColor = accentColor,
                onClick = { onModeChange(KeyboardMode.NUMERIC) }
            )
            // Comma
            KeyButton(
                text = ",",
                modifier = Modifier.weight(0.8f),
                keyColor = keyColor,
                keyTextColor = keyTextColor,
                accentColor = accentColor,
                onClick = { onKeyPress(",") }
            )
            // Space
            KeyButton(
                modifier = Modifier.weight(3f),
                keyColor = keyColor,
                keyTextColor = keyTextColor,
                isSpecial = false,
                accentColor = accentColor,
                onClick = { onKeyPress(" ") }
            ) {
                Text("space", color = keyTextColor.copy(alpha = 0.6f), fontSize = 12.sp)
            }
            // Period
            KeyButton(
                text = ".",
                modifier = Modifier.weight(0.8f),
                keyColor = keyColor,
                keyTextColor = keyTextColor,
                accentColor = accentColor,
                onClick = { onKeyPress(".") }
            )
            // Emoji toggle
            KeyButton(
                modifier = Modifier.weight(1.2f),
                keyColor = keyColor,
                keyTextColor = keyTextColor,
                isSpecial = true,
                accentColor = accentColor,
                onClick = { onModeChange(KeyboardMode.EMOJI) }
            ) {
                Text("😊", fontSize = 18.sp)
            }
            // Enter
            KeyButton(
                modifier = Modifier.weight(1.2f),
                keyColor = keyColor,
                keyTextColor = keyTextColor,
                isSpecial = true,
                accentColor = accentColor,
                onClick = { onKeyPress("\n") }
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardReturn,
                    contentDescription = "Enter",
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun NumericKeyboard(
    keyColor: Color,
    keyTextColor: Color,
    accentColor: Color,
    onKeyPress: (String) -> Unit,
    onDeletePress: () -> Unit,
    onModeChange: (KeyboardMode) -> Unit,
) {
    val row1 = listOf("1","2","3","4","5","6","7","8","9","0")
    val row2 = listOf("!","@","#","$","%","^","&","*","(",")")
    val row3 = listOf("-","_","=","+","[","]","{","}","\\","|")
    val row4 = listOf(";",":","\\'","\"","<",">",",",".","/","?")

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        listOf(row1, row2, row3, row4).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                row.forEach { key ->
                    KeyButton(
                        text = key,
                        modifier = Modifier.weight(1f),
                        keyColor = keyColor,
                        keyTextColor = keyTextColor,
                        fontSize = 15,
                        accentColor = accentColor,
                        onClick = { onKeyPress(key) }
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            KeyButton(
                text = "ABC",
                modifier = Modifier.weight(1.5f),
                keyColor = keyColor,
                keyTextColor = keyTextColor,
                fontSize = 12,
                isSpecial = true,
                accentColor = accentColor,
                onClick = { onModeChange(KeyboardMode.ALPHA) }
            )
            KeyButton(
                modifier = Modifier.weight(4f),
                keyColor = keyColor,
                keyTextColor = keyTextColor,
                accentColor = accentColor,
                onClick = { onKeyPress(" ") }
            ) { Text("space", color = keyTextColor.copy(alpha = 0.6f), fontSize = 12.sp) }
            KeyButton(
                modifier = Modifier.weight(1.5f),
                keyColor = keyColor,
                keyTextColor = keyTextColor,
                isSpecial = true,
                accentColor = accentColor,
                onClick = onDeletePress
            ) {
                Icon(Icons.Filled.Backspace, contentDescription = null, tint = keyTextColor, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun EmojiKeyboard(
    keyTextColor: Color,
    onEmojiClick: (String) -> Unit,
    onModeChange: (KeyboardMode) -> Unit,
) {
    var selectedCategory by remember { mutableIntStateOf(0) }
    val categories = EmojiData.categories

    Column {
        // Category tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KeyButton(
                text = "ABC",
                modifier = Modifier.width(52.dp),
                keyColor = Color(0x336750A4),
                keyTextColor = keyTextColor,
                fontSize = 11,
                isSpecial = true,
                accentColor = Color(0xFF6750A4),
                onClick = { onModeChange(KeyboardMode.ALPHA) }
            )
            categories.forEachIndexed { idx, cat ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (idx == selectedCategory) Color(0xFF6750A4).copy(alpha = 0.3f)
                            else Color.Transparent
                        )
                        .clickable { selectedCategory = idx },
                    contentAlignment = Alignment.Center
                ) {
                    Text(cat.icon, fontSize = 20.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        // Emoji grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(10),
            modifier = Modifier.height(160.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(categories[selectedCategory].emojis) { emoji ->
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onEmojiClick(emoji) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(emoji, fontSize = 22.sp, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
private fun KaomojiKeyboard(
    keyTextColor: Color,
    onKaomojiClick: (String) -> Unit,
    onModeChange: (KeyboardMode) -> Unit,
) {
    var selectedCategory by remember { mutableIntStateOf(0) }
    val categories = KaomojiData.categories

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KeyButton(
                text = "ABC",
                modifier = Modifier.width(52.dp),
                keyColor = Color(0x336750A4),
                keyTextColor = keyTextColor,
                fontSize = 11,
                isSpecial = true,
                accentColor = Color(0xFF6750A4),
                onClick = { onModeChange(KeyboardMode.ALPHA) }
            )
            categories.forEachIndexed { idx, cat ->
                FilterChip(
                    selected = idx == selectedCategory,
                    onClick = { selectedCategory = idx },
                    label = { Text(cat.name, fontSize = 11.sp) }
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.height(200.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(categories[selectedCategory].kaomoji) { k ->
                Surface(
                    modifier = Modifier.clickable { onKaomojiClick(k) },
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0x226750A4)
                ) {
                    Text(
                        k,
                        modifier = Modifier.padding(8.dp),
                        fontSize = 12.sp,
                        color = keyTextColor,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun ClipboardKeyboard(
    items: List<String>,
    keyColor: Color,
    keyTextColor: Color,
    accentColor: Color,
    onPaste: (String) -> Unit,
    onModeChange: (KeyboardMode) -> Unit,
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            KeyButton(
                text = "ABC",
                modifier = Modifier.width(60.dp),
                keyColor = keyColor,
                keyTextColor = keyTextColor,
                fontSize = 12,
                isSpecial = true,
                accentColor = accentColor,
                onClick = { onModeChange(KeyboardMode.ALPHA) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Clipboard", color = keyTextColor, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        if (items.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No clipboard items", color = keyTextColor.copy(alpha = 0.5f))
            }
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(120.dp)
            ) {
                items(items) { item ->
                    Card(
                        modifier = Modifier
                            .width(160.dp)
                            .fillMaxHeight()
                            .clickable { onPaste(item) },
                        colors = CardDefaults.cardColors(containerColor = keyColor)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                item.take(80),
                                color = keyTextColor,
                                fontSize = 12.sp,
                                maxLines = 4
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FontKeyboard(
    keyColor: Color,
    keyTextColor: Color,
    accentColor: Color,
    onKeyPress: (String) -> Unit,
    onModeChange: (KeyboardMode) -> Unit,
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            KeyButton(
                text = "ABC",
                modifier = Modifier.width(60.dp),
                keyColor = keyColor,
                keyTextColor = keyTextColor,
                fontSize = 12,
                isSpecial = true,
                accentColor = accentColor,
                onClick = { onModeChange(KeyboardMode.ALPHA) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Font Styles", color = keyTextColor, fontSize = 14.sp)
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(80.dp).fillMaxWidth()
        ) {
            items(FontConverter.allFontStyles) { style ->
                Card(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(120.dp)
                        .clickable { onKeyPress(style.preview) },
                    colors = CardDefaults.cardColors(containerColor = keyColor)
                ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Text(style.name, color = accentColor, fontSize = 10.sp)
                        Text(style.preview.take(12), color = keyTextColor, fontSize = 11.sp, maxLines = 1)
                    }
                }
            }
        }
    }
}

@Composable
private fun CursorControlPanel(
    keyColor: Color,
    keyTextColor: Color,
    accentColor: Color,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Cursor Control", color = keyTextColor, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(
                Icons.Filled.FirstPage to "Home",
                Icons.Filled.ChevronLeft to "Left",
                Icons.Filled.ChevronRight to "Right",
                Icons.Filled.LastPage to "End"
            ).forEach { (icon, label) ->
                KeyButton(
                    modifier = Modifier.size(48.dp),
                    keyColor = keyColor,
                    keyTextColor = keyTextColor,
                    accentColor = accentColor,
                    onClick = { /* Cursor move handled via InputConnection */ }
                ) {
                    Icon(icon, label, tint = keyTextColor, modifier = Modifier.size(20.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        TextButton(onClick = onDismiss) {
            Text("Close", color = accentColor)
        }
    }
}
