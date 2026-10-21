package com.upchiapas.codigoroto.quiz.data.model

import kotlinx.serialization.Serializable

/**
 * Una pregunta del quiz.
 *
 * - data class: clase que solo guarda datos. Kotlin le genera copy(), equals() y toString()
 * - @Serializable: permite convertir el JSON de preguntas.json en objetos Pregunta (y al revés)
 * - Todo es val (inmutable): una vez creada, la pregunta no cambia.
 *
 * Los nombres de las propiedades deben ser iguales a los campos del JSON.
 */
@Serializable
data class Pregunta(
    val id: String,                 // "seed-01" para las del JSON
    val enunciado: String,          // ej. "¿Qué imprime este código?"
    val codigo: String,             // el código a analizar; \n es un salto de línea
    val opciones: List<String>,     // siempre 4 opciones
    val respuestaCorrecta: String,  // TEXTO de la correcta, no su posición, porque las opciones se revuelven
    val tema: Tema,
    val dificultad: Dificultad,
    val explicacion: String = "",   // opcional: si no viene en el JSON queda vacía
    val esDelUsuario: Boolean = false // true para las preguntas que agregue el usuario
)
