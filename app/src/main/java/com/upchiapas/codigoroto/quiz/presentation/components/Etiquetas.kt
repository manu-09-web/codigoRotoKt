package com.upchiapas.codigoroto.quiz.presentation.components

import androidx.annotation.StringRes
import com.upchiapas.codigoroto.R
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

// Texto de cada dificultad.
@StringRes
fun Dificultad.etiqueta(): Int = when (this) {
    Dificultad.FACIL -> R.string.dificultad_facil
    Dificultad.MEDIA -> R.string.dificultad_media
    Dificultad.DIFICIL -> R.string.dificultad_dificil
}
