package it.polito.madgroup4.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "users")

data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String? = null,
    val surname: String? = null,
    val nickname: String? = null,
    val email: String? = null,
    val photo: String? = null,
)