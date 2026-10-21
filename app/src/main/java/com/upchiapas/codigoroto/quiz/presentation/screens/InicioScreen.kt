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
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.upchiapas.codigoroto.R
import com.upchiapas.codigoroto.quiz.data.model.Dificultad
import com.upchiapas.codigoroto.quiz.data.model.Tema
import com.upchiapas.codigoroto.quiz.presentation.components.etiqueta
import kotlin.math.roundToInt

/**
 * Pantalla de Inicio: el usuario elige temas, dificultad y número de preguntas.
 * Es stateless: recibe la configuración ya calculada por el ViewModel y avisa cada cambio con lambdas.
 */
@Composable
fun InicioScreen(
    config: ConfigPartida,
    onTemaSeleccionado: (Tema) -> Unit,
    onTodosLosTemas: () -> Unit,
    onDificultadSeleccionada: (Dificultad?) -> Unit,
    onNumeroPreguntasCambiado: (Int) -> Unit,
    onIniciarPartida: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.inicio_subtitulo),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        SelectorTemas(
            seleccionados = config.temasSeleccionados,
            onTemaSeleccionado = onTemaSeleccionado,
            onTodosLosTemas = onTodosLosTemas
        )

        SelectorDificultad(
            seleccionada = config.dificultad,
            onDificultadSeleccionada = onDificultadSeleccionada
        )

        SelectorNumeroPreguntas(
            config = config,
            onNumeroPreguntasCambiado = onNumeroPreguntasCambiado
        )

        Button(
            onClick = onIniciarPartida,
            enabled = config.puedeIniciar,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(text = stringResource(R.string.inicio_comenzar))
        }
    }
}

/** Chips de temas (selección múltiple) y el botón "Todos". FlowRow acomoda los chips en varias filas. */
@Composable
private fun SelectorTemas(
    seleccionados: Set<Tema>,
    onTemaSeleccionado: (Tema) -> Unit,
    onTodosLosTemas: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TituloSeccion(R.string.inicio_temas)
            TextButton(onClick = onTodosLosTemas) {
                Text(text = stringResource(R.string.inicio_todos))
            }
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Tema.entries es la lista de todos los valores del enum.
            Tema.entries.forEach { tema ->
                FilterChip(
                    selected = tema in seleccionados,
                    onClick = { onTemaSeleccionado(tema) },
                    label = { Text(text = stringResource(tema.etiqueta())) }
                )
            }
        }
    }
}

/** Chips de dificultad (selección única). "Todas" se representa con null. */
@Composable
private fun SelectorDificultad(
    seleccionada: Dificultad?,
    onDificultadSeleccionada: (Dificultad?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TituloSeccion(R.string.inicio_dificultad)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = seleccionada == null,
                onClick = { onDificultadSeleccionada(null) },
                label = { Text(text = stringResource(R.string.dificultad_todas)) }
            )
            Dificultad.entries.forEach { dificultad ->
                FilterChip(
                    selected = dificultad == seleccionada,
                    onClick = { onDificultadSeleccionada(dificultad) },
                    label = { Text(text = stringResource(dificultad.etiqueta())) }
                )
            }
        }
    }
}

/** Slider para elegir cuántas preguntas jugar. Se oculta si solo hay una cantidad posible. */
@Composable
private fun SelectorNumeroPreguntas(
    config: ConfigPartida,
    onNumeroPreguntasCambiado: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TituloSeccion(R.string.inicio_preguntas)
            Text(
                text = config.numeroPreguntas.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        if (config.sliderHabilitado) {
            // El Slider trabaja con Float; se redondea a Int antes de avisar al ViewModel.
            // steps = número de puntos intermedios, para que solo se pueda elegir números enteros.
            Slider(
                value = config.numeroPreguntas.toFloat(),
                onValueChange = { onNumeroPreguntasCambiado(it.roundToInt()) },
                valueRange = config.minimoPreguntas.toFloat()..config.maximoDisponible.toFloat(),
                steps = config.maximoDisponible - config.minimoPreguntas - 1
            )
        }
        Text(
            text = if (config.puedeIniciar) {
                stringResource(R.string.inicio_disponibles, config.maximoDisponible)
            } else {
                stringResource(R.string.inicio_sin_preguntas)
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** Título de cada sección. @StringRes indica que recibe el id de un texto de strings.xml. */
@Composable
private fun TituloSeccion(@StringRes textoRes: Int) {
    Text(
        text = stringResource(textoRes),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
}
