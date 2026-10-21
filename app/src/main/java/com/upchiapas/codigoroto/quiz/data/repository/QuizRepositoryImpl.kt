package com.upchiapas.codigoroto.quiz.data.repository

import com.upchiapas.codigoroto.quiz.data.datasource.SeedDataSource
import com.upchiapas.codigoroto.quiz.data.model.Pregunta

/**
 * Implementación del repositorio: aquí está el CÓMO se obtienen los datos.
 *
 * Por ahora solo entrega las preguntas del JSON. Más adelante aquí se juntarán con las preguntas
 * que agregue el usuario (guardadas en DataStore), sin que el ViewModel tenga que cambiar.
 */
class QuizRepositoryImpl(
    private val seedDataSource: SeedDataSource
) : QuizRepository {

    // override: cumple la función que pide la interface QuizRepository.
    override suspend fun obtenerPreguntas(): List<Pregunta> =
        seedDataSource.cargarPreguntas()
}
