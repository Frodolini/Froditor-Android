package no.frodolini.froditor

import no.frodolini.froditor.data.NoteRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/** Vanlege JUnit-testar for lagringa. Køyrer utan Android. */
class NoteRepositoryTest {

    @get:Rule
    val tmp = TemporaryFolder()

    @Test
    fun `lagrar og les eit notat`() {
        val repo = NoteRepository(tmp.root)
        val id = repo.newId()
        repo.save(id, "Handleliste\nmjølk\nbrød")

        val note = repo.get(id)!!
        assertEquals("Handleliste", note.title)
        assertEquals("mjølk", note.preview)
        assertTrue(tmp.root.resolve("$id.txt").exists())
    }

    @Test
    fun `nyaste notat kjem først`() {
        val repo = NoteRepository(tmp.root)
        val a = repo.newId(); repo.save(a, "A")
        tmp.root.resolve("$a.txt").setLastModified(1_000)
        val b = repo.newId(); repo.save(b, "B")
        tmp.root.resolve("$b.txt").setLastModified(2_000)

        assertEquals(listOf("B", "A"), repo.all().map { it.title })
    }

    @Test
    fun `slettar notat`() {
        val repo = NoteRepository(tmp.root)
        val id = repo.newId()
        repo.save(id, "Bort med dette")
        repo.delete(id)
        assertNull(repo.get(id))
        assertTrue(repo.all().isEmpty())
    }

    @Test
    fun `tittel hoppar over tomme linjer`() {
        val repo = NoteRepository(tmp.root)
        val id = repo.newId()
        val note = repo.save(id, "\n\n   Tittel her  \n\nmeir")
        assertEquals("Tittel her", note.title)
        assertEquals("meir", note.preview)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `nektar farlege filnamn`() {
        NoteRepository(tmp.root).save("../utanfor", "nei")
    }

    @Test
    fun `nye id-ar er unike`() {
        val repo = NoteRepository(tmp.root)
        val a = repo.newId(); repo.save(a, "x")
        val b = repo.newId()
        assertTrue(a != b)
    }
}
