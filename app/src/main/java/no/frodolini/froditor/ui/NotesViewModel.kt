package no.frodolini.froditor.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import no.frodolini.froditor.data.Note
import no.frodolini.froditor.data.NoteRepository
import java.io.File

/**
 * ViewModel: held på tilstanden til appen (lista og notatet som er ope).
 * Han overlever at skjermen blir snudd, i motsetnad til sjølve skjermbileta.
 */
class NotesViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = NoteRepository(File(app.filesDir, "notes"))

    /** Alle notat som blir viste i lista. */
    var notes by mutableStateOf<List<Note>>(emptyList())
        private set

    /** Id-en til notatet som er ope i editoren, eller null når lista blir vist. */
    var openNoteId by mutableStateOf<String?>(null)
        private set

    /** Teksten i editoren akkurat no. */
    var editorText by mutableStateOf("")
        private set

    private var savedText = ""
    private var saveJob: Job? = null

    init {
        refresh()
    }

    fun refresh() {
        notes = repo.all()
    }

    fun newNote() {
        openNoteId = repo.newId()
        editorText = ""
        savedText = ""
    }

    fun open(note: Note) {
        openNoteId = note.id
        editorText = note.text
        savedText = note.text
    }

    /** Blir kalla for kvart tastetrykk. Vi lagrar automatisk når brukaren tek ein liten pause. */
    fun onTextChange(text: String) {
        editorText = text
        saveJob?.cancel()
        saveJob = viewModelScope.launch {
            delay(AUTOSAVE_DELAY_MS)
            saveNow()
        }
    }

    /** Lagrar med ein gong. Eit tomt notat blir sletta i staden for å bli lagra. */
    fun saveNow() {
        saveJob?.cancel()
        val id = openNoteId ?: return
        if (editorText.isBlank()) {
            repo.delete(id)
            savedText = ""
        } else if (editorText != savedText) {
            repo.save(id, editorText)
            savedText = editorText
        }
    }

    /** Lukkar editoren og går tilbake til lista. */
    fun close() {
        saveNow()
        openNoteId = null
        refresh()
    }

    fun deleteOpenNote() {
        saveJob?.cancel()
        openNoteId?.let { repo.delete(it) }
        openNoteId = null
        editorText = ""
        savedText = ""
        refresh()
    }

    override fun onCleared() {
        saveNow()
    }

    companion object {
        const val AUTOSAVE_DELAY_MS = 600L
    }
}
