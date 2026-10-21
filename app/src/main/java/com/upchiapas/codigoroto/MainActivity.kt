package com.upchiapas.codigoroto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.upchiapas.codigoroto.quiz.presentation.screens.QuizPage
import com.upchiapas.codigoroto.ui.theme.CodigoRotoTheme

/**
 * Única Activity de la app: es la ventana donde Compose dibuja todo.
 * No tiene lógica; solo aplica el tema y muestra QuizPage.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // la app usa toda la pantalla, incluida la zona de la barra de estado
        setContent {
            CodigoRotoTheme {
                // Scaffold da la estructura base; innerPadding evita que el contenido quede debajo de las barras del sistema.
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    QuizPage(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
