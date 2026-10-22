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

// Pantalla principal: aplica el tema y muestra el quiz.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // usa toda la pantalla
        setContent {
            CodigoRotoTheme {
                // Estructura base; el padding evita que se encime con las barras del sistema.
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    QuizPage(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
