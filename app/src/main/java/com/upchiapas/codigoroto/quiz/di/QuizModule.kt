package com.upchiapas.codigoroto.quiz.di

import android.content.Context
import com.upchiapas.codigoroto.quiz.data.datasource.SeedDataSource
import com.upchiapas.codigoroto.quiz.data.repository.QuizRepository
import com.upchiapas.codigoroto.quiz.data.repository.QuizRepositoryImpl
import com.upchiapas.codigoroto.quiz.presentation.viewmodels.QuizViewModelFactory

/**
 * Módulo de inyección de dependencias (di) del quiz.
 *
 * Aquí se arman todas las piezas en un solo lugar y en orden:
 * DataSource -> Repository -> Factory del ViewModel.
 * Ninguna clase crea sus propias dependencias; se las pasa este módulo.
 *
 * by lazy: cada pieza se crea la primera vez que se necesita y después se reutiliza la misma.
 */
class QuizModule(private val context: Context) {

    private val seedDataSource: SeedDataSource by lazy {
        SeedDataSource(context)
    }

    private val repository: QuizRepository by lazy {
        QuizRepositoryImpl(seedDataSource)
    }

    val factoryQuiz: QuizViewModelFactory by lazy {
        QuizViewModelFactory(repository)
    }
}
