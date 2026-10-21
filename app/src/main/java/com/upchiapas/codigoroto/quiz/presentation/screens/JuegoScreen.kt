package com.upchiapas.codigoroto.quiz.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.upchiapas.codigoroto.R
import com.upchiapas.codigoroto.quiz.data.model.Pregunta
import com.upchiapas.codigoroto.quiz.presentation.components.CodigoCard
import com.upchiapas.codigoroto.quiz.presentation.components.OpcionRespuesta
import com.upchiapas.codigoroto.ui.theme.RojoIncorrecto
import com.upchiapas.codigoroto.ui.theme.RojoIncorrectoFondo
import com.upchiapas.codigoroto.ui.theme.VerdeCorrecto
import com.upchiapas.codigoroto.ui.theme.VerdeCorrectoFondo

/**
 * Pantalla del juego. Es stateless (sin estado propio): solo dibuja lo que recibe en uiState
 * y avisa al ViewModel con lambdas cuando el usuario toca algo (state hoisting).
 * No decide nada: los colores, puntos y textos ya vienen calculados en el estado.
 */
@Composable
fun JuegoScreen(
    uiState: QuizUiState,
    onOpcionSeleccionada: (String) -> Unit,
    onSiguiente: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pregunta = uiState.preguntaActual ?: return
    PreguntaActual(uiState, pregunta, onOpcionSeleccionada, onSiguiente, modifier)
}

/** Muestra la pregunta actual: encabezado, progreso, código, opciones y la retroalimentación. */
@Composable
private fun PreguntaActual(
    uiState: QuizUiState,
    pregunta: Pregunta,
    onOpcionSeleccionada: (String) -> Unit,
    onSiguiente: () -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        EncabezadoJuego(uiState)
        LinearProgressIndicator(
            progress = { uiState.progreso },
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = pregunta.enunciado,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        CodigoCard(codigo = pregunta.codigo)

        // Una OpcionRespuesta por cada opción; al tocarla se avisa al ViewModel con su texto.
        uiState.opciones.forEach { opcion ->
            OpcionRespuesta(
                texto = opcion.texto,
                estado = opcion.estado,
                onClick = { onOpcionSeleccionada(opcion.texto) }
            )
        }

        // as? intenta convertir la fase a Respondida; si es otra fase, queda null.
        // AnimatedVisibility muestra la tarjeta con animación solo cuando ya se respondió.
        val respondida = uiState.fase as? FaseQuiz.Respondida
        AnimatedVisibility(visible = respondida != null) {
            if (respondida != null) {
                Retroalimentacion(
                    esCorrecta = respondida.esCorrecta,
                    explicacion = pregunta.explicacion,
                    onSiguiente = onSiguiente
                )
            }
        }
    }
}

/** "Pregunta 3 de 10" a la izquierda y "Racha · puntos" a la derecha. Los textos salen de strings.xml. */
@Composable
private fun EncabezadoJuego(uiState: QuizUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.juego_progreso, uiState.numeroPregunta, uiState.totalPreguntas),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = stringResource(R.string.juego_racha_puntos, uiState.racha, uiState.puntaje),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

/** Tarjeta verde o roja con "¡Correcto!"/"Incorrecto", la explicación y el botón Siguiente. */
@Composable
private fun Retroalimentacion(
    esCorrecta: Boolean,
    explicacion: String,
    onSiguiente: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (esCorrecta) VerdeCorrectoFondo else RojoIncorrectoFondo
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(if (esCorrecta) R.string.juego_correcto else R.string.juego_incorrecto),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (esCorrecta) VerdeCorrecto else RojoIncorrecto
            )
            if (explicacion.isNotBlank()) {
                Text(text = explicacion, style = MaterialTheme.typography.bodyMedium)
            }
            Button(onClick = onSiguiente, modifier = Modifier.fillMaxWidth()) {
                Text(text = stringResource(R.string.juego_siguiente))
            }
        }
    }
}
