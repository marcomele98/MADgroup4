package it.polito.madgroup4.Model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PlayingCourtDAO {
    @Query("SELECT * FROM playing_courts")
    fun getAllPlayingCourts(): LiveData<List<PlayingCourt>>

    @Query("SELECT * FROM playing_courts WHERE id = :id")
    fun getPlayingCourtById(id: String): LiveData<PlayingCourt>

    @Query("SELECT * FROM playing_courts WHERE sport = :sport")
    fun getAllPlayingCourtsBySport(sport: String): LiveData<List<PlayingCourt>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(playingCourt: PlayingCourt)

    @Delete
    fun delete(playingCourt: PlayingCourt)
}