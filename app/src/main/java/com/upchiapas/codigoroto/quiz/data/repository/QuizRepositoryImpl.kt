package com.upchiapas.codigoroto.quiz.data.repository

import com.upchiapas.codigoroto.quiz.data.datasource.QuizLocalDataSource
import com.upchiapas.codigoroto.quiz.data.datasource.SeedDataSource
import com.upchiapas.codigoroto.quiz.data.model.Partida
import com.upchiapas.codigoroto.quiz.data.model.Pregunta
import kotlinx.coroutines.flow.Flow

// Obtiene los datos del JSON (preguntas) y de DataStore (historial y récord).
class QuizRepositoryImpl(
    private val seedDataSource: SeedDataSource,
    private val localDataSource: QuizLocalDataSource
) : QuizRepository {

    override suspend fun obtenerPreguntas(): List<Pregunta> =
        seedDataSource.cargarPreguntas()

    override fun obtenerHistorial(): Flow<List<Partida>> =
        localDataSource.historial

    override fun obtenerRecord(): Flow<Int> =
        localDataSource.record

    override suspend fun guardarPartida(partida: Partida) =
        localDataSource.guardarPartida(partida)
}
