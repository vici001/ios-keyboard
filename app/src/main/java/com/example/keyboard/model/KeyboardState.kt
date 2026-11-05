package com.example.keyboard.model

enum class KeyboardLanguage {
    FARSI,
    ENGLISH
}

enum class KeyboardMode {
    CHARACTERS,
    NUMBERS,
    SYMBOLS,
    EMOJI
}

enum class IosKeyboardTheme {
    LIGHT,
    DARK
}

enum class KeyType {
    CHARACTER,
    SHIFT,
    BACKSPACE,
    MODE_CHANGE,     // 123 / ABC / الفبا
    SYMBOL_CHANGE,   // #+=
    SPACE,
    HALF_SPACE,      // نیم‌فاصله (ZWNJ)
    RETURN,
    GLOBE,           // Switch language (FA <-> EN)
    EMOJI,
    MIC
}

data class KeyModel(
    val primaryText: String,
    val displayText: String = primaryText,
    val secondaryText: String? = null,
    val alternates: List<String> = emptyList(),
    val keyType: KeyType = KeyType.CHARACTER,
    val weight: Float = 1.0f
)
