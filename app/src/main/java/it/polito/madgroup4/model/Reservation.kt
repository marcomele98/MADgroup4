package it.polito.madgroup4.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "reservations",
    foreignKeys = [
        ForeignKey(
            entity = PlayingCourt::class,
            parentColumns = ["id"],
            childColumns = ["court_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class Reservation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "court_id")
    val courtId: Long,

    @ColumnInfo(name = "user_id")
    val userId: Long,

    @ColumnInfo(name = "slot_number")
    var slotNumber: Int,

    val date: Date,

    var particularRequests: String? = null,

)
