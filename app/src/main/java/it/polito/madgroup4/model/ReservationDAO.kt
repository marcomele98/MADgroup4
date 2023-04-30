package it.polito.madgroup4.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import java.util.*

@Dao
interface ReservationDAO {

    @Query("SELECT * FROM reservations")
    fun getAll() : LiveData<List<Reservation>>

    @Transaction
    @Query("SELECT * FROM reservations WHERE date = :date")
    fun getAllByDate(date: Date): LiveData<List<ReservationWithCourt>>

    @Transaction
    @Query("SELECT * FROM reservations, playing_courts WHERE date = :date and sport = :sport")
    fun getAllByDateAndSport(date: Date, sport: String): LiveData<List<ReservationWithCourt>>

    @Query("SELECT slot_number FROM reservations WHERE court_id = :courtId AND date = :date")
    fun getAllByCourtIdAndDate(courtId: Long, date: Date) : LiveData<List<Int>>


    @Insert(onConflict = REPLACE)
    fun save(reservation: Reservation)

    @Delete
    fun delete(reservation: Reservation)

}