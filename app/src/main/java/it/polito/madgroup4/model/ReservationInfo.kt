package it.polito.madgroup4.model

data class ReservationInfo(
    val totalNumber: Int? = null,
    var totalAvailable: Int? = null,
    var privateReservation: Boolean? = false,
    var confirmedUsers: MutableList<String> = mutableListOf(),
    var pendingUsers: MutableList<String> = mutableListOf()
)

