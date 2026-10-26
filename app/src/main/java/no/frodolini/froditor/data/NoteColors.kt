package no.frodolini.froditor.data

import java.io.File
import java.util.Properties

/**
 * Fargane eit notat kan ha. Kvar farge har éin variant for lyst tema (lys pastell)
 * og éin for mørkt tema (dempa og mørk), slik at teksten alltid er lett å lese.
 * Verdiane er ARGB-tal: 0xFF + RRGGBB.
 */
enum class NoteColor(val light: Long?, val dark: Long?) {
    DEFAULT(null, null),
    RED(0xFFF6C9C4, 0xFF6E2A2A),
    ORANGE(0xFFFBDDB5, 0xFF6B3E1C),
    YELLOW(0xFFFFF1A8, 0xFF6A5A12),
    GREEN(0xFFD3EFC9, 0xFF2B5136),
    BLUE(0xFFCCE3F5, 0xFF1F4A6B),
    PURPLE(0xFFE2D3F0, 0xFF4A3260),
    GRAY(0xFFE4E2DE, 0xFF4A4744);

    companion object {
        /** Gjer om lagra tekst til farge. Ukjend eller manglande verdi blir standard. */
        fun fromName(name: String?): NoteColor =
            entries.firstOrNull { it.name == name } ?: DEFAULT
    }
}

/**
 * Hugsar fargen på kvart notat i ei lita fil (colors.properties), til dømes:
 *     notat-1761469200000=GREEN
 * Sjølve notata blir verande reine .txt-filer. Notat med standardfarge står ikkje i fila.
 */
class ColorStore(private val file: File) {

    private val props = Properties().apply {
        if (file.exists()) file.inputStream().use { load(it) }
    }

    fun get(id: String): NoteColor = NoteColor.fromName(props.getProperty(id))

    fun all(): Map<String, NoteColor> =
        props.stringPropertyNames().associateWith { get(it) }

    fun set(id: String, color: NoteColor) {
        if (color == NoteColor.DEFAULT) props.remove(id) else props.setProperty(id, color.name)
        write()
    }

    fun remove(id: String) {
        if (props.remove(id) != null) write()
    }

    /** Ryddar bort fargar for notat som ikkje finst lenger. */
    fun retainOnly(ids: Set<String>) {
        val gone = props.stringPropertyNames() - ids
        if (gone.isNotEmpty()) {
            gone.forEach { props.remove(it) }
            write()
        }
    }

    private fun write() {
        file.parentFile?.mkdirs()
        val tmp = File(file.path + ".tmp")
        tmp.outputStream().use { props.store(it, "Froditor: fargar på notat") }
        if (!tmp.renameTo(file)) {
            tmp.copyTo(file, overwrite = true)
            tmp.delete()
        }
    }
}
