package com.example.keyboard.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardBackspace
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.keyboard.data.KeyboardLayouts
import com.example.keyboard.model.KeyModel
import com.example.keyboard.model.KeyType
import com.example.keyboard.model.KeyboardLanguage
import com.example.keyboard.model.KeyboardMode
import com.example.keyboard.sound.KeyboardFeedback

@Composable
fun IosKeyboard(
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = false,
    isSoundEnabled: Boolean = true,
    isHapticEnabled: Boolean = true,
    isPopupEnabled: Boolean = true,
    useFarsiDigits: Boolean = true,
    initialLanguage: KeyboardLanguage = KeyboardLanguage.FARSI,
    onCommitText: (String) -> Unit,
    onDeleteText: () -> Unit,
    onReturnKey: () -> Unit,
    onCursorMove: ((Int) -> Unit)? = null,
    onVoiceInputRequested: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val feedback = remember { KeyboardFeedback(context) }

    var language by remember { mutableStateOf(initialLanguage) }
    var keyboardMode by remember { mutableStateOf(KeyboardMode.CHARACTERS) }
    var isShifted by remember { mutableStateOf(false) }
    var isCapsLock by remember { mutableStateOf(false) }

    val keyboardBg = if (isDarkMode) Color(0xFF1E1E20) else Color(0xFFD1D3D9)
    val bottomIconColor = if (isDarkMode) Color(0xFFEBEBF5) else Color(0xFF4C4C4E)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(keyboardBg)
            .padding(horizontal = 3.dp, vertical = 6.dp)
            .testTag("ios_keyboard_container"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedContent(
            targetState = keyboardMode,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "keyboard_mode_switch"
        ) { mode ->
            when (mode) {
                KeyboardMode.EMOJI -> {
                    IosEmojiPanel(
                        isDarkMode = isDarkMode,
                        onEmojiSelected = { emoji ->
                            feedback.playClick(isSoundEnabled)
                            feedback.triggerHaptic(isHapticEnabled)
                            onCommitText(emoji)
                        },
                        onBackToKeyboard = {
                            keyboardMode = KeyboardMode.CHARACTERS
                        }
                    )
                }
                KeyboardMode.NUMBERS -> {
                    IosNumbersLayout(
                        isDarkMode = isDarkMode,
                        useFarsiDigits = useFarsiDigits,
                        isFarsi = language == KeyboardLanguage.FARSI,
                        isPopupEnabled = isPopupEnabled,
                        onKeyPress = { text, keyType ->
                            handleKeyPress(
                                text = text,
                                keyType = keyType,
                                language = language,
                                onCommitText = onCommitText,
                                onDeleteText = onDeleteText,
                                onReturnKey = onReturnKey,
                                onModeChange = { keyboardMode = KeyboardMode.CHARACTERS },
                                onSymbolChange = { keyboardMode = KeyboardMode.SYMBOLS },
                                onEmojiOpen = { keyboardMode = KeyboardMode.EMOJI }
                            )
                        },
                        onHaptic = { isDel -> feedback.triggerHaptic(isHapticEnabled, isDel) },
                        onClickSound = { feedback.playClick(isSoundEnabled) }
                    )
                }
                KeyboardMode.SYMBOLS -> {
                    IosSymbolsLayout(
                        isDarkMode = isDarkMode,
                        isFarsi = language == KeyboardLanguage.FARSI,
                        isPopupEnabled = isPopupEnabled,
                        onKeyPress = { text, keyType ->
                            handleKeyPress(
                                text = text,
                                keyType = keyType,
                                language = language,
                                onCommitText = onCommitText,
                                onDeleteText = onDeleteText,
                                onReturnKey = onReturnKey,
                                onModeChange = { keyboardMode = KeyboardMode.CHARACTERS },
                                onSymbolChange = { keyboardMode = KeyboardMode.NUMBERS },
                                onEmojiOpen = { keyboardMode = KeyboardMode.EMOJI }
                            )
                        },
                        onHaptic = { isDel -> feedback.triggerHaptic(isHapticEnabled, isDel) },
                        onClickSound = { feedback.playClick(isSoundEnabled) }
                    )
                }
                KeyboardMode.CHARACTERS -> {
                    if (language == KeyboardLanguage.FARSI) {
                        IosFarsiLayout(
                            isDarkMode = isDarkMode,
                            isPopupEnabled = isPopupEnabled,
                            onKeyPress = { text, keyType ->
                                handleKeyPress(
                                    text = text,
                                    keyType = keyType,
                                    language = language,
                                    onCommitText = onCommitText,
                                    onDeleteText = onDeleteText,
                                    onReturnKey = onReturnKey,
                                    onModeChange = { keyboardMode = KeyboardMode.NUMBERS },
                                    onSymbolChange = { keyboardMode = KeyboardMode.SYMBOLS },
                                    onEmojiOpen = { keyboardMode = KeyboardMode.EMOJI }
                                )
                            },
                            onHaptic = { isDel -> feedback.triggerHaptic(isHapticEnabled, isDel) },
                            onClickSound = { feedback.playClick(isSoundEnabled) },
                            onCursorMove = onCursorMove
                        )
                    } else {
                        IosEnglishLayout(
                            isDarkMode = isDarkMode,
                            isShifted = isShifted,
                            isCapsLock = isCapsLock,
                            isPopupEnabled = isPopupEnabled,
                            onShiftToggle = {
                                if (isShifted) {
                                    isShifted = false
                                    isCapsLock = false
                                } else {
                                    isShifted = true
                                }
                            },
                            onKeyPress = { text, keyType ->
                                if (keyType == KeyType.CHARACTER && isShifted && !isCapsLock) {
                                    isShifted = false
                                }
                                handleKeyPress(
                                    text = text,
                                    keyType = keyType,
                                    language = language,
                                    onCommitText = onCommitText,
                                    onDeleteText = onDeleteText,
                                    onReturnKey = onReturnKey,
                                    onModeChange = { keyboardMode = KeyboardMode.NUMBERS },
                                    onSymbolChange = { keyboardMode = KeyboardMode.SYMBOLS },
                                    onEmojiOpen = { keyboardMode = KeyboardMode.EMOJI }
                                )
                            },
                            onHaptic = { isDel -> feedback.triggerHaptic(isHapticEnabled, isDel) },
                            onClickSound = { feedback.playClick(isSoundEnabled) },
                            onCursorMove = onCursorMove
                        )
                    }
                }
            }
        }

        // Bottom action bar below keyboard (Globe on left, Home bar center, Mic on right)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Globe button: Instant toggle between FA and EN
            IconButton(
                onClick = {
                    feedback.playClick(isSoundEnabled)
                    feedback.triggerHaptic(isHapticEnabled)
                    language = if (language == KeyboardLanguage.FARSI) {
                        KeyboardLanguage.ENGLISH
                    } else {
                        KeyboardLanguage.FARSI
                    }
                },
                modifier = Modifier
                    .size(44.dp)
                    .testTag("globe_language_switch_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "Switch Language",
                    tint = bottomIconColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            // Current Language Pill Tag
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDarkMode) Color(0xFF2C2C2E) else Color(0xFFBCBFC6))
                    .clickable {
                        language = if (language == KeyboardLanguage.FARSI) KeyboardLanguage.ENGLISH else KeyboardLanguage.FARSI
                        feedback.triggerHaptic(isHapticEnabled)
                    }
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (language == KeyboardLanguage.FARSI) "فارسی (FA)" else "English (US)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDarkMode) Color.White else Color.Black
                )
            }

            // Voice input / Microphone icon
            IconButton(
                onClick = {
                    feedback.playClick(isSoundEnabled)
                    feedback.triggerHaptic(isHapticEnabled)
                    onVoiceInputRequested?.invoke()
                },
                modifier = Modifier
                    .size(44.dp)
                    .testTag("microphone_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Input",
                    tint = bottomIconColor,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        // Subtle iOS Home Bar indicator at very bottom
        Box(
            modifier = Modifier
                .padding(bottom = 2.dp)
                .width(135.dp)
                .height(4.5.dp)
                .background(
                    color = if (isDarkMode) Color(0x99FFFFFF) else Color(0x66000000),
                    shape = RoundedCornerShape(3.dp)
                )
        )
    }
}

