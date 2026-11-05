package com.example.keyboard.data

import com.example.keyboard.model.KeyModel
import com.example.keyboard.model.KeyType

object KeyboardLayouts {

    // -------------------------------------------------------------
    // PERSIAN (FARSI) LAYOUT - Exact replica of Apple iOS Persian
    // -------------------------------------------------------------
    val farsiRow1 = listOf(
        KeyModel("ض", alternates = listOf("ص")),
        KeyModel("ص", alternates = listOf("ض")),
        KeyModel("ق"),
        KeyModel("ف", alternates = listOf("ڤ")),
        KeyModel("غ", alternates = listOf("ع")),
        KeyModel("ع", alternates = listOf("غ")),
        KeyModel("ه", alternates = listOf("ة", "ۀ")),
        KeyModel("خ", alternates = listOf("ح")),
        KeyModel("ح", alternates = listOf("خ")),
        KeyModel("ج", alternates = listOf("چ")),
        KeyModel("چ", alternates = listOf("ج"))
    )

    val farsiRow2 = listOf(
        KeyModel("ش", alternates = listOf("س")),
        KeyModel("س", alternates = listOf("ش")),
        KeyModel("ی", alternates = listOf("ي", "ئ")),
        KeyModel("ب", alternates = listOf("پ")),
        KeyModel("ل", alternates = listOf("لا")),
        KeyModel("ا", alternates = listOf("آ", "أ", "إ", "ء")),
        KeyModel("ت", alternates = listOf("ة", "ط")),
        KeyModel("ن"),
        KeyModel("م"),
        KeyModel("ک", alternates = listOf("ك")),
        KeyModel("گ", alternates = listOf("ک"))
    )

    val farsiRow3 = listOf(
        KeyModel("ظ", alternates = listOf("ط")),
        KeyModel("ط", alternates = listOf("ظ")),
        KeyModel("ژ", alternates = listOf("ز")),
        KeyModel("ز", alternates = listOf("ژ")),
        KeyModel("ر"),
        KeyModel("ذ", alternates = listOf("د")),
        KeyModel("د", alternates = listOf("ذ")),
        KeyModel("پ", alternates = listOf("ب")),
        KeyModel("و", alternates = listOf("ؤ")),
        KeyModel("ث", alternates = listOf("س", "ص")),
        KeyModel("⌫", keyType = KeyType.BACKSPACE, weight = 1.15f)
    )

    val farsiBottomRow = listOf(
        KeyModel("۱۲۳", keyType = KeyType.MODE_CHANGE, weight = 1.35f),
        KeyModel("😀", keyType = KeyType.EMOJI, weight = 1.0f),
        KeyModel(" ", displayText = "فاصله", keyType = KeyType.SPACE, weight = 4.2f),
        KeyModel("\u200C", displayText = "‌", keyType = KeyType.HALF_SPACE, weight = 1.15f),
        KeyModel("⏎", keyType = KeyType.RETURN, weight = 1.35f)
    )

    // -------------------------------------------------------------
    // ENGLISH LAYOUT - Exact replica of Apple iOS QWERTY
    // -------------------------------------------------------------
    val englishRow1 = listOf(
        KeyModel("q", alternates = listOf("1")),
        KeyModel("w", alternates = listOf("2")),
        KeyModel("e", alternates = listOf("3", "é", "è", "ê", "ë")),
        KeyModel("r", alternates = listOf("4")),
        KeyModel("t", alternates = listOf("5")),
        KeyModel("y", alternates = listOf("6", "ÿ")),
        KeyModel("u", alternates = listOf("7", "ú", "ù", "û", "ü")),
        KeyModel("i", alternates = listOf("8", "í", "ì", "î", "ï")),
        KeyModel("o", alternates = listOf("9", "ó", "ò", "ô", "ö", "ø", "œ")),
        KeyModel("p", alternates = listOf("0"))
    )

    val englishRow2 = listOf(
        KeyModel("a", alternates = listOf("á", "à", "â", "ä", "æ", "ã", "å")),
        KeyModel("s", alternates = listOf("ß", "ś", "š")),
        KeyModel("d"),
        KeyModel("f"),
        KeyModel("g"),
        KeyModel("h"),
        KeyModel("j"),
        KeyModel("k"),
        KeyModel("l")
    )

