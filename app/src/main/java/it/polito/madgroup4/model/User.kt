package it.polito.madgroup4.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "users")

data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val surname: String,
    val nickname: String,
    val email: String,
    val phone: String,
    val gender: String,
    val birthday: Date,
    val photo: String? = null,
)