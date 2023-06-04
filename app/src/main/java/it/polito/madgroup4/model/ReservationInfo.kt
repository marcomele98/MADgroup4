package it.polito.madgroup4.model

data class ReservationInfo(
    val totalNumber: Int? = null,
    var totalAvailable: Int? = null,
    var confirmedUsers: MutableList<String> = mutableListOf(),
    var pendingUsers: MutableList<String> = mutableListOf(),
    var status: String? = null,
    var public: Boolean? = false,
    var suggestedLevel: String? = null,
)

