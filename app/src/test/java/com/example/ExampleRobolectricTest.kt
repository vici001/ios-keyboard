package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.keyboard.data.KeyboardLayouts
import com.example.keyboard.model.KeyType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("iOS Keyboard", appName)
    }

    @Test
    fun `verify persian ios keyboard layout`() {
        // Row 1 should have 11 keys starting with ض and ending with چ
        assertEquals(11, KeyboardLayouts.farsiRow1.size)
        assertEquals("ض", KeyboardLayouts.farsiRow1.first().primaryText)
        assertEquals("چ", KeyboardLayouts.farsiRow1.last().primaryText)

        // Row 2 should have 11 keys starting with ش and ending with گ
        assertEquals(11, KeyboardLayouts.farsiRow2.size)
        assertEquals("ش", KeyboardLayouts.farsiRow2.first().primaryText)
        assertEquals("گ", KeyboardLayouts.farsiRow2.last().primaryText)

        // Row 3 should have 11 keys: 10 letters and backspace at end
        assertEquals(11, KeyboardLayouts.farsiRow3.size)
        assertEquals("ظ", KeyboardLayouts.farsiRow3.first().primaryText)
        assertEquals(KeyType.BACKSPACE, KeyboardLayouts.farsiRow3.last().keyType)

        // Verify letter 'و' is in Row 3 (exactly as shown by the user's arrow)
        assertTrue(KeyboardLayouts.farsiRow3.any { it.primaryText == "و" })

        // Verify half-space (ZWNJ \u200C) key exists in bottom row
        val halfSpaceKey = KeyboardLayouts.farsiBottomRow.firstOrNull { it.keyType == KeyType.HALF_SPACE }
        assertTrue(halfSpaceKey != null)
        assertEquals("\u200C", halfSpaceKey?.primaryText)
    }
}