private fun handleKeyPress(
    text: String,
    keyType: KeyType,
    language: KeyboardLanguage,
    onCommitText: (String) -> Unit,
    onDeleteText: () -> Unit,
    onReturnKey: () -> Unit,
    onModeChange: () -> Unit,
    onSymbolChange: () -> Unit,
    onEmojiOpen: () -> Unit
) {
    when (keyType) {
        KeyType.CHARACTER -> onCommitText(text)
        KeyType.HALF_SPACE -> onCommitText("\u200C") // Zero-width non-joiner (نیم‌فاصله)
        KeyType.SPACE -> onCommitText(" ")
        KeyType.BACKSPACE -> onDeleteText()
        KeyType.RETURN -> onReturnKey()
        KeyType.MODE_CHANGE -> onModeChange()
        KeyType.SYMBOL_CHANGE -> onSymbolChange()
        KeyType.EMOJI -> onEmojiOpen()
        else -> {}
    }
}

/**
 * EXACT REPLICA OF FARSI (PERSIAN) IOS KEYBOARD AS SENT IN USER'S IMAGE
 */
@Composable
fun IosFarsiLayout(
    isDarkMode: Boolean,
    isPopupEnabled: Boolean,
    onKeyPress: (String, KeyType) -> Unit,
    onHaptic: (isDelete: Boolean) -> Unit,
    onClickSound: () -> Unit,
    onCursorMove: ((Int) -> Unit)? = null
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        // Row 1 (11 keys): ض ص ق ف غ ع ه خ ح ج چ
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            KeyboardLayouts.farsiRow1.forEach { key ->
                IosKey(
                    key = key,
                    modifier = Modifier.weight(1f),
                    isDarkMode = isDarkMode,
                    isPopupEnabled = isPopupEnabled,
                    onKeyPress = onKeyPress,
                    onHaptic = onHaptic,
                    onClickSound = onClickSound
                )
            }
        }

        // Row 2 (11 keys): ش س ی ب ل ا ت ن م ک گ
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            KeyboardLayouts.farsiRow2.forEach { key ->
                IosKey(
                    key = key,
                    modifier = Modifier.weight(1f),
                    isDarkMode = isDarkMode,
                    isPopupEnabled = isPopupEnabled,
                    onKeyPress = onKeyPress,
                    onHaptic = onHaptic,
                    onClickSound = onClickSound
                )
            }
        }

        // Row 3 (11 keys): ظ ط ژ ز ر ذ د پ و ث ⌫
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            KeyboardLayouts.farsiRow3.forEach { key ->
                IosKey(
                    key = key,
                    modifier = Modifier.weight(key.weight),
                    isDarkMode = isDarkMode,
                    isPopupEnabled = isPopupEnabled,
                    onKeyPress = onKeyPress,
                    onHaptic = onHaptic,
                    onClickSound = onClickSound
                )
            }
        }

        // Row 4 (Bottom row): ۱۲۳ | 😀 | [فاصله / Space] | [نیم‌فاصله] | [⏎ Return]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            KeyboardLayouts.farsiBottomRow.forEach { key ->
                if (key.keyType == KeyType.SPACE) {
                    IosSpaceKey(
                        key = key,
                        modifier = Modifier.weight(key.weight),
                        isDarkMode = isDarkMode,
                        onKeyPress = onKeyPress,
                        onHaptic = onHaptic,
                        onClickSound = onClickSound,
                        onCursorMove = onCursorMove
                    )
                } else {
                    IosKey(
                        key = key,
                        modifier = Modifier.weight(key.weight),
                        isDarkMode = isDarkMode,
                        isPopupEnabled = isPopupEnabled,
                        onKeyPress = onKeyPress,
                        onHaptic = onHaptic,
                        onClickSound = onClickSound
                    )
                }
            }
        }
    }
}

