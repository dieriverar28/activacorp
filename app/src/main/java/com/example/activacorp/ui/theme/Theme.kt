package com.example.activacorp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * ESQUEMA DE COLORES: Modo Oscuro (DarkColorScheme)
 * Define las paletas de color secundarias para interfaces en entornos con poca luz.
 */
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

/**
 * ESQUEMA DE COLORES: Modo Claro (LightColorScheme)
 * Define las paletas de color estándar según Material Design 3.
 */
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Otros colores opcionales configurables según la identidad de marca:
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

/**
 * TEMA DE LA APLICACIÓN: ActivacorpTheme (Material Design 3)
 * Envuelve el contenido de la aplicación para aplicar estilos, colores dinámicos (Android 12+)
 * y tipografías personalizadas globales.
 */
@Composable
fun ActivacorpTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Detecta automáticamente el modo oscuro del sistema
    dynamicColor: Boolean = true,              // Habilita colores dinámicos en Android 12+ (API 31+)
    content: @Composable () -> Unit             // Contenido de la UI que heredará el estilo
) {
    // Selección dinámica de la paleta de colores según la versión de Android y la preferencia de tema
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Inyección global de la configuración de MaterialTheme en Compose
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}