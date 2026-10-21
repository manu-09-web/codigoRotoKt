package com.upchiapas.codigoroto.quiz.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upchiapas.codigoroto.quiz.data.model.Dificultad
import com.upchiapas.codigoroto.quiz.data.model.Pregunta
import com.upchiapas.codigoroto.quiz.data.model.Tema
import com.upchiapas.codigoroto.quiz.data.repository.QuizRepository
import com.upchiapas.codigoroto.quiz.presentation.screens.ConfigPartida
import com.upchiapas.codigoroto.quiz.presentation.screens.EstadoOpcion
import com.upchiapas.codigoroto.quiz.presentation.screens.FaseQuiz
import com.upchiapas.codigoroto.quiz.presentation.screens.OpcionUi
import com.upchiapas.codigoroto.quiz.presentation.screens.QuizUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * El "cerebro" del quiz. Toda la lógica vive aquí: filtrar, revisar respuestas, sumar puntos.
 *
 * Recibe el repositorio por constructor (no lo crea él): inyección de dependencias.
 * Sobrevive a la rotación de pantalla, por eso la partida no se pierde al girar el celular.
 */
class QuizViewModel(
    private val repository: QuizRepository
) : ViewModel() {

    // Backing property:
    // - _uiState (privado y Mutable): solo el ViewModel lo puede cambiar.
    // - uiState (público y de solo lectura): la pantalla lo lee pero no lo puede modificar.
    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    // init se ejecuta al crear el ViewModel: carga las preguntas en cuanto se abre la app.
    init {
        cargarBanco()
    }

    // ---------- Inicio (fase Configurando) ----------

    // Esta función se llama cuando el usuario selecciona un tema.
    // Si el tema ya está seleccionado, se deselecciona.
    fun onTemaSeleccionado(tema: Tema) {
        // update{} toma el estado actual (it) y lo reemplaza por la copia que regresamos.
        _uiState.update {
            val temas = it.config.temasSeleccionados
            val nuevos = if (tema in temas) temas - tema else temas + tema
            it.copy(config = recalcularConfig(it.bancoPreguntas, it.config.copy(temasSeleccionados = nuevos)))
        }
    }

    // Esta función se llama cuando el usuario toca "Todos": quita la selección para usar todos los temas.
    fun onTodosLosTemas() {
        _uiState.update {
            it.copy(config = recalcularConfig(it.bancoPreguntas, it.config.copy(temasSeleccionados = emptySet())))
        }
    }

    // Esta función se llama cuando el usuario selecciona una dificultad (null = todas).
    fun onDificultadSeleccionada(dificultad: Dificultad?) {
        _uiState.update {
            it.copy(config = recalcularConfig(it.bancoPreguntas, it.config.copy(dificultad = dificultad)))
        }
    }

    // Esta función se llama cuando el usuario cambia el número de preguntas.
    // coerceIn evita que el número quede fuera del rango permitido.
    fun onNumeroPreguntasCambiado(numero: Int) {
        _uiState.update {
            val config = it.config
            it.copy(config = config.copy(numeroPreguntas = numero.coerceIn(config.minimoPreguntas, config.maximoDisponible)))
        }
    }

    // Esta función se llama cuando el usuario inicia el juego.
    fun onIniciarPartida() {
        val estado = _uiState.value
        if (!estado.config.puedeIniciar) return

        // Filtra por temas y dificultad, revuelve y toma solo las que pidió el usuario.
        val preguntas = filtrar(estado.bancoPreguntas, estado.config)
            .shuffled()
            .take(estado.config.numeroPreguntas)

        // Actualiza el estado con las preguntas seleccionadas y reinicia puntaje, racha y aciertos.
        _uiState.update {
            it.copy(
                fase = FaseQuiz.Respondiendo,
                preguntas = preguntas,
                indiceActual = 0,
                opciones = revolverOpciones(preguntas.first()),
                puntaje = 0,
                racha = 0,
                aciertos = 0
            )
        }
    }

    // ---------- Juego ----------

    // Esta función se llama cuando el usuario selecciona una opción de respuesta.
    fun onOpcionSeleccionada(texto: String) {
        val estado = _uiState.value
        val pregunta = estado.preguntaActual ?: return       // ?: return -> si no hay pregunta, no hace nada
        if (estado.fase != FaseQuiz.Respondiendo) return     // evita contestar dos veces la misma pregunta

        val esCorrecta = texto == pregunta.respuestaCorrecta
        val nuevaRacha = if (esCorrecta) estado.racha + 1 else 0 // un error reinicia la racha
        val puntosGanados = if (esCorrecta) {
            (pregunta.dificultad.puntos * multiplicador(nuevaRacha)).toInt()
        } else {
            0
        }

        // Se crea un estado NUEVO; la pantalla lo recibe y se vuelve a dibujar con los colores.
        _uiState.update {
            it.copy(
                fase = FaseQuiz.Respondida(elegida = texto, esCorrecta = esCorrecta),
                opciones = marcarOpciones(it.opciones, pregunta.respuestaCorrecta, texto),
                puntaje = it.puntaje + puntosGanados,
                racha = nuevaRacha,
                aciertos = if (esCorrecta) it.aciertos + 1 else it.aciertos
            )
        }
    }

    // Esta función se llama cuando el usuario toca "Siguiente":
    // pasa a la siguiente pregunta o, si era la última, termina la partida.
    fun onSiguiente() {
        val estado = _uiState.value
        if (estado.fase !is FaseQuiz.Respondida) return

        val siguiente = estado.indiceActual + 1
        if (siguiente >= estado.totalPreguntas) {
            _uiState.update { it.copy(fase = FaseQuiz.Finalizada) }
            return
        }
        _uiState.update {
            it.copy(
                fase = FaseQuiz.Respondiendo,
                indiceActual = siguiente,
                opciones = revolverOpciones(it.preguntas[siguiente])
            )
        }
    }

    // Regresa a la pantalla de Inicio conservando los filtros que había elegido.
    fun onVolverAlInicio() {
        _uiState.update { it.copy(fase = FaseQuiz.Configurando) }
    }

    // ---------- Lógica interna (private: solo se usa dentro del ViewModel) ----------

    // Pide las preguntas al repositorio y pasa a la pantalla de Inicio.
    // viewModelScope.launch inicia una corrutina que se cancela sola si el ViewModel se destruye.
    private fun cargarBanco() {
        viewModelScope.launch {
            val banco = repository.obtenerPreguntas()
            _uiState.update {
                it.copy(
                    fase = FaseQuiz.Configurando,
                    bancoPreguntas = banco,
                    config = recalcularConfig(banco, ConfigPartida(numeroPreguntas = PREGUNTAS_POR_DEFECTO))
                )
            }
        }
    }

    // Ajusta el mínimo, el máximo y el número elegido según los filtros actuales.
    private fun recalcularConfig(banco: List<Pregunta>, config: ConfigPartida): ConfigPartida {
        val disponibles = filtrar(banco, config).size
        val maximo = minOf(disponibles, MAXIMO_PREGUNTAS)
        val minimo = minOf(MINIMO_PREGUNTAS, maximo)
        return config.copy(
            minimoPreguntas = minimo,
            maximoDisponible = maximo,
            numeroPreguntas = config.numeroPreguntas.coerceIn(minimo, maximo)
        )
    }

    // Filtra las preguntas según los temas y dificultad seleccionados (vacío o null = no filtrar).
    private fun filtrar(banco: List<Pregunta>, config: ConfigPartida): List<Pregunta> =
        banco.filter { pregunta ->
            (config.temasSeleccionados.isEmpty() || pregunta.tema in config.temasSeleccionados) &&
                (config.dificultad == null || pregunta.dificultad == config.dificultad)
        }

    // Reordena las opciones de una pregunta en un orden aleatorio.
    private fun revolverOpciones(pregunta: Pregunta): List<OpcionUi> =
        pregunta.opciones.shuffled().map { OpcionUi(texto = it) }

    // Marca las opciones de respuesta según si están correctas, incorrectas o deshabilitadas.
    private fun marcarOpciones(
        opciones: List<OpcionUi>,
        correcta: String,
        elegida: String
    ): List<OpcionUi> = opciones.map { opcion ->
        val estado = when (opcion.texto) {
            correcta -> EstadoOpcion.CORRECTA
            elegida -> EstadoOpcion.INCORRECTA
            else -> EstadoOpcion.DESHABILITADA
        }
        opcion.copy(estado = estado)
    }

    // Calcula el multiplicador de puntos según la racha de respuestas correctas.
    private fun multiplicador(racha: Int): Double = when {
        racha >= RACHA_DOBLE -> 2.0
        racha >= RACHA_MEDIA -> 1.5
        else -> 1.0
    }

    // companion object: cosas de la CLASE, no de cada objeto (como "static" en Java).
    // Aquí van las constantes del juego. La Factory está en QuizViewModelFactory.kt.
    private companion object {
        const val RACHA_MEDIA = 3          // desde 3 aciertos seguidos: x1.5
        const val RACHA_DOBLE = 5          // desde 5 aciertos seguidos: x2
        const val MINIMO_PREGUNTAS = 5
        const val MAXIMO_PREGUNTAS = 20
        const val PREGUNTAS_POR_DEFECTO = 10
    }
}
