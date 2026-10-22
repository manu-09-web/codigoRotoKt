package com.upchiapas.codigoroto.quiz.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.upchiapas.codigoroto.quiz.data.model.Tema
import com.upchiapas.codigoroto.ui.theme.JetBrainsMono

// Círculo del color del tema con su símbolo.
@Composable
fun TemaIcono(
    tema: Tema,
    modifier: Modifier = Modifier,
    tamano: Dp = 22.dp
) {
    Box(
        modifier = modifier
            .size(tamano)
            .background(tema.color(), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(tema.simbolo()),
            color = Color.White,
            fontFamily = JetBrainsMono,
            fontWeight = FontWeight.Bold,
            fontSize = (tamano.value * 0.42f).sp
        )
    }
}
