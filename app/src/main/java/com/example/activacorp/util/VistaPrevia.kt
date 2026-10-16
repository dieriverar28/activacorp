package com.example.activacorp.util

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

// Convierte la ruta guardada (la que devuelve guardarImagenInterna) en una imagen para mostrar.
// Reduce el tamaño al leerla para no gastar memoria con fotos muy grandes.
fun cargarImagenPrevia(rutaUri: String, anchoMaximo: Int = 1024): ImageBitmap? {
    return try {
        val ruta = Uri.parse(rutaUri).path ?: return null

        // Primero se lee solo el tamaño de la imagen
        val opcionesTamano = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(ruta, opcionesTamano)

        var reduccion = 1
        while (opcionesTamano.outWidth / reduccion > anchoMaximo) {
            reduccion *= 2
        }

        // Luego se carga reducida
        val opcionesCarga = BitmapFactory.Options().apply {
            inSampleSize = reduccion
        }
        BitmapFactory.decodeFile(ruta, opcionesCarga)?.asImageBitmap()
    } catch (e: Exception) {
        null
    }
}
