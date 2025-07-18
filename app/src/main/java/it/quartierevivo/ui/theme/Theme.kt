package it.quartierevivo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = VerdeOliva,
    onPrimary = Bianco,
    background = Bianco,
    onBackground = Nero
)

private val DarkColors = darkColorScheme(
    primary = VerdeOliva,
    onPrimary = Bianco,
    background = Nero,
    onBackground = Bianco
)

@Composable
fun QuartiereVivoTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        content = content
    )
}
