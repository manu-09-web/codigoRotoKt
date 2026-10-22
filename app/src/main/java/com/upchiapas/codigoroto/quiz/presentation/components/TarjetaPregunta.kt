package com.upchiapas.codigoroto.quiz.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.upchiapas.codigoroto.R
import com.upchiapas.codigoroto.quiz.data.model.Pregunta

// Tarjeta de una pregunta del usuario con su botón para borrarla.
@Composable
fun TarjetaPregunta(
    pregunta: Pregunta,
    onEliminar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = pregunta.enunciado,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            CodigoCard(codigo = pregunta.codigo)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(
                        R.string.mis_preguntas_detalle,
                        stringResource(pregunta.tema.etiqueta()),
                        stringResource(pregunta.dificultad.etiqueta())
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onEliminar) {
                    Text(
                        text = stringResource(R.string.mis_preguntas_eliminar),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
