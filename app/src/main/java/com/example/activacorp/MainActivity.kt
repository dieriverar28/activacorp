package com.example.activacorp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.activacorp.navegacion.Navegacion
import com.example.activacorp.ui.theme.ActivacorpTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ActivacorpTheme {
                Navegacion()
            }
        }
    }
}


