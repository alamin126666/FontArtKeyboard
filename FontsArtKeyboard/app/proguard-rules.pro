-keep class com.bdalamin.fontkeyboard.** { *; }
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

# Hilt
-keep class dagger.hilt.** { *; }
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }
-keep @dagger.hilt.InstallIn class * { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keepclasseswithmembers class * {
    @androidx.room.* <methods>;
}

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }

# Compose
-keep class androidx.compose.** { *; }
