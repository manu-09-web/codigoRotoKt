package com.upchiapas.codigoroto.quiz.presentation.screens

import com.upchiapas.codigoroto.quiz.data.model.Dificultad
import com.upchiapas.codigoroto.quiz.data.model.Pregunta
import com.upchiapas.codigoroto.quiz.data.model.Tema

// Fases de la partida (solo puede estar en una a la vez).
sealed interface FaseQuiz {
    data object Cargando : FaseQuiz      // cargando preguntas
    data object Configurando : FaseQuiz  // en Inicio
    data object Respondiendo : FaseQuiz  // esperando respuesta
    data class Respondida(val elegida: String, val esCorrecta: Boolean) : FaseQuiz // ya respondió (guarda qué eligió)
    data object Finalizada : FaseQuiz    // terminó la partida
}

// Cómo se pinta cada opción.
enum class EstadoOpcion { NORMAL, CORRECTA, INCORRECTA, DESHABILITADA }

// Una opción con su estado.
data class OpcionUi(
    val texto: String,
    val estado: EstadoOpcion = EstadoOpcion.NORMAL
)

// Filtros elegidos en Inicio.
data class ConfigPartida(
    val temasSeleccionados: Set<Tema> = emptySet(),   // vacío = todos
    val dificultad: Dificultad? = null,                // null = todas
    val numeroPreguntas: Int = 0,                      // preguntas elegidas
    val minimoPreguntas: Int = 0,                      // mínimo del Slider
    val maximoDisponible: Int = 0                      // máximo del Slider
) {
    val puedeIniciar: Boolean get() = maximoDisponible > 0
    val sliderHabilitado: Boolean get() = maximoDisponible > minimoPreguntas
}

// Qué tan bien le fue en un tema.
enum class NivelAcierto { ALTO, MEDIO, BAJO }

// Resultado de un tema.
data class ResultadoTema(
    val tema: Tema,
    val aciertos: Int,
    val total: Int,
    val porcentaje: Int,
    val nivel: NivelAcierto
)

// Resumen de la partida para Resultados.
data class ResultadoPartida(
    val puntaje: Int,
    val aciertos: Int,
    val total: Int,
    val porcentaje: Int,
    val porTema: List<ResultadoTema>,  // del mejor al peor
    val temaMasDebil: Tema?,           // null si fue perfecto
    val esNuevoRecord: Boolean         // superó el récord
)

// Datos de la tarjeta de Inicio.
data class Estadisticas(
    val record: Int = 0,
    val partidasJugadas: Int = 0,
    val porcentajeAcierto: Int = 0     // % total de aciertos
)

// Todo lo que se muestra en pantalla. Solo el ViewModel lo cambia.
data class QuizUiState(
    val fase: FaseQuiz = FaseQuiz.Cargando,
    val bancoPreguntas: List<Pregunta> = emptyList(), // todas las preguntas
    val config: ConfigPartida = ConfigPartida(),
    val preguntas: List<Pregunta> = emptyList(),      // preguntas de esta partida
    val indiceActual: Int = 0,
    val opciones: List<OpcionUi> = emptyList(),       // opciones de la pregunta actual
    val puntaje: Int = 0,
    val racha: Int = 0,                               // aciertos seguidos
    val aciertos: Int = 0,
    val respuestas: List<Boolean> = emptyList(),      // si acertó cada pregunta
    val resultado: ResultadoPartida? = null,          // resumen al terminar
    val estadisticas: Estadisticas = Estadisticas()   // récord y partidas
) {
    val preguntaActual: Pregunta? get() = preguntas.getOrNull(indiceActual)
    val numeroPregunta: Int get() = indiceActual + 1  // empieza en 1
    val totalPreguntas: Int get() = preguntas.size
    val progreso: Float get() = if (preguntas.isEmpty()) 0f else numeroPregunta / totalPreguntas.toFloat()
}
