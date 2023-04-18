package it.polito.madgroup4.Model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [PlayingCourt::class, Reservation::class], version = 1)
@TypeConverters(Converters::class)
abstract class CourtsDatabase : RoomDatabase() {
    abstract fun playingCourtDAO(): PlayingCourtDAO
    abstract fun reservationDAO(): ReservationDAO

    companion object {
        @Volatile
        private var INSTANCE: CourtsDatabase? = null
        fun getDatabase(context: Context): CourtsDatabase =
            (INSTANCE ?: synchronized(this) {
                val i = INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext, CourtsDatabase::class.java, "courts_database"
                ).build()
                INSTANCE = i
                INSTANCE
            })!!
    }

}
