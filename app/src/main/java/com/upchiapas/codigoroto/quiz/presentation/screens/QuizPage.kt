package com.upchiapas.codigoroto.quiz.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.upchiapas.codigoroto.CodigoRotoApp
import com.upchiapas.codigoroto.quiz.presentation.viewmodels.QuizViewModel

// Conecta el ViewModel con las pantallas y muestra la que toca según la fase.
@Composable
fun QuizPage(
    modifier: Modifier = Modifier,
    // Obtiene el ViewModel usando la Factory del QuizModule.
    viewModel: QuizViewModel = viewModel(
        factory = (LocalContext.current.applicationContext as CodigoRotoApp).quizModule.factoryQuiz
    )
) {
    // Escucha el estado; cuando cambia, la pantalla se vuelve a dibujar.
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState.fase) {
        FaseQuiz.Cargando -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        FaseQuiz.Configurando -> InicioScreen(
            config = uiState.config,
            estadisticas = uiState.estadisticas,
            onTemaSeleccionado = viewModel::onTemaSeleccionado,
            onTodosLosTemas = viewModel::onTodosLosTemas,
            onDificultadSeleccionada = viewModel::onDificultadSeleccionada,
            onNumeroPreguntasCambiado = viewModel::onNumeroPreguntasCambiado,
            onIniciarPartida = viewModel::onIniciarPartida,
            modifier = modifier
        )

        FaseQuiz.Finalizada -> uiState.resultado?.let { resultado ->
            ResultadosScreen(
                resultado = resultado,
                onJugarDeNuevo = viewModel::onJugarDeNuevo,
                onVolverAlInicio = viewModel::onVolverAlInicio,
                modifier = modifier
            )
        }

        FaseQuiz.Respondiendo, is FaseQuiz.Respondida -> JuegoScreen(
            uiState = uiState,
            onOpcionSeleccionada = viewModel::onOpcionSeleccionada,
            onSiguiente = viewModel::onSiguiente,
            modifier = modifier
        )
    }
}
