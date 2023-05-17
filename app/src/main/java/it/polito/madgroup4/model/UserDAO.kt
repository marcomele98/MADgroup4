package it.polito.madgroup4.model

import androidx.room.Dao
import androidx.room.Query
import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE

@Dao
interface UserDAO {

    @Query("SELECT * FROM users")
    fun getAll() : LiveData<List<User>>

    @Query("SELECT * FROM users WHERE id = :id")
    fun getById(id: Long) : LiveData<User>

    @Query("SELECT * FROM users WHERE email = :email")
    fun getByEmail(email: String) : LiveData<User>

    @Insert(onConflict = REPLACE)
    suspend fun save(user: User)

    @Delete
    suspend fun delete(user: User)

}