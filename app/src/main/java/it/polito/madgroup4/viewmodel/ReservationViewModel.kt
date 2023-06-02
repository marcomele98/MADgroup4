package it.polito.madgroup4.viewmodel

import android.os.AsyncTask
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
import it.polito.madgroup4.utility.calculateStartEndTime
import it.polito.madgroup4.utility.formatDate
import it.polito.madgroup4.utility.formatDateToTimestamp
import it.polito.madgroup4.utility.getAllSlots
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalTime
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
    private var pendingReservationListener: ListenerRegistration? = null

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
                if (Firebase.auth.currentUser != null)
                    createReservationsListener(Firebase.auth.currentUser!!.uid)
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }
    }

    fun createReservationsListener(userId: String) {
        reservationListener?.remove()
        pendingReservationListener?.remove()
        val confirmedUsersQuery = db.collection("reservations")
            .whereArrayContains("reservationInfo.confirmedUsers", userId)
        val pendingUsersQuery = db.collection("reservations")
            .whereArrayContains("reservationInfo.pendingUsers", userId)

        fun isInThePast(it: ReservationWithCourt): Boolean {
            return it.reservation!!.date.toDate() < formatDate(Date())
                    || (formatDate(Date()) == formatDate(it.reservation.date.toDate())
                    && LocalTime.parse(
                calculateStartEndTime(
                    it.playingCourt?.openingTime!!,
                    it.reservation.slotNumber
                ).split("-")[0].trim()
            ).isBefore(
                LocalTime.now()
            ))
        }

        reservationListener = confirmedUsersQuery
            .addSnapshotListener { r, e ->
                pendingUsersQuery.get().addOnSuccessListener {
                    db.collection("courts")
                        .get()
                        .addOnSuccessListener { documents ->
                            val courts = documents.map { it.toObject(Court::class.java) }
                            val confirmed = if (e != null) throw e
                            else r?.map {
                                val res = it.toObject(Reservation::class.java)
                                val court = courts.find { it.name == res.courtName }
                                val resCourt = ReservationWithCourt(res, court)
                                resCourt.reservation!!.reservationInfo?.status =
                                    if (isInThePast(resCourt) && (userId == resCourt.reservation.userId) && (resCourt.reservation.review == null)) {
                                        "Reviewable"
                                    } else {
                                        "Confirmed"
                                    }
                                resCourt
                            }
                            val pending = it.map {
                                val res = it.toObject(Reservation::class.java)
                                res.reservationInfo?.status = "Invited"
                                val court = courts.find { it.name == res.courtName }
                                ReservationWithCourt(res, court)
                            }.filter {
                                !isInThePast(it)
                            }

                            if (confirmed != null) {
                                _allRes.value =
                                    (confirmed + pending).sortedBy { it.reservation?.date }
                            }

                            println(allRes.value)
                        }
                }
            }

        pendingReservationListener = pendingUsersQuery
            .addSnapshotListener { r, e ->
                confirmedUsersQuery.get().addOnSuccessListener {
                    db.collection("courts")
                        .get()
                        .addOnSuccessListener { documents ->
                            val courts = documents.map { it.toObject(Court::class.java) }
                            val pending = if (e != null) throw e
                            else r?.map {
                                val res = it.toObject(Reservation::class.java)
                                res.reservationInfo?.status = "Invited"
                                val court = courts.find { it.name == res.courtName }
                                ReservationWithCourt(res, court)
                            }?.filter {
                                !isInThePast(it)
                            }
                            val confirmed = it.map {
                                val res = it.toObject(Reservation::class.java)
                                res.reservationInfo?.status = "Confirmed"
                                val court = courts.find { it.name == res.courtName }
                                val resCourt = ReservationWithCourt(res, court)
                                resCourt.reservation!!.reservationInfo?.status =
                                    if (isInThePast(resCourt) && userId == resCourt.reservation?.userId && (resCourt.reservation.review == null)) {
                                        "Reviewable"
                                    } else {
                                        "Confirmed"
                                    }
                                resCourt
                            }

                            if (pending != null) {
                                _allRes.value =
                                    (confirmed + pending).sortedBy { it.reservation?.date }
                            }
                            println(allRes.value)
                        }
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
            .whereGreaterThan("reservationInfo.totalNumber", "reservationInfo.totalAvailable")
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
        userId: String,
        stateViewModel: LoadingStateViewModel,
        message: String,
        error: String,
        nextRoute: String? = "Reservations"
    ) {
        var id = reservation.id!!
        var reservationInfo = reservation.reservationInfo
        if (reservationInfo!!.pendingUsers.contains(userId)) {
            //rimuovi l'utente dalla lista degli inviti da accettare e settalo come utente confermato
            reservationInfo!!.pendingUsers.remove(userId)
            reservationInfo!!.confirmedUsers.add(userId)
        } else {
            throw IllegalStateException("User $userId is not invited in this reservation")
        }
        saveReservationOnDB(id, reservation, stateViewModel, message, nextRoute, error)
    }

    fun rejectAndSaveReservationInvitation(
        reservation: Reservation,
        userId: String,
        stateViewModel: LoadingStateViewModel,
        message: String,
        error: String,
        nextRoute: String? = "Reservations"
    ) {
        var id = reservation.id!!
        var reservationInfo = reservation.reservationInfo
        if (reservationInfo!!.pendingUsers.contains(userId)) {
            //rimuovi l'utente dalla lista degli inviti da accettare
            reservationInfo!!.pendingUsers.remove(userId)
            //poiché ha rifiutato, aumentiamo di 1 il numero di posti disponibili
            reservationInfo.totalAvailable = (reservationInfo.totalAvailable ?: 0) + 1

        } else {
            throw IllegalStateException("User $userId is not invited in this reservation")
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
                reservation.reservationInfo?.pendingUsers?.forEach {
                    inviaNotifica(it, "You've been invited to play!", id)
                }
                stateViewModel.setStatus(Status.Success(message, nextRoute))
            }.addOnFailureListener {
                stateViewModel.setStatus(Status.Error(error, null))
            }
    }

    private fun inviaNotifica(id: String, message: String, reservationId: String) {
        val db = Firebase.firestore
        val usersCollection = db.collection("users2")

        usersCollection.document(id).get()
            .addOnSuccessListener { documentSnapshot ->
                val token = documentSnapshot.getString("token")
                if (token != null) {
                    val notification_title = "CUS Torino"
                    val notification_des = message

                    FCMMessages().sendMessageSingle(
                        token,
                        notification_title,
                        notification_des,
                        mapOf("screen" to "Reservation Details", "reservationId" to reservationId)
                    )


                }

            }
    }
}


class FCMMessages {


    fun sendMessageSingle(
        recipient: String,
        title: String,
        body: String,
        dataMap: Map<String, String>?
    ) {

        val notificationMap = HashMap<String, Any>()
        notificationMap["body"] = body
        notificationMap["title"] = title

        val rootMap = HashMap<String, Any>()
        rootMap["notification"] = notificationMap
        rootMap["to"] = recipient
        dataMap?.let { rootMap["data"] = it }

        SendFCM().setFcm(rootMap).execute()
    }

    fun sendMessageMulti(
        recipients: JSONArray,
        title: String,
        body: String,
        dataMap: Map<String, String>?
    ) {

        val notificationMap = HashMap<String, Any>()
        notificationMap["body"] = body
        notificationMap["title"] = title

        val rootMap = HashMap<String, Any>()
        rootMap["notification"] = notificationMap
        rootMap["registration_ids"] = recipients
        dataMap?.let { rootMap["data"] = it }

        SendFCM().setFcm(rootMap).execute()
    }

    private inner class SendFCM : AsyncTask<String, String, String>() {

        private val FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send"
        private lateinit var fcm: Map<String, Any>

        fun setFcm(fcm: Map<String, Any>): SendFCM {
            this.fcm = fcm
            return this
        }

        override fun doInBackground(vararg strings: String): String? {
            return try {
                val JSON: MediaType? = "application/json; charset=utf-8".toMediaTypeOrNull()
                val body: RequestBody = RequestBody.create(JSON, JSONObject(fcm).toString())
                val request: Request = Request.Builder()
                    .url(FCM_MESSAGE_URL)
                    .post(body)
                    .addHeader(
                        "Authorization",
                        "key=" + "AAAAHNkiKAA:APA91bH3NrTbbrcq06JEuMAScb73370vmAqWvf8i-b7LLLtBpIrv3y_JQ82NAjykYGTfF71bmOJh7ra9-NUX7HjoKHLz2OGCc_qmRb0bbzwqSj6OfYu2vPyJqT2LTo9HRBIGPJusEGkT"
                    )
                    .build()
                val response: Response = OkHttpClient().newCall(request).execute()
                response.body?.string()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        override fun onPostExecute(result: String?) {
            try {
                val resultJson = JSONObject(result)
                val success: Int = resultJson.getInt("success")
                val failure: Int = resultJson.getInt("failure")
                //Toast.makeText(context, "Sent: " + success + "/" + (success + failure), Toast.LENGTH_LONG).show();
            } catch (e: JSONException) {
                e.printStackTrace()
                //Toast.makeText(context, "Message Failed, Unknown error occurred.", Toast.LENGTH_LONG).show();
            }
        }
    }

}