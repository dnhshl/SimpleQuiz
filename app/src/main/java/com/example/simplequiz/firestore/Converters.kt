package com.example.simplequiz.firestore

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

val dateTimeFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

fun convertDateStringToTimestamp(dateTimeString: String): Timestamp {
    val dateTime = dateTimeFormat.parse(dateTimeString)
    return dateTime?.let { Timestamp(it) } ?: Timestamp.now()
}

fun convertTimestampToDateTimeString(timestamp: Timestamp): String {
    return dateTimeFormat.format(timestamp.toDate())
}
