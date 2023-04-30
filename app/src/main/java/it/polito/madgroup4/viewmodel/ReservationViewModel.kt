package it.polito.madgroup4.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.madgroup4.model.PlayingCourt
import it.polito.madgroup4.model.Repository
import it.polito.madgroup4.model.Reservation
import it.polito.madgroup4.model.ReservationWithCourt
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ReservationViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private var _reservations =
        MutableLiveData<List<ReservationWithCourt>>().apply { value = emptyList() }
    var reservations: LiveData<List<ReservationWithCourt>> = _reservations

    private var _slots = MutableLiveData<List<Int>>().apply { value = emptyList() }
    var slots: LiveData<List<Int>> = _slots

    private var _playingCourts = MutableLiveData<List<PlayingCourt>>().apply { value = emptyList() }
    var playingCourts: LiveData<List<PlayingCourt>> = _playingCourts


    fun getReservationsByDate(date: Date) {
        repository.getAllReservationsByDate(date).observeForever { reservations ->
            _reservations.value = reservations
        }
    }

    fun getSlotsByCourtIdAndDate(courtId: Long, date: Date) {
        repository.getAllSlotsByCourtIdAndDate(courtId, date).observeForever { slots ->
            _slots.value = slots
        }
    }


    fun getAllPlayingCourtsBySport(date: Date, sport: String) {
        repository.getAllBySport(sport).observeForever { playingCourts ->
            _playingCourts.value = playingCourts
        }
    }

    fun saveReservation(reservation: Reservation) = repository.saveReservation(reservation)

    fun deleteReservation(reservation: Reservation) = repository.deleteReservation(reservation)

    fun savePlayingCourt(playingCourt: PlayingCourt) = repository.savePlayingCourt(playingCourt)

}