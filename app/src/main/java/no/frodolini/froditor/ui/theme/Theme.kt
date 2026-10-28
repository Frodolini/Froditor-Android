package no.frodolini.froditor.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val FroditorBlue = Color(0xFF2F5D8A)

private val LightColors = lightColorScheme(primary = FroditorBlue)
private val DarkColors = darkColorScheme(primary = Color(0xFF9FC3EA))

/**
 * Fargetema. På Android 12 og nyare hentar appen fargane frå bakgrunnsbiletet
 * på telefonen ("Material You"). Elles blir Froditor-blått brukt.
 * Lyst/mørkt tema følgjer innstillinga på telefonen.
 */
@Composable
fun FroditorTheme(content: @Composable () -> Unit) {
    val dark = isSystemInDarkTheme()
    val colors = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = LocalContext.current
            if (dark) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }
        dark -> DarkColors
        else -> LightColors
    }
    MaterialTheme(colorScheme = colors, content = content)
}

private val FroditorGreen = Color(0xFF2E7D32)
private val FroditorGreenDark = Color(0xFF1B5E20)

/**
 * Grøn tittellinje, som i Froditor for Linux. Fargen er den same uansett
 * Material You-fargane frå telefonen, men litt mørkare i mørkt tema.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun froditorTopBarColors(): TopAppBarColors {
    val green = if (isSystemInDarkTheme()) FroditorGreenDark else FroditorGreen
    return TopAppBarDefaults.topAppBarColors(
        containerColor = green,
        scrolledContainerColor = green,
        navigationIconContentColor = Color.White,
        titleContentColor = Color.White,
        actionIconContentColor = Color.White,
    )
}
