package com.upchiapas.codigoroto.quiz.presentation.components

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.upchiapas.codigoroto.R
import com.upchiapas.codigoroto.ui.theme.TemaColecciones
import com.upchiapas.codigoroto.ui.theme.TemaControlFlujo
import com.upchiapas.codigoroto.ui.theme.TemaFunciones
import com.upchiapas.codigoroto.ui.theme.TemaNulos
import com.upchiapas.codigoroto.ui.theme.TemaVariables
import com.upchiapas.codigoroto.quiz.data.model.Dificultad
import com.upchiapas.codigoroto.quiz.data.model.Tema

// Texto de cada tema.
@StringRes
fun Tema.etiqueta(): Int = when (this) {
    Tema.VARIABLES -> R.string.tema_variables
    Tema.NULOS -> R.string.tema_nulos
    Tema.COLECCIONES -> R.string.tema_colecciones
    Tema.CONTROL_FLUJO -> R.string.tema_control_flujo
    Tema.FUNCIONES -> R.string.tema_funciones
}

// Símbolo corto de cada tema (x=, ?., [], if, fn).
@StringRes
fun Tema.simbolo(): Int = when (this) {
    Tema.VARIABLES -> R.string.simbolo_variables
    Tema.NULOS -> R.string.simbolo_nulos
    Tema.COLECCIONES -> R.string.simbolo_colecciones
    Tema.CONTROL_FLUJO -> R.string.simbolo_control_flujo
    Tema.FUNCIONES -> R.string.simbolo_funciones
}

// Color de cada tema.
fun Tema.color(): Color = when (this) {
    Tema.VARIABLES -> TemaVariables
    Tema.NULOS -> TemaNulos
    Tema.COLECCIONES -> TemaColecciones
    Tema.CONTROL_FLUJO -> TemaControlFlujo
    Tema.FUNCIONES -> TemaFunciones
}

// Texto de cada dificultad.
@StringRes
fun Dificultad.etiqueta(): Int = when (this) {
    Dificultad.FACIL -> R.string.dificultad_facil
    Dificultad.MEDIA -> R.string.dificultad_media
    Dificultad.DIFICIL -> R.string.dificultad_dificil
}