/**
 * EXACT REPLICA OF ENGLISH IOS KEYBOARD AS SENT IN USER'S IMAGE
 */
@Composable
fun IosEnglishLayout(
    isDarkMode: Boolean,
    isShifted: Boolean,
    isCapsLock: Boolean,
    isPopupEnabled: Boolean,
    onShiftToggle: () -> Unit,
    onKeyPress: (String, KeyType) -> Unit,
    onHaptic: (isDelete: Boolean) -> Unit,
    onClickSound: () -> Unit,
    onCursorMove: ((Int) -> Unit)? = null
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        // Row 1 (10 keys): q w e r t y u i o p
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            KeyboardLayouts.englishRow1.forEach { key ->
                IosKey(
                    key = key,
                    modifier = Modifier.weight(1f),
                    isDarkMode = isDarkMode,
                    isShifted = isShifted || isCapsLock,
                    isCapsLock = isCapsLock,
                    isPopupEnabled = isPopupEnabled,
                    onKeyPress = onKeyPress,
                    onHaptic = onHaptic,
                    onClickSound = onClickSound
                )
            }
        }

        // Row 2 (9 keys): a s d f g h j k l (with horizontal padding)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            KeyboardLayouts.englishRow2.forEach { key ->
                IosKey(
                    key = key,
                    modifier = Modifier.weight(1f),
                    isDarkMode = isDarkMode,
                    isShifted = isShifted || isCapsLock,
                    isCapsLock = isCapsLock,
                    isPopupEnabled = isPopupEnabled,
                    onKeyPress = onKeyPress,
                    onHaptic = onHaptic,
                    onClickSound = onClickSound
                )
            }
        }

        // Row 3: Shift (⬆) | z x c v b n m | Backspace (⌫)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            KeyboardLayouts.englishRow3.forEach { key ->
                if (key.keyType == KeyType.SHIFT) {
                    IosKey(
                        key = key,
                        modifier = Modifier.weight(key.weight),
                        isDarkMode = isDarkMode,
                        isShifted = isShifted,
                        isCapsLock = isCapsLock,
                        isPopupEnabled = false,
                        onKeyPress = { _, _ -> onShiftToggle() },
                        onHaptic = onHaptic,
                        onClickSound = onClickSound
                    )
                } else {
                    IosKey(
                        key = key,
                        modifier = Modifier.weight(key.weight),
                        isDarkMode = isDarkMode,
                        isShifted = isShifted || isCapsLock,
                        isCapsLock = isCapsLock,
                        isPopupEnabled = isPopupEnabled,
                        onKeyPress = onKeyPress,
                        onHaptic = onHaptic,
                        onClickSound = onClickSound
                    )
                }
            }
        }

        // Row 4 (Bottom row): 123 | 😀 | [space] | return
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            KeyboardLayouts.englishBottomRow.forEach { key ->
                if (key.keyType == KeyType.SPACE) {
                    IosSpaceKey(
                        key = key,
                        modifier = Modifier.weight(key.weight),
                        isDarkMode = isDarkMode,
                        onKeyPress = onKeyPress,
                        onHaptic = onHaptic,
                        onClickSound = onClickSound,
                        onCursorMove = onCursorMove
                    )
                } else {
                    IosKey(
                        key = key,
                        modifier = Modifier.weight(key.weight),
                        isDarkMode = isDarkMode,
                        isPopupEnabled = false,
                        onKeyPress = onKeyPress,
                        onHaptic = onHaptic,
                        onClickSound = onClickSound
                    )
                }
            }
        }
    }
}

