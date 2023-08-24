package com.example.simplequiz.firestore

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Quizdata (
    var question: String = "",
    var answer: String = ""
) {
    override fun toString(): String {
        return "'$question' ist $answer"
    }
}

data class Highscoredata (
    var userName: String = "",
    var userScore: Int = 0,
    @ServerTimestamp
    var created_at: Timestamp? = null
)  {
    override fun toString(): String {
        var datestring = "01.01.2000 00:00"
        created_at?.let {
            datestring = convertTimestampToDateTimeString(it)
        }
        return "$userScore by $userName at $datestring"
    }
}