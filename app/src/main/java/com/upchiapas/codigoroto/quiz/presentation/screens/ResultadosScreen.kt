package com.upchiapas.codigoroto.quiz.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.upchiapas.codigoroto.R
import com.upchiapas.codigoroto.quiz.data.model.Tema
import com.upchiapas.codigoroto.quiz.presentation.components.BarraTema
import com.upchiapas.codigoroto.quiz.presentation.components.etiqueta
import com.upchiapas.codigoroto.ui.theme.AmbarReforzar
import com.upchiapas.codigoroto.ui.theme.AmbarReforzarFondo
import com.upchiapas.codigoroto.ui.theme.VerdeCorrecto
import com.upchiapas.codigoroto.ui.theme.VerdeCorrectoFondo

/**
 * Pantalla de Resultados (fase Finalizada).
 * Stateless: recibe el ResultadoPartida ya calculado por el ViewModel y avisa con lambdas.
 */
@Composable
fun ResultadosScreen(
    resultado: ResultadoPartida,
    onJugarDeNuevo: () -> Unit,
    onVolverAlInicio: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.resultados_titulo),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        AnilloAciertos(resultado)

        Text(
            text = stringResource(R.string.resultados_puntos, resultado.puntaje),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        AciertoPorTema(resultado.porTema)

        TemaAReforzar(resultado.temaMasDebil)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onVolverAlInicio,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            ) {
                Text(text = stringResource(R.string.resultados_inicio))
            }
            Button(
                onClick = onJugarDeNuevo,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            ) {
                Text(text = stringResource(R.string.resultados_jugar_de_nuevo))
            }
        }
    }
}

/** Anillo con el % de aciertos y "8/10" en el centro. */
@Composable
private fun AnilloAciertos(resultado: ResultadoPartida) {
    Box(contentAlignment = Alignment.Center) {
        // Pista gris de fondo y, encima, el progreso real.
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.size(150.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            strokeWidth = 12.dp
        )
        CircularProgressIndicator(
            progress = { resultado.porcentaje / 100f },
            modifier = Modifier.size(150.dp),
            strokeWidth = 12.dp
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.resultados_fraccion, resultado.aciertos, resultado.total),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.resultados_correctas),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** Tarjeta con una BarraTema por cada tema que salió en la partida. */
@Composable
private fun AciertoPorTema(porTema: List<ResultadoTema>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = stringResource(R.string.resultados_por_tema),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            porTema.forEach { BarraTema(resultado = it) }
        }
    }
}

/** Tarjeta ámbar con el tema a reforzar, o verde si le fue perfecto en todos. */
@Composable
private fun TemaAReforzar(temaMasDebil: Tema?) {
    val perfecto = temaMasDebil == null
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (perfecto) VerdeCorrectoFondo else AmbarReforzarFondo
        )
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = if (temaMasDebil == null) {
                    stringResource(R.string.resultados_perfecto)
                } else {
                    stringResource(R.string.resultados_reforzar, stringResource(temaMasDebil.etiqueta()))
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (perfecto) VerdeCorrecto else AmbarReforzar
            )
            Text(
                text = stringResource(
                    if (perfecto) R.string.resultados_perfecto_detalle else R.string.resultados_reforzar_detalle
                ),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
