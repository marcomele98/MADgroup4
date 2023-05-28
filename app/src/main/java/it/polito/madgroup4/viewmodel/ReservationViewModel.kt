package it.polito.madgroup4.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Tasks.await
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
import java.lang.Thread.sleep
import java.util.Date

class ReservationViewModel : ViewModel() {

    private var _reservations =
        MutableLiveData<List<ReservationWithCourt>>().apply { value = null }
    val reservations: LiveData<List<ReservationWithCourt>> = _reservations

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

    private var courtWithSlotsListener: ListenerRegistration? = null

    init {
        db.collection("courts")
            .get()
            .addOnSuccessListener { documents ->
                _allCourts.value = documents.map { it.toObject(Court::class.java) }
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }
    }

    fun createReservationsListener(userId: String) {
        reservationListener = db.collection("reservations")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { r, e ->
                _allRes.value = if (e != null) throw e
                else r?.map {
                    val res = it.toObject(Reservation::class.java)
                    val court = _allCourts.value?.find { it.name == res.courtName }
                    ReservationWithCourt(res, court)
                }
            }

    }

    override fun onCleared() {
        super.onCleared();
        reservationListener?.remove();
        courtWithSlotsListener?.remove();
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
                var res = documents.map { it.toObject(Reservation::class.java) }.map { it.review }
                _reviews.value = res.filterNotNull()


            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }
    }


    fun getAllPlayingCourtsBySportAndDate(date: Date, sport: String) {
        db.collection("courts").whereEqualTo("sport", sport)
            .get()
            .addOnSuccessListener { documents ->
                courtWithSlotsListener = db.collection("reservations")
                    .whereEqualTo("date", formatDateToTimestamp(date))
                    .addSnapshotListener() { r, e ->
                        val reservations = r?.map { it.toObject(Reservation::class.java) }
                        val courts = documents.map { it.toObject(Court::class.java) }
                        _playingCourts.value = courts.map { c ->
                            val res = reservations?.filter { res -> res.courtName == c.name }
                            val slotsNotAvailable =
                                res?.filter { it.date.toDate() == date }?.map { it.slotNumber }
                            val totSlot =
                                slotsNotAvailable?.let {
                                    getAllSlots(
                                        it,
                                        c.openingTime!!,
                                        c.closingTime!!
                                    )
                                }
                            CourtWithSlots(c, totSlot)
                        }
                    }

            }
    }


    fun deleteReservation(
        reservation: Reservation,
        stateViewModel: LoadingStateViewModel,
        message: String,
        error: String,
        nextRoute: String ? = null
    ) {
        db.collection("reservations").document(reservation.id!!)
            .delete()
            .addOnSuccessListener {
                stateViewModel.setStatus(Status.Success(message, nextRoute))
            }.addOnFailureListener {
                stateViewModel.setStatus(Status.Error(error, null))
            }
    }

}