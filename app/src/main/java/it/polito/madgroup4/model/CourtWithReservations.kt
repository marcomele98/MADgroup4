package it.polito.madgroup4.model

import androidx.room.Embedded
import androidx.room.Relation

data class CourtWithReservations(
    val court: Court,
    val reservations: List<Reservation>
)