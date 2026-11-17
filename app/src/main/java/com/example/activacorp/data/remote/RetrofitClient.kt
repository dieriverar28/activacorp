package com.example.activacorp.data.remote

import android.os.Build
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Emulador: 10.0.2.2 es el "localhost" del PC.
    // Teléfono físico: localhost + `adb reverse tcp:8082 tcp:8082`
    // (reenvía el puerto del PC al teléfono por el cable USB).
    private val baseUrl: String
        get() = if (esEmulador()) {
            "http://10.0.2.2:8082/"
        } else {
            "http://localhost:8082/"
        }

    val api: ActivacorpApi by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ActivacorpApi::class.java)
    }

    private fun esEmulador(): Boolean {
        return Build.FINGERPRINT.startsWith("generic") ||
                Build.FINGERPRINT.contains("emulator") ||
                Build.MODEL.contains("Emulator") ||
                Build.MODEL.contains("Android SDK built for") ||
                Build.PRODUCT.contains("sdk")
    }
}
