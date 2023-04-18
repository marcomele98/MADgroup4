package it.polito.madgroup4.Model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [PlayingCourt::class, Reservation::class], version = 1)
@TypeConverters(Converters::class)
abstract class LocalDatabaseImpl():
    RoomDatabase(), LocalDatabase {
    abstract override fun playingCourtDAO(): PlayingCourtDAO
    abstract override fun reservationDAO(): ReservationDAO

    //E' necessario il companion object per poter accedere al database anche se posso usare @Singleton e @Inject? Chiedere!
    //https://stackoverflow.com/questions/58225000/how-to-use-room-database-with-dagger-hilt
    /*companion object {
        @Volatile
        private var INSTANCE: LocalDatabaseImpl? = null
        fun getDatabase(context: Context): LocalDatabaseImpl  =
            (INSTANCE ?: synchronized(this) {
                val i = INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext, LocalDatabaseImpl::class.java, "local_database"
                ).build()
                INSTANCE = i
                INSTANCE
            })!!
    }*/
    }
