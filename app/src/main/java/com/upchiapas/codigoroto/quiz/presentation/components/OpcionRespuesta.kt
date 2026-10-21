package com.upchiapas.codigoroto.quiz.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.upchiapas.codigoroto.quiz.presentation.screens.EstadoOpcion
import com.upchiapas.codigoroto.ui.theme.RojoIncorrecto
import com.upchiapas.codigoroto.ui.theme.RojoIncorrectoFondo
import com.upchiapas.codigoroto.ui.theme.VerdeCorrecto
import com.upchiapas.codigoroto.ui.theme.VerdeCorrectoFondo

/**
 * Botón de una opción de respuesta. Componente reutilizable y stateless:
 * - recibe el texto y el estado (NORMAL, CORRECTA, INCORRECTA, DESHABILITADA) ya decidido por el ViewModel;
 * - avisa con onClick cuando lo tocan (state hoisting); no guarda nada por su cuenta.
 */
@Composable
fun OpcionRespuesta(
    texto: String,
    estado: EstadoOpcion,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Elige el color de fondo y del borde según el estado (Pair con "to", separado con val (a, b)).
    val (fondo, borde) = when (estado) {
        EstadoOpcion.CORRECTA -> VerdeCorrectoFondo to VerdeCorrecto
        EstadoOpcion.INCORRECTA -> RojoIncorrectoFondo to RojoIncorrecto
        else -> Color.Transparent to MaterialTheme.colorScheme.outlineVariant
    }
    val resaltada = estado == EstadoOpcion.CORRECTA || estado == EstadoOpcion.INCORRECTA

    OutlinedCard(
        onClick = onClick,
        enabled = estado == EstadoOpcion.NORMAL, // después de responder ya no se puede tocar
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(if (resaltada) 2.dp else 1.dp, borde),
        colors = CardDefaults.outlinedCardColors(
            containerColor = fondo,
            disabledContainerColor = fondo,
            disabledContentColor = if (resaltada) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            }
        )
    ) {
        Text(
            text = texto,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (resaltada) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
