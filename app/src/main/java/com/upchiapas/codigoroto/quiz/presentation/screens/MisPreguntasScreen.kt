package com.upchiapas.codigoroto.quiz.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.upchiapas.codigoroto.CodigoRotoApp
import com.upchiapas.codigoroto.R
import com.upchiapas.codigoroto.quiz.data.model.Pregunta
import com.upchiapas.codigoroto.quiz.presentation.components.TarjetaPregunta
import com.upchiapas.codigoroto.quiz.presentation.viewmodels.MisPreguntasViewModel

// Conecta el MisPreguntasViewModel con la lista y el formulario.
@Composable
fun MisPreguntasPage(
    modifier: Modifier = Modifier,
    viewModel: MisPreguntasViewModel = viewModel(
        factory = (LocalContext.current.applicationContext as CodigoRotoApp).quizModule.factoryQuiz
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.mostrandoFormulario) {
        NuevaPreguntaScreen(
            formulario = uiState.formulario,
            errores = uiState.errores,
            onEnunciadoCambiado = viewModel::onEnunciadoCambiado,
            onCodigoCambiado = viewModel::onCodigoCambiado,
            onOpcionCambiada = viewModel::onOpcionCambiada,
            onCorrectaSeleccionada = viewModel::onCorrectaSeleccionada,
            onTemaSeleccionado = viewModel::onTemaSeleccionado,
            onDificultadSeleccionada = viewModel::onDificultadSeleccionada,
            onExplicacionCambiada = viewModel::onExplicacionCambiada,
            onGuardar = viewModel::onGuardar,
            onCancelar = viewModel::onCancelar,
            modifier = modifier
        )
    } else {
        MisPreguntasScreen(
            preguntas = uiState.preguntas,
            onNuevaPregunta = viewModel::onNuevaPregunta,
            onEliminar = viewModel::onEliminar,
            modifier = modifier
        )
    }
}

// Lista de las preguntas del usuario con el botón para agregar.
@Composable
fun MisPreguntasScreen(
    preguntas: List<Pregunta>,
    onNuevaPregunta: () -> Unit,
    onEliminar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.mis_preguntas_titulo),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (preguntas.isEmpty()) {
                        stringResource(R.string.mis_preguntas_vacio)
                    } else {
                        stringResource(R.string.mis_preguntas_cantidad, preguntas.size)
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // Una tarjeta por pregunta; key ayuda a Compose a identificar cada una.
            items(preguntas, key = { it.id }) { pregunta ->
                TarjetaPregunta(pregunta = pregunta, onEliminar = { onEliminar(pregunta.id) })
            }
        }

        ExtendedFloatingActionButton(
            onClick = onNuevaPregunta,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            Text(text = stringResource(R.string.mis_preguntas_nueva))
        }
    }
}
