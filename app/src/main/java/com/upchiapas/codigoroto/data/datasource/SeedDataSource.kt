package com.upchiapas.codigoroto.data.datasource

import android.content.Context
import com.upchiapas.codigoroto.data.model.Pregunta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class SeedDataSource(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun cargarPreguntas(): List<Pregunta> = withContext(Dispatchers.IO) {
        val texto = context.assets.open(ARCHIVO_PREGUNTAS)
            .bufferedReader()
            .use { it.readText() }
        json.decodeFromString<List<Pregunta>>(texto)
    }

    private companion object {
        const val ARCHIVO_PREGUNTAS = "preguntas.json"
    }
}
