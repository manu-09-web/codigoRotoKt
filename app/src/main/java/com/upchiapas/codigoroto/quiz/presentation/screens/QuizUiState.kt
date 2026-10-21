package com.upchiapas.codigoroto.quiz.presentation.screens

import com.upchiapas.codigoroto.quiz.data.model.Dificultad
import com.upchiapas.codigoroto.quiz.data.model.Pregunta
import com.upchiapas.codigoroto.quiz.data.model.Tema

/**
 * Fases por las que pasa una partida.
 *
 * sealed interface = lista CERRADA de fases: Kotlin sabe que no existe ninguna otra.
 * - La partida solo puede estar en UNA fase a la vez (con varios Boolean podrían quedar dos en true).
 * - En un `whn` el compilador avisa si falta manejar alguna fase.
 *
 * data object vs data class:
 * - data object: la fase no necesita guardar datos, así que existe un solo objeto de ella.
 * - data class: la fase SÍ guarda datos (Respondida guarda qué opción se eligió y si fue correcta),
 *   y cada respuesta crea un objeto distinto.
 */
sealed interface FaseQuiz {
    data object Cargando : FaseQuiz      // leyendo el JSON
    data object Configurando : FaseQuiz  // pantalla de Inicio, eligiendo filtros
    data object Respondiendo : FaseQuiz  // pregunta en pantalla, esperando respuesta
    data class Respondida(val elegida: String, val esCorrecta: Boolean) : FaseQuiz // ya respondió
    data object Finalizada : FaseQuiz    // se acabaron las preguntas
}

/** Cómo se debe pintar cada opción. Lo decide el ViewModel; la pantalla solo lo dibuja. */
enum class EstadoOpcion { NORMAL, CORRECTA, INCORRECTA, DESHABILITADA }

/** Una opción de respuesta tal como se muestra en pantalla. */
data class OpcionUi(
    val texto: String,
    val estado: EstadoOpcion = EstadoOpcion.NORMAL
)

/** Lo que el usuario eligió en la pantalla de Inicio. */
data class ConfigPartida(
    val temasSeleccionados: Set<Tema> = emptySet(),   // vacío = todos los temas
    val dificultad: Dificultad? = null,                // null = todas las dificultades
    val numeroPreguntas: Int = 0,                      // valor del Slider
    val minimoPreguntas: Int = 0,                      // inicio del Slider
    val maximoDisponible: Int = 0                      // fin del Slider (preguntas que hay con estos filtros)
) {
    // Propiedades calculadas: no se guardan, se calculan cada vez que se leen.
    val puedeIniciar: Boolean get() = maximoDisponible > 0
    val sliderHabilitado: Boolean get() = maximoDisponible > minimoPreguntas
}

/**
 * Estado completo de la UI del quiz: la "foto" de todo lo que se ve en pantalla.
 *
 * Es inmutable (todo val). Para cambiarlo, el ViewModel crea una copia nueva con copy().
 * La pantalla nunca lo modifica, solo lo lee.
 */
data class QuizUiState(
    val fase: FaseQuiz = FaseQuiz.Cargando,
    val bancoPreguntas: List<Pregunta> = emptyList(), // todas las preguntas del JSON
    val config: ConfigPartida = ConfigPartida(),
    val preguntas: List<Pregunta> = emptyList(),      // las de la partida actual (ya filtradas y revueltas)
    val indiceActual: Int = 0,
    val opciones: List<OpcionUi> = emptyList(),       // opciones de la pregunta actual, ya revueltas
    val puntaje: Int = 0,
    val racha: Int = 0,                               // aciertos seguidos
    val aciertos: Int = 0
) {
    val preguntaActual: Pregunta? get() = preguntas.getOrNull(indiceActual)
    val numeroPregunta: Int get() = indiceActual + 1  // para mostrar "Pregunta 1 de 10" (empieza en 1)
    val totalPreguntas: Int get() = preguntas.size
    val progreso: Float get() = if (preguntas.isEmpty()) 0f else numeroPregunta / totalPreguntas.toFloat()
}
