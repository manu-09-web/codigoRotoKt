package com.upchiapas.codigoroto.quiz.data.model

import kotlinx.serialization.Serializable

// Una pregunta del quiz. @Serializable permite leerla del JSON.
@Serializable
data class Pregunta(
    val id: String,                 // identificador
    val enunciado: String,          // la pregunta
    val codigo: String,             // código a analizar
    val opciones: List<String>,     // 4 opciones
    val respuestaCorrecta: String,  // texto de la respuesta correcta
    val tema: Tema,
    val dificultad: Dificultad,
    val explicacion: String = "",   // opcional
    val esDelUsuario: Boolean = false // true si la creó el usuario
)
