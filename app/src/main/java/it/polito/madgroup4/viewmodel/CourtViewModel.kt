package it.polito.madgroup4.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
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
class CourtViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private var _allCourts =
        MutableLiveData<List<PlayingCourt>>().apply { value = emptyList() }
    val allCourts: LiveData<List<PlayingCourt>> = _allCourts


    private val db = Firebase.firestore

    init {
        db.collection("courts")
            .get()
            .addOnSuccessListener { documents ->
                _allCourts.value = documents.map { it.toObject(PlayingCourt::class.java) }
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }
    }


}