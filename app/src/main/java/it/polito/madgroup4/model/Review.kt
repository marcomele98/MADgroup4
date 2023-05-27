package it.polito.madgroup4.model

import com.google.firebase.Timestamp

data class Review(
    val courtName: String = "",
    var userId: String = "",
    var title: String = "",
    var serviceRating: Float? = null,
    var structureRating: Float? = null,
    var cleaningRating: Float? = null,
    var score: Float = 0f,
    val date: Timestamp = Timestamp.now(),
    var text: String? = null
)