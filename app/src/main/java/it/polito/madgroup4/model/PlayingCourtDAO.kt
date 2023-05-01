package it.polito.madgroup4.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import it.polito.madgroup4.utility.CourtWithSlots
import it.polito.madgroup4.utility.Slot
import it.polito.madgroup4.utility.getAllSlots
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
        val courtsWithSlotsLiveData = MutableLiveData<List<CourtWithSlots>>()
        var courtsWithReservations : List<CourtWithReservations> = getCourtsWithReservations(sport, date);
        var courtWithSlots = mutableListOf<CourtWithSlots>();
        for (courtWithReservations in courtsWithReservations) {
            val court = courtWithReservations.court
            val reservations = courtWithReservations.reservations
            var slotsNotAvailable: List<Int> = reservations.map { it.slotNumber }
            val totSlot : List<Slot> = getAllSlots(slotsNotAvailable, court.openingTime, court.closingTime);
            courtWithSlots.add(CourtWithSlots(court, totSlot))
        }

        courtsWithSlotsLiveData.value = courtWithSlots
        return courtsWithSlotsLiveData
    }


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(playingCourt: PlayingCourt)

    @Delete
    fun delete(playingCourt: PlayingCourt)
}