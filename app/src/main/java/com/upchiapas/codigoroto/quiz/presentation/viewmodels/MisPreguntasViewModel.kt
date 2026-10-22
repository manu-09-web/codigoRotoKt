package com.upchiapas.codigoroto.quiz.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upchiapas.codigoroto.R
import com.upchiapas.codigoroto.quiz.data.model.Dificultad
import com.upchiapas.codigoroto.quiz.data.model.Pregunta
import com.upchiapas.codigoroto.quiz.data.model.Tema
import com.upchiapas.codigoroto.quiz.data.repository.QuizRepository
import com.upchiapas.codigoroto.quiz.presentation.screens.ErroresFormulario
import com.upchiapas.codigoroto.quiz.presentation.screens.FormularioPregunta
import com.upchiapas.codigoroto.quiz.presentation.screens.MisPreguntasUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

// Lógica de Mis preguntas: listar, agregar (con validación) y borrar.
class MisPreguntasViewModel(
    private val repository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MisPreguntasUiState())
    val uiState: StateFlow<MisPreguntasUiState> = _uiState.asStateFlow()

    // Escucha las preguntas guardadas del usuario.
    init {
        viewModelScope.launch {
            repository.obtenerPreguntasUsuario().collect { preguntas ->
                _uiState.update { it.copy(preguntas = preguntas) }
            }
        }
    }

    // ---------- Lista ----------

    fun onNuevaPregunta() {
        _uiState.update { it.copy(mostrandoFormulario = true, formulario = FormularioPregunta(), errores = ErroresFormulario()) }
    }

    fun onEliminar(id: String) {
        viewModelScope.launch { repository.eliminarPregunta(id) }
    }

    // ---------- Formulario ----------

    fun onCancelar() {
        _uiState.update { it.copy(mostrandoFormulario = false) }
    }

    fun onEnunciadoCambiado(texto: String) = actualizarFormulario { it.copy(enunciado = texto) }

    fun onCodigoCambiado(texto: String) = actualizarFormulario { it.copy(codigo = texto) }

    fun onOpcionCambiada(indice: Int, texto: String) =
        actualizarFormulario { form -> form.copy(opciones = form.opciones.mapIndexed { i, o -> if (i == indice) texto else o }) }

    fun onCorrectaSeleccionada(indice: Int) = actualizarFormulario { it.copy(indiceCorrecta = indice) }

    fun onTemaSeleccionado(tema: Tema) = actualizarFormulario { it.copy(tema = tema) }

    fun onDificultadSeleccionada(dificultad: Dificultad) = actualizarFormulario { it.copy(dificultad = dificultad) }

    fun onExplicacionCambiada(texto: String) = actualizarFormulario { it.copy(explicacion = texto) }

    // Valida; si todo está bien, guarda la pregunta y cierra el formulario.
    fun onGuardar() {
        val form = _uiState.value.formulario
        val errores = validar(form)
        if (errores.hayErrores) {
            _uiState.update { it.copy(errores = errores) }
            return
        }
        val opciones = form.opciones.map { it.trim() }
        val pregunta = Pregunta(
            id = UUID.randomUUID().toString(),
            enunciado = form.enunciado.trim(),
            codigo = form.codigo.trimEnd(),
            opciones = opciones,
            respuestaCorrecta = opciones[form.indiceCorrecta ?: 0],
            tema = form.tema ?: Tema.VARIABLES,
            dificultad = form.dificultad,
            explicacion = form.explicacion.trim(),
            esDelUsuario = true
        )
        viewModelScope.launch { repository.agregarPregunta(pregunta) }
        _uiState.update { it.copy(mostrandoFormulario = false) }
    }

    // ---------- Lógica interna ----------

    // Cambia el formulario y quita los errores mostrados.
    private fun actualizarFormulario(cambio: (FormularioPregunta) -> FormularioPregunta) {
        _uiState.update { it.copy(formulario = cambio(it.formulario), errores = ErroresFormulario()) }
    }

    // Revisa que todos los campos obligatorios estén bien.
    private fun validar(form: FormularioPregunta): ErroresFormulario {
        val opciones = form.opciones.map { it.trim() }
        return ErroresFormulario(
            enunciado = if (form.enunciado.isBlank()) R.string.error_campo_vacio else null,
            codigo = if (form.codigo.isBlank()) R.string.error_campo_vacio else null,
            opciones = when {
                opciones.any { it.isEmpty() } -> R.string.error_opciones_vacias
                opciones.toSet().size < opciones.size -> R.string.error_opciones_repetidas
                else -> null
            },
            correcta = if (form.indiceCorrecta == null) R.string.error_sin_correcta else null,
            tema = if (form.tema == null) R.string.error_sin_tema else null
        )
    }
}
