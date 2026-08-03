package com.bdalamin.fontkeyboard.data.model

data class KeyboardTheme(
    val id: String,
    val name: String,
    val category: String,
    val bgColor: Long,
    val keyColor: Long,
    val keyTextColor: Long,
    val accentColor: Long,
    val keyBorderColor: Long = 0x00000000,
    val isPremium: Boolean = false,
    val previewEmoji: String = "⌨️"
)

object KeyboardThemes {
    val all: List<KeyboardTheme> = listOf(
        // Default
        KeyboardTheme("default", "Default", "Classic", 0xFFE6E0E9, 0xFFFFFFFF, 0xFF1C1B1F, 0xFF6750A4),
        KeyboardTheme("dark", "Dark Mode", "Classic", 0xFF1C1B1F, 0xFF48464C, 0xFFE6E0E9, 0xFF9C89B8),
        KeyboardTheme("midnight", "Midnight", "Dark", 0xFF0D0D1A, 0xFF1A1A2E, 0xFFE0E0FF, 0xFF6B6BFF),
        KeyboardTheme("ocean_dark", "Ocean Dark", "Dark", 0xFF0A192F, 0xFF172A45, 0xFFCCD6F6, 0xFF64FFDA),
        KeyboardTheme("forest_dark", "Forest Night", "Dark", 0xFF0D1B0D, 0xFF1A2E1A, 0xFFB7E4C7, 0xFF40916C),
        // Nature
        KeyboardTheme("ocean", "Ocean Blue", "Nature", 0xFFE0F4FF, 0xFFFFFFFF, 0xFF006494, 0xFF0096C7),
        KeyboardTheme("forest", "Forest Green", "Nature", 0xFFE8F5E9, 0xFFFFFFFF, 0xFF1B5E20, 0xFF4CAF50),
        KeyboardTheme("sunset", "Sunset", "Nature", 0xFFFFF3E0, 0xFFFFFFFF, 0xFF5D1F00, 0xFFFF6F00),
        KeyboardTheme("lavender", "Lavender", "Nature", 0xFFF3E5F5, 0xFFFFFFFF, 0xFF4A148C, 0xFF9C27B0),
        KeyboardTheme("rose", "Rose Garden", "Nature", 0xFFFCE4EC, 0xFFFFFFFF, 0xFF880E4F, 0xFFE91E63),
        KeyboardTheme("sky", "Sky Blue", "Nature", 0xFFE1F5FE, 0xFFFFFFFF, 0xFF01579B, 0xFF03A9F4),
        KeyboardTheme("mint", "Mint Fresh", "Nature", 0xFFE8F5E9, 0xFFFFFFFF, 0xFF1B5E20, 0xFF26A69A),
        // Neon
        KeyboardTheme("neon_purple", "Neon Purple", "Neon", 0xFF1A001A, 0xFF2A002A, 0xFFFF00FF, 0xFFCC00CC),
        KeyboardTheme("neon_green", "Neon Green", "Neon", 0xFF001A00, 0xFF002A00, 0xFF00FF41, 0xFF00CC33),
        KeyboardTheme("neon_blue", "Neon Blue", "Neon", 0xFF00001A, 0xFF00002A, 0xFF00B4FF, 0xFF0090CC),
        KeyboardTheme("neon_red", "Neon Red", "Neon", 0xFF1A0000, 0xFF2A0000, 0xFFFF1744, 0xFFD50000),
        KeyboardTheme("cyberpunk", "Cyberpunk", "Neon", 0xFF0A0A1A, 0xFF1A0A2A, 0xFFFFE600, 0xFFFF00AA, true),
        // Gradient
        KeyboardTheme("aurora", "Aurora", "Gradient", 0xFF0A1A2A, 0xFF0D2137, 0xFFB8FFEC, 0xFF00D2FF, true),
        KeyboardTheme("galaxy", "Galaxy", "Gradient", 0xFF0B0C10, 0xFF1F2833, 0xFFC5C6C7, 0xFF66FCF1, true),
        KeyboardTheme("candy", "Candy", "Gradient", 0xFFFFF0F5, 0xFFFFE4F0, 0xFF5C0033, 0xFFFF69B4),
        KeyboardTheme("tropical", "Tropical", "Gradient", 0xFFFFF9C4, 0xFFFFFFFF, 0xFF1A237E, 0xFFFF8F00, true),
        // Material
        KeyboardTheme("material_red", "Material Red", "Material", 0xFFFFEBEE, 0xFFFFFFFF, 0xFFB71C1C, 0xFFF44336),
        KeyboardTheme("material_blue", "Material Blue", "Material", 0xFFE3F2FD, 0xFFFFFFFF, 0xFF0D47A1, 0xFF2196F3),
        KeyboardTheme("material_green", "Material Green", "Material", 0xFFE8F5E9, 0xFFFFFFFF, 0xFF1B5E20, 0xFF4CAF50),
        KeyboardTheme("material_amber", "Material Amber", "Material", 0xFFFFF8E1, 0xFFFFFFFF, 0xFF FF6F00, 0xFFFFC107),
        KeyboardTheme("material_teal", "Material Teal", "Material", 0xFFE0F2F1, 0xFFFFFFFF, 0xFF004D40, 0xFF009688),
        // Seasonal
        KeyboardTheme("christmas", "Christmas", "Seasonal", 0xFF1A3A1A, 0xFF2A4A2A, 0xFFFFD700, 0xFFFF0000, true),
        KeyboardTheme("halloween", "Halloween", "Seasonal", 0xFF1A0A00, 0xFF2A1200, 0xFFFF6600, 0xFFCC00CC, true),
        KeyboardTheme("valentine", "Valentine", "Seasonal", 0xFF2A0010, 0xFF3A0020, 0xFFFFB3C6, 0xFFFF006E, true),
        KeyboardTheme("spring", "Spring", "Seasonal", 0xFFF0FFF0, 0xFFFFFFFF, 0xFF2E7D32, 0xFFFF80AB),
        // Special
        KeyboardTheme("amoled_black", "AMOLED Black", "Special", 0xFF000000, 0xFF111111, 0xFFFFFFFF, 0xFF6750A4),
        KeyboardTheme("glass", "Glass", "Special", 0xCCFFFFFF, 0xAAFFFFFF, 0xFF1C1B1F, 0xFF6750A4, true),
        KeyboardTheme("retro", "Retro", "Special", 0xFF1A1A00, 0xFF2A2A00, 0xFF00FF00, 0xFF00CC00),
        KeyboardTheme("pastel", "Pastel Dream", "Special", 0xFFFFF9FB, 0xFFFFF0F8, 0xFF5C4A6E, 0xFFDDA0DD),
        KeyboardTheme("bangla_gold", "Bangla Gold", "Special", 0xFF1A1000, 0xFF2A1800, 0xFFFFD700, 0xFFFFA500, true),
    )
}
