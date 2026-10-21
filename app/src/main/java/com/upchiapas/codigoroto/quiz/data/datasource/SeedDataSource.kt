package com.upchiapas.codigoroto.quiz.data.datasource

import android.content.Context
import com.upchiapas.codigoroto.quiz.data.model.Pregunta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

/**
 * Fuente de datos semilla (las preguntas base que vienen con la app).
 *
 * En esta clase se llama al archivo JSON, se leen los datos y se convierten a objetos de la clase Pregunta.
 * Recibe el Context porque solo con él Android deja abrir la carpeta assets.
 * Solo la usa el QuizRepository; las pantallas no saben que existe.
 */
class SeedDataSource(private val context: Context) {

    // Convertidor de JSON. ignoreUnknownKeys = true: si el JSON trae un campo extra, lo ignora en vez de fallar.
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Lee preguntas.json y regresa la lista de preguntas.
     *
     * suspend indica que la función puede esperar sin congelar la app.
     * withContext(Dispatchers.IO) manda el trabajo a un hilo de fondo especial para leer archivos.
     */
    suspend fun cargarPreguntas(): List<Pregunta> = withContext(Dispatchers.IO) {
        val texto = context.assets.open(ARCHIVO_PREGUNTAS) // 1. abre el archivo de la carpeta assets
            .bufferedReader()                               // 2. lo prepara para leerlo como texto
            .use { it.readText() }                          // 3. lo lee completo; use{} lo cierra al terminar
        json.decodeFromString<List<Pregunta>>(texto)        // 4. convierte el texto JSON en List<Pregunta>
    }

    // companion object: cosas de la clase (no de cada objeto). Aquí, el nombre del archivo como constante.
    private companion object {
        const val ARCHIVO_PREGUNTAS = "preguntas.json"
    }
}
