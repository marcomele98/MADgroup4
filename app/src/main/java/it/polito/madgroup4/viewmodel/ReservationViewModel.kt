package it.polito.madgroup4.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.madgroup4.model.PlayingCourt
import it.polito.madgroup4.model.Repository
import it.polito.madgroup4.model.Reservation
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.utility.CourtWithSlots
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ReservationViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    val viewModelScope = CoroutineScope(Dispatchers.Main)
    private var _reservations =
        MutableLiveData<List<ReservationWithCourt>>().apply { value = emptyList() }
    var reservations: LiveData<List<ReservationWithCourt>> = _reservations

    private var _slots = MutableLiveData<List<Int>>().apply { value = emptyList() }
    var slots: LiveData<List<Int>> = _slots

    private var _playingCourts =
        MutableLiveData<List<CourtWithSlots>>().apply { value = emptyList() }
    var playingCourts: LiveData<List<CourtWithSlots>> = _playingCourts

    private var _allRes = MutableLiveData<List<Reservation>>().apply { value = emptyList() }
    var allRes: LiveData<List<Reservation>> = _allRes

    private var _allCourtsBySport = MutableLiveData<List<PlayingCourt>>().apply { value = emptyList() }
    var allCourtsBySport: LiveData<List<PlayingCourt>> = _allCourtsBySport

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

    fun getAllPlayingCourtsBySportAndDate(date: Date, sport: String) {
        repository.getCourtsWithSlotsForSportAndDate(sport, date)
            .observeForever { playingCourts ->
                _playingCourts.value = playingCourts
            }

    }

    fun getAllPlayingCourtBySport(sport: String) {
        repository.getAllPlayingCourtsBySport(sport).observeForever { allCourtsBySport ->
            _allCourtsBySport.value = allCourtsBySport
        }
    }


    fun getAllReservations() {
        repository.getAllReservations().observeForever { allRes ->
            _allRes.value = allRes
        }
    }

    fun saveReservation(reservation: Reservation) = viewModelScope.launch {
        repository.saveReservation(reservation)
    }

    fun deleteReservation(reservation: Reservation) = viewModelScope.launch {
        repository.deleteReservation(reservation)
    }

    fun savePlayingCourt(playingCourt: PlayingCourt) = viewModelScope.launch {
        repository.savePlayingCourt(playingCourt)
    }

}