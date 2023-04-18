package it.polito.madgroup4.Model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PlayingCourtDAO {
    @Query("SELECT * FROM playing_courts")
    fun getAll(): LiveData<List<PlayingCourt>>

    @Query("SELECT * FROM playing_courts WHERE id = :id")
    fun getById(id: String): LiveData<PlayingCourt>

    @Query("SELECT * FROM playing_courts WHERE sport = :sport")
    fun getAllBySport(sport: String): LiveData<List<PlayingCourt>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(playingCourt: PlayingCourt)

    @Delete
    fun delete(playingCourt: PlayingCourt)
}