package com.upchiapas.codigoroto.quiz.presentation.components

import androidx.annotation.StringRes
import com.upchiapas.codigoroto.R
import com.upchiapas.codigoroto.quiz.data.model.Dificultad
import com.upchiapas.codigoroto.quiz.data.model.Tema

// Funciones de extensión: agregan una función nueva a Tema y Dificultad sin modificar esos archivos.
// Están en presentation porque los textos son cosa de la pantalla; la capa data no sabe de strings.xml.

/** Texto que se muestra en pantalla para cada tema (ej. Tema.NULOS -> "Nulos"). */
@StringRes
fun Tema.etiqueta(): Int = when (this) {
    Tema.VARIABLES -> R.string.tema_variables
    Tema.NULOS -> R.string.tema_nulos
    Tema.COLECCIONES -> R.string.tema_colecciones
    Tema.CONTROL_FLUJO -> R.string.tema_control_flujo
    Tema.FUNCIONES -> R.string.tema_funciones
}

/** Texto que se muestra en pantalla para cada dificultad. */
@StringRes
fun Dificultad.etiqueta(): Int = when (this) {
    Dificultad.FACIL -> R.string.dificultad_facil
    Dificultad.MEDIA -> R.string.dificultad_media
    Dificultad.DIFICIL -> R.string.dificultad_dificil
}
