package com.upchiapas.codigoroto.quiz.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upchiapas.codigoroto.ui.theme.CodigoBarra
import com.upchiapas.codigoroto.ui.theme.CodigoFondo
import com.upchiapas.codigoroto.ui.theme.CodigoNumeros
import com.upchiapas.codigoroto.ui.theme.CodigoTexto
import com.upchiapas.codigoroto.ui.theme.JetBrainsMono

// Tarjeta oscura estilo editor que muestra el código con números de línea.
@Composable
fun CodigoCard(
    codigo: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CodigoFondo)
    ) {
        // Barra de ventana con los 3 puntitos.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CodigoBarra)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(Color(0xFFFF5F57), Color(0xFFFEBC2E), Color(0xFF28C840)).forEach { color ->
                Box(Modifier.size(10.dp).background(color, CircleShape))
            }
        }
        // Una fila por línea: número + código. Se puede deslizar si la línea es larga.
        Column(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 12.dp)
        ) {
            codigo.lines().forEachIndexed { indice, linea ->
                Row {
                    Text(
                        text = (indice + 1).toString(),
                        modifier = Modifier.width(32.dp),
                        color = CodigoNumeros,
                        fontFamily = JetBrainsMono,
                        fontSize = 13.sp,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.End
                    )
                    Text(
                        text = linea,
                        modifier = Modifier.padding(start = 12.dp, end = 16.dp),
                        color = CodigoTexto,
                        fontFamily = JetBrainsMono,
                        fontSize = 13.sp,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}
