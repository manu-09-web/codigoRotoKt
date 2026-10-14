package com.upchiapas.codigoroto.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Pregunta(
    val id: String,
    val enunciado: String,
    val codigo: String,
    val opciones: List<String>,
    val respuestaCorrecta: String,
    val tema: Tema,
    val dificultad: Dificultad,
    val explicacion: String = "",
    val esDelUsuario: Boolean = false
)