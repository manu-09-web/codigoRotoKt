package com.upchiapas.codigoroto.data.repository

import com.upchiapas.codigoroto.data.datasource.SeedDataSource
import com.upchiapas.codigoroto.data.model.Pregunta

class QuizRepository(
    private val seedDataSource: SeedDataSource
) {

    suspend fun obtenerPreguntas(): List<Pregunta> =
        seedDataSource.cargarPreguntas()
}
