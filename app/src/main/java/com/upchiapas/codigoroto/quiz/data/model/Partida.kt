package com.upchiapas.codigoroto.quiz.data.model

import kotlinx.serialization.Serializable

/**
 * Resultado de una partida terminada, para el historial.
 *
 * Todavía no se usa: se guardará en DataStore cuando agreguemos el récord y el historial.
 * Es @Serializable porque DataStore guarda texto y la lista de partidas se guardará como JSON.
 */
@Serializable
data class Partida(
    val fecha: Long,          // momento en que terminó, en milisegundos (System.currentTimeMillis())
    val puntaje: Int,
    val aciertos: Int,
    val totalPreguntas: Int
)
