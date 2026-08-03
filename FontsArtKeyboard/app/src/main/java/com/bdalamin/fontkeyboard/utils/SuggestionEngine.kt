package com.bdalamin.fontkeyboard.utils

import android.content.Context
import android.view.textservice.SentenceSuggestionsInfo
import android.view.textservice.SpellCheckerSession
import android.view.textservice.TextInfo
import android.view.textservice.TextServicesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SuggestionEngine(private val context: Context) {

    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions: StateFlow<List<String>> = _suggestions

    // Common English words for offline suggestions
    private val commonWords = listOf(
        "the", "be", "to", "of", "and", "a", "in", "that", "have", "it",
        "for", "not", "on", "with", "he", "as", "you", "do", "at", "this",
        "but", "his", "by", "from", "they", "we", "say", "her", "she", "or",
        "an", "will", "my", "one", "all", "would", "there", "their", "what",
        "so", "up", "out", "if", "about", "who", "get", "which", "go", "me",
        "when", "make", "can", "like", "time", "no", "just", "him", "know",
        "take", "people", "into", "year", "your", "good", "some", "could",
        "them", "see", "other", "than", "then", "now", "look", "only", "come",
        "its", "over", "think", "also", "back", "after", "use", "two", "how",
        "hello", "world", "please", "thank", "sorry", "help", "love", "day",
        "work", "life", "home", "friend", "family", "happy", "great", "nice"
    )

    // Common Bangla words for offline suggestions
    private val commonBanglaWords = listOf(
        "আমি", "তুমি", "সে", "আমরা", "তোমরা", "তারা", "এটা", "ওটা",
        "এখানে", "ওখানে", "কি", "কেন", "কোথায়", "কখন", "কিভাবে",
        "ভালো", "খারাপ", "সুন্দর", "বড়", "ছোট", "নতুন", "পুরনো",
        "বাংলা", "ইংরেজি", "বন্ধু", "পরিবার", "ভালোবাসা", "ধন্যবাদ",
        "হ্যালো", "নমস্কার", "আসসালামু", "আলাইকুম", "শুভ", "সকাল",
        "দিন", "রাত", "খাবার", "পানি", "বাড়ি", "স্কুল", "কাজ", "মানুষ"
    )

    private var currentWord = ""
    private var isBangla = false

    fun onWordChanged(word: String, banglaMode: Boolean) {
        currentWord = word.lowercase()
        isBangla = banglaMode
        if (word.isEmpty()) {
            _suggestions.value = emptyList()
            return
        }
        val wordList = if (isBangla) commonBanglaWords else commonWords
        val filtered = wordList
            .filter { it.startsWith(currentWord) && it != currentWord }
            .take(5)
        _suggestions.value = filtered
    }

    fun addWordToDictionary(word: String) {
        // In a real implementation, persist this to a database
    }
}
