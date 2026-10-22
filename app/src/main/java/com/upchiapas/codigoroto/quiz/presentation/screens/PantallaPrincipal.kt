package com.upchiapas.codigoroto.quiz.presentation.screens

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.upchiapas.codigoroto.R

// Secciones de la barra inferior.
enum class Seccion(@StringRes val titulo: Int, @DrawableRes val icono: Int) {
    JUGAR(R.string.seccion_jugar, R.drawable.ic_jugar),
    MIS_PREGUNTAS(R.string.seccion_mis_preguntas, R.drawable.ic_mis_preguntas)
}

// Pantalla principal con la barra inferior para cambiar de sección.
@Composable
fun PantallaPrincipal() {
    // Sección elegida; se conserva al girar el celular.
    var seccion by rememberSaveable { mutableStateOf(Seccion.JUGAR) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                Seccion.entries.forEach { opcion ->
                    NavigationBarItem(
                        selected = seccion == opcion,
                        onClick = { seccion = opcion },
                        icon = { Icon(painterResource(opcion.icono), contentDescription = null) },
                        label = { Text(stringResource(opcion.titulo)) }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (seccion) {
            Seccion.JUGAR -> QuizPage(modifier = Modifier.padding(innerPadding))
            Seccion.MIS_PREGUNTAS -> MisPreguntasPage(modifier = Modifier.padding(innerPadding))
        }
    }
}
