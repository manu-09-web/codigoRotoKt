package com.upchiapas.codigoroto

import android.app.Application
import com.upchiapas.codigoroto.quiz.di.QuizModule

/**
 * Clase Application: Android la crea antes que cualquier pantalla y vive mientras la app esté abierta.
 * Está registrada en el AndroidManifest con android:name=".CodigoRotoApp".
 *
 * Guarda el módulo de dependencias del quiz para que toda la app use las mismas piezas.
 */
class CodigoRotoApp : Application() {

    // by lazy: el módulo se crea la primera vez que alguien lo pide, no antes.
    val quizModule: QuizModule by lazy {
        QuizModule(this)
    }
}