/**
 * Numbers mode
 */
@Composable
fun IosNumbersLayout(
    isDarkMode: Boolean,
    useFarsiDigits: Boolean,
    isFarsi: Boolean,
    isPopupEnabled: Boolean,
    onKeyPress: (String, KeyType) -> Unit,
    onHaptic: (isDelete: Boolean) -> Unit,
    onClickSound: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            KeyboardLayouts.getNumbersRow1(useFarsiDigits).forEach { key ->
                IosKey(
                    key = key,
                    modifier = Modifier.weight(1f),
                    isDarkMode = isDarkMode,
                    isPopupEnabled = isPopupEnabled,
                    onKeyPress = onKeyPress,
                    onHaptic = onHaptic,
                    onClickSound = onClickSound
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            KeyboardLayouts.numbersRow2.forEach { key ->
                IosKey(
                    key = key,
                    modifier = Modifier.weight(1f),
                    isDarkMode = isDarkMode,
                    isPopupEnabled = isPopupEnabled,
                    onKeyPress = onKeyPress,
                    onHaptic = onHaptic,
                    onClickSound = onClickSound
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            KeyboardLayouts.numbersRow3.forEach { key ->
                IosKey(
                    key = key,
                    modifier = Modifier.weight(key.weight),
                    isDarkMode = isDarkMode,
                    isPopupEnabled = isPopupEnabled,
                    onKeyPress = onKeyPress,
                    onHaptic = onHaptic,
                    onClickSound = onClickSound
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            KeyboardLayouts.getNumbersBottomRow(isFarsi).forEach { key ->
                IosKey(
                    key = key,
                    modifier = Modifier.weight(key.weight),
                    isDarkMode = isDarkMode,
                    isPopupEnabled = false,
                    onKeyPress = onKeyPress,
                    onHaptic = onHaptic,
                    onClickSound = onClickSound
                )
            }
        }
    }
}

/**
 * Alternate Symbols (#+=) mode
 */
@Composable
fun IosSymbolsLayout(
    isDarkMode: Boolean,
    isFarsi: Boolean,
    isPopupEnabled: Boolean,
    onKeyPress: (String, KeyType) -> Unit,
    onHaptic: (isDelete: Boolean) -> Unit,
    onClickSound: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            KeyboardLayouts.symbolsRow1.forEach { key ->
                IosKey(
                    key = key,
                    modifier = Modifier.weight(1f),
                    isDarkMode = isDarkMode,
                    isPopupEnabled = isPopupEnabled,
                    onKeyPress = onKeyPress,
                    onHaptic = onHaptic,
                    onClickSound = onClickSound
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            KeyboardLayouts.symbolsRow2.forEach { key ->
                IosKey(
                    key = key,
                    modifier = Modifier.weight(1f),
                    isDarkMode = isDarkMode,
                    isPopupEnabled = isPopupEnabled,
                    onKeyPress = onKeyPress,
                    onHaptic = onHaptic,
                    onClickSound = onClickSound
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            KeyboardLayouts.symbolsRow3.forEach { key ->
                IosKey(
                    key = key,
                    modifier = Modifier.weight(key.weight),
                    isDarkMode = isDarkMode,
                    isPopupEnabled = isPopupEnabled,
                    onKeyPress = onKeyPress,
                    onHaptic = onHaptic,
                    onClickSound = onClickSound
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            KeyboardLayouts.getNumbersBottomRow(isFarsi).forEach { key ->
                IosKey(
                    key = key,
                    modifier = Modifier.weight(key.weight),
                    isDarkMode = isDarkMode,
                    isPopupEnabled = false,
                    onKeyPress = onKeyPress,
                    onHaptic = onHaptic,
                    onClickSound = onClickSound
                )
            }
        }
    }
}

/**
 * iOS Space Bar with cursor trackpad sliding capability!
 */
@Composable
fun IosSpaceKey(
    key: KeyModel,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean,
    onKeyPress: (String, KeyType) -> Unit,
    onHaptic: (isDelete: Boolean) -> Unit,
    onClickSound: () -> Unit,
    onCursorMove: ((Int) -> Unit)? = null
) {
    var accumulatedDrag by remember { mutableFloatStateOf(0f) }

    IosKey(
        key = key,
        modifier = modifier.pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { accumulatedDrag = 0f },
                onDrag = { _, dragAmount ->
                    accumulatedDrag += dragAmount.x
                    val threshold = 28f
                    if (accumulatedDrag > threshold) {
                        onCursorMove?.invoke(1)
                        onHaptic(false)
                        accumulatedDrag = 0f
                    } else if (accumulatedDrag < -threshold) {
                        onCursorMove?.invoke(-1)
                        onHaptic(false)
                        accumulatedDrag = 0f
                    }
                }
            )
        },
        isDarkMode = isDarkMode,
        isPopupEnabled = false,
        onKeyPress = onKeyPress,
        onHaptic = onHaptic,
        onClickSound = onClickSound
    )
}

/**
 * iOS Emoji Grid panel
 */
@Composable
fun IosEmojiPanel(
    isDarkMode: Boolean,
    onEmojiSelected: (String) -> Unit,
    onBackToKeyboard: () -> Unit
) {
    val panelBg = if (isDarkMode) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .background(panelBg, RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ایموجی‌های iOS",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDarkMode) Color.White else Color.Black
            )
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (isDarkMode) Color(0xFF3A3A3C) else Color(0xFFD1D1D6))
                    .clickable { onBackToKeyboard() }
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardBackspace,
                        contentDescription = "Back to letters",
                        modifier = Modifier.size(16.dp),
                        tint = if (isDarkMode) Color.White else Color.Black
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "کیبورد",
                        fontSize = 12.sp,
                        color = if (isDarkMode) Color.White else Color.Black
                    )
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 40.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 8.dp)
        ) {
            items(KeyboardLayouts.popularEmojis) { emoji ->
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onEmojiSelected(emoji) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 24.sp)
                }
            }
        }
    }
}
