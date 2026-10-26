package no.frodolini.froditor

import no.frodolini.froditor.data.ColorStore
import no.frodolini.froditor.data.NoteColor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/** Testar at fargane blir hugsa, også etter at appen er starta på nytt. */
class ColorStoreTest {

    @get:Rule
    val tmp = TemporaryFolder()

    private fun file() = tmp.root.resolve("colors.properties")

    @Test
    fun `fargen blir hugsa etter omstart`() {
        ColorStore(file()).set("notat-1", NoteColor.GREEN)
        assertEquals(NoteColor.GREEN, ColorStore(file()).get("notat-1"))
    }

    @Test
    fun `ukjent notat har standardfarge`() {
        assertEquals(NoteColor.DEFAULT, ColorStore(file()).get("notat-x"))
    }

    @Test
    fun `standardfarge blir ikkje lagra`() {
        val s = ColorStore(file())
        s.set("notat-1", NoteColor.RED)
        s.set("notat-1", NoteColor.DEFAULT)
        assertFalse(ColorStore(file()).all().containsKey("notat-1"))
    }

    @Test
    fun `ryddar bort fargar for sletta notat`() {
        val s = ColorStore(file())
        s.set("notat-1", NoteColor.RED)
        s.set("notat-2", NoteColor.BLUE)
        s.retainOnly(setOf("notat-2"))
        assertEquals(mapOf("notat-2" to NoteColor.BLUE), ColorStore(file()).all())
    }

    @Test
    fun `øydelagd verdi blir standard`() {
        file().writeText("notat-1=REGNBOGE\n")
        assertEquals(NoteColor.DEFAULT, ColorStore(file()).get("notat-1"))
    }
}
