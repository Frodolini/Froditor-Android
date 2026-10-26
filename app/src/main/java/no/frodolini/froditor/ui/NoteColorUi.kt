package no.frodolini.froditor.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import no.frodolini.froditor.R
import no.frodolini.froditor.data.NoteColor

/** Gjer om ein notatfarge til ein Compose-farge for gjeldande tema. Null = standard. */
@Composable
fun NoteColor.toColorOrNull(): Color? {
    val argb = if (isSystemInDarkTheme()) dark else light
    return argb?.let { Color(it) }
}

@Composable
fun NoteColor.label(): String = stringResource(
    when (this) {
        NoteColor.DEFAULT -> R.string.color_default
        NoteColor.RED -> R.string.color_red
        NoteColor.ORANGE -> R.string.color_orange
        NoteColor.YELLOW -> R.string.color_yellow
        NoteColor.GREEN -> R.string.color_green
        NoteColor.BLUE -> R.string.color_blue
        NoteColor.PURPLE -> R.string.color_purple
        NoteColor.GRAY -> R.string.color_gray
    },
)

/** Dialog med 8 fargerundingar i to rader. */
@Composable
fun ColorPickerDialog(
    current: NoteColor,
    onPick: (NoteColor) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.pick_color)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                NoteColor.entries.chunked(4).forEach { row ->
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        row.forEach { c -> ColorDot(c, selected = c == current) { onPick(c) } }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.close)) }
        },
    )
}

@Composable
private fun ColorDot(color: NoteColor, selected: Boolean, onClick: () -> Unit) {
    val fill = color.toColorOrNull() ?: MaterialTheme.colorScheme.surface
    val label = color.label()
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(fill)
            .border(
                width = if (selected) 3.dp else 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                shape = CircleShape,
            )
            .clickable(role = Role.RadioButton, onClick = onClick)
            .semantics {
                contentDescription = label
                this.selected = selected
            },
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            Icon(Icons.Filled.Check, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
        }
    }
}
