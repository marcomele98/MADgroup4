package it.polito.madgroup4.model

import androidx.room.Embedded
import androidx.room.Relation

data class ReservationWithCourt(
    @Embedded
    val reservation: Reservation?,

    @Relation(parentColumn = "court_id", entityColumn = "id")
    val playingCourt: PlayingCourt?
)