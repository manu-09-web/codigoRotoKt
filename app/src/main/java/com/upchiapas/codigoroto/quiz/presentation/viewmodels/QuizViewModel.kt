package com.upchiapas.codigoroto.quiz.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upchiapas.codigoroto.quiz.data.model.Dificultad
import com.upchiapas.codigoroto.quiz.data.model.Partida
import com.upchiapas.codigoroto.quiz.data.model.Pregunta
import com.upchiapas.codigoroto.quiz.data.model.Tema
import com.upchiapas.codigoroto.quiz.data.repository.QuizRepository
import com.upchiapas.codigoroto.quiz.presentation.screens.ConfigPartida
import com.upchiapas.codigoroto.quiz.presentation.screens.EstadoOpcion
import com.upchiapas.codigoroto.quiz.presentation.screens.Estadisticas
import com.upchiapas.codigoroto.quiz.presentation.screens.FaseQuiz
import com.upchiapas.codigoroto.quiz.presentation.screens.NivelAcierto
import com.upchiapas.codigoroto.quiz.presentation.screens.OpcionUi
import com.upchiapas.codigoroto.quiz.presentation.screens.QuizUiState
import com.upchiapas.codigoroto.quiz.presentation.screens.ResultadoPartida
import com.upchiapas.codigoroto.quiz.presentation.screens.ResultadoTema
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Lógica del quiz: filtrar, revisar respuestas, sumar puntos y guardar partidas.
class QuizViewModel(
    private val repository: QuizRepository
) : ViewModel() {

    // _uiState lo cambia el ViewModel; uiState solo lo lee la pantalla.
    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    // Al crearse: carga preguntas y estadísticas.
    init {
        cargarBanco()
        observarEstadisticas()
    }

    // ---------- Inicio (fase Configurando) ----------

    // Marca o desmarca un tema.
    fun onTemaSeleccionado(tema: Tema) {
        _uiState.update {
            val temas = it.config.temasSeleccionados
            val nuevos = if (tema in temas) temas - tema else temas + tema
            it.copy(config = recalcularConfig(it.bancoPreguntas, it.config.copy(temasSeleccionados = nuevos)))
        }
    }

    // Usa todos los temas.
    fun onTodosLosTemas() {
        _uiState.update {
            it.copy(config = recalcularConfig(it.bancoPreguntas, it.config.copy(temasSeleccionados = emptySet())))
        }
    }

    // Cambia la dificultad (null = todas).
    fun onDificultadSeleccionada(dificultad: Dificultad?) {
        _uiState.update {
            it.copy(config = recalcularConfig(it.bancoPreguntas, it.config.copy(dificultad = dificultad)))
        }
    }

    // Cambia cuántas preguntas jugar.
    fun onNumeroPreguntasCambiado(numero: Int) {
        _uiState.update {
            val config = it.config
            it.copy(config = config.copy(numeroPreguntas = numero.coerceIn(config.minimoPreguntas, config.maximoDisponible)))
        }
    }

    // Empieza la partida.
    fun onIniciarPartida() {
        val estado = _uiState.value
        if (!estado.config.puedeIniciar) return

        // Filtra, revuelve y toma las que pidió.
        val preguntas = filtrar(estado.bancoPreguntas, estado.config)
            .shuffled()
            .take(estado.config.numeroPreguntas)

        // Reinicia la partida.
        _uiState.update {
            it.copy(
                fase = FaseQuiz.Respondiendo,
                preguntas = preguntas,
                indiceActual = 0,
                opciones = revolverOpciones(preguntas.first()),
                puntaje = 0,
                racha = 0,
                aciertos = 0,
                respuestas = emptyList(),
                resultado = null
            )
        }
    }

    // Otra partida con los mismos filtros.
    fun onJugarDeNuevo() {
        onIniciarPartida()
    }

    // ---------- Juego ----------

    // Revisa la respuesta y suma puntos.
    fun onOpcionSeleccionada(texto: String) {
        val estado = _uiState.value
        val pregunta = estado.preguntaActual ?: return       // si no hay pregunta, no hace nada
        if (estado.fase != FaseQuiz.Respondiendo) return     // no deja contestar dos veces

        val esCorrecta = texto == pregunta.respuestaCorrecta
        val nuevaRacha = if (esCorrecta) estado.racha + 1 else 0 // si falla, la racha vuelve a 0
        val puntosGanados = if (esCorrecta) {
            (pregunta.dificultad.puntos * multiplicador(nuevaRacha)).toInt()
        } else {
            0
        }

        // Nuevo estado con la respuesta marcada.
        _uiState.update {
            it.copy(
                fase = FaseQuiz.Respondida(elegida = texto, esCorrecta = esCorrecta),
                opciones = marcarOpciones(it.opciones, pregunta.respuestaCorrecta, texto),
                puntaje = it.puntaje + puntosGanados,
                racha = nuevaRacha,
                aciertos = if (esCorrecta) it.aciertos + 1 else it.aciertos,
                respuestas = it.respuestas + esCorrecta   // para el % por tema
            )
        }
    }

    // Siguiente pregunta o termina la partida.
    fun onSiguiente() {
        val estado = _uiState.value
        if (estado.fase !is FaseQuiz.Respondida) return

        val siguiente = estado.indiceActual + 1
        if (siguiente >= estado.totalPreguntas) {
            // Era la última: muestra Resultados y guarda la partida.
            _uiState.update { it.copy(fase = FaseQuiz.Finalizada, resultado = calcularResultado(it)) }
            guardarPartida(estado)
            return
        }
        _uiState.update {
            it.copy(
                fase = FaseQuiz.Respondiendo,
                indiceActual = siguiente,
                opciones = revolverOpciones(it.preguntas[siguiente])
            )
        }
    }

    // Regresa a Inicio.
    fun onVolverAlInicio() {
        _uiState.update { it.copy(fase = FaseQuiz.Configurando) }
    }

    // ---------- Lógica interna ----------

    // Escucha las preguntas (JSON + usuario) y muestra Inicio.
    // Si el usuario agrega o borra una pregunta, el banco se actualiza solo.
    private fun cargarBanco() {
        viewModelScope.launch {
            repository.obtenerPreguntas().collect { banco ->
                _uiState.update {
                    val primeraVez = it.fase == FaseQuiz.Cargando
                    val config = if (primeraVez) ConfigPartida(numeroPreguntas = PREGUNTAS_POR_DEFECTO) else it.config
                    it.copy(
                        fase = if (primeraVez) FaseQuiz.Configurando else it.fase,
                        bancoPreguntas = banco,
                        config = recalcularConfig(banco, config)
                    )
                }
            }
        }
    }

    // Ajusta el rango del Slider según los filtros.
    private fun recalcularConfig(banco: List<Pregunta>, config: ConfigPartida): ConfigPartida {
        val disponibles = filtrar(banco, config).size
        val maximo = minOf(disponibles, MAXIMO_PREGUNTAS)
        val minimo = minOf(MINIMO_PREGUNTAS, maximo)
        return config.copy(
            minimoPreguntas = minimo,
            maximoDisponible = maximo,
            numeroPreguntas = config.numeroPreguntas.coerceIn(minimo, maximo)
        )
    }

    // Filtra por tema y dificultad.
    private fun filtrar(banco: List<Pregunta>, config: ConfigPartida): List<Pregunta> =
        banco.filter { pregunta ->
            (config.temasSeleccionados.isEmpty() || pregunta.tema in config.temasSeleccionados) &&
                (config.dificultad == null || pregunta.dificultad == config.dificultad)
        }

    // Revuelve las opciones.
    private fun revolverOpciones(pregunta: Pregunta): List<OpcionUi> =
        pregunta.opciones.shuffled().map { OpcionUi(texto = it) }

    // Marca cuál es correcta, incorrecta o deshabilitada.
    private fun marcarOpciones(
        opciones: List<OpcionUi>,
        correcta: String,
        elegida: String
    ): List<OpcionUi> = opciones.map { opcion ->
        val estado = when (opcion.texto) {
            correcta -> EstadoOpcion.CORRECTA
            elegida -> EstadoOpcion.INCORRECTA
            else -> EstadoOpcion.DESHABILITADA
        }
        opcion.copy(estado = estado)
    }

    // Calcula el resumen: % total, % por tema y tema más débil.
    private fun calcularResultado(estado: QuizUiState): ResultadoPartida {
        // Junta cada pregunta con su respuesta y agrupa por tema.
        val porTema = estado.preguntas.zip(estado.respuestas)
            .groupBy({ (pregunta, _) -> pregunta.tema }, { (_, correcta) -> correcta })
            .map { (tema, respuestas) ->
                val aciertos = respuestas.count { it }
                val porcentaje = porcentaje(aciertos, respuestas.size)
                ResultadoTema(
                    tema = tema,
                    aciertos = aciertos,
                    total = respuestas.size,
                    porcentaje = porcentaje,
                    nivel = nivelDe(porcentaje)
                )
            }
            .sortedByDescending { it.porcentaje }

        // Tema con menor %, si no fue perfecto.
        val masDebil = porTema.minByOrNull { it.porcentaje }?.takeIf { it.porcentaje < 100 }

        return ResultadoPartida(
            puntaje = estado.puntaje,
            aciertos = estado.aciertos,
            total = estado.totalPreguntas,
            porcentaje = porcentaje(estado.aciertos, estado.totalPreguntas),
            porTema = porTema,
            temaMasDebil = masDebil?.tema,
            esNuevoRecord = estado.puntaje > estado.estadisticas.record
        )
    }

    // ---------- Persistencia (DataStore) ----------

    // Escucha historial y récord y actualiza las estadísticas.
    private fun observarEstadisticas() {
        viewModelScope.launch {
            combine(repository.obtenerHistorial(), repository.obtenerRecord()) { historial, record ->
                val totalPreguntas = historial.sumOf { it.totalPreguntas }
                val totalAciertos = historial.sumOf { it.aciertos }
                Estadisticas(
                    record = record,
                    partidasJugadas = historial.size,
                    porcentajeAcierto = porcentaje(totalAciertos, totalPreguntas)
                )
            }.collect { estadisticas ->
                _uiState.update { it.copy(estadisticas = estadisticas) }
            }
        }
    }

    // Guarda la partida en DataStore.
    private fun guardarPartida(estado: QuizUiState) {
        val partida = Partida(
            fecha = System.currentTimeMillis(),
            puntaje = estado.puntaje,
            aciertos = estado.aciertos,
            totalPreguntas = estado.totalPreguntas
        )
        viewModelScope.launch {
            repository.guardarPartida(partida)
        }
    }

    private fun porcentaje(aciertos: Int, total: Int): Int =
        if (total == 0) 0 else aciertos * 100 / total

    // % -> nivel (alto, medio, bajo).
    private fun nivelDe(porcentaje: Int): NivelAcierto = when {
        porcentaje >= NIVEL_ALTO -> NivelAcierto.ALTO
        porcentaje >= NIVEL_MEDIO -> NivelAcierto.MEDIO
        else -> NivelAcierto.BAJO
    }

    // Multiplicador de puntos según la racha.
    private fun multiplicador(racha: Int): Double = when {
        racha >= RACHA_DOBLE -> 2.0
        racha >= RACHA_MEDIA -> 1.5
        else -> 1.0
    }

    // Constantes del juego.
    private companion object {
        const val RACHA_MEDIA = 3          // x1.5 desde 3 seguidos
        const val RACHA_DOBLE = 5          // x2 desde 5 seguidos
        const val MINIMO_PREGUNTAS = 5
        const val MAXIMO_PREGUNTAS = 20
        const val PREGUNTAS_POR_DEFECTO = 10
        const val NIVEL_ALTO = 90          // 90% o más
        const val NIVEL_MEDIO = 70         // 70% a 89%
    }
}
