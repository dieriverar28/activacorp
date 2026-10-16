package com.example.activacorp.util

import com.example.activacorp.model.EstadoOportunidad
import com.example.activacorp.model.Rol
import com.example.activacorp.model.Usuario
import com.example.activacorp.model.obtenerRol

fun esEstadoFinal(estado: EstadoOportunidad): Boolean {
    return estado == EstadoOportunidad.PARTICIPACION_CONFIRMADA ||
            estado == EstadoOportunidad.OPORTUNIDAD_DESCARTADA
}

// El supervisor accede a todo; el ejecutivo solo a las oportunidades que tiene asignadas
fun puedeGestionar(usuario: Usuario, responsableId: Int?): Boolean {
    return usuario.obtenerRol() == Rol.SUPERVISOR || responsableId == usuario.id
}

// Estados a los que puede pasar una oportunidad según el rol (transiciones permitidas)
fun estadosSiguientes(estadoActual: EstadoOportunidad, rol: Rol): List<EstadoOportunidad> {
    if (esEstadoFinal(estadoActual)) {
        return emptyList()
    }

    return when (rol) {
        // El ejecutivo avanza paso a paso, pero NO puede confirmar ni descartar
        Rol.EJECUTIVO -> when (estadoActual) {
            EstadoOportunidad.EVENTO_IDENTIFICADO -> listOf(EstadoOportunidad.CONTACTO_PENDIENTE)
            EstadoOportunidad.CONTACTO_PENDIENTE -> listOf(EstadoOportunidad.CONTACTO_REALIZADO)
            EstadoOportunidad.CONTACTO_REALIZADO -> listOf(EstadoOportunidad.PROPUESTA_ENVIADA)
            EstadoOportunidad.PROPUESTA_ENVIADA -> listOf(EstadoOportunidad.EN_ESPERA_DE_RESPUESTA)
            else -> emptyList()
        }

        // El supervisor decide: confirma (solo si está en espera de respuesta) o descarta
        Rol.SUPERVISOR -> {
            val decisiones = mutableListOf<EstadoOportunidad>()
            if (estadoActual == EstadoOportunidad.EN_ESPERA_DE_RESPUESTA) {
                decisiones.add(EstadoOportunidad.PARTICIPACION_CONFIRMADA)
            }
            decisiones.add(EstadoOportunidad.OPORTUNIDAD_DESCARTADA)
            decisiones
        }
    }
}
