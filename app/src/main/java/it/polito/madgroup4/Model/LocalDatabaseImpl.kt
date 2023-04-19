package it.polito.madgroup4.Model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [PlayingCourt::class, Reservation::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class LocalDatabaseImpl():
    RoomDatabase(), LocalDatabase {
    abstract override fun playingCourtDAO(): PlayingCourtDAO
    abstract override fun reservationDAO(): ReservationDAO

    }
