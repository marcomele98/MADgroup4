package it.polito.madgroup4.model

import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    private val reservationDAO: ReservationDAO,
    private val playingCourtDAO: PlayingCourtDAO,
    private val userDAO: UserDAO,
    private val reviewDAO: ReviewDAO,
) {
    fun getAllReservations() = reservationDAO.getAll()
    fun getAllSlotsByCourtIdAndDate(courtId: Long, date: Date) =
        reservationDAO.getAllByCourtIdAndDate(courtId, date)

    /*fun getAllBySport(sport: String) =
        playingCourtDAO.getAllBySport(sport)*/

    fun getCourtsWithSlotsForSportAndDate(sport: String, date: Date) =
        playingCourtDAO.getCourtsWithSlotsForSportAndDate(sport, date)

    fun getAllReservationsByDate(date: Date) = reservationDAO.getAllByDate(date)
    suspend fun saveReservation(reservation: Reservation) = reservationDAO.save(reservation)
    suspend fun deleteReservation(reservation: Reservation) = reservationDAO.delete(reservation)
    /*fun getAllPlayingCourts() = playingCourtDAO.getAll()
    fun getPlayingCourtById(id: String) = playingCourtDAO.getById(id)*/
    fun getAllPlayingCourtsBySport(sport: String) = playingCourtDAO.getAllBySport(sport)
    suspend fun savePlayingCourt(playingCourt: PlayingCourt) = playingCourtDAO.save(playingCourt)

    fun getAllUsers() = userDAO.getAll()
    fun getById(id: Long) = userDAO.getById(id)
    suspend fun saveUser(user: User) = userDAO.save(user)
    suspend fun deleteUser(user: User) = userDAO.delete(user)

    fun getAllReviewsByCourtId(courtId: Long) = reviewDAO.getAllReviewsByCourtId(courtId)
    suspend fun saveReview(review: Review) = reviewDAO.save(review)
    suspend fun deleteReview(review: Review) = reviewDAO.delete(review)


    //suspend fun deletePlayingCourt(playingCourt: PlayingCourt) = playingCourtDAO.delete(playingCourt)

}