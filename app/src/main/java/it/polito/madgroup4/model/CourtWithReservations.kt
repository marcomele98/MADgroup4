package it.polito.madgroup4.model

import androidx.room.Embedded
import androidx.room.Relation

data class CourtWithReservations(
    @Embedded val court: PlayingCourt,
    @Relation(
        parentColumn = "id",
        entityColumn = "court_id"
    )
    val reservations: List<Reservation>
)