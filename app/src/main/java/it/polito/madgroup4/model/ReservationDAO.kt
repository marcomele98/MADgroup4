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

    @Query("SELECT * FROM reservations WHERE user_id = :userId")
    fun getAllByUserId(userId: Long) : LiveData<List<Reservation>>

    @Transaction
    @Query("SELECT * FROM reservations WHERE date = :date and user_id = :userId")
    fun getAllByDateAndUserId(date: Date, userId: Long): LiveData<List<ReservationWithCourt>>

    //TODO: AGGIUNGO ANCHE L'UTENTE COME FILTRO
    @Transaction
    @Query("SELECT * FROM reservations WHERE date < :date OR (date = :date AND slot_number < :slotNumber) AND user_id = :userId")
    fun getAllBeforeMomentAndUserId(date: Date, slotNumber: Int, userId: Long): LiveData<List<ReservationWithCourt>>

    @Query("SELECT slot_number FROM reservations WHERE court_id = :courtId AND date = :date AND user_id = :userId")
    fun getAllByCourtIdAndDateAndUserId(courtId: Long, date: Date, userId: Long) : LiveData<List<Int>>

    @Insert(onConflict = REPLACE)
    suspend fun save(reservation: Reservation)

    @Delete
    suspend fun delete(reservation: Reservation)

}