package com.example

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.keyboard.model.KeyboardLanguage
import com.example.keyboard.ui.IosKeyboard
import com.example.keyboard.ui.KeyboardSettingsManager
import com.example.ui.theme.IosBlue
import com.example.ui.theme.IosGreen
import com.example.ui.theme.IosOrange
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private lateinit var settingsManager: KeyboardSettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        settingsManager = KeyboardSettingsManager(this)

        setContent {
            val settings by settingsManager.settingsFlow.collectAsState()

            MyApplicationTheme(darkTheme = settings.isDarkMode) {
                MainScreen(
                    settingsManager = settingsManager,
                    onOpenInputMethodSettings = {
                        val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    },
                    onOpenInputMethodPicker = {
                        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                        imm?.showInputMethodPicker()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    settingsManager: KeyboardSettingsManager,
    onOpenInputMethodSettings: () -> Unit,
    onOpenInputMethodPicker: () -> Unit
) {
    val context = LocalContext.current
    val settings by settingsManager.settingsFlow.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var typedText by remember { mutableStateOf("سلام! این کیبورد دقیقا مثل نسخه اصلی iOS طراحی شده است.\nبا کلید اختصاصی نیم‌فاصله (‌) می‌توانید کلماتی مانند «می‌خواهم» و «خانه‌ها» را تایپ کنید.") }
    var cursorPosition by remember { mutableIntStateOf(typedText.length) }
    var currentTestLanguage by remember { mutableStateOf(KeyboardLanguage.FARSI) }

    // Check if keyboard is enabled in system
    val isEnabledInSystem = remember {
        try {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.enabledInputMethodList?.any { it.packageName == context.packageName } == true
        } catch (_: Exception) {
            false
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(IosBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "iOS",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "کیبورد آیفون (iOS Keyboard)",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "چیدمان دقیق فارسی و انگلیسی + نیم‌فاصله",
                                fontSize = 11.sp,
                                color = if (settings.isDarkMode) Color.LightGray else Color.DarkGray
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { settingsManager.setDarkMode(!settings.isDarkMode) },
                        modifier = Modifier.testTag("toggle_dark_mode_button")
                    ) {
                        Icon(
                            imageVector = if (settings.isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = if (settings.isDarkMode) IosOrange else Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (settings.isDarkMode) Color(0xFF1C1C1E) else Color(0xFFF2F2F7)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(if (settings.isDarkMode) Color(0xFF000000) else Color(0xFFF2F2F7))
        ) {
            // Tabs: 0: تست کیبورد, 1: فعال‌سازی در سیستم, 2: تنظیمات
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = if (settings.isDarkMode) Color(0xFF1C1C1E) else Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = IosBlue
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("تست کیبورد", fontWeight = FontWeight.Medium) },
                    icon = { Icon(Icons.Default.Keyboard, contentDescription = null, Modifier.size(20.dp)) },
                    modifier = Modifier.testTag("tab_test_keyboard")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("فعال‌سازی", fontWeight = FontWeight.Medium) },
                    icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, Modifier.size(20.dp)) },
                    modifier = Modifier.testTag("tab_activation")
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("تنظیمات", fontWeight = FontWeight.Medium) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null, Modifier.size(20.dp)) },
                    modifier = Modifier.testTag("tab_settings")
                )
            }

            when (selectedTab) {
                0 -> {
                    // Interactive Playground
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        // iOS Notes Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(12.dp)
                                .testTag("notes_preview_card"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (settings.isDarkMode) Color(0xFF1C1C1E) else Color.White
                            ),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp)
                            ) {
                                // Header of the Note
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(IosOrange, CircleShape)
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            text = "یادداشت تست کیبورد iOS",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = IosOrange
                                        )
                                    }

                                    Row {
                                        IconButton(
                                            onClick = {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                val clip = ClipData.newPlainText("iOS Keyboard text", typedText)
                                                clipboard.setPrimaryClip(clip)
                                                Toast.makeText(context, "متن کپی شد!", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.ContentCopy,
                                                contentDescription = "Copy Text",
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        IconButton(
                                            onClick = {
                                                typedText = ""
                                                cursorPosition = 0
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.DeleteOutline,
                                                contentDescription = "Clear Text",
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }

                                // Quick Sample Insert Chips
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState())
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    QuickActionChip("تست نیم‌فاصله: می‌خواهم") {
                                        val sample = "می\u200Cخواهم "
                                        typedText += sample
                                        cursorPosition = typedText.length
                                    }
                                    QuickActionChip("خانه‌ها") {
                                        val sample = "خانه\u200Cها "
                                        typedText += sample
                                        cursorPosition = typedText.length
                                    }
                                    QuickActionChip("The quick brown fox") {
                                        val sample = "The quick brown fox "
                                        typedText += sample
                                        cursorPosition = typedText.length
                                    }
                                    QuickActionChip("پاک‌کردن متن") {
                                        typedText = ""
                                        cursorPosition = 0
                                    }
                                }

                                // Note Body content
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .padding(top = 4.dp)
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    if (typedText.isEmpty()) {
                                        Text(
                                            text = "اینجا کلیک کنید و با کیبورد زیر شروع به نوشتن کنید...\n(کلید نیم‌فاصله، لرزش و صدای کلیک آیفون فعال است)",
                                            color = Color.Gray,
                                            fontSize = 15.sp,
                                            lineHeight = 22.sp
                                        )
                                    } else {
                                        Text(
                                            text = typedText,
                                            fontSize = 16.sp,
                                            lineHeight = 24.sp,
                                            color = if (settings.isDarkMode) Color.White else Color.Black
                                        )
                                    }
                                }

                                // Status row at bottom of note
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "تعداد کاراکتر: ${typedText.length}",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = "نیم‌فاصله‌ها: ${typedText.count { it == '\u200C' }}",
                                        fontSize = 11.sp,
                                        color = IosGreen,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        // The Interactive iOS Keyboard
                        IosKeyboard(
                            modifier = Modifier.fillMaxWidth(),
                            isDarkMode = settings.isDarkMode,
                            isSoundEnabled = settings.isSoundEnabled,
                            isHapticEnabled = settings.isHapticEnabled,
                            isPopupEnabled = settings.isPopupEnabled,
                            useFarsiDigits = settings.useFarsiDigits,
                            initialLanguage = currentTestLanguage,
                            onCommitText = { text ->
                                val pos = cursorPosition.coerceIn(0, typedText.length)
                                val newText = typedText.substring(0, pos) + text + typedText.substring(pos)
                                typedText = newText
                                cursorPosition = pos + text.length
                            },
                            onDeleteText = {
                                val pos = cursorPosition.coerceIn(0, typedText.length)
                                if (pos > 0) {
                                    val newText = typedText.substring(0, pos - 1) + typedText.substring(pos)
                                    typedText = newText
                                    cursorPosition = pos - 1
                                }
                            },
                            onReturnKey = {
                                val pos = cursorPosition.coerceIn(0, typedText.length)
                                val newText = typedText.substring(0, pos) + "\n" + typedText.substring(pos)
                                typedText = newText
                                cursorPosition = pos + 1
                            },
                            onCursorMove = { delta ->
                                val newPos = (cursorPosition + delta).coerceIn(0, typedText.length)
                                cursorPosition = newPos
                            },
                            onVoiceInputRequested = {
                                Toast.makeText(context, "ورودی صوتی در حال حاضر در دسترس است", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }

                1 -> {
                    // Setup & System Activation Screen
                    ActivationScreen(
                        isEnabledInSystem = isEnabledInSystem,
                        onOpenInputMethodSettings = onOpenInputMethodSettings,
                        onOpenInputMethodPicker = onOpenInputMethodPicker
                    )
                }

                2 -> {
                    // Settings Screen
                    SettingsScreen(
                        settingsManager = settingsManager
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionChip(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(IosBlue.copy(alpha = 0.12f))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = IosBlue,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ActivationScreen(
    isEnabledInSystem: Boolean,
    onOpenInputMethodSettings: () -> Unit,
    onOpenInputMethodPicker: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = IosBlue)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "راهنمای فعال‌سازی کیبورد در سراسر اندروید",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "برای استفاده از این کیبورد در تمامی برنامه‌ها (تلگرام، واتساپ، اینستاگرام و پیامک)، کافیست دو مرحله زیر را انجام دهید:",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            }
        }

        // Step 1
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "مرحله ۱: فعال‌سازی در تنظیمات",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    if (isEnabledInSystem) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(IosGreen.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("فعال شده ✅", color = IosGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "در صفحه تنظیمات ورودی اندروید، سوئیچ کنار «iOS Keyboard» را روشن کنید.",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    lineHeight = 19.sp
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onOpenInputMethodSettings,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("enable_keyboard_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = IosBlue)
                ) {
                    Text("باز کردن تنظیمات کیبورد اندروید", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Step 2
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "مرحله ۲: انتخاب کیبورد پیش‌فرض",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "روی دکمه زیر بزنید و از لیست باز شده، «iOS Keyboard» را به عنوان کیبورد جاری انتخاب کنید.",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    lineHeight = 19.sp
                )
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onOpenInputMethodPicker,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("select_keyboard_button")
                ) {
                    Text("انتخاب کیبورد پیش‌فرض (Change Input Method)", fontWeight = FontWeight.Medium)
                }
            }
        }

        // Feature Highlights
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "ویژگی‌های اختصاصی کیبورد iOS:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(10.dp))
                FeatureRow("چیدمان استاندارد فارسی iOS", "محل دقیق حرف «و» در ردیف سوم، دقیقاً همانند تصویر ارسالی")
                FeatureRow("کلید نیم‌فاصله (ZWNJ)", "کلید اختصاصی با آیکون فلش و خط‌چین عمودی برای نگارش صحیح فارسی")
                FeatureRow("صدای کلیک طبیعی آیفون", "صدای چوبی ظریف و دلنشین کیبورد اپل با کمترین تاخیر")
                FeatureRow("لرزش لمسی (Haptic Feedback)", "بازخورد لمسی دقیق مشابه Taptic Engine آیفون")
                FeatureRow("ذره‌بین پیش‌نمایش کلید", "نمایش بزرگ‌نمایی کلید فشرده شده در بالای انگشت")
                FeatureRow("سوئیچ سریع زبان با دکمه کره زمین 🌐", "تغییر سریع بین حروف فارسی و انگلیسی")
            }
        }
    }
}

@Composable
fun FeatureRow(title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = IosGreen,
            modifier = Modifier
                .size(18.dp)
                .padding(top = 2.dp)
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(desc, fontSize = 11.sp, color = Color.Gray)
        }
    }
}

