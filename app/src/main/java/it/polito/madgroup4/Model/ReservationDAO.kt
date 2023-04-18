package it.polito.madgroup4.Model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import java.util.*

@Dao
interface ReservationDAO {

    @Query("SELECT * FROM reservations")
    fun getAll() : LiveData<List<Reservation>>

    @Query("SELECT * FROM reservations WHERE court_id = :courtId")
    fun getAllByCourtId(courtId: String) : LiveData<List<Reservation>>

    @Query("SELECT * FROM reservations WHERE date = :date")
    fun getAllByDate(date: Date) : LiveData<List<Reservation>>

    @Insert(onConflict = REPLACE)
    fun save(reservation: Reservation)

    @Delete
    fun delete(reservation: Reservation)

}