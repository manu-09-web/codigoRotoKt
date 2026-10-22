package com.upchiapas.codigoroto

import android.app.Application
import com.upchiapas.codigoroto.quiz.di.QuizModule

// Clase que arranca primero y guarda las dependencias de la app.
class CodigoRotoApp : Application() {

    // Se crea solo cuando se usa por primera vez.
    val quizModule: QuizModule by lazy {
        QuizModule(this)
    }
}
