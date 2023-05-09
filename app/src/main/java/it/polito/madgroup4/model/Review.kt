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

    @ColumnInfo(name = "service_rating")
    var serviceRating: Float,

    @ColumnInfo(name = "structure_rating")
    var structureRating: Float,

    @ColumnInfo(name = "cleaning_rating")
    var cleaningRating: Float,

    val date: Date,

    var text: String? = null,
)