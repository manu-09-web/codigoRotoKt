package com.upchiapas.codigoroto.quiz.presentation.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.upchiapas.codigoroto.R
import com.upchiapas.codigoroto.quiz.data.model.Dificultad
import com.upchiapas.codigoroto.quiz.data.model.Tema
import com.upchiapas.codigoroto.quiz.presentation.components.etiqueta

// Formulario para agregar una pregunta. Los errores los calcula el ViewModel.
@Composable
fun NuevaPreguntaScreen(
    formulario: FormularioPregunta,
    errores: ErroresFormulario,
    onEnunciadoCambiado: (String) -> Unit,
    onCodigoCambiado: (String) -> Unit,
    onOpcionCambiada: (Int, String) -> Unit,
    onCorrectaSeleccionada: (Int) -> Unit,
    onTemaSeleccionado: (Tema) -> Unit,
    onDificultadSeleccionada: (Dificultad) -> Unit,
    onExplicacionCambiada: (String) -> Unit,
    onGuardar: () -> Unit,
    onCancelar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.nueva_titulo),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = formulario.enunciado,
            onValueChange = onEnunciadoCambiado,
            label = { Text(stringResource(R.string.nueva_enunciado)) },
            isError = errores.enunciado != null,
            supportingText = errores.enunciado?.let { { Text(stringResource(it)) } },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = formulario.codigo,
            onValueChange = onCodigoCambiado,
            label = { Text(stringResource(R.string.nueva_codigo)) },
            isError = errores.codigo != null,
            supportingText = errores.codigo?.let { { Text(stringResource(it)) } },
            textStyle = TextStyle(fontFamily = FontFamily.Monospace),
            minLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        // Las 4 opciones; el RadioButton marca la correcta.
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Titulo(R.string.nueva_opciones)
            formulario.opciones.forEachIndexed { indice, opcion ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = formulario.indiceCorrecta == indice,
                        onClick = { onCorrectaSeleccionada(indice) }
                    )
                    OutlinedTextField(
                        value = opcion,
                        onValueChange = { onOpcionCambiada(indice, it) },
                        label = { Text(stringResource(R.string.nueva_opcion, indice + 1)) },
                        isError = errores.opciones != null,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            MensajeError(errores.opciones)
            MensajeError(errores.correcta)
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Titulo(R.string.nueva_tema)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Tema.entries.forEach { tema ->
                    FilterChip(
                        selected = formulario.tema == tema,
                        onClick = { onTemaSeleccionado(tema) },
                        label = { Text(stringResource(tema.etiqueta())) }
                    )
                }
            }
            MensajeError(errores.tema)
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Titulo(R.string.inicio_dificultad)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Dificultad.entries.forEach { dificultad ->
                    FilterChip(
                        selected = formulario.dificultad == dificultad,
                        onClick = { onDificultadSeleccionada(dificultad) },
                        label = { Text(stringResource(dificultad.etiqueta())) }
                    )
                }
            }
        }

        OutlinedTextField(
            value = formulario.explicacion,
            onValueChange = onExplicacionCambiada,
            label = { Text(stringResource(R.string.nueva_explicacion)) },
            modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onCancelar, modifier = Modifier.weight(1f).height(52.dp)) {
                Text(stringResource(R.string.nueva_cancelar))
            }
            Button(onClick = onGuardar, modifier = Modifier.weight(1f).height(52.dp)) {
                Text(stringResource(R.string.nueva_guardar))
            }
        }
    }
}

// Título de cada sección del formulario.
@Composable
private fun Titulo(@StringRes texto: Int) {
    Text(text = stringResource(texto), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
}

// Texto rojo de error; no muestra nada si no hay error.
@Composable
private fun MensajeError(@StringRes error: Int?) {
    if (error != null) {
        Text(
            text = stringResource(error),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
