package com.example.dsmevento.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm"

private fun createDateTimeFormatter(): SimpleDateFormat {
    return SimpleDateFormat(DATE_TIME_PATTERN, Locale.US)
}

fun formatMillisToDateTime(millis: Long): String {
    return if (millis <= 0L) "-" else createDateTimeFormatter().format(Date(millis))
}

fun formatMillisToInputDateTime(millis: Long): String {
    return if (millis <= 0L) "" else createDateTimeFormatter().format(Date(millis))
}

fun parseDateTimeToMillis(text: String): Long? {
    return try {
        createDateTimeFormatter().parse(text)?.time
    } catch (_: Exception) {
        null
    }
}