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

/**
 * Página del quiz (Composable con estado / stateful).
 * Conecta el QuizViewModel con las pantallas: lee el estado una sola vez y,
 * según la fase, muestra Inicio o Juego.
 *
 * Es el único lugar que conoce al ViewModel: las pantallas reciben datos y lambdas,
 * así se pueden reutilizar y probar sin ViewModel.
 */
@Composable
fun QuizPage(
    modifier: Modifier = Modifier,
    // viewModel(): si el ViewModel ya existe (por ejemplo, después de girar la pantalla) lo reutiliza;
    // si no, lo crea con la Factory que está en el QuizModule de CodigoRotoApp.
    viewModel: QuizViewModel = viewModel(
        factory = (LocalContext.current.applicationContext as CodigoRotoApp).quizModule.factoryQuiz
    )
) {
    // collectAsStateWithLifecycle: escucha el StateFlow. Cada vez que el ViewModel emite un estado nuevo,
    // uiState cambia y Compose vuelve a dibujar (recomposición). Deja de escuchar si la app está en segundo plano.
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState.fase) {
        FaseQuiz.Cargando -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        FaseQuiz.Configurando -> InicioScreen(
            config = uiState.config,
            // viewModel::onTemaSeleccionado es una referencia a la función: se ejecuta cuando la pantalla la llame.
            onTemaSeleccionado = viewModel::onTemaSeleccionado,
            onTodosLosTemas = viewModel::onTodosLosTemas,
            onDificultadSeleccionada = viewModel::onDificultadSeleccionada,
            onNumeroPreguntasCambiado = viewModel::onNumeroPreguntasCambiado,
            onIniciarPartida = viewModel::onIniciarPartida,
            modifier = modifier
        )

        // Respondiendo, Respondida y Finalizada se muestran en JuegoScreen.
        else -> JuegoScreen(
            uiState = uiState,
            onOpcionSeleccionada = viewModel::onOpcionSeleccionada,
            onSiguiente = viewModel::onSiguiente,
            onVolverAlInicio = viewModel::onVolverAlInicio,
            modifier = modifier
        )
    }
}
