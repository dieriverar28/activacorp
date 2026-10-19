package com.example.activacorp.util

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

/**
 * HELPER DE CÁMARA: CamaraHelper
 * Permite gestionar la captura de fotografías integrándose con el contrato de ActivityResult Launcher de Compose.
 * Utiliza FileProvider para crear URIs seguras y compatibles con Android 7.0+.
 */
class CamaraHelper(
    private val context: Context
) {
    /**
     * Genera un archivo temporal dentro del directorio cache de la aplicación
     * y retorna su URI segura mediante FileProvider.
     */
    fun crearUriFotoTemporal(): Uri {
        val archivoTemporal = File.createTempFile(
            "foto_evidencia_${System.currentTimeMillis()}",
            ".jpg",
            context.cacheDir
        ).apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider", // Debe coincidir con el authorities registrado en AndroidManifest.xml
            archivoTemporal
        )
    }
}

/**
 * COMPOSABLE HELPER: rememberCamaraLauncher
 * Hook personalizado que encapsula el launcher de cámara para Jetpack Compose.
 */
@Composable
fun rememberCamaraLauncher(
    onFotoCapturada: (Boolean, Uri?) -> Unit
): Pair<Uri?, () -> Unit> {
    val context = LocalContext.current
    val camaraHelper = remember { CamaraHelper(context) }
    var tempUri = remember { mutableOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito ->
        onFotoCapturada(exito, tempUri.value)
    }

    val tomarFoto: () -> Unit = {
        val uri = camaraHelper.crearUriFotoTemporal()
        tempUri.value = uri
        launcher.launch(uri)
    }

    return Pair(tempUri.value, tomarFoto)
}

// Extensión auxiliar de estado
private fun <T> mutableOf(value: T) = androidx.compose.runtime.mutableStateOf(value)