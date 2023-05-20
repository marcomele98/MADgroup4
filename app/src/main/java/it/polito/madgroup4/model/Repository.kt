package it.polito.madgroup4.model

import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    private val reservationDAO: ReservationDAO,
    private val playingCourtDAO: PlayingCourtDAO,
    private val reviewDAO: ReviewDAO,
) {
    fun getAllReservationsByUserId(userId: String) = reservationDAO.getAllByUserId(userId)
    fun getAllSlotsByCourtIdAndDate(courtId: Long, date: Date, userId: String) =
        reservationDAO.getAllByCourtIdAndDateAndUserId(courtId, date, userId)

    /*fun getAllBySport(sport: String) =
        playingCourtDAO.getAllBySport(sport)*/

    fun getCourtsWithSlotsForSportAndDate(sport: String, date: Date) =
        playingCourtDAO.getCourtsWithSlotsForSportAndDate(sport, date)

    fun getAllReservationsByDate(date: Date, userId: String) = reservationDAO.getAllByDateAndUserId(date, userId)
    suspend fun saveReservation(reservation: Reservation) = reservationDAO.save(reservation)
    suspend fun deleteReservation(reservation: Reservation) = reservationDAO.delete(reservation)
    /*fun getAllPlayingCourts() = playingCourtDAO.getAll()
    fun getPlayingCourtById(id: String) = playingCourtDAO.getById(id)*/
    fun getAllPlayingCourtsBySport(sport: String) = playingCourtDAO.getAllBySport(sport)
    suspend fun savePlayingCourt(playingCourt: PlayingCourt) = playingCourtDAO.save(playingCourt)


    fun getAllReservationsByCourtIdAndDateAndUserId(courtId: Long, date: Date, userId: String) =
        reservationDAO.getAllByCourtIdAndDateAndUserId(courtId, date, userId)
    fun getAllReviewsByCourtId(courtId: Long) = reviewDAO.getAllReviewsByCourtId(courtId)
    suspend fun saveReview(review: Review) = reviewDAO.save(review)
    suspend fun deleteReview(review: Review) = reviewDAO.delete(review)

    fun getReviewById(id: Long) = reviewDAO.getById(id)

    fun getReviewByReservationId(reservationId: Long) = reviewDAO.getReviewByReservationId(reservationId)

    //suspend fun deletePlayingCourt(playingCourt: PlayingCourt) = playingCourtDAO.delete(playingCourt)

}