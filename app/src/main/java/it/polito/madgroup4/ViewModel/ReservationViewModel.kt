package it.polito.madgroup4.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.madgroup4.Model.PlayingCourt
import it.polito.madgroup4.Model.Repository
import it.polito.madgroup4.Model.Reservation
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ReservationViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private val _selectedDate = MutableLiveData<Date>().apply { value = Date() }
    var selectedDate: LiveData<Date> = _selectedDate

    private var _reservations = MutableLiveData<List<Reservation>>().apply { value = emptyList() }
    var reservations: LiveData<List<Reservation>> = _reservations

    fun setSelectedDate(date: Date) {
        _selectedDate.value = (date)
        println(_selectedDate.value)
    }

    fun getReservationsByDate(date: Date) {
        repository.getAllReservationsByDate(date).observeForever { reservations ->
            _reservations.value = reservations
        }
    }

    fun saveReservation(reservation: Reservation) = repository.saveReservation(reservation)

}