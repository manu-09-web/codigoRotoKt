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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.upchiapas.codigoroto.quiz.presentation.components.TemaIcono
import com.upchiapas.codigoroto.quiz.presentation.components.color
import com.upchiapas.codigoroto.ui.theme.AmbarReforzar
import com.upchiapas.codigoroto.ui.theme.DegradadoFin
import com.upchiapas.codigoroto.ui.theme.DegradadoInicio
import com.upchiapas.codigoroto.ui.theme.VerdeCorrecto
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

// Pantalla de Inicio: elegir temas, dificultad y número de preguntas.
@Composable
fun InicioScreen(
    config: ConfigPartida,
    estadisticas: Estadisticas,
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
    ) {
        Encabezado()

        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
        TarjetaEstadisticas(estadisticas)

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
            shape = RoundedCornerShape(26.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text(text = stringResource(R.string.inicio_comenzar), fontWeight = FontWeight.SemiBold)
        }
        }
    }
}

// Encabezado con degradado morado, título y subtítulo.
@Composable
private fun Encabezado() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(listOf(DegradadoInicio, DegradadoFin)),
                RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
            )
            .padding(horizontal = 24.dp, vertical = 28.dp)
    ) {
        Text(
            text = stringResource(R.string.inicio_saludo),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.85f)
        )
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = stringResource(R.string.inicio_subtitulo),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.85f)
        )
    }
}

// Tarjeta con récord, partidas y % de acierto.
@Composable
private fun TarjetaEstadisticas(estadisticas: Estadisticas) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DatoEstadistica(estadisticas.record.toString(), R.string.inicio_record, AmbarReforzar)
            DatoEstadistica(
                estadisticas.partidasJugadas.toString(),
                R.string.inicio_partidas,
                MaterialTheme.colorScheme.primary
            )
            DatoEstadistica(
                stringResource(R.string.inicio_porcentaje, estadisticas.porcentajeAcierto),
                R.string.inicio_acierto,
                VerdeCorrecto
            )
        }
    }
}

// Un número de color con su etiqueta.
@Composable
private fun DatoEstadistica(valor: String, @StringRes etiqueta: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = valor,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = stringResource(etiqueta),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Chips para elegir temas.
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
            Tema.entries.forEach { tema ->
                FilterChip(
                    selected = tema in seleccionados,
                    onClick = { onTemaSeleccionado(tema) },
                    label = { Text(text = stringResource(tema.etiqueta())) },
                    leadingIcon = { TemaIcono(tema = tema) },
                    shape = RoundedCornerShape(50),
                    // Al seleccionarlo se pinta con el color del tema.
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = tema.color().copy(alpha = 0.15f),
                        selectedLabelColor = tema.color()
                    )
                )
            }
        }
    }
}

// Botones segmentados para elegir dificultad (null = todas).
@Composable
private fun SelectorDificultad(
    seleccionada: Dificultad?,
    onDificultadSeleccionada: (Dificultad?) -> Unit
) {
    val opciones: List<Dificultad?> = listOf(null) + Dificultad.entries
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TituloSeccion(R.string.inicio_dificultad)
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            opciones.forEachIndexed { indice, dificultad ->
                SegmentedButton(
                    selected = dificultad == seleccionada,
                    onClick = { onDificultadSeleccionada(dificultad) },
                    shape = SegmentedButtonDefaults.itemShape(index = indice, count = opciones.size),
                    label = {
                        Text(
                            text = stringResource(dificultad?.etiqueta() ?: R.string.dificultad_todas),
                            maxLines = 1
                        )
                    }
                )
            }
        }
    }
}

// Slider para elegir cuántas preguntas jugar.
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

// Título de cada sección.
@Composable
private fun TituloSeccion(@StringRes textoRes: Int) {
    Text(
        text = stringResource(textoRes),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
}
