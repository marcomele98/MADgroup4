package it.polito.madgroup4.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.polito.madgroup4.Model.Reservation

class MainViewModel : ViewModel() {
  private val reservations = MutableLiveData<List<Reservation>>()
  private val newList = mutableListOf<Reservation>()

  fun addReservation(reservation: Reservation) {
    newList.add(reservation)
    reservations.value = newList
  }

  fun removeReservation(reservation: Reservation) {
    newList.remove(reservation)
    reservations.value = newList
  }

  fun getReservations() = reservations
}