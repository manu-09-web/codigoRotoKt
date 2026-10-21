package com.upchiapas.codigoroto.quiz.data.repository

import com.upchiapas.codigoroto.quiz.data.model.Pregunta

/**
 * Contrato del repositorio del quiz: dice QUÉ datos se pueden pedir, no CÓMO se obtienen.
 *
 * El ViewModel depende de esta interface y no de la implementación. Así, si cambia la forma
 * de obtener los datos (JSON, DataStore, una versión falsa para pruebas), el ViewModel no cambia.
 */
interface QuizRepository {

    /** Regresa todas las preguntas disponibles. */
    suspend fun obtenerPreguntas(): List<Pregunta>
}
