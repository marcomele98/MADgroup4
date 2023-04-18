package it.polito.madgroup4.Model

interface LocalDatabase {
    fun playingCourtDAO(): PlayingCourtDAO
    fun reservationDAO(): ReservationDAO
}