package no.frodolini.froditor.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.res.painterResource
import no.frodolini.froditor.data.NoteColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import no.frodolini.froditor.R
import no.frodolini.froditor.ui.theme.froditorTopBarColors

/** Skjermbiletet der du skriv. Teksten blir lagra automatisk. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    text: String,
    color: NoteColor,
    onTextChange: (String) -> Unit,
    onColorChange: (NoteColor) -> Unit,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit,
) {
    var confirmDelete by rememberSaveable { mutableStateOf(false) }
    var pickColor by rememberSaveable { mutableStateOf(false) }
    val focus = remember { FocusRequester() }

    // Heile skjermen, bortsett frå den grøne tittellinja, får fargen til notatet.
    val bg = color.toColorOrNull() ?: MaterialTheme.colorScheme.surface

    Scaffold(
        containerColor = bg,
        contentColor = MaterialTheme.colorScheme.onSurface,
        topBar = {
            TopAppBar(
                colors = froditorTopBarColors(),
                title = { Text(stringResource(R.string.note)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
                    }
                },
                actions = {
                    IconButton(onClick = { pickColor = true }) {
                        Icon(painterResource(R.drawable.ic_palette), stringResource(R.string.color))
                    }
                    IconButton(onClick = onShare, enabled = text.isNotBlank()) {
                        Icon(Icons.Filled.Share, stringResource(R.string.share))
                    }
                    IconButton(onClick = { confirmDelete = true }) {
                        Icon(Icons.Filled.Delete, stringResource(R.string.delete))
                    }
                },
            )
        },
    ) { padding ->
        TextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .focusRequester(focus)
                .testTag("editor"),
            placeholder = { Text(stringResource(R.string.placeholder)) },
            textStyle = MaterialTheme.typography.bodyLarge,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
        )
    }

    // Eit nytt, tomt notat får tastaturet opp med ein gong.
    LaunchedEffect(Unit) {
        if (text.isEmpty()) focus.requestFocus()
    }

    if (pickColor) {
        ColorPickerDialog(
            current = color,
            onPick = {
                onColorChange(it)
                pickColor = false
            },
            onDismiss = { pickColor = false },
        )
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text(stringResource(R.string.delete_title)) },
            text = { Text(stringResource(R.string.delete_text)) },
            confirmButton = {
                TextButton(onClick = {
                    confirmDelete = false
                    onDelete()
                }) { Text(stringResource(R.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) { Text(stringResource(R.string.cancel)) }
            },
        )
    }
}
