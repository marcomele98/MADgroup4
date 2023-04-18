package it.polito.madgroup4.Model

import java.util.*
import javax.inject.Inject

class Repository @Inject constructor(private val reservationRepository: ReservationRepository, private val playingCourtRepository: PlayingCourtRepository) {
    fun getAllReservations() = reservationRepository.getAll()
    fun getAllReservationsByCourtId(courtId: String) = reservationRepository.getAllByCourtId(courtId)
    fun getAllReservationsByDate(date: Date) = reservationRepository.getAllByDate(date)
    fun saveReservation(reservation: Reservation) = reservationRepository.save(reservation)
    fun deleteReservation(reservation: Reservation) = reservationRepository.delete(reservation)
    fun getAllPlayingCourts() = playingCourtRepository.getAll()
    fun getPlayingCourtById(id: String) = playingCourtRepository.getById(id)
    fun getAllPlayingCourtsBySport(sport: String) = playingCourtRepository.getAllBySport(sport)
    fun savePlayingCourt(playingCourt: PlayingCourt) = playingCourtRepository.save(playingCourt)
    fun deletePlayingCourt(playingCourt: PlayingCourt) = playingCourtRepository.delete(playingCourt)

}