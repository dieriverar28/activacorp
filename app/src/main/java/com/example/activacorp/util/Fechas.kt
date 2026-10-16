package com.example.activacorp.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Fecha de hoy con formato AAAA-MM-DD
fun hoyTexto(): String {
    return LocalDate.now().toString()
}

// Fecha y hora actual con formato AAAA-MM-DD HH:mm
fun ahoraTexto(): String {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
}
