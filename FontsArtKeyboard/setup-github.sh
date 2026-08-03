#!/bin/bash
# ============================================================
# Fonts Art Keyboard — GitHub Setup Script
# ============================================================
# Run this script after cloning / after creating your GitHub repo.
# Usage: bash setup-github.sh https://github.com/YOUR_USERNAME/FontsArtKeyboard.git

set -e

REPO_URL="${1}"

if [ -z "$REPO_URL" ]; then
  echo ""
  echo "❌ Usage: bash setup-github.sh <github-repo-url>"
  echo "   Example: bash setup-github.sh https://github.com/bdalamin/FontsArtKeyboard.git"
  echo ""
  exit 1
fi

echo ""
echo "🚀 Setting up Fonts Art Keyboard GitHub repository..."
echo ""

# Initialize git if not already done
if [ ! -d ".git" ]; then
  git init
  echo "✅ Git initialized"
fi

# Download Gradle wrapper JAR
echo "📦 Downloading Gradle wrapper..."
if [ -d "FontsArtKeyboard" ]; then
  cd FontsArtKeyboard
  gradle wrapper --gradle-version 8.9 2>/dev/null || \
    curl -L -o gradle/wrapper/gradle-wrapper.jar \
      https://github.com/gradle/gradle/raw/v8.9.0/gradle/wrapper/gradle-wrapper.jar
  cd ..
fi

# Add all files
git add .
git commit -m "🎉 Initial commit — Fonts Art Keyboard v1.0.0

✨ Features:
- Material 3 Premium UI (Jetpack Compose)
- 15+ Unicode font styles (Bold, Italic, Script, Fraktur...)
- 50+ Keyboard themes (Dark, Neon, Nature, Gradient...)
- 1000+ Emoji in 10 categories
- 150+ Kaomoji in 8 mood categories
- Bangla + English keyboard support
- Text decorators (Zalgo, Bubble, Small Caps, Cursive, Glitch...)
- Smart auto-suggestion engine
- Clipboard manager with pin support
- Voice typing (Google Speech Recognition)
- Dark/Light theme with Material You
- Key press sound & vibration feedback
- 100% offline — zero data collection
- GitHub Actions CI/CD workflow

Tech: Kotlin + Jetpack Compose + Material 3 + Hilt + Room
Package: com.bdalamin.fontkeyboard
Min SDK: 24 (Android 7.0)"

# Add remote and push
git remote remove origin 2>/dev/null || true
git remote add origin "$REPO_URL"

# Create and switch to main branch
git branch -M main

echo ""
echo "📤 Pushing to GitHub..."
git push -u origin main

echo ""
echo "✅ Done! Your repository is live at: $REPO_URL"
echo ""
echo "🔄 GitHub Actions will now automatically build your APK!"
echo "   Check: ${REPO_URL/%.git/}/actions"
echo ""
