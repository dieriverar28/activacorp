package com.example.activacorp.ui.theme

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
 * Paleta Verde Menta + Naranja Coral para modo oscuro.
 */
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryEmeraldDark,
    onPrimary = OnPrimaryEmeraldDark,
    primaryContainer = PrimaryContainerEmeraldDark,
    onPrimaryContainer = OnPrimaryContainerEmeraldDark,

    secondary = SecondaryCoralDark,
    onSecondary = OnSecondaryCoralDark,
    secondaryContainer = SecondaryContainerCoralDark,
    onSecondaryContainer = OnSecondaryContainerCoralDark,

    tertiary = TertiaryPurpleDark,
    onTertiary = OnTertiaryPurpleDark,
    tertiaryContainer = TertiaryContainerPurpleDark,
    onTertiaryContainer = OnTertiaryContainerPurpleDark,

    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    error = StateError
)

/**
 * ESQUEMA DE COLORES: Modo Claro (LightColorScheme)
 * Paleta corporativa ActivaCorp (Verde Esmeralda + Naranja Coral + Violeta).
 */
private val LightColorScheme = lightColorScheme(
    primary = PrimaryEmerald,
    onPrimary = OnPrimaryEmerald,
    primaryContainer = PrimaryContainerEmerald,
    onPrimaryContainer = OnPrimaryContainerEmerald,

    secondary = SecondaryCoral,
    onSecondary = OnSecondaryCoral,
    secondaryContainer = SecondaryContainerCoral,
    onSecondaryContainer = OnSecondaryContainerCoral,

    tertiary = TertiaryPurple,
    onTertiary = OnTertiaryPurple,
    tertiaryContainer = TertiaryContainerPurple,
    onTertiaryContainer = OnTertiaryContainerPurple,

    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    error = StateError
)

/**
 * TEMA DE LA APLICACIÓN: ActivacorpTheme (Material Design 3)
 * Aplica la identidad de marca personalizada de ActivaCorp (o colores dinámicos si se especifica).
 */
@Composable
fun ActivacorpTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Detecta el modo oscuro del sistema
    dynamicColor: Boolean = false,             // Deshabilitado para forzar el tema verde esmeralda + coral de la marca
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
