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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.io.IOException

// Archivo de DataStore donde se guardan los datos del usuario.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "codigo_roto_prefs")

// Guarda y lee en el celular el historial de partidas y el récord.
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

    // Nombres con los que se guarda cada dato.
    private companion object {
        val HISTORIAL = stringPreferencesKey("historial_partidas")
        val RECORD = intPreferencesKey("record_puntaje")
    }
}
