package com.example.activacorp.data

import com.example.activacorp.model.Contacto
import com.example.activacorp.model.EstadoOportunidad
import com.example.activacorp.model.Oportunidad
import com.example.activacorp.model.Organizacion
import com.example.activacorp.model.Prioridad
import com.example.activacorp.model.Rol
import com.example.activacorp.model.Seguimiento
import com.example.activacorp.model.Usuario
import com.example.activacorp.util.ahoraTexto
import com.example.activacorp.util.hoyTexto

// Carga datos ficticios solo la primera vez (cuando no hay usuarios)
suspend fun cargarDatosIniciales(database: AppDatabase) {
    val usuarioDao = database.usuarioDao()

    if (usuarioDao.contar() > 0) {
        return
    }

    val organizacionDao = database.organizacionDao()
    val contactoDao = database.contactoDao()
    val oportunidadDao = database.oportunidadDao()
    val seguimientoDao = database.seguimientoDao()

    // ---------- Usuarios (3 ejecutivos y 1 supervisor) ----------
    val camila = usuarioDao.insertar(
        Usuario(
            nombre = "Camila Rojas",
            correo = "camila.rojas@agencia.cl",
            clave = "ejecutivo123",
            rol = Rol.EJECUTIVO.name
        )
    ).toInt()
    val diego = usuarioDao.insertar(
        Usuario(
            nombre = "Diego Soto",
            correo = "diego.soto@agencia.cl",
            clave = "ejecutivo123",
            rol = Rol.EJECUTIVO.name
        )
    ).toInt()
    val valentina = usuarioDao.insertar(
        Usuario(
            nombre = "Valentina Pérez",
            correo = "valentina.perez@agencia.cl",
            clave = "ejecutivo123",
            rol = Rol.EJECUTIVO.name
        )
    ).toInt()
    val marcos = usuarioDao.insertar(
        Usuario(
            nombre = "Marcos Fuentes",
            correo = "marcos.fuentes@agencia.cl",
            clave = "supervisor123",
            rol = Rol.SUPERVISOR.name
        )
    ).toInt()

    // ---------- Organizaciones ----------
    val municipalidad = organizacionDao.insertar(
        Organizacion(nombre = "Municipalidad Valle Sur", tipo = "Municipalidad", comuna = "Puente Alto")
    ).toInt()
    val constructora = organizacionDao.insertar(
        Organizacion(nombre = "Constructora Andes Norte", tipo = "Empresa privada", comuna = "Providencia")
    ).toInt()
    val colegio = organizacionDao.insertar(
        Organizacion(nombre = "Colegio San Rafael", tipo = "Institución educativa", comuna = "Maipú")
    ).toInt()
    val retail = organizacionDao.insertar(
        Organizacion(nombre = "Retail Mercado Centro", tipo = "Empresa privada", comuna = "Santiago")
    ).toInt()

    // ---------- Contactos ----------
    val contactoMunicipalidad = contactoDao.insertar(
        Contacto(
            organizacionId = municipalidad,
            nombre = "Andrea Molina",
            cargo = "Encargada de eventos",
            correo = "andrea.molina@ejemplo.cl",
            telefono = "+56 9 5550 0001"
        )
    ).toInt()
    val contactoConstructora = contactoDao.insertar(
        Contacto(
            organizacionId = constructora,
            nombre = "Rodrigo Vera",
            cargo = "Jefe de bienestar",
            correo = "rodrigo.vera@ejemplo.cl",
            telefono = "+56 9 5550 0002"
        )
    ).toInt()
    val contactoColegio = contactoDao.insertar(
        Contacto(
            organizacionId = colegio,
            nombre = "Paula Herrera",
            cargo = "Coordinadora de actividades",
            correo = "paula.herrera@ejemplo.cl",
            telefono = "+56 9 5550 0003"
        )
    ).toInt()
    val contactoRetail = contactoDao.insertar(
        Contacto(
            organizacionId = retail,
            nombre = "Nicolás Araya",
            cargo = "Gerente de marketing",
            correo = "nicolas.araya@ejemplo.cl",
            telefono = "+56 9 5550 0004"
        )
    ).toInt()

    // ---------- Oportunidades de prueba ----------
    val oportunidades = listOf(
        Oportunidad(
            nombre = "Feria de beneficios municipal",
            tipoEvento = "Feria municipal",
            fechaEvento = "2026-11-14",
            ubicacion = "Plaza de Armas",
            comuna = "Puente Alto",
            publicoEsperado = 800,
            prioridad = Prioridad.ALTA.name,
            estado = EstadoOportunidad.CONTACTO_PENDIENTE.name,
            organizacionId = municipalidad,
            contactoId = contactoMunicipalidad,
            responsableId = camila,
            creadoPorId = camila,
            fechaCreacion = hoyTexto()
        ),
        Oportunidad(
            nombre = "Jornada de bienestar corporativo",
            tipoEvento = "Jornada de bienestar",
            fechaEvento = "2026-11-20",
            ubicacion = "Casa matriz",
            comuna = "Providencia",
            publicoEsperado = 250,
            prioridad = Prioridad.MEDIA.name,
            estado = EstadoOportunidad.PROPUESTA_ENVIADA.name,
            organizacionId = constructora,
            contactoId = contactoConstructora,
            responsableId = camila,
            creadoPorId = camila,
            fechaCreacion = hoyTexto()
        ),
        Oportunidad(
            nombre = "Feria empresarial regional",
            tipoEvento = "Feria empresarial",
            fechaEvento = "2026-12-02",
            ubicacion = "Centro de eventos",
            comuna = "Santiago",
            publicoEsperado = 1500,
            prioridad = Prioridad.ALTA.name,
            estado = EstadoOportunidad.EN_ESPERA_DE_RESPUESTA.name,
            organizacionId = retail,
            contactoId = contactoRetail,
            responsableId = diego,
            creadoPorId = diego,
            fechaCreacion = hoyTexto()
        ),
        Oportunidad(
            nombre = "Semana de beneficios del colegio",
            tipoEvento = "Actividad institucional",
            fechaEvento = "2026-11-28",
            ubicacion = "Patio central",
            comuna = "Maipú",
            publicoEsperado = 400,
            prioridad = Prioridad.BAJA.name,
            estado = EstadoOportunidad.EVENTO_IDENTIFICADO.name,
            organizacionId = colegio,
            contactoId = contactoColegio,
            responsableId = valentina,
            creadoPorId = valentina,
            fechaCreacion = hoyTexto()
        ),
        Oportunidad(
            nombre = "Aniversario comunal",
            tipoEvento = "Actividad municipal",
            fechaEvento = "2026-12-10",
            ubicacion = "Estadio municipal",
            comuna = "Puente Alto",
            publicoEsperado = 3000,
            prioridad = Prioridad.ALTA.name,
            estado = EstadoOportunidad.EVENTO_IDENTIFICADO.name,
            organizacionId = municipalidad,
            contactoId = null,
            // Sin responsable: sirve para probar la asignación del supervisor
            responsableId = null,
            creadoPorId = marcos,
            fechaCreacion = hoyTexto()
        )
    )

    oportunidades.forEach { oportunidad ->
        val id = oportunidadDao.insertar(oportunidad).toInt()
        seguimientoDao.insertar(
            Seguimiento(
                oportunidadId = id,
                usuarioId = oportunidad.creadoPorId,
                fecha = ahoraTexto(),
                accion = "REGISTRO",
                descripcion = "Oportunidad registrada (dato de prueba)",
                estadoAnterior = null,
                estadoNuevo = oportunidad.estado
            )
        )
    }
}
