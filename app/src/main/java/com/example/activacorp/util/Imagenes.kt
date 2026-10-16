package com.example.activacorp.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.util.UUID

fun guardarImagenInterna(context: Context, uriOrigen: Uri): String? {
    return try {
        val carpeta = File(context.filesDir, "imagenes").apply { mkdirs() }
        val destino = File(carpeta, "${UUID.randomUUID()}.jpg")

        context.contentResolver.openInputStream(uriOrigen)?.use { entrada ->
            destino.outputStream().use { salida ->
                entrada.copyTo(salida)
            }
        } ?: return null

        Uri.fromFile(destino).toString()
    } catch (e: Exception) {
        null
    }
}