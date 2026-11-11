package com.example.activacorp.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.activacorp.ui.theme.StateError
import com.example.activacorp.ui.theme.StateSuccess

/**
 * ANIMACIÓN 1 (Mecanismo 1: Transición de visibilidad con AnimatedVisibility)
 * Este componente muestra u oculta mensajes de alerta de manera suave usando desvanecimiento (fadeIn / fadeOut).
 * Se utiliza para confirmar operaciones exitosas o mostrar errores de validación.
 */
@Composable
fun MensajeAlertaAnimado(
    visible: Boolean,       // Controla si el mensaje debe mostrarse u ocultarse
    mensaje: String,        // Texto explicativo del mensaje
    esError: Boolean = false // Define si se usará el color de error o el de éxito
) {
    // Transición animada de visibilidad requerida por la pauta
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),  // Efecto de entrada suave
        exit = fadeOut()   // Efecto de salida suave
    ) {
        // Asignación de color semántico según el tipo de mensaje
        val colorFondo = if (esError) StateError else StateSuccess

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .background(color = colorFondo, shape = RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Text(
                text = mensaje,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * ANIMACIONES 2 Y 3 (Mecanismo 2: Animar estado de color con animateColorAsState)
 * Transiciona el color de fondo de la etiqueta de un estado cuando cambia su valor.
 * Permite dar retroalimentación visual inmediata al usuario sobre el estado de la oportunidad.
 */
@Composable
fun IndicadorEstadoAnimado(
    estadoConfirmado: Boolean, // Estado para determinar si la oportunidad está activa/confirmada
    etiqueta: String           // Texto que se muestra dentro de la etiqueta
) {
    // Mecanismo 2: Anima la transición del color de fondo entre Éxito y Error
    val colorAnimado by animateColorAsState(
        targetValue = if (estadoConfirmado) StateSuccess else StateError,
        label = "TransicionColorEstado"
    )

    Box(
        modifier = Modifier
            .background(color = colorAnimado, shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = etiqueta,
            color = Color.White,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

/**
 * ANIMACIÓN 4 (Mecanismo 3: Expansión de contenido con AnimatedVisibility + expandVertically)
 * Muestra u oculta secciones secundarias (como detalles adicionales o formularios desplegables)
 * con una animación combinada de expansión vertical y desvanecimiento.
 */
@Composable
fun ContenidoDesplegableAnimado(
    visible: Boolean,                  // Controla si el contenido desplegable se muestra
    content: @Composable () -> Unit    // Contenido visual que se expandirá o contraerá
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(),  // Expansión suave hacia abajo
        exit = fadeOut() + shrinkVertically()   // Contracción suave hacia arriba
    ) {
        content()
    }
}

/**
 * ANIMACIÓN 5 (Mecanismo 4: Opacidad animada con animateFloatAsState)
 * Anima la transparencia/opacidad de un elemento cuando se habilita o inhabilita,
 * proporcionando una transición suave en lugar de un cambio brusco.
 */
@Composable
fun OpacidadEstadoAnimada(
    activo: Boolean,                   // Define si el elemento está activo (opacidad 1.0) o inactivo (opacidad 0.4)
    content: @Composable () -> Unit    // Contenido sobre el cual se aplicará el efecto de opacidad
) {
    // Anima la opacidad (alpha) entre 1.0f (activo) y 0.4f (inactivo)
    val alphaAnimado by animateFloatAsState(
        targetValue = if (activo) 1.0f else 0.4f,
        label = "TransicionOpacidad"
    )

    Box(
        modifier = Modifier.alpha(alphaAnimado)
    ) {
        content()
    }
}

/**
 * ANIMACIÓN 6 (Mecanismo 5: Desplazamiento/Padding animado con animateDpAsState)
 * Transiciona suavemente el margen/espaciado de un elemento al ser seleccionado o activado.
 */
@Composable
fun TarjetaSeleccionadaAnimada(
    seleccionada: Boolean,              // Define si la tarjeta está seleccionada
    content: @Composable () -> Unit     // Contenido de la tarjeta
) {
    // Anima el padding interno entre 8.dp (normal) y 16.dp (destacado/seleccionado)
    val paddingAnimado by animateDpAsState(
        targetValue = if (seleccionada) 16.dp else 8.dp,
        label = "TransicionPaddingTarjeta"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingAnimado)
    ) {
        content()
    }
}
