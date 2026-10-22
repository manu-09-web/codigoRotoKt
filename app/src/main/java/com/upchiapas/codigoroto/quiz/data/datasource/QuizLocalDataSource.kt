package com.upchiapas.codigoroto.quiz.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.upchiapas.codigoroto.quiz.data.model.Partida
import com.upchiapas.codigoroto.quiz.data.model.Pregunta
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.io.IOException

// Archivo de DataStore donde se guardan los datos del usuario.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "codigo_roto_prefs")

// Guarda y lee en el celular las preguntas del usuario, el historial y el récord.
class QuizLocalDataSource(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    // Lee los datos guardados; si falla, regresa vacío.
    private val preferencias: Flow<Preferences> = context.dataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }

    // Lista de partidas guardadas.
    val historial: Flow<List<Partida>> = preferencias.map { prefs ->
        prefs[HISTORIAL]?.let { texto -> json.decodeFromString<List<Partida>>(texto) } ?: emptyList()
    }

    // Mejor puntaje guardado.
    val record: Flow<Int> = preferencias.map { prefs -> prefs[RECORD] ?: 0 }

    // Guarda la partida y actualiza el récord si lo superó.
    suspend fun guardarPartida(partida: Partida) {
        context.dataStore.edit { prefs ->
            val actual = prefs[HISTORIAL]?.let { json.decodeFromString<List<Partida>>(it) } ?: emptyList()
            prefs[HISTORIAL] = json.encodeToString(actual + partida)   // la lista se guarda como JSON

            if (partida.puntaje > (prefs[RECORD] ?: 0)) {
                prefs[RECORD] = partida.puntaje
            }
        }
    }

    // Preguntas que agregó el usuario.
    val preguntasUsuario: Flow<List<Pregunta>> = preferencias.map { prefs ->
        prefs[PREGUNTAS_USUARIO]?.let { texto -> json.decodeFromString<List<Pregunta>>(texto) } ?: emptyList()
    }

    // Agrega una pregunta nueva del usuario.
    suspend fun guardarPregunta(pregunta: Pregunta) {
        context.dataStore.edit { prefs ->
            val actuales = prefs[PREGUNTAS_USUARIO]?.let { json.decodeFromString<List<Pregunta>>(it) } ?: emptyList()
            prefs[PREGUNTAS_USUARIO] = json.encodeToString(actuales + pregunta)
        }
    }

    // Borra una pregunta del usuario por su id.
    suspend fun eliminarPregunta(id: String) {
        context.dataStore.edit { prefs ->
            val actuales = prefs[PREGUNTAS_USUARIO]?.let { json.decodeFromString<List<Pregunta>>(it) } ?: emptyList()
            prefs[PREGUNTAS_USUARIO] = json.encodeToString(actuales.filterNot { it.id == id })
        }
    }

    // Nombres con los que se guarda cada dato.
    private companion object {
        val PREGUNTAS_USUARIO = stringPreferencesKey("preguntas_usuario")
        val HISTORIAL = stringPreferencesKey("historial_partidas")
        val RECORD = intPreferencesKey("record_puntaje")
    }
}
