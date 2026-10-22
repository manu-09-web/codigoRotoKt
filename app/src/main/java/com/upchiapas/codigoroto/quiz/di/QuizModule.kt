package com.upchiapas.codigoroto.quiz.di

import android.content.Context
import com.upchiapas.codigoroto.quiz.data.datasource.QuizLocalDataSource
import com.upchiapas.codigoroto.quiz.data.datasource.SeedDataSource
import com.upchiapas.codigoroto.quiz.data.repository.QuizRepository
import com.upchiapas.codigoroto.quiz.data.repository.QuizRepositoryImpl
import com.upchiapas.codigoroto.quiz.presentation.viewmodels.QuizViewModelFactory

// Crea las piezas del quiz en orden: DataSource -> Repository -> Factory.
class QuizModule(private val context: Context) {

    private val seedDataSource: SeedDataSource by lazy {
        SeedDataSource(context)
    }

    private val localDataSource: QuizLocalDataSource by lazy {
        QuizLocalDataSource(context)
    }

    private val repository: QuizRepository by lazy {
        QuizRepositoryImpl(seedDataSource, localDataSource)
    }

    val factoryQuiz: QuizViewModelFactory by lazy {
        QuizViewModelFactory(repository)
    }
}
