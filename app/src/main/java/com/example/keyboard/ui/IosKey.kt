package com.example.keyboard.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.keyboard.model.KeyModel
import com.example.keyboard.model.KeyType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun IosKey(
    key: KeyModel,
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = false,
    isShifted: Boolean = false,
    isCapsLock: Boolean = false,
    isPopupEnabled: Boolean = true,
    onKeyPress: (String, KeyType) -> Unit,
    onHaptic: (isDelete: Boolean) -> Unit,
    onClickSound: () -> Unit,
    onCursorMove: ((Int) -> Unit)? = null
) {
    var isPressed by remember { mutableStateOf(false) }
    var showAlternates by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var repeatDeleteJob by remember { mutableStateOf<Job?>(null) }

    // Colors matching exact iOS Palette
    val keyBgLight = when (key.keyType) {
        KeyType.CHARACTER -> Color(0xFFFFFFFF)
        KeyType.SPACE, KeyType.HALF_SPACE -> Color(0xFFFFFFFF)
        KeyType.EMOJI, KeyType.RETURN -> Color(0xFFFFFFFF)
        KeyType.BACKSPACE -> Color(0xFFFFFFFF) // In Persian layout image 2, backspace is white with dark glyph
        KeyType.MODE_CHANGE, KeyType.SYMBOL_CHANGE -> Color(0xFFB4B7BF)
        KeyType.SHIFT -> if (isShifted || isCapsLock) Color(0xFFFFFFFF) else Color(0xFFB4B7BF)
        else -> Color(0xFFFFFFFF)
    }

    val keyBgDark = when (key.keyType) {
        KeyType.CHARACTER -> Color(0xFF6B6C70)
        KeyType.SPACE, KeyType.HALF_SPACE -> Color(0xFF6B6C70)
        KeyType.EMOJI, KeyType.RETURN -> Color(0xFF6B6C70)
        KeyType.BACKSPACE -> Color(0xFF6B6C70)
        KeyType.MODE_CHANGE, KeyType.SYMBOL_CHANGE -> Color(0xFF45464A)
        KeyType.SHIFT -> if (isShifted || isCapsLock) Color(0xFFFFFFFF) else Color(0xFF45464A)
        else -> Color(0xFF6B6C70)
    }

    val shadowColor = if (isDarkMode) Color(0x66000000) else Color(0x33000000)
    val baseKeyBg = if (isDarkMode) keyBgDark else keyBgLight
    val activeKeyBg = if (isPressed) {
        if (isDarkMode) Color(0xFF444447) else Color(0xFFD6D8DC)
    } else {
        baseKeyBg
    }

    val textColor = when {
        isDarkMode && (key.keyType == KeyType.SHIFT && (isShifted || isCapsLock)) -> Color(0xFF000000)
        isDarkMode -> Color(0xFFFFFFFF)
        else -> Color(0xFF000000)
    }

    val effectiveText = if (isShifted && key.keyType == KeyType.CHARACTER) {
        key.primaryText.uppercase()
    } else {
        key.primaryText
    }

    Box(
        modifier = modifier
            .padding(horizontal = 2.5.dp, vertical = 4.dp)
            .height(44.dp)
            .zIndex(if (isPressed && isPopupEnabled && key.keyType == KeyType.CHARACTER) 10f else 1f),
        contentAlignment = Alignment.Center
    ) {
        // iOS Magnifier Popup Balloon on letter keys
        if (isPopupEnabled && isPressed && key.keyType == KeyType.CHARACTER && !showAlternates) {
            IosKeyPopup(
                char = effectiveText,
                isDarkMode = isDarkMode
            )
        }

        // Alternates dialog on long-press
        if (showAlternates && key.alternates.isNotEmpty()) {
            IosAlternatesPopup(
                alternates = key.alternates,
                isDarkMode = isDarkMode,
                onSelect = { alt ->
                    showAlternates = false
                    isPressed = false
                    onHaptic(false)
                    onClickSound()
                    onKeyPress(alt, KeyType.CHARACTER)
                },
                onDismiss = {
                    showAlternates = false
                    isPressed = false
                }
            )
        }

        // The Keycap itself
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(
                    elevation = if (isPressed) 0.dp else 1.2.dp,
                    shape = RoundedCornerShape(5.dp),
                    clip = false,
                    ambientColor = shadowColor,
                    spotColor = shadowColor
                )
                .background(
                    color = activeKeyBg,
                    shape = RoundedCornerShape(5.dp)
                )
                .clip(RoundedCornerShape(5.dp))
                .pointerInput(key) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            onHaptic(key.keyType == KeyType.BACKSPACE)
                            onClickSound()

                            if (key.keyType == KeyType.BACKSPACE) {
                                // First delete
                                onKeyPress(key.primaryText, key.keyType)
                                // Start repeating after delay
                                repeatDeleteJob = coroutineScope.launch {
                                    delay(400)
                                    while (isActive) {
                                        onHaptic(true)
                                        onClickSound()
                                        onKeyPress(key.primaryText, key.keyType)
                                        delay(50)
                                    }
                                }
                            }

                            val released = tryAwaitRelease()
                            repeatDeleteJob?.cancel()
                            repeatDeleteJob = null
                            isPressed = false

                            if (released && key.keyType != KeyType.BACKSPACE && !showAlternates) {
                                onKeyPress(effectiveText, key.keyType)
                            }
                        },
                        onLongPress = {
                            if (key.alternates.isNotEmpty()) {
                                showAlternates = true
                                onHaptic(false)
                            }
                        }
                    )
                }
                .testTag("key_${key.primaryText.ifEmpty { key.keyType.name }}"),
            contentAlignment = Alignment.Center
        ) {
            when (key.keyType) {
                KeyType.CHARACTER -> {
                    Text(
                        text = effectiveText,
                        color = textColor,
                        fontSize = if (effectiveText.length > 1) 18.sp else 22.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
                KeyType.HALF_SPACE -> {
                    // Exact iOS Persian Half-Space (ZWNJ) Icon:
                    // Solid downward arrow caret on top of a vertical dotted line
                    IosHalfSpaceIcon(color = textColor)
                }
                KeyType.BACKSPACE -> {
                    Icon(
                        imageVector = Icons.Default.Backspace,
                        contentDescription = "Backspace",
                        tint = textColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                KeyType.SHIFT -> {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "Shift",
                        tint = textColor,
                        modifier = Modifier.size(19.dp)
                    )
                }
                KeyType.RETURN -> {
                    if (key.displayText == "return") {
                        Text(
                            text = "return",
                            color = textColor,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardReturn,
                            contentDescription = "Return",
                            tint = textColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                KeyType.SPACE -> {
                    Text(
                        text = key.displayText,
                        color = if (isDarkMode) Color(0xFF9E9E9E) else Color(0xFF8E8E93),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
                KeyType.MODE_CHANGE, KeyType.SYMBOL_CHANGE -> {
                    Text(
                        text = key.primaryText,
                        color = textColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                KeyType.EMOJI -> {
                    Icon(
                        imageVector = Icons.Default.SentimentSatisfiedAlt,
                        contentDescription = "Emoji",
                        tint = textColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
                KeyType.GLOBE -> {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Language",
                        tint = textColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
                KeyType.MIC -> {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Microphone",
                        tint = textColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

/**
 * Exact replica of Apple iOS Persian Half-Space (ZWNJ) Key Icon:
 * Downward arrowhead triangle with a vertical dotted line below it.
 */
@Composable
fun IosHalfSpaceIcon(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .width(18.dp)
            .height(24.dp)
    ) {
        val centerX = size.width / 2f
        val topY = 2f
        val triangleBottom = 9f
        val triangleWidth = 10f

        // Downward filled triangle caret
        val path = Path().apply {
            moveTo(centerX - triangleWidth / 2f, topY)
            lineTo(centerX + triangleWidth / 2f, topY)
            lineTo(centerX, triangleBottom)
            close()
        }
        drawPath(path, color = color)

        // Vertical dotted line below the caret
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(3f, 3f), 0f)
        drawLine(
            color = color,
            start = Offset(centerX, triangleBottom + 3f),
            end = Offset(centerX, size.height - 2f),
            strokeWidth = 2f,
            cap = StrokeCap.Round,
            pathEffect = dashEffect
        )
    }
}

/**
 * Iconic Apple iOS Key Popup (Magnifier bubble above the key).
 */
@Composable
private fun IosKeyPopup(
    char: String,
    isDarkMode: Boolean
) {
    val bgColor = if (isDarkMode) Color(0xFF48484A) else Color(0xFFFFFFFF)
    val txtColor = if (isDarkMode) Color(0xFFFFFFFF) else Color(0xFF000000)

    Box(
        modifier = Modifier
            .offset { IntOffset(0, -110) }
            .size(54.dp, 58.dp)
            .shadow(6.dp, shape = RoundedCornerShape(8.dp), spotColor = Color(0x40000000))
            .background(bgColor, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char,
            fontSize = 30.sp,
            fontWeight = FontWeight.Medium,
            color = txtColor
        )
    }
}

/**
 * iOS Long-press Alternates Row (e.g. ا -> آ, أ, إ).
 */
@Composable
private fun IosAlternatesPopup(
    alternates: List<String>,
    isDarkMode: Boolean,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val bgColor = if (isDarkMode) Color(0xFF2C2C2E) else Color(0xFFF2F2F7)
    val itemBg = if (isDarkMode) Color(0xFF3A3A3C) else Color(0xFFFFFFFF)
    val txtColor = if (isDarkMode) Color(0xFFFFFFFF) else Color(0xFF000000)

    Box(
        modifier = Modifier
            .offset { IntOffset(0, -120) }
            .shadow(8.dp, RoundedCornerShape(10.dp))
            .background(bgColor, RoundedCornerShape(10.dp))
            .padding(4.dp)
            .zIndex(20f)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            alternates.forEach { alt ->
                Box(
                    modifier = Modifier
                        .size(38.dp, 44.dp)
                        .background(itemBg, RoundedCornerShape(6.dp))
                        .pointerInput(alt) {
                            detectTapGestures {
                                onSelect(alt)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = alt,
                        fontSize = 20.sp,
                        color = txtColor,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}
