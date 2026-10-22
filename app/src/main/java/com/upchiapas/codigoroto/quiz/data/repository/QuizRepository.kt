package com.upchiapas.codigoroto.quiz.data.repository

import com.upchiapas.codigoroto.quiz.data.model.Partida
import com.upchiapas.codigoroto.quiz.data.model.Pregunta
import kotlinx.coroutines.flow.Flow

// Define qué datos se le pueden pedir al repositorio.
interface QuizRepository {

    // Todas las preguntas.
    suspend fun obtenerPreguntas(): List<Pregunta>

    // Partidas guardadas.
    fun obtenerHistorial(): Flow<List<Partida>>

    // Mejor puntaje.
    fun obtenerRecord(): Flow<Int>

    // Guarda una partida terminada.
    suspend fun guardarPartida(partida: Partida)
}
