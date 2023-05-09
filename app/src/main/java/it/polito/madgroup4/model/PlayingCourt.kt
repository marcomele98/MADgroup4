package it.polito.madgroup4.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playing_courts")
data class PlayingCourt(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String? = null,

    val price: Double? = null,

    @ColumnInfo(name = "opening_time")
    val openingTime: String? = null,

    @ColumnInfo(name = "closing_time")
    val closingTime: String? = null,

    val sport: String? = null,

    val address: String? = null,

    val city: String? = null,

    val province: String? = null,

    val phone: String? = null,

    val email: String? = null,


    /*  var region: String,
        var country: String,
        var latitude: Double,
        var longitude: Double,
        var website: String,
        var description: String,
        var photo: String,
        var owner: String,*/
)

