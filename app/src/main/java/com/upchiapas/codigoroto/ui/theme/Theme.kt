package com.upchiapas.codigoroto.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Colores de la app en modo claro.
private val LightColorScheme = lightColorScheme(
    primary = Indigo,
    onPrimary = Color.White,
    primaryContainer = IndigoClaro,
    onPrimaryContainer = Indigo,
    secondary = Violeta,
    background = Fondo,
    onBackground = TextoOscuro,
    surface = Fondo,
    onSurface = TextoOscuro,
    onSurfaceVariant = TextoSecundario,
    surfaceContainerLow = Color.White,
    outlineVariant = Borde
)

// Colores de la app en modo oscuro.
private val DarkColorScheme = darkColorScheme(
    primary = IndigoOscuro,
    primaryContainer = Color(0xFF2E2A5C),
    onPrimaryContainer = Color(0xFFE4E0FF),
    secondary = Violeta,
    background = FondoOscuro,
    surface = FondoOscuro,
    surfaceContainerLow = SuperficieOscura
)

// Tema de la app: siempre usa nuestros colores (sin color dinámico del celular).
@Composable
fun CodigoRotoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
