package com.upchiapas.codigoroto.quiz.data.model

import kotlinx.serialization.Serializable

// Datos de una partida terminada (para el historial).
@Serializable
data class Partida(
    val fecha: Long,          // cuándo terminó
    val puntaje: Int,
    val aciertos: Int,
    val totalPreguntas: Int
)
