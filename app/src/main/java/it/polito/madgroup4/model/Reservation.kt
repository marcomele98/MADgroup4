package it.polito.madgroup4.model

import com.google.firebase.Timestamp


data class Reservation (

    val id: String? = null,

    val courtName: String = "",

    val userId: String = "",

    var slotNumber: Int = 0,

    val date: Timestamp = Timestamp.now(),

    var particularRequests: String? = null,

    var review: Review? = null
)