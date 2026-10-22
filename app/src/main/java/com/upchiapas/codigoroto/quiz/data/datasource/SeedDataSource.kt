package com.upchiapas.codigoroto.quiz.data.datasource

import android.content.Context
import com.upchiapas.codigoroto.quiz.data.model.Pregunta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

// Lee las preguntas base del archivo preguntas.json.
class SeedDataSource(private val context: Context) {

    // Convierte JSON a objetos; ignora campos que no conoce.
    private val json = Json { ignoreUnknownKeys = true }

    // Lee el archivo en segundo plano y regresa la lista de preguntas.
    suspend fun cargarPreguntas(): List<Pregunta> = withContext(Dispatchers.IO) {
        val texto = context.assets.open(ARCHIVO_PREGUNTAS) // abre el archivo
            .bufferedReader()                               // lo prepara para leer texto
            .use { it.readText() }                          // lo lee y lo cierra
        json.decodeFromString<List<Pregunta>>(texto)        // texto JSON -> lista de preguntas
    }

    // Nombre del archivo.
    private companion object {
        const val ARCHIVO_PREGUNTAS = "preguntas.json"
    }
}
