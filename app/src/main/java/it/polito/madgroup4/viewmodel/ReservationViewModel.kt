package it.polito.madgroup4.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.madgroup4.model.Court
import it.polito.madgroup4.model.Reservation
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.model.CourtWithSlots
import it.polito.madgroup4.model.Review
import it.polito.madgroup4.utility.formatDateToTimestamp
import it.polito.madgroup4.utility.getAllSlots
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.Date

class ReservationViewModel : ViewModel() {

    private val viewModelScope = CoroutineScope(Dispatchers.Main)
    private var _reservations =
        MutableLiveData<List<ReservationWithCourt>>().apply { value = null }
    val reservations: LiveData<List<ReservationWithCourt>> = _reservations

    private var _slots = MutableLiveData<List<Int>>().apply { value = emptyList() }
    val slots: LiveData<List<Int>> = _slots

    private var _playingCourts =
        MutableLiveData<List<CourtWithSlots>>().apply { value = emptyList() }
    val playingCourts: LiveData<List<CourtWithSlots>> = _playingCourts

    private var _allRes =
        MutableLiveData<List<ReservationWithCourt>>().apply { value = emptyList() }
    val allRes: LiveData<List<ReservationWithCourt>> = _allRes

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private var reservationListener: ListenerRegistration? = null

    private var _allCourts =
        MutableLiveData<List<Court>>().apply { value = emptyList() }
    val allCourts: LiveData<List<Court>> = _allCourts

    private var _reviews =
        MutableLiveData<List<Review>>().apply { value = emptyList() }
    val reviews: LiveData<List<Review>> = _reviews

    init {
        db.collection("courts")
            .get()
            .addOnSuccessListener { documents ->
                _allCourts.value = documents.map { it.toObject(Court::class.java) }
                reservationListener =
                    db.collection("reservations")
                        .whereEqualTo("userId", auth.currentUser?.uid)
                        .addSnapshotListener { r, e ->
                            _allRes.value = if (e != null) throw e
                            else r?.map {
                                val res = it.toObject(Reservation::class.java)
                                val court = _allCourts.value?.find { it.name == res.courtName }
                                ReservationWithCourt(res, court)
                            }
                        }
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }
    }

    fun saveReservation(
        reservation: Reservation,
        stateViewModel: LoadingStateViewModel,
        message: String,
        error: String,
        nextRoute: String? = "Reservations"
    ) {
        var id = ""
        if (reservation.id != null) {
            id = reservation.id
        } else {
            id = db.collection("reservations").document().id
        }
        db.collection("reservations").document(id)
            .set(reservation.copy(id = id), SetOptions.merge())
            .addOnSuccessListener {
                stateViewModel.setStatus(Status.Success(message, nextRoute))
            }.addOnFailureListener {
                stateViewModel.setStatus(Status.Error(error, null))
            }
    }

    fun getAllReviewsByCourtName(name: String) {
        db.collection("reservations")
            .whereEqualTo("courtName", name)
            .get()
            .addOnSuccessListener { documents ->
                var res = documents.map { it.toObject(Reservation::class.java)}.map { it.review }
                if(res.isNotEmpty()) {
                    _reviews.value = res.filterNotNull()
                }

            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }
    }


    /*
      fun getReservationsByDate(date: Date, userId: String) {
          repository.getAllReservationsByDate(date, userId).observeForever { reservations ->
              _reservations.value = reservations
          }
      }

      fun getSlotsByCourtIdAndDate(courtId: Long, date: Date, userId: String) {
          repository.getAllSlotsByCourtIdAndDate(courtId, date, userId).observeForever { slots ->
              _slots.value = slots
          }
      } */

    fun getAllPlayingCourtsBySportAndDate(date: Date, sport: String) {
        db.collection("reservations")
            .whereEqualTo("date", formatDateToTimestamp(date))
            .get()
            .addOnSuccessListener { documents ->
                val reservations = documents.map { it.toObject(Reservation::class.java) }
                db.collection("courts")
                    .whereEqualTo("sport", sport)
                    .get()
                    .addOnSuccessListener { documents ->
                        val courts = documents.map { it.toObject(Court::class.java) }
                        _playingCourts.value = courts.map { c ->
                            val res = reservations.filter { res -> res.courtName == c.name }
                            val slotsNotAvailable =
                                res.filter { it.date.toDate() == date }.map { it.slotNumber }
                            val totSlot =
                                getAllSlots(slotsNotAvailable, c.openingTime!!, c.closingTime!!)
                            CourtWithSlots(c, totSlot)
                        }
                    }
                    .addOnFailureListener { exception ->
                        println("Error getting documents: $exception")
                    }
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }
    }


//TODO: metto l'id dell'utente loggato in preferences o lo hardcodato
    /* fun getAllReservations(userId: String) {
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
    }*/

    /*    fun saveReservationUtility(reservation: Reservation){

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
      }*/

    /*    fun savePlayingCourt(playingCourt: PlayingCourt) = viewModelScope.launch {
          repository.savePlayingCourt(playingCourt)
      }*/

}