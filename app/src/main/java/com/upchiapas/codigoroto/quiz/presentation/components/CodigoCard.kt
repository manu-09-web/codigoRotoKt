package com.upchiapas.codigoroto.quiz.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upchiapas.codigoroto.ui.theme.CodigoFondo
import com.upchiapas.codigoroto.ui.theme.CodigoTexto

// Tarjeta oscura que muestra el código de la pregunta.
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
        Text(
            text = codigo,
            modifier = Modifier.padding(16.dp),
            color = CodigoTexto,
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp,
            lineHeight = 22.sp
        )
    }
}
