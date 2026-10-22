package com.upchiapas.codigoroto.quiz.data.repository

import com.upchiapas.codigoroto.quiz.data.datasource.QuizLocalDataSource
import com.upchiapas.codigoroto.quiz.data.datasource.SeedDataSource
import com.upchiapas.codigoroto.quiz.data.model.Partida
import com.upchiapas.codigoroto.quiz.data.model.Pregunta
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

// Obtiene los datos del JSON (preguntas base) y de DataStore (preguntas del usuario, historial y récord).
class QuizRepositoryImpl(
    private val seedDataSource: SeedDataSource,
    private val localDataSource: QuizLocalDataSource
) : QuizRepository {

    // Junta las preguntas del JSON con las del usuario; se actualiza solo si el usuario agrega o borra.
    override fun obtenerPreguntas(): Flow<List<Pregunta>> =
        combine(
            flow { emit(seedDataSource.cargarPreguntas()) },
            localDataSource.preguntasUsuario
        ) { base, delUsuario -> base + delUsuario }

    override fun obtenerPreguntasUsuario(): Flow<List<Pregunta>> =
        localDataSource.preguntasUsuario

    override suspend fun agregarPregunta(pregunta: Pregunta) =
        localDataSource.guardarPregunta(pregunta)

    override suspend fun eliminarPregunta(id: String) =
        localDataSource.eliminarPregunta(id)

    override fun obtenerHistorial(): Flow<List<Partida>> =
        localDataSource.historial

    override fun obtenerRecord(): Flow<Int> =
        localDataSource.record

    override suspend fun guardarPartida(partida: Partida) =
        localDataSource.guardarPartida(partida)
}
