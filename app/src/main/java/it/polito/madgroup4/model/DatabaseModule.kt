package it.polito.madgroup4.model

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Singleton
    @Provides
    fun provideLocalDatabase(@ApplicationContext context: Context): LocalDatabase {
        return Room.databaseBuilder(
            context.applicationContext, LocalDatabaseImpl::class.java, "local_database"
        ).build()
    }

    @Singleton
    @Provides
    fun providePlayingCourtDAO(localDatabase: LocalDatabase): PlayingCourtDAO {
        return localDatabase.playingCourtDAO()
    }

    @Singleton
    @Provides
    fun provideReservationDAO(localDatabase: LocalDatabase): ReservationDAO {
        return localDatabase.reservationDAO()
    }
}