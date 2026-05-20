package com.example.dsmevento.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

fun formatMillisToDateTime(millis: Long): String {
    return if (millis <= 0L) "-" else formatter.format(Date(millis))
}

fun parseDateTimeToMillis(text: String): Long? {
    return try {
        formatter.parse(text)?.time
    } catch (_: Exception) {
        null
    }
}