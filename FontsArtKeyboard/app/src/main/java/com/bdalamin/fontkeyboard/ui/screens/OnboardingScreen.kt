package com.bdalamin.fontkeyboard.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
    val emoji: String,
    val gradient: List<Color>
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pages = listOf(
        OnboardingPage(
            "Welcome to\nFonts Art Keyboard",
            "Express yourself with 50+ beautiful themes, stylish Unicode fonts, and a world-class typing experience.",
            "⌨️",
            listOf(Color(0xFF6750A4), Color(0xFF9C89B8))
        ),
        OnboardingPage(
            "Bangla & English\nSeamless Typing",
            "Switch between Bangla and English instantly. Full phonetic Bangla support with smart auto-suggestions.",
            "🌍",
            listOf(Color(0xFF006874), Color(0xFF4DB6AC))
        ),
        OnboardingPage(
            "Emoji, Kaomoji\n& Text Art",
            "Thousands of emoji, cute kaomoji, text decorations like Zalgo, Bubble text, Small Caps, and more!",
            "😊",
            listOf(Color(0xFF7D5260), Color(0xFFEF9A9A))
        ),
        OnboardingPage(
            "Your Privacy,\nOur Promise",
            "Fonts Art Keyboard works completely offline. We never read, store, or transmit what you type. Ever.",
            "🔒",
            listOf(Color(0xFF1B5E20), Color(0xFF66BB6A))
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            OnboardingPage(page = pages[page])
        }

        // Page indicators
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 160.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(pages.size) { idx ->
                val isSelected = pagerState.currentPage == idx
                val width by animateFloatAsState(if (isSelected) 24f else 8f, label = "dot")
                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .width(width.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        )
                )
            }
        }

        // Navigation buttons
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 48.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (pagerState.currentPage < pages.size - 1) {
                TextButton(onClick = onFinish) {
                    Text("Skip", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            Button(
                onClick = {
                    if (pagerState.currentPage < pages.size - 1) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        onFinish()
                    }
                },
                shape = RoundedCornerShape(50),
                modifier = Modifier.height(56.dp).widthIn(min = 140.dp)
            ) {
                Text(
                    if (pagerState.currentPage < pages.size - 1) "Next →" else "Get Started",
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun OnboardingPage(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(page.gradient[0].copy(alpha = 0.1f), page.gradient[1].copy(alpha = 0.05f))
                )
            )
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Emoji icon with gradient circle
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(page.gradient)),
            contentAlignment = Alignment.Center
        ) {
            Text(page.emoji, fontSize = 64.sp)
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            page.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            lineHeight = 26.sp
        )
    }
}
