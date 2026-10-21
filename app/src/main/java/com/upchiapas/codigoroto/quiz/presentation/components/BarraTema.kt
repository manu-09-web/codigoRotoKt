package com.upchiapas.codigoroto.quiz.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.upchiapas.codigoroto.R
import com.upchiapas.codigoroto.quiz.presentation.screens.NivelAcierto
import com.upchiapas.codigoroto.quiz.presentation.screens.ResultadoTema
import com.upchiapas.codigoroto.ui.theme.AmbarReforzar
import com.upchiapas.codigoroto.ui.theme.VerdeCorrecto

/**
 * Barra de progreso del % de aciertos de un tema (ej. "Nulos   3 de 4 · 75%").
 * Componente reutilizable y stateless: el color sale del NivelAcierto que calculó el ViewModel.
 */
@Composable
fun BarraTema(
    resultado: ResultadoTema,
    modifier: Modifier = Modifier
) {
    val color = when (resultado.nivel) {
        NivelAcierto.ALTO -> VerdeCorrecto
        NivelAcierto.MEDIO -> MaterialTheme.colorScheme.primary
        NivelAcierto.BAJO -> AmbarReforzar
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = stringResource(resultado.tema.etiqueta()),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(R.string.resultados_tema_detalle, resultado.aciertos, resultado.total, resultado.porcentaje),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        LinearProgressIndicator(
            progress = { resultado.porcentaje / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
