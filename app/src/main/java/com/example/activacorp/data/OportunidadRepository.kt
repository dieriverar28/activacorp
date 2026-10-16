package com.example.activacorp.data

import com.example.activacorp.model.EstadoOportunidad
import com.example.activacorp.model.Oportunidad
import com.example.activacorp.model.OportunidadConDatos
import com.example.activacorp.model.Rol
import com.example.activacorp.model.Seguimiento
import com.example.activacorp.model.SeguimientoDetalle
import com.example.activacorp.model.Usuario
import com.example.activacorp.model.obtenerRol
import com.example.activacorp.util.ahoraTexto
import com.example.activacorp.util.esEstadoFinal
import com.example.activacorp.util.estadosSiguientes
import com.example.activacorp.util.puedeGestionar
import kotlinx.coroutines.flow.Flow

class OportunidadRepository(
    private val oportunidadDao: OportunidadDao,
    private val seguimientoDao: SeguimientoDao
) {

    // El supervisor ve todas; el ejecutivo solo las que tiene asignadas
    fun observarParaUsuario(usuario: Usuario): Flow<List<OportunidadConDatos>> {
        return if (usuario.obtenerRol() == Rol.SUPERVISOR) {
            oportunidadDao.observarTodas()
        } else {
            oportunidadDao.observarPorResponsable(usuario.id)
        }
    }

    fun observarDetalle(id: Int): Flow<OportunidadConDatos?> {
        return oportunidadDao.observarPorId(id)
    }

    fun observarHistorial(id: Int): Flow<List<SeguimientoDetalle>> {
        return seguimientoDao.observarPorOportunidad(id)
    }

    // Solo los ejecutivos registran oportunidades
    suspend fun crear(oportunidad: Oportunidad, usuario: Usuario): Boolean {
        if (usuario.obtenerRol() != Rol.EJECUTIVO) {
            return false
        }

        val id = oportunidadDao.insertar(oportunidad).toInt()
        registrarSeguimiento(
            oportunidadId = id,
            usuarioId = usuario.id,
            accion = "REGISTRO",
            descripcion = "Oportunidad registrada",
            estadoAnterior = null,
            estadoNuevo = oportunidad.estado
        )
        return true
    }

    // Valida permisos y transición antes de cambiar el estado
    suspend fun cambiarEstado(
        oportunidadId: Int,
        nuevoEstado: EstadoOportunidad,
        usuario: Usuario
    ): Boolean {
        val oportunidad = oportunidadDao.obtenerPorId(oportunidadId) ?: return false

        if (!puedeGestionar(usuario, oportunidad.responsableId)) {
            return false
        }

        val estadoActual = EstadoOportunidad.valueOf(oportunidad.estado)
        val permitidos = estadosSiguientes(estadoActual, usuario.obtenerRol())

        if (nuevoEstado !in permitidos) {
            return false
        }

        oportunidadDao.actualizarEstado(oportunidadId, nuevoEstado.name)
        registrarSeguimiento(
            oportunidadId = oportunidadId,
            usuarioId = usuario.id,
            accion = "CAMBIO_ESTADO",
            descripcion = "Estado: ${estadoActual.etiqueta} → ${nuevoEstado.etiqueta}",
            estadoAnterior = estadoActual.name,
            estadoNuevo = nuevoEstado.name
        )
        return true
    }

    // Solo el supervisor asigna o reasigna, y no sobre oportunidades ya cerradas
    suspend fun asignar(
        oportunidadId: Int,
        ejecutivo: Usuario,
        usuario: Usuario
    ): Boolean {
        if (usuario.obtenerRol() != Rol.SUPERVISOR) {
            return false
        }

        val oportunidad = oportunidadDao.obtenerPorId(oportunidadId) ?: return false

        if (esEstadoFinal(EstadoOportunidad.valueOf(oportunidad.estado))) {
            return false
        }

        oportunidadDao.actualizarResponsable(oportunidadId, ejecutivo.id)
        registrarSeguimiento(
            oportunidadId = oportunidadId,
            usuarioId = usuario.id,
            accion = "ASIGNACION",
            descripcion = "Asignada a ${ejecutivo.nombre}",
            estadoAnterior = null,
            estadoNuevo = null
        )
        return true
    }

    private suspend fun registrarSeguimiento(
        oportunidadId: Int,
        usuarioId: Int,
        accion: String,
        descripcion: String,
        estadoAnterior: String?,
        estadoNuevo: String?
    ) {
        seguimientoDao.insertar(
            Seguimiento(
                oportunidadId = oportunidadId,
                usuarioId = usuarioId,
                fecha = ahoraTexto(),
                accion = accion,
                descripcion = descripcion,
                estadoAnterior = estadoAnterior,
                estadoNuevo = estadoNuevo
            )
        )
    }
}
