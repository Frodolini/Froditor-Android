package no.frodolini.froditor.data

import java.io.File

/**
 * Eitt notat. Teksten blir lagra som ei vanleg .txt-fil.
 * [id] er filnamnet utan ".txt".
 */
data class Note(
    val id: String,
    val text: String,
    val lastModified: Long,
) {
    /** Første linje med innhald blir brukt som tittel. */
    val title: String
        get() = nonEmptyLines().firstOrNull() ?: ""

    /** Andre linje med innhald blir vist som ei lita førehandsvising i lista. */
    val preview: String
        get() = nonEmptyLines().drop(1).firstOrNull() ?: ""

    private fun nonEmptyLines() =
        text.lineSequence().map { it.trim() }.filter { it.isNotEmpty() }
}

/**
 * Les og skriv notat som .txt-filer i ei mappe.
 * Klassen veit ingenting om Android, så han kan testast med vanlege JUnit-testar.
 */
class NoteRepository(private val dir: File) {

    init {
        dir.mkdirs()
    }

    /** Alle notat, nyaste først. */
    fun all(): List<Note> =
        dir.listFiles { f -> f.isFile && f.name.endsWith(".txt") }
            .orEmpty()
            .map { read(it) }
            .sortedByDescending { it.lastModified }

    fun get(id: String): Note? {
        val f = fileFor(id)
        return if (f.exists()) read(f) else null
    }

    /** Lagar ein ny, unik id basert på klokka, til dømes "notat-1761469200000". */
    fun newId(): String {
        var t = System.currentTimeMillis()
        while (fileFor("notat-$t").exists()) t++
        return "notat-$t"
    }

    /**
     * Lagrar teksten. Vi skriv først til ei mellombels fil og byter namn etterpå,
     * slik at notatet ikkje blir øydelagt om appen blir stoppa midt i lagringa.
     */
    fun save(id: String, text: String): Note {
        val target = fileFor(id)
        val tmp = File(dir, "$id.tmp")
        tmp.writeText(text, Charsets.UTF_8)
        if (!tmp.renameTo(target)) {
            target.writeText(text, Charsets.UTF_8)
            tmp.delete()
        }
        return read(target)
    }

    fun delete(id: String) {
        fileFor(id).delete()
    }

    private fun read(f: File) = Note(
        id = f.name.removeSuffix(".txt"),
        text = f.readText(Charsets.UTF_8),
        lastModified = f.lastModified(),
    )

    private fun fileFor(id: String): File {
        // Tryggleik: id-en skal berre innehalde bokstavar, tal og bindestrek,
        // slik at ingen kan lure appen til å skrive utanfor notatmappa.
        require(id.matches(Regex("[A-Za-z0-9-]+"))) { "Ugyldig notat-id: $id" }
        return File(dir, "$id.txt")
    }
}
