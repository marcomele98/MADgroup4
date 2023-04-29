package it.polito.madgroup4.model

import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    private val reservationDAO: ReservationDAO,
    private val playingCourtDAO: PlayingCourtDAO
) {
    fun getAllReservations() = reservationDAO.getAll()
    fun getAllSlotsByCourtIdAndDate(courtId: Long, date: Date) =
        reservationDAO.getAllByCourtIdAndDate(courtId, date)

    fun getAllReservationsByDate(date: Date) = reservationDAO.getAllByDate(date)
    fun saveReservation(reservation: Reservation) = reservationDAO.save(reservation)
    fun deleteReservation(reservation: Reservation) = reservationDAO.delete(reservation)
    fun getAllPlayingCourts() = playingCourtDAO.getAll()
    fun getPlayingCourtById(id: String) = playingCourtDAO.getById(id)
    fun getAllPlayingCourtsBySport(sport: String) = playingCourtDAO.getAllBySport(sport)
    fun savePlayingCourt(playingCourt: PlayingCourt) = playingCourtDAO.save(playingCourt)
    fun deletePlayingCourt(playingCourt: PlayingCourt) = playingCourtDAO.delete(playingCourt)

}