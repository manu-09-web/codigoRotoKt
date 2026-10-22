package com.upchiapas.codigoroto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.upchiapas.codigoroto.quiz.presentation.screens.PantallaPrincipal
import com.upchiapas.codigoroto.ui.theme.CodigoRotoTheme

// Pantalla principal: aplica el tema y muestra la app.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // usa toda la pantalla
        setContent {
            CodigoRotoTheme {
                PantallaPrincipal()
            }
        }
    }
}
