package com.upchiapas.codigoroto.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.upchiapas.codigoroto.R

// Fuente Poppins para toda la interfaz.
val Poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_bold, FontWeight.Bold)
)

// Fuente monoespaciada para el código.
val JetBrainsMono = FontFamily(Font(R.font.jetbrains_mono))

// Aplica Poppins a todos los estilos de texto de Material.
private val base = Typography()
val Typography = Typography(
    displayLarge = base.displayLarge.copy(fontFamily = Poppins),
    displayMedium = base.displayMedium.copy(fontFamily = Poppins),
    displaySmall = base.displaySmall.copy(fontFamily = Poppins),
    headlineLarge = base.headlineLarge.copy(fontFamily = Poppins),
    headlineMedium = base.headlineMedium.copy(fontFamily = Poppins),
    headlineSmall = base.headlineSmall.copy(fontFamily = Poppins),
    titleLarge = base.titleLarge.copy(fontFamily = Poppins),
    titleMedium = base.titleMedium.copy(fontFamily = Poppins),
    titleSmall = base.titleSmall.copy(fontFamily = Poppins),
    bodyLarge = base.bodyLarge.copy(fontFamily = Poppins),
    bodyMedium = base.bodyMedium.copy(fontFamily = Poppins),
    bodySmall = base.bodySmall.copy(fontFamily = Poppins),
    labelLarge = base.labelLarge.copy(fontFamily = Poppins),
    labelMedium = base.labelMedium.copy(fontFamily = Poppins),
    labelSmall = base.labelSmall.copy(fontFamily = Poppins)
)
