package it.polito.madgroup4.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playing_courts")
data class PlayingCourt(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    val price: Double,

    @ColumnInfo(name = "opening_time")
    val openingTime: String,

    @ColumnInfo(name = "closing_time")
    val closingTime: String,

    val sport: String,

    val address: String,

    val city: String,

    val province: String,

    val phone: String,

    val email: String,


    /*  var region: String,
        var country: String,
        var latitude: Double,
        var longitude: Double,
        var website: String,
        var description: String,
        var photo: String,
        var owner: String,*/
)

