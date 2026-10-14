package com.upchiapas.codigoroto

import android.app.Application
import com.upchiapas.codigoroto.data.datasource.SeedDataSource
import com.upchiapas.codigoroto.data.repository.QuizRepository

class CodigoRotoApp : Application() {

    val quizRepository: QuizRepository by lazy {
        QuizRepository(SeedDataSource(this))
    }
}
