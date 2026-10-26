package no.frodolini.froditor

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * Testar heile appen slik ein brukar ville gjort det: trykk +, skriv, gå tilbake,
 * opne att og slett. Robolectric køyrer dette utan telefon eller emulator.
 */
@RunWith(AndroidJUnit4::class)
class AppFlowTest {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    private fun notesDir() = File(rule.activity.filesDir, "notes")

    @Test
    fun skrivLagreOpneOgSlett() {
        rule.onNodeWithText("Ingen notat", substring = true).assertIsDisplayed()

        // Nytt notat
        rule.onNodeWithContentDescription("Nytt notat").performClick()
        rule.onNodeWithTag("editor").performTextInput("Handleliste\nmjølk")
        rule.onNodeWithContentDescription("Tilbake").performClick()

        // Notatet er i lista og lagra som .txt
        rule.onNodeWithText("Handleliste").assertIsDisplayed()
        rule.onNodeWithText("mjølk").assertIsDisplayed()
        val files = notesDir().listFiles { f -> f.name.endsWith(".txt") }!!
        assertEquals(1, files.size)
        assertEquals("Handleliste\nmjølk", files[0].readText())

        // Opne att og slett
        rule.onNodeWithText("Handleliste").performClick()
        rule.onNodeWithContentDescription("Slett").performClick()
        rule.onNodeWithText("Slette notatet?").assertIsDisplayed()
        rule.onNodeWithText("Slett").performClick()

        rule.onNodeWithText("Ingen notat", substring = true).assertIsDisplayed()
        assertTrue(notesDir().listFiles { f -> f.name.endsWith(".txt") }!!.isEmpty())
    }

    @Test
    fun tomtNotatBlirIkkjeLagra() {
        rule.onNodeWithContentDescription("Nytt notat").performClick()
        rule.onNodeWithContentDescription("Tilbake").performClick()
        rule.onNodeWithText("Ingen notat", substring = true).assertIsDisplayed()
        assertTrue(notesDir().listFiles { f -> f.name.endsWith(".txt") }.orEmpty().isEmpty())
    }
}
