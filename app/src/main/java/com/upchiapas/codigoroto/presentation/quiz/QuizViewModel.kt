package com.upchiapas.codigoroto.presentation.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.upchiapas.codigoroto.CodigoRotoApp
import com.upchiapas.codigoroto.data.model.Dificultad
import com.upchiapas.codigoroto.data.model.Pregunta
import com.upchiapas.codigoroto.data.model.Tema
import com.upchiapas.codigoroto.data.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuizViewModel(
    private val repository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    init {
        cargarBanco()
    }

    // ---------- Inicio (fase Configurando) ----------

    fun onTemaSeleccionado(tema: Tema) {
        _uiState.update {
            val temas = it.config.temasSeleccionados
            val nuevos = if (tema in temas) temas - tema else temas + tema
            it.copy(config = recalcularConfig(it.bancoPreguntas, it.config.copy(temasSeleccionados = nuevos)))
        }
    }

    fun onTodosLosTemas() {
        _uiState.update {
            it.copy(config = recalcularConfig(it.bancoPreguntas, it.config.copy(temasSeleccionados = emptySet())))
        }
    }

    fun onDificultadSeleccionada(dificultad: Dificultad?) {
        _uiState.update {
            it.copy(config = recalcularConfig(it.bancoPreguntas, it.config.copy(dificultad = dificultad)))
        }
    }

    fun onNumeroPreguntasCambiado(numero: Int) {
        _uiState.update {
            val config = it.config
            it.copy(config = config.copy(numeroPreguntas = numero.coerceIn(config.minimoPreguntas, config.maximoDisponible)))
        }
    }

    fun onIniciarPartida() {
        val estado = _uiState.value
        if (!estado.config.puedeIniciar) return

        val preguntas = filtrar(estado.bancoPreguntas, estado.config)
            .shuffled()
            .take(estado.config.numeroPreguntas)

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

    fun onOpcionSeleccionada(texto: String) {
        val estado = _uiState.value
        val pregunta = estado.preguntaActual ?: return
        if (estado.fase != FaseQuiz.Respondiendo) return

        val esCorrecta = texto == pregunta.respuestaCorrecta
        val nuevaRacha = if (esCorrecta) estado.racha + 1 else 0
        val puntosGanados = if (esCorrecta) {
            (pregunta.dificultad.puntos * multiplicador(nuevaRacha)).toInt()
        } else {
            0
        }

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

    fun onVolverAlInicio() {
        _uiState.update { it.copy(fase = FaseQuiz.Configurando) }
    }

    // ---------- Lógica interna ----------

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

    /** Ajusta el mínimo, el máximo y el número elegido según los filtros actuales. */
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

    private fun filtrar(banco: List<Pregunta>, config: ConfigPartida): List<Pregunta> =
        banco.filter { pregunta ->
            (config.temasSeleccionados.isEmpty() || pregunta.tema in config.temasSeleccionados) &&
                (config.dificultad == null || pregunta.dificultad == config.dificultad)
        }

    private fun revolverOpciones(pregunta: Pregunta): List<OpcionUi> =
        pregunta.opciones.shuffled().map { OpcionUi(texto = it) }

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

    private fun multiplicador(racha: Int): Double = when {
        racha >= RACHA_DOBLE -> 2.0
        racha >= RACHA_MEDIA -> 1.5
        else -> 1.0
    }

    companion object {
        private const val RACHA_MEDIA = 3
        private const val RACHA_DOBLE = 5
        private const val MINIMO_PREGUNTAS = 5
        private const val MAXIMO_PREGUNTAS = 20
        private const val PREGUNTAS_POR_DEFECTO = 10

        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as CodigoRotoApp
                QuizViewModel(app.quizRepository)
            }
        }
    }
}
