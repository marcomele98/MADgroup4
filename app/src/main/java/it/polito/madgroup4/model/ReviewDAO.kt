package it.polito.madgroup4.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query

@Dao
interface ReviewDAO {
    @Query("SELECT * FROM reviews WHERE court_id = :courtId")
    fun getAllReviewsByCourtId(courtId: Long) : LiveData<List<Review>>
    @Insert(onConflict = REPLACE)
    suspend fun save(review: Review)
    @Delete
    suspend fun delete(review: Review)
}