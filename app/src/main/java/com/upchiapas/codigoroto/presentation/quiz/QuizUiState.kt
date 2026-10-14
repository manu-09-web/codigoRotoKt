package com.upchiapas.codigoroto.presentation.quiz

import com.upchiapas.codigoroto.data.model.Pregunta

sealed interface FaseQuiz {
    data object Cargando : FaseQuiz
    data object Respondiendo : FaseQuiz
    data class Respondida(val elegida: String, val esCorrecta: Boolean) : FaseQuiz
    data object Finalizada : FaseQuiz
}

enum class EstadoOpcion { NORMAL, CORRECTA, INCORRECTA, DESHABILITADA }

data class OpcionUi(
    val texto: String,
    val estado: EstadoOpcion = EstadoOpcion.NORMAL
)

data class QuizUiState(
    val fase: FaseQuiz = FaseQuiz.Cargando,
    val preguntas: List<Pregunta> = emptyList(),
    val indiceActual: Int = 0,
    val opciones: List<OpcionUi> = emptyList(),
    val puntaje: Int = 0,
    val racha: Int = 0,
    val aciertos: Int = 0
) {
    val preguntaActual: Pregunta? get() = preguntas.getOrNull(indiceActual)
    val numeroPregunta: Int get() = indiceActual + 1
    val totalPreguntas: Int get() = preguntas.size
    val progreso: Float get() = if (preguntas.isEmpty()) 0f else numeroPregunta / totalPreguntas.toFloat()
}
