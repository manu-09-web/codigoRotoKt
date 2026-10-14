package com.upchiapas.codigoroto.presentation.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.upchiapas.codigoroto.R
import com.upchiapas.codigoroto.data.model.Pregunta
import com.upchiapas.codigoroto.presentation.components.CodigoCard
import com.upchiapas.codigoroto.presentation.components.OpcionRespuesta
import com.upchiapas.codigoroto.ui.theme.RojoIncorrecto
import com.upchiapas.codigoroto.ui.theme.RojoIncorrectoFondo
import com.upchiapas.codigoroto.ui.theme.VerdeCorrecto
import com.upchiapas.codigoroto.ui.theme.VerdeCorrectoFondo

/** Conecta el ViewModel con la pantalla: lee el estado y pasa los eventos. */
@Composable
fun JuegoRoute(
    modifier: Modifier = Modifier,
    viewModel: QuizViewModel = viewModel(factory = QuizViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    JuegoScreen(
        uiState = uiState,
        onOpcionSeleccionada = viewModel::onOpcionSeleccionada,
        onSiguiente = viewModel::onSiguiente,
        onJugarDeNuevo = viewModel::onJugarDeNuevo,
        modifier = modifier
    )
}

/** Pantalla sin estado propio: solo dibuja lo que recibe y avisa con lambdas. */
@Composable
fun JuegoScreen(
    uiState: QuizUiState,
    onOpcionSeleccionada: (String) -> Unit,
    onSiguiente: () -> Unit,
    onJugarDeNuevo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pregunta = uiState.preguntaActual
    when {
        uiState.fase == FaseQuiz.Cargando -> Cargando(modifier)
        uiState.fase == FaseQuiz.Finalizada -> FinDePartida(uiState, onJugarDeNuevo, modifier)
        pregunta != null -> PreguntaActual(uiState, pregunta, onOpcionSeleccionada, onSiguiente, modifier)
    }
}

@Composable
private fun Cargando(modifier: Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

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

        uiState.opciones.forEach { opcion ->
            OpcionRespuesta(
                texto = opcion.texto,
                estado = opcion.estado,
                onClick = { onOpcionSeleccionada(opcion.texto) }
            )
        }

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

@Composable
private fun FinDePartida(
    uiState: QuizUiState,
    onJugarDeNuevo: () -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.fin_titulo),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.fin_resumen, uiState.aciertos, uiState.totalPreguntas, uiState.puntaje),
            style = MaterialTheme.typography.titleMedium
        )
        Button(onClick = onJugarDeNuevo) {
            Text(text = stringResource(R.string.fin_jugar_de_nuevo))
        }
    }
}
