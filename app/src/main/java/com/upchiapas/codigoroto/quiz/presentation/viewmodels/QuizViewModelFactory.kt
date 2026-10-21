package com.upchiapas.codigoroto.quiz.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.upchiapas.codigoroto.quiz.data.repository.QuizRepository

/**
 * Factory (fábrica) del QuizViewModel.
 *
 * Android es quien crea los ViewModels y por defecto solo sabe crearlos sin parámetros.
 * Como QuizViewModel necesita un repositorio, esta clase le dice a Android cómo crearlo.
 */
class QuizViewModelFactory(
    private val repository: QuizRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Si piden un QuizViewModel, lo crea pasándole el repositorio.
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            return QuizViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida: ${modelClass.name}")
    }
}
