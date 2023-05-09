package it.polito.madgroup4.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.room.*
import it.polito.madgroup4.utility.CourtWithSlots
import it.polito.madgroup4.utility.Slot
import it.polito.madgroup4.utility.getAllSlots
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import java.util.*

@Dao
interface PlayingCourtDAO {
    @Query("SELECT * FROM playing_courts")
    fun getAll(): LiveData<List<PlayingCourt>>

    @Query("SELECT * FROM playing_courts WHERE id = :id")
    fun getById(id: String): LiveData<PlayingCourt>

    @Query("SELECT * FROM playing_courts WHERE sport = :sport")
    fun getAllBySport(sport: String): LiveData<List<PlayingCourt>>


    //TODO questi slot number etc non vengono davvero restituiti. Si può cambiare un attimo la query poi
    //TODO ritorno la valutazione media'
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
    fun getCourtsWithReservations(sport: String, date: Date): List<CourtWithReservations>


    fun getCourtsWithSlotsForSportAndDate(sport: String, date: Date): LiveData<List<CourtWithSlots>> {
        return liveData {
            val courtsWithSlots = mutableListOf<CourtWithSlots>()
            val courtsWithReservations =
                withContext(Dispatchers.IO) { getCourtsWithReservations(sport, date) }
            for (courtWithReservations in courtsWithReservations) {
                val court = courtWithReservations.court
                val reservations = courtWithReservations.reservations
                val slotsNotAvailable =
                    reservations.filter { it.date == date }.map { it.slotNumber }
                val totSlot = getAllSlots(slotsNotAvailable, court.openingTime!!, court.closingTime!!)
                courtsWithSlots.add(CourtWithSlots(court, totSlot))
            }
            emit(courtsWithSlots)
        }
    }


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(playingCourt: PlayingCourt)

    @Delete
    fun delete(playingCourt: PlayingCourt)
}