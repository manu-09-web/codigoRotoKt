package com.upchiapas.codigoroto.presentation.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.upchiapas.codigoroto.CodigoRotoApp
import com.upchiapas.codigoroto.data.model.Pregunta
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
        iniciarPartida()
    }

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

    fun onJugarDeNuevo() {
        iniciarPartida()
    }

    private fun iniciarPartida() {
        _uiState.value = QuizUiState()
        viewModelScope.launch {
            val preguntas = repository.obtenerPreguntas().shuffled()
            _uiState.update {
                it.copy(
                    fase = FaseQuiz.Respondiendo,
                    preguntas = preguntas,
                    opciones = preguntas.firstOrNull()?.let(::revolverOpciones).orEmpty()
                )
            }
        }
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

        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as CodigoRotoApp
                QuizViewModel(app.quizRepository)
            }
        }
    }
}
