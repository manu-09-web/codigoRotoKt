package com.upchiapas.codigoroto.quiz.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.upchiapas.codigoroto.quiz.data.repository.QuizRepository

// Le dice a Android cómo crear los ViewModels del quiz con su repositorio.
class QuizViewModelFactory(
    private val repository: QuizRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            return QuizViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(MisPreguntasViewModel::class.java)) {
            return MisPreguntasViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida: ${modelClass.name}")
    }
}
