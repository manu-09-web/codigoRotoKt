package com.upchiapas.codigoroto.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Partida(
    val fecha: Long,
    val puntaje: Int,
    val aciertos: Int,
    val totalPreguntas: Int
)