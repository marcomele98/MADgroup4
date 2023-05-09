package it.polito.madgroup4.model

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [PlayingCourt::class, Reservation::class, User::class, Review::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class LocalDatabaseImpl():
    RoomDatabase(), LocalDatabase {
    abstract override fun playingCourtDAO(): PlayingCourtDAO
    abstract override fun reservationDAO(): ReservationDAO

    abstract override fun userDAO(): UserDAO

    abstract override fun reviewDAO(): ReviewDAO

    }
