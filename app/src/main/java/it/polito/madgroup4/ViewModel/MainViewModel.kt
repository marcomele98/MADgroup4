package it.polito.madgroup4.ViewModel

import androidx.lifecycle.ViewModel
import it.polito.madgroup4.Model.PlayingCourt
import it.polito.madgroup4.Model.Repository
import it.polito.madgroup4.Model.Reservation
import java.util.Date

class MainViewModel(private val repository: Repository) : ViewModel() {
  fun getReservations() = repository.getAllReservations()

  fun getReservationsByCourtId(courtId: String) = repository.getAllReservationsByCourtId(courtId)

  fun getReservationsByDate(date: Date) = repository.getAllReservationsByDate(date)

  fun saveReservation(reservation: Reservation) = repository.saveReservation(reservation)

  fun deleteReservation(reservation: Reservation) = repository.deleteReservation(reservation)

  fun getPlayingCourts() = repository.getAllPlayingCourts()

  fun getPlayingCourtById(id: String) = repository.getPlayingCourtById(id)

  fun getPlayingCourtsBySport(sport: String) = repository.getAllPlayingCourtsBySport(sport)

  fun savePlayingCourt(playingCourt: PlayingCourt) = repository.savePlayingCourt(playingCourt)

  fun deletePlayingCourt(playingCourt: PlayingCourt) = repository.deletePlayingCourt(playingCourt)

}