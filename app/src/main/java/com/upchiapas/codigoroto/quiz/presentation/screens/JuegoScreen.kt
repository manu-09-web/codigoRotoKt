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
import com.upchiapas.codigoroto.quiz.presentation.components.TemaIcono
import com.upchiapas.codigoroto.quiz.presentation.components.color
import com.upchiapas.codigoroto.quiz.presentation.components.etiqueta
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import com.upchiapas.codigoroto.ui.theme.RojoIncorrecto
import com.upchiapas.codigoroto.ui.theme.RojoIncorrectoFondo
import com.upchiapas.codigoroto.ui.theme.VerdeCorrecto
import com.upchiapas.codigoroto.ui.theme.VerdeCorrectoFondo

// Pantalla del juego: muestra la pregunta actual.
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

// Pregunta, código, opciones y retroalimentación.
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
        ProgresoPorPregunta(uiState)
        EtiquetaTema(pregunta)
        Text(
            text = pregunta.enunciado,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        CodigoCard(codigo = pregunta.codigo)

        // Un botón por cada opción, con su letra A, B, C, D.
        uiState.opciones.forEachIndexed { indice, opcion ->
            OpcionRespuesta(
                letra = ('A' + indice).toString(),
                texto = opcion.texto,
                estado = opcion.estado,
                onClick = { onOpcionSeleccionada(opcion.texto) }
            )
        }

        // La tarjeta aparece solo después de responder.
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

// Una barrita por pregunta: verde si acertó, roja si falló, morada la actual, gris las que faltan.
@Composable
private fun ProgresoPorPregunta(uiState: QuizUiState) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(uiState.totalPreguntas) { indice ->
            val color = when {
                indice < uiState.respuestas.size ->
                    if (uiState.respuestas[indice]) VerdeCorrecto else RojoIncorrecto
                indice == uiState.indiceActual -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.outlineVariant
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .background(color, RoundedCornerShape(3.dp))
            )
        }
    }
}

// Tema (con su color) y dificultad de la pregunta.
@Composable
private fun EtiquetaTema(pregunta: Pregunta) {
    Row(
        modifier = Modifier
            .background(pregunta.tema.color().copy(alpha = 0.12f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TemaIcono(tema = pregunta.tema, tamano = 18.dp)
        Text(
            text = stringResource(
                R.string.mis_preguntas_detalle,
                stringResource(pregunta.tema.etiqueta()),
                stringResource(pregunta.dificultad.etiqueta())
            ),
            style = MaterialTheme.typography.labelLarge,
            color = pregunta.tema.color()
        )
    }
}

// Progreso, racha y puntos.
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

// Tarjeta de correcto/incorrecto con la explicación.
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
