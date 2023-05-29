package it.polito.madgroup4.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.madgroup4.model.Court
import it.polito.madgroup4.model.CourtWithSlots
import it.polito.madgroup4.model.Reservation
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.model.Review
import it.polito.madgroup4.utility.formatDateToTimestamp
import it.polito.madgroup4.utility.getAllSlots
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
                if(Firebase.auth.currentUser != null)
                    createReservationsListener(Firebase.auth.currentUser!!.uid)
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }
    }

    fun createReservationsListener(userId: String) {
        reservationListener?.remove()
        reservationListener = db.collection("reservations")
            .whereEqualTo("userId", userId)
            //.whereArrayContains("reservationInfo.confirmedUsers", userId) //integrando le condivise si dovrà mettere questo filtro e non quello di prima
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
        saveReservationOnDB(id, reservation, stateViewModel, message, nextRoute, error)
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
        nextRoute: String? = null
    ) {
        db.collection("reservations").document(reservation.id!!)
            .delete()
            .addOnSuccessListener {
                stateViewModel.setStatus(Status.Success(message, nextRoute))
            }.addOnFailureListener {
                stateViewModel.setStatus(Status.Error(error, null))
            }
    }

    //TODO query che dovrebbe prendere tutte le prenotazioni della nuova sezione con gli inviti ricevuti
    fun getPendingReservationsBySportAndUser(userId: String, sport: String) {
        db.collection("reservations")
            .whereArrayContains("reservationInfo.pendingUsers", userId)
            .whereEqualTo("sport", sport)
            .get()
            .addOnSuccessListener { documents ->
               documents.map { it.toObject(Reservation::class.java) }

            }
    }

    //TODO query che dovrebbe prendere tutte le prenotazioni della nuova sezione 'Scopri' con le partite pubbliche a cui ci si può unire
    fun getAllPublicReservationsBySportAndDate(date: Date, sport: String) {
        db.collection("reservations")
            .whereEqualTo("sport", sport)
            .whereEqualTo("date", date)
            .whereEqualTo("reservationInfo.privateReservation", false)
            .whereGreaterThan("reservationInfo.totalNumber","reservationInfo.totalAvailable")
            .get()
            .addOnSuccessListener { documents ->
                documents.map { it.toObject(Reservation::class.java) }
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }
    }

    fun addInAPublicReservationAndSaveReservation(
        reservation: Reservation,
        stateViewModel: LoadingStateViewModel,
        message: String,
        error: String,
        nextRoute: String? = "Reservations"
    ) {
        var id = reservation.id!!
        var reservationInfo = reservation.reservationInfo
        if (true) { //Todo come quando salviamo una prenotazione e vediamo se quello slot è ancora libero, dovremmo
            //Todo vedere se c'è ancora posto prima di salvare. quindi magari bisognerebbe fare una query e vedere se il posto sia ancora libero
            reservationInfo!!.totalAvailable = (reservationInfo.totalAvailable ?: 0) + 1
            reservationInfo!!.confirmedUsers.add(id)
        } else {
            throw IllegalStateException("Reservation no longer available")
        }
        saveReservationOnDB(id, reservation, stateViewModel, message, nextRoute, error)
    }

    fun acceptAndSaveReservationInvitation(
        reservation: Reservation,
        stateViewModel: LoadingStateViewModel,
        message: String,
        error: String,
        nextRoute: String? = "Reservations"
    ) {
        var id = reservation.id!!
        var reservationInfo = reservation.reservationInfo
        if (reservationInfo!!.pendingUsers.contains(id)) {
            //rimuovi l'utente dalla lista degli inviti da accettare e settalo come utente confermato
            reservationInfo!!.pendingUsers.remove(id)
            reservationInfo!!.confirmedUsers.add(id)
        } else {
            throw IllegalStateException("User $id is not invited in this reservation")
        }
        saveReservationOnDB(id, reservation, stateViewModel, message, nextRoute, error)
    }

    fun rejectAndSaveReservationInvitation(
        reservation: Reservation,
        stateViewModel: LoadingStateViewModel,
        message: String,
        error: String,
        nextRoute: String? = "Reservations"
    ) {
        var id = reservation.id!!
        var reservationInfo = reservation.reservationInfo
        if (reservationInfo!!.pendingUsers.contains(id)) {
            //rimuovi l'utente dalla lista degli inviti da accettare
            reservationInfo!!.pendingUsers.remove(id)
            //poiché ha rifiutato, aumentiamo di 1 il numero di posti disponibili
            reservationInfo.totalAvailable = (reservationInfo.totalAvailable ?: 0) + 1

        } else {
            throw IllegalStateException("User $id is not invited in this reservation")
        }
        saveReservationOnDB(id, reservation, stateViewModel, message, nextRoute, error)
    }


    //prima di chiamare le funzioni faremo un check. se chi vuole cancellare la prenotazione è l'owner (il suo id è quello userId salvato nella prenotazione)
    //altrimenti uno semplicemente abbandona la partita, che sarà disponibile per altri, quindi si chiamerà questa funzione
    fun cancelPartecipationToReservation(
        reservation: Reservation,
        stateViewModel: LoadingStateViewModel,
        message: String,
        error: String,
        nextRoute: String? = "Reservations"
    ) {
        var id = reservation.id!!
        var reservationInfo = reservation.reservationInfo
        if (reservationInfo!!.confirmedUsers.contains(id)) {
            //rimuovi l'utente dalla lista degli utenti confermati
            reservationInfo.totalAvailable = (reservationInfo.totalAvailable ?: 0) + 1
            reservationInfo!!.confirmedUsers.remove(id)
        } else {
            throw IllegalStateException("User $id is not a partecipant of this reservation")
        }
        saveReservationOnDB(id, reservation, stateViewModel, message, nextRoute, error)
    }

    private fun saveReservationOnDB(
        id: String,
        reservation: Reservation,
        stateViewModel: LoadingStateViewModel,
        message: String,
        nextRoute: String?,
        error: String
    ) {
        db.collection("reservations").document(id)
            .set(reservation.copy(id = id), SetOptions.merge())
            .addOnSuccessListener {
                stateViewModel.setStatus(Status.Success(message, nextRoute))
            }.addOnFailureListener {
                stateViewModel.setStatus(Status.Error(error, null))
            }
    }

}