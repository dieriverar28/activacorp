package com.example.activacorp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * CONFIGURACIÓN DE TIPOGRAFÍA: Material Design 3
 * Define los estilos de texto globales de la aplicación (fuentes, tamaños, pesos y espaciados).
 * Permite estandarizar el aspecto tipográfico en todas las pantallas y componentes Compose.
 */
val Typography = Typography(
    // Estilo predeterminado para bloques de texto principal y descripciones
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Sobrescrituras adicionales disponibles según la jerarquía visual deseada:
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)