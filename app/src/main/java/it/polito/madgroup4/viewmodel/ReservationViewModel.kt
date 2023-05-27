package it.polito.madgroup4.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
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

    private val viewModelScope = CoroutineScope(Dispatchers.Main)
    private var _reservations =
        MutableLiveData<List<ReservationWithCourt>>().apply { value = null }
    val reservations: LiveData<List<ReservationWithCourt>> = _reservations

    private var _slots = MutableLiveData<List<Int>>().apply { value = emptyList() }
    val slots: LiveData<List<Int>> = _slots

    private var _playingCourts =
        MutableLiveData<List<CourtWithSlots>>().apply { value = emptyList() }
    val playingCourts: LiveData<List<CourtWithSlots>> = _playingCourts

    private var _allRes = MutableLiveData<List<Reservation>>().apply { value = emptyList() }
    val allRes: LiveData<List<Reservation>> = _allRes




    fun getReservationsByDate(date: Date, userId: String) {
        repository.getAllReservationsByDate(date, userId).observeForever { reservations ->
            _reservations.value = reservations
        }
    }

    fun getSlotsByCourtIdAndDate(courtId: Long, date: Date, userId: String) {
        repository.getAllSlotsByCourtIdAndDate(courtId, date, userId).observeForever { slots ->
            _slots.value = slots
        }
    }

    fun getAllPlayingCourtsBySportAndDate(date: Date, sport: String) {
        repository.getCourtsWithSlotsForSportAndDate(sport, date)
            .observeForever { playingCourts ->
                _playingCourts.value = playingCourts
            }

    }


    //TODO: metto l'id dell'utente loggato in preferences o lo hardcodato
    fun getAllReservations(userId: String) {
        repository.getAllReservationsByUserId(userId).observeForever { allRes ->
            _allRes.value = allRes
        }
    }



    fun saveReservation(reservation: Reservation, stateViewModel: LoadingStateViewModel, message: String, error: String) {
        viewModelScope.launch {
            try {
                repository.saveReservation(reservation)
                stateViewModel.setStatus(Status.Success(message, null))
            } catch (e: Exception) {
                stateViewModel.setStatus(Status.Error(error, null))
            }
        }
    }

    fun saveReservationUtility(reservation: Reservation){

        viewModelScope.launch {
            repository.saveReservation(reservation)
        }
    }

    fun deleteReservation(reservation: Reservation, stateViewModel: LoadingStateViewModel, message: String, error: String) {
        viewModelScope.launch {
            try {
                repository.deleteReservation(reservation)
                stateViewModel.setStatus(Status.Success(message, null))
            } catch (e: Exception) {
                stateViewModel.setStatus(Status.Error(error, null))
            }
        }
    }

    fun savePlayingCourt(playingCourt: PlayingCourt) = viewModelScope.launch {
        repository.savePlayingCourt(playingCourt)
    }

}