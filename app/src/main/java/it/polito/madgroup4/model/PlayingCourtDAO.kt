package it.polito.madgroup4.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.room.*
import java.util.*

@Dao
interface PlayingCourtDAO {
    @Query("SELECT * FROM playing_courts")
    fun getAll(): LiveData<List<PlayingCourt>>

    @Query("SELECT * FROM playing_courts WHERE id = :id")
    fun getById(id: String): LiveData<PlayingCourt>

    @Query("SELECT * FROM playing_courts WHERE sport = :sport")
    fun getAllBySport(sport: String): LiveData<List<PlayingCourt>>


    @Transaction
    @Query(
        """
        SELECT playing_courts.*, 
               (SELECT GROUP_CONCAT(slot_number, ',') 
                FROM reservations 
                WHERE court_id = playing_courts.id 
                  AND date = :date 
                GROUP BY court_id) as reserved_slots
        FROM playing_courts
        WHERE sport = :sport
        """
    )
    fun getCourtsWithSlotsForSportAndDate(sport: String, date: Date): LiveData<List<CourtWithReservations>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(playingCourt: PlayingCourt)

    @Delete
    fun delete(playingCourt: PlayingCourt)
}