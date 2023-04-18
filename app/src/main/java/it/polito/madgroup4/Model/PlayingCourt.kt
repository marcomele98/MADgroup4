package it.polito.madgroup4.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playing_courts")
data class PlayingCourt(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,

    val price: Double,

    @ColumnInfo(name = "opening_time")
    val openingTime: String,

    @ColumnInfo(name = "closing_time")
    val closingTime: String,

    val sport: String,

/*    var address: String,
    var city: String,
    var province: String,
    var region: String,
    var country: String,
    var latitude: Double,
    var longitude: Double,
    var phone: String,
    var email: String,
    var website: String,
    var description: String,
    var photo: String,
    var owner: String,*/
)
