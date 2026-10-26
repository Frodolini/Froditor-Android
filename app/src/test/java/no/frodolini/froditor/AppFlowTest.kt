package no.frodolini.froditor

import android.app.Application
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.ViewModelProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import no.frodolini.froditor.ui.NotesViewModel
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

    /**
     * Mappa der appen faktisk lagrar. NB: Robolectric kan gje ViewModel-en app-objektet
     * frå ein tidlegare test (fabrikken i Android hugsar det). På ein ekte telefon finst
     * det berre eitt app-objekt, så dette gjeld berre testane.
     */
    private fun dataDir(): File {
        val vm = ViewModelProvider(rule.activity)[NotesViewModel::class.java]
        return vm.getApplication<Application>().filesDir
    }

    private fun noteFiles() =
        File(dataDir(), "notes").listFiles { f -> f.name.endsWith(".txt") }.orEmpty()

    private fun colorsFileText() = File(dataDir(), "colors.properties").readText()

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
        val files = noteFiles()
        assertEquals(1, files.size)
        assertEquals("Handleliste\nmjølk", files[0].readText())

        // Opne att og slett
        rule.onNodeWithText("Handleliste").performClick()
        rule.onNodeWithContentDescription("Slett").performClick()
        rule.onNodeWithText("Slette notatet?").assertIsDisplayed()
        rule.onNodeWithText("Slett").performClick()

        rule.onNodeWithText("Ingen notat", substring = true).assertIsDisplayed()
        assertTrue(noteFiles().isEmpty())
    }

    @Test
    fun velFargePaaNotat() {
        rule.onNodeWithContentDescription("Nytt notat").performClick()
        rule.onNodeWithTag("editor").performTextInput("Fargetest")
        rule.onNodeWithContentDescription("Farge").performClick()
        rule.onNodeWithText("Vel farge").assertIsDisplayed()
        rule.onNodeWithContentDescription("Grøn").performClick()
        rule.onNodeWithText("Vel farge").assertDoesNotExist()
        rule.onNodeWithContentDescription("Tilbake").performClick()

        val colors = colorsFileText()
        assertTrue(colors, colors.contains("=GREEN"))

        // Opne att: grøn skal vere vald i fargeveljaren
        rule.onNodeWithText("Fargetest").performClick()
        rule.onNodeWithContentDescription("Farge").performClick()
        rule.onNodeWithContentDescription("Grøn").assertIsSelected()
        rule.onNodeWithText("Lukk").performClick()

        // Sletting fjernar fargen òg
        rule.onNodeWithContentDescription("Slett").performClick()
        rule.onNodeWithText("Slett").performClick()
        val after = colorsFileText()
        assertTrue(after, !after.contains("=GREEN"))
    }

    @Test
    fun tomtNotatBlirIkkjeLagra() {
        rule.onNodeWithContentDescription("Nytt notat").performClick()
        rule.onNodeWithContentDescription("Tilbake").performClick()
        rule.onNodeWithText("Ingen notat", substring = true).assertIsDisplayed()
        assertTrue(noteFiles().isEmpty())
    }
}