@Composable
fun SettingsScreen(
    settingsManager: KeyboardSettingsManager
) {
    val settings by settingsManager.settingsFlow.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "شخصی‌سازی کیبورد",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                // Theme Toggle
                SettingSwitchRow(
                    title = "پوسته تیره (Dark Mode)",
                    subtitle = "نمایش کیبورد با تم تیره شبیه به iOS Dark Mode",
                    icon = Icons.Default.DarkMode,
                    checked = settings.isDarkMode,
                    onCheckedChange = { settingsManager.setDarkMode(it) }
                )

                // Sound Toggle
                SettingSwitchRow(
                    title = "صدای کلیک آیفون (Sound)",
                    subtitle = "پخش صدای دلنشین کلیک چوبی هنگام فشردن هر کلید",
                    icon = Icons.Default.VolumeUp,
                    checked = settings.isSoundEnabled,
                    onCheckedChange = { settingsManager.setSoundEnabled(it) }
                )

                // Haptic Toggle
                SettingSwitchRow(
                    title = "لرزش لمسی کلیدها (Haptic)",
                    subtitle = "احساس ویبره ظریف هماهنگ با تایپ شبیه به Taptic Engine",
                    icon = Icons.Default.Vibration,
                    checked = settings.isHapticEnabled,
                    onCheckedChange = { settingsManager.setHapticEnabled(it) }
                )

                // Popup Toggle
                SettingSwitchRow(
                    title = "پیش‌نمایش ذره‌بینی (Key Popup)",
                    subtitle = "نمایش حباب بزرگ‌نمایی کاراکتر بالای کلید فشرده شده",
                    icon = Icons.Default.Visibility,
                    checked = settings.isPopupEnabled,
                    onCheckedChange = { settingsManager.setPopupEnabled(it) }
                )

                // Farsi Digits Toggle
                SettingSwitchRow(
                    title = "استفاده از اعداد فارسی",
                    subtitle = "نمایش ارقام به صورت ۱ ۲ ۳ در بخش صفحه اعداد",
                    icon = Icons.Default.Keyboard,
                    checked = settings.useFarsiDigits,
                    onCheckedChange = { settingsManager.setUseFarsiDigits(it) }
                )
            }
        }
    }
}

@Composable
fun SettingSwitchRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(IosBlue.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = IosBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(text = subtitle, fontSize = 11.sp, color = Color.Gray, lineHeight = 16.sp)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = IosGreen
            )
        )
    }
}
