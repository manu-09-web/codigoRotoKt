package com.upchiapas.codigoroto.quiz.data.model

/**
 * Niveles de dificultad de una pregunta.
 *
 * Cada valor trae sus puntos base, que se suman cuando el usuario acierta.
 * Ejemplo: pregunta.dificultad.puntos -> 10, 20 o 30.
 */
enum class Dificultad(val puntos: Int) {
    FACIL(10),
    MEDIA(20),
    DIFICIL(30)
}
