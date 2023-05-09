package it.polito.madgroup4.model

interface LocalDatabase {
    fun playingCourtDAO(): PlayingCourtDAO
    fun reservationDAO(): ReservationDAO

    fun userDAO(): UserDAO

    fun reviewDAO(): ReviewDAO
}