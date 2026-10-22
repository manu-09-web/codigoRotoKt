package com.upchiapas.codigoroto.quiz.presentation.screens

import androidx.annotation.StringRes
import com.upchiapas.codigoroto.quiz.data.model.Dificultad
import com.upchiapas.codigoroto.quiz.data.model.Pregunta
import com.upchiapas.codigoroto.quiz.data.model.Tema

// Lo que el usuario va escribiendo en el formulario.
data class FormularioPregunta(
    val enunciado: String = "",
    val codigo: String = "",
    val opciones: List<String> = List(4) { "" },
    val indiceCorrecta: Int? = null,     // cuál de las 4 es la correcta
    val tema: Tema? = null,
    val dificultad: Dificultad = Dificultad.FACIL,
    val explicacion: String = ""
)

// Errores de cada campo (id del texto en strings.xml; null = sin error).
data class ErroresFormulario(
    @StringRes val enunciado: Int? = null,
    @StringRes val codigo: Int? = null,
    @StringRes val opciones: Int? = null,
    @StringRes val correcta: Int? = null,
    @StringRes val tema: Int? = null
) {
    val hayErrores: Boolean get() = listOf(enunciado, codigo, opciones, correcta, tema).any { it != null }
}

// Todo lo que muestra la sección Mis preguntas.
data class MisPreguntasUiState(
    val preguntas: List<Pregunta> = emptyList(),
    val mostrandoFormulario: Boolean = false,
    val formulario: FormularioPregunta = FormularioPregunta(),
    val errores: ErroresFormulario = ErroresFormulario()
)