    val englishRow3 = listOf(
        KeyModel("⬆", keyType = KeyType.SHIFT, weight = 1.3f),
        KeyModel("z"),
        KeyModel("x"),
        KeyModel("c", alternates = listOf("ç", "ć", "č")),
        KeyModel("v"),
        KeyModel("b"),
        KeyModel("n", alternates = listOf("ñ", "ń")),
        KeyModel("m"),
        KeyModel("⌫", keyType = KeyType.BACKSPACE, weight = 1.3f)
    )

    val englishBottomRow = listOf(
        KeyModel("123", keyType = KeyType.MODE_CHANGE, weight = 1.35f),
        KeyModel("😀", keyType = KeyType.EMOJI, weight = 1.0f),
        KeyModel(" ", displayText = "space", keyType = KeyType.SPACE, weight = 4.5f),
        KeyModel("return", displayText = "return", keyType = KeyType.RETURN, weight = 1.4f)
    )

    // -------------------------------------------------------------
    // NUMBERS & SYMBOLS LAYOUTS
    // -------------------------------------------------------------
    fun getNumbersRow1(useFarsiDigits: Boolean): List<KeyModel> {
        val digits = if (useFarsiDigits) {
            listOf("۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹", "۰")
        } else {
            listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
        }
        return digits.map { KeyModel(it) }
    }

    val numbersRow2 = listOf(
        KeyModel("-"),
        KeyModel("/"),
        KeyModel(":"),
        KeyModel(";"),
        KeyModel("("),
        KeyModel(")"),
        KeyModel("﷼", alternates = listOf("$", "€", "£", "¥")),
        KeyModel("&"),
        KeyModel("@"),
        KeyModel("\"")
    )

    val numbersRow3 = listOf(
        KeyModel("#+=", keyType = KeyType.SYMBOL_CHANGE, weight = 1.3f),
        KeyModel("."),
        KeyModel("،", alternates = listOf(",")),
        KeyModel("؟", alternates = listOf("?")),
        KeyModel("!"),
        KeyModel("'", alternates = listOf("`", "«", "»")),
        KeyModel("⌫", keyType = KeyType.BACKSPACE, weight = 1.3f)
    )

    fun getNumbersBottomRow(isFarsi: Boolean): List<KeyModel> {
        val label = if (isFarsi) "الفبا" else "ABC"
        return listOf(
            KeyModel(label, keyType = KeyType.MODE_CHANGE, weight = 1.4f),
            KeyModel("😀", keyType = KeyType.EMOJI, weight = 1.0f),
            KeyModel(" ", displayText = if (isFarsi) "فاصله" else "space", keyType = KeyType.SPACE, weight = 4.5f),
            KeyModel("⏎", displayText = if (isFarsi) "⏎" else "return", keyType = KeyType.RETURN, weight = 1.4f)
        )
    }

    // Additional symbols row (#+= mode)
    val symbolsRow1 = listOf(
        KeyModel("["),
        KeyModel("]"),
        KeyModel("{"),
        KeyModel("}"),
        KeyModel("#"),
        KeyModel("%"),
        KeyModel("^"),
        KeyModel("*"),
        KeyModel("+"),
        KeyModel("=")
    )

    val symbolsRow2 = listOf(
        KeyModel("_"),
        KeyModel("\\"),
        KeyModel("|"),
        KeyModel("~"),
        KeyModel("<"),
        KeyModel(">"),
        KeyModel("«"),
        KeyModel("»"),
        KeyModel("•"),
        KeyModel("°")
    )

    val symbolsRow3 = listOf(
        KeyModel("123", keyType = KeyType.SYMBOL_CHANGE, weight = 1.3f),
        KeyModel("."),
        KeyModel("،", alternates = listOf(",")),
        KeyModel("؟", alternates = listOf("?")),
        KeyModel("!"),
        KeyModel("'", alternates = listOf("`")),
        KeyModel("⌫", keyType = KeyType.BACKSPACE, weight = 1.3f)
    )

    // Curated iOS Popular Emojis
    val popularEmojis = listOf(
        "😂", "❤️", "😍", "😊", "🙏", "😭", "😘", "👍", "🔥", "✨",
        "🥰", "🥺", "🎉", "👏", "😁", "🌹", " Persian", "🇮🇷", "🇮🇸", "🇺🇸",
        "👌", "😎", "🤔", "💪", "🙌", "🤍", "💙", "💖", "🌟", "🌸",
        "🎂", "☕", "🍕", "🍔", "🍉", "🍓", "🚗", "✈️", "🚀", "📱"
    ).filter { it.length <= 4 && !it.contains(" ") }
}
