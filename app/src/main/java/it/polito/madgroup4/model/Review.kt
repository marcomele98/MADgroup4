package it.polito.madgroup4.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "reviews",
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
        ),
        ForeignKey(
            entity = Reservation::class,
            parentColumns = ["id"],
            childColumns = ["reservation_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class Review(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "court_id")
    val courtId: Long,

    @ColumnInfo(name = "user_id")
    val userId: Long,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "service_rating")
    var serviceRating: Float? = null,

    @ColumnInfo(name = "structure_rating")
    var structureRating: Float? = null,

    @ColumnInfo(name = "cleaning_rating")
    var cleaningRating: Float? = null,

    @ColumnInfo(name = "average_rating")
    var averageRating: Float? = null,

    @ColumnInfo(name = "score")
    var score: Float = 0f,

    @ColumnInfo(name = "reservation_id")
    var reservationId: Long? = null,

    val date: Date,

    var text: String? = null,
)