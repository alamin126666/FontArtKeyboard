# ⌨️ Fonts Art Keyboard

<p align="center">
  <img src="assets/banner.png" alt="Fonts Art Keyboard" width="100%"/>
</p>

<p align="center">
  <a href="https://github.com/bdalamin/FontsArtKeyboard/actions/workflows/build.yml">
    <img src="https://github.com/bdalamin/FontsArtKeyboard/actions/workflows/build.yml/badge.svg" alt="Build Status"/>
  </a>
  <img src="https://img.shields.io/badge/minSdk-24-brightgreen" alt="Min SDK"/>
  <img src="https://img.shields.io/badge/targetSdk-35-blue" alt="Target SDK"/>
  <img src="https://img.shields.io/badge/language-Kotlin-orange" alt="Kotlin"/>
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-purple" alt="Compose"/>
  <img src="https://img.shields.io/badge/Material-3-blueviolet" alt="Material 3"/>
</p>

---

## 📱 App Name
**Fonts Art Keyboard** — `com.bdalamin.fontkeyboard`

## ✨ Features

| Feature | Description |
|---------|-------------|
| 🎨 **50+ Themes** | Classic, Dark, Neon, Nature, Gradient, Material, Seasonal |
| 🔤 **15+ Unicode Fonts** | Bold, Italic, Script, Fraktur, Double Struck, Mono, Bubble, Flip... |
| 😊 **Emoji Keyboard** | 1000+ emoji across 10 categories with search |
| 😎 **Kaomoji** | 150+ kaomoji in 8 mood categories |
| 📋 **Clipboard Manager** | Smart clipboard history with pin support |
| 🔮 **Auto Suggestion** | Real-time word suggestions for English & Bangla |
| 🌙 **Dark/Light Theme** | Full Material 3 dynamic theming |
| 📳 **Haptic Feedback** | Key press sound + vibration (configurable) |
| 🌍 **Bangla + English** | Full phonetic Bangla input with English |
| 🎤 **Voice Typing** | Google speech recognition integration |
| ✨ **Text Decorators** | Zalgo, Bubble Text, Small Caps, Cursive, Glitch, Flip, Mirror, Border |
| 🏷️ **Nickname Generator** | 15+ stylish nickname templates |
| 🔤 **Number + Text Fonts** | Unicode fonts apply to both digits and letters |
| 🔒 **100% Private** | Fully offline, zero data collection |

## 📸 Screens

- Splash Screen
- Onboarding (4 pages)
- Enable Keyboard
- Select Keyboard
- Home (Font Converter + Quick Access)
- Theme Store (50+ themes with preview)
- Font Store (15+ styles with copy)
- Emoji & Kaomoji Browser
- Settings (Sound, Vibration, Theme, Font, Language)
- About

## 🛠️ Tech Stack

```
Language:      Kotlin
UI:            Jetpack Compose + Material 3
Architecture:  MVVM + Clean Architecture
DI:            Hilt (Dagger)
Database:      Room
Storage:       DataStore Preferences
Keyboard:      InputMethodService
Min SDK:       24 (Android 7.0)
Target SDK:    35 (Android 15)
Build:         Gradle Kotlin DSL
Package:       com.bdalamin.fontkeyboard
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug (2024.2.1) or newer
- JDK 17+
- Android SDK 35

### Clone & Build

```bash
git clone https://github.com/bdalamin/FontsArtKeyboard.git
cd FontsArtKeyboard
./gradlew assembleDebug
```

The APK will be at:
```
app/build/outputs/apk/debug/app-debug.apk
```

### Build Release APK

```bash
./gradlew assembleRelease
```

## 📂 Project Structure

```
FontsArtKeyboard/
├── app/
│   └── src/main/
│       ├── java/com/bdalamin/fontkeyboard/
│       │   ├── MainActivity.kt
│       │   ├── FontKeyboardApp.kt          # Hilt Application
│       │   ├── service/
│       │   │   └── FontKeyboardService.kt  # InputMethodService
│       │   ├── ui/
│       │   │   ├── theme/                  # Material 3 Theme
│       │   │   └── screens/               # All Compose screens
│       │   ├── data/
│       │   │   ├── model/                  # Data models + Theme/Emoji data
│       │   │   ├── database/               # Room DB + DAO
│       │   │   └── repository/             # Repository layer
│       │   ├── utils/
│       │   │   ├── FontConverter.kt        # 15+ Unicode font converters
│       │   │   ├── TextDecorator.kt        # Zalgo, Bubble, etc.
│       │   │   ├── SuggestionEngine.kt     # Auto-suggestion
│       │   │   ├── SoundFeedback.kt        # Key press sounds
│       │   │   ├── VibrationFeedback.kt    # Haptic feedback
│       │   │   └── PreferenceManager.kt    # App preferences
│       │   ├── navigation/                 # Compose Navigation
│       │   └── di/                         # Hilt modules
│       └── res/
│           ├── values/                     # Strings, colors, themes
│           └── xml/
│               └── method.xml             # IME subtypes (EN + BN)
├── .github/
│   └── workflows/
│       └── build.yml                      # CI/CD - auto build on push
└── gradle/
    └── libs.versions.toml                 # Version catalog
```

## 🔧 GitHub Actions CI/CD

Every push to `main`/`master`/`develop` triggers:
1. ✅ Build Debug APK
2. ✅ Run Unit Tests
3. ✅ Lint Check
4. 📤 Upload APK as artifact

For releases, push a tag `v1.0.0` to trigger a GitHub Release with the APK attached.

```bash
git tag v1.0.0
git push origin v1.0.0
```

## 📜 How to Install

1. Download the APK from [Releases](https://github.com/bdalamin/FontsArtKeyboard/releases)
2. Enable **Install from Unknown Sources** in Android Settings
3. Install the APK
4. Go to **Settings → Language & Input → Virtual Keyboard**
5. Enable **Fonts Art Keyboard**
6. Set it as your default keyboard

## 🌍 Supported Languages

| Language | Mode | Status |
|----------|------|--------|
| English (US) | Full QWERTY | ✅ |
| বাংলা (BD) | Phonetic | ✅ |

## 👤 Developer

**BD Al Amin**  
Package: `com.bdalamin.fontkeyboard`

## 📄 License

```
Copyright © 2024 BD Al Amin
All Rights Reserved.
```
