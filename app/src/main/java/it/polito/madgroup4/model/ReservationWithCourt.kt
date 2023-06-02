package it.polito.madgroup4.model

import androidx.room.Embedded
import androidx.room.Relation

data class ReservationWithCourt(
    val reservation: Reservation? = null,
    val playingCourt: Court? = null
)