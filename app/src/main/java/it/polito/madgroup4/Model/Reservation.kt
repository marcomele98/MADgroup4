package it.polito.madgroup4.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date


@Entity(
    tableName = "reservations",
    /*foreignKeys = [
        ForeignKey(
            entity = PlayingCourt::class,
            parentColumns = ["id"],
            childColumns = ["court_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]*/
)

data class Reservation(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "court_id")
    val courtId: String,

    @ColumnInfo(name = "slot_number")
    val slotNumber: Int,

    val date: Date
)
