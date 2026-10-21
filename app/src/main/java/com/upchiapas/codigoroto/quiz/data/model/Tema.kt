package com.upchiapas.codigoroto.quiz.data.model

/**
 * Temas de las preguntas.
 *
 * Es un enum (lista cerrada de valores): una pregunta solo puede ser de uno de estos 5 temas.
 * Así se evitan errores de escritura como "nulos" vs "Nulos".
 * En el JSON el tema se escribe igual que aquí, por ejemplo "NULOS".
 */
enum class Tema {
    VARIABLES,
    NULOS,
    COLECCIONES,
    CONTROL_FLUJO,
    FUNCIONES
}
