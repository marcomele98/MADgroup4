package it.polito.madgroup4.Model

import java.util.*
import javax.inject.Inject

abstract class ReservationRepository @Inject constructor(private val reservationDao: ReservationDAO) {
    fun getAll() = reservationDao.getAll()
    fun getAllByCourtId(courtId: String) = reservationDao.getAllByCourtId(courtId)
    fun getAllByDate(date: Date) = reservationDao.getAllByDate(date)
    fun save(reservation: Reservation) = reservationDao.save(reservation)
    fun delete(reservation: Reservation) = reservationDao.delete(reservation)

}