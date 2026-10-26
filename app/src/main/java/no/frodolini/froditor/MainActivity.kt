package no.frodolini.froditor

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import no.frodolini.froditor.ui.EditorScreen
import no.frodolini.froditor.ui.NoteListScreen
import no.frodolini.froditor.ui.NotesViewModel
import no.frodolini.froditor.ui.theme.FroditorTheme

/**
 * Inngangen til appen. Ei Activity er eit "vindauge" i Android.
 * Her bestemmer vi berre kva skjermbilete som skal visast: lista eller editoren.
 */
class MainActivity : ComponentActivity() {

    private val vm: NotesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FroditorTheme {
                if (vm.openNoteId == null) {
                    NoteListScreen(
                        notes = vm.notes,
                        colors = vm.noteColors,
                        onNoteClick = vm::open,
                        onNewNote = vm::newNote,
                    )
                } else {
                    // Tilbake-knappen på telefonen lukkar notatet i staden for appen.
                    BackHandler { vm.close() }
                    EditorScreen(
                        text = vm.editorText,
                        color = vm.editorColor,
                        onTextChange = vm::onTextChange,
                        onColorChange = vm::setColor,
                        onBack = vm::close,
                        onShare = { share(vm.editorText) },
                        onDelete = vm::deleteOpenNote,
                    )
                }
            }
        }
    }

    /** Lagrar når appen går i bakgrunnen, så ingenting går tapt om Android stoppar han. */
    override fun onStop() {
        super.onStop()
        vm.saveNow()
    }

    private fun share(text: String) {
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_SUBJECT, text.lineSequence().firstOrNull()?.trim().orEmpty())
        }
        startActivity(Intent.createChooser(send, null))
    }
}
