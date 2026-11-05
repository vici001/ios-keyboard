package com.example.keyboard.ui

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class KeyboardPrefs(
    val isDarkMode: Boolean = false,
    val isSoundEnabled: Boolean = true,
    val isHapticEnabled: Boolean = true,
    val isPopupEnabled: Boolean = true,
    val useFarsiDigits: Boolean = true
)

class KeyboardSettingsManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("ios_keyboard_prefs", Context.MODE_PRIVATE)

    private val _settingsFlow = MutableStateFlow(loadSettings())
    val settingsFlow: StateFlow<KeyboardPrefs> = _settingsFlow.asStateFlow()

    fun loadSettings(): KeyboardPrefs {
        return KeyboardPrefs(
            isDarkMode = prefs.getBoolean("is_dark_mode", false),
            isSoundEnabled = prefs.getBoolean("is_sound_enabled", true),
            isHapticEnabled = prefs.getBoolean("is_haptic_enabled", true),
            isPopupEnabled = prefs.getBoolean("is_popup_enabled", true),
            useFarsiDigits = prefs.getBoolean("use_farsi_digits", true)
        )
    }

    fun setDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean("is_dark_mode", enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(isDarkMode = enabled)
    }

    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("is_sound_enabled", enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(isSoundEnabled = enabled)
    }

    fun setHapticEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("is_haptic_enabled", enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(isHapticEnabled = enabled)
    }

    fun setPopupEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("is_popup_enabled", enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(isPopupEnabled = enabled)
    }

    fun setUseFarsiDigits(enabled: Boolean) {
        prefs.edit().putBoolean("use_farsi_digits", enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(useFarsiDigits = enabled)
    }
}
