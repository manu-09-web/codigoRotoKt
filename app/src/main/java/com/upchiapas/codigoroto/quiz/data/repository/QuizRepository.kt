package com.upchiapas.codigoroto.quiz.data.repository

import com.upchiapas.codigoroto.quiz.data.model.Partida
import com.upchiapas.codigoroto.quiz.data.model.Pregunta
import kotlinx.coroutines.flow.Flow

// Define qué datos se le pueden pedir al repositorio.
interface QuizRepository {

    // Todas las preguntas (las del JSON + las del usuario).
    fun obtenerPreguntas(): Flow<List<Pregunta>>

    // Solo las preguntas que agregó el usuario.
    fun obtenerPreguntasUsuario(): Flow<List<Pregunta>>

    // Guarda una pregunta nueva del usuario.
    suspend fun agregarPregunta(pregunta: Pregunta)

    // Borra una pregunta del usuario.
    suspend fun eliminarPregunta(id: String)

    // Partidas guardadas.
    fun obtenerHistorial(): Flow<List<Partida>>

    // Mejor puntaje.
    fun obtenerRecord(): Flow<Int>

    // Guarda una partida terminada.
    suspend fun guardarPartida(partida: Partida)
}
