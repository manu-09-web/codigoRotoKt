package com.upchiapas.codigoroto.quiz.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.upchiapas.codigoroto.R
import com.upchiapas.codigoroto.quiz.presentation.screens.EstadoOpcion
import com.upchiapas.codigoroto.ui.theme.RojoIncorrecto
import com.upchiapas.codigoroto.ui.theme.RojoIncorrectoFondo
import com.upchiapas.codigoroto.ui.theme.VerdeCorrecto
import com.upchiapas.codigoroto.ui.theme.VerdeCorrectoFondo

// Botón de una opción con su letra (A, B, C, D); su color depende del estado.
@Composable
fun OpcionRespuesta(
    letra: String,
    texto: String,
    estado: EstadoOpcion,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Colores según el estado.
    val (fondo, borde) = when (estado) {
        EstadoOpcion.CORRECTA -> VerdeCorrectoFondo to VerdeCorrecto
        EstadoOpcion.INCORRECTA -> RojoIncorrectoFondo to RojoIncorrecto
        else -> MaterialTheme.colorScheme.surfaceContainerLow to MaterialTheme.colorScheme.outlineVariant
    }
    val resaltada = estado == EstadoOpcion.CORRECTA || estado == EstadoOpcion.INCORRECTA

    // Lo que va en el círculo: ✓ si es correcta, ✕ si es incorrecta, o la letra.
    val marca = when (estado) {
        EstadoOpcion.CORRECTA -> stringResource(R.string.marca_correcta)
        EstadoOpcion.INCORRECTA -> stringResource(R.string.marca_incorrecta)
        else -> letra
    }
    val colorCirculo = if (resaltada) borde else MaterialTheme.colorScheme.primaryContainer
    val colorMarca = if (resaltada) Color.White else MaterialTheme.colorScheme.primary

    OutlinedCard(
        onClick = onClick,
        enabled = estado == EstadoOpcion.NORMAL, // solo se puede tocar antes de responder
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
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
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(colorCirculo, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = marca, color = colorMarca, fontWeight = FontWeight.Bold)
            }
            Text(
                text = texto,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (resaltada) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}
