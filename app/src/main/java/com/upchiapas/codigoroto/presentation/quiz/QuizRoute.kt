package com.upchiapas.codigoroto.presentation.quiz

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Conecta el QuizViewModel con las pantallas del quiz.
 * Lee el estado una sola vez y, según la fase, muestra Inicio o Juego.
 */
@Composable
fun QuizRoute(
    modifier: Modifier = Modifier,
    viewModel: QuizViewModel = viewModel(factory = QuizViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState.fase) {
        FaseQuiz.Cargando -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        FaseQuiz.Configurando -> InicioScreen(
            config = uiState.config,
            onTemaSeleccionado = viewModel::onTemaSeleccionado,
            onTodosLosTemas = viewModel::onTodosLosTemas,
            onDificultadSeleccionada = viewModel::onDificultadSeleccionada,
            onNumeroPreguntasCambiado = viewModel::onNumeroPreguntasCambiado,
            onIniciarPartida = viewModel::onIniciarPartida,
            modifier = modifier
        )

        else -> JuegoScreen(
            uiState = uiState,
            onOpcionSeleccionada = viewModel::onOpcionSeleccionada,
            onSiguiente = viewModel::onSiguiente,
            onVolverAlInicio = viewModel::onVolverAlInicio,
            modifier = modifier
        )
    }
}
