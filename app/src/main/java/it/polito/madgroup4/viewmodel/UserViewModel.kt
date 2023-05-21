package it.polito.madgroup4.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.madgroup4.model.Achievement
import it.polito.madgroup4.model.Repository
import it.polito.madgroup4.model.Sport
import it.polito.madgroup4.model.User
import it.polito.madgroup4.utility.formatDateToTimestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private val viewModelScope = CoroutineScope(Dispatchers.Main)/*    private var _user = MutableLiveData<User>().apply { value = null }
        val user: LiveData<User> = _user*/

    private var _user = MutableLiveData<User>().apply { value = null }
    val user: LiveData<User> = _user

    private val userListener: ListenerRegistration
    private val db = Firebase.firestore

    init {
        userListener =
            db.collection("users").document("48JnBn7vpjvj0minb62P")
                .addSnapshotListener { r, e ->
                    _user.value = if (e != null) throw e
                    else r?.toObject(User::class.java)
                }
    }

    override fun onCleared() {
        super.onCleared(); userListener.remove(); }


    private var storage = Firebase.storage("gs://madgroup4-5de93")
    var storageRef = storage.reference


    fun saveUser(editedUser: User, stateViewModel: LoadingStateViewModel, message: String, error: String) {
        stateViewModel.setStatus(Status.Loading)
        db.collection("users").document("48JnBn7vpjvj0minb62P").set(editedUser, SetOptions.merge())
            .addOnSuccessListener {
                Log.i("test", "User updated successfully")
                stateViewModel.setStatus(Status.Success(message, null))
            }.addOnFailureListener {
                Log.i("test", "$it")
                stateViewModel.setStatus(Status.Error(error, null))
            }
    }

    fun uploadImage(photoString: String): Uri {
        // Defining the child of storageReference
        val ref = storageRef.child(
            "images/" + "48JnBn7vpjvj0minb62P"
        )
        Log.i("test_vm", "after ref ${ref.toString()}")
        var downloadUri: Uri = Uri.EMPTY
        if (photoString != "") {
            // adding listeners on upload
            // or failure of image
            var uploadTask = ref.putFile(Uri.parse(photoString))
            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                Log.i("test_vm", "after ref ${ref.downloadUrl.toString()}")
                ref.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i("test_vm", "salvata immagine " + ref + " " + Uri.parse(photoString))
                    downloadUri = task.result
                } else {
                    Log.i("test_vm", "Errore")
                }
            }.addOnFailureListener { e ->
                Log.i("test", "Error adding document", e)
            }
        }
        Log.i("test_vm", "after ref ${ref.downloadUrl.toString()}")
        return downloadUri
    }

    fun removeAchievement(sportName: String, achievmentTitle: String, stateViewModel: LoadingStateViewModel) {
        var userTmp = _user.value!!
        userTmp.sports?.forEach { sport ->
            if (sport.name == sportName) {
                sport.achievements = sport.achievements.filter { achievement ->
                    achievement.title != achievmentTitle
                }
            }
        }
        saveUser(userTmp, stateViewModel, "Achievement removed successfully", "Error while removing achievement")
    }


    //TODO: metto l'id dell'utente loggato in preferences o lo hardcodato
    /*    fun getUser(id: String) {
            repository.getById(id).observeForever { user ->
                _user.value = user
            }
        }*/

    /*    fun saveUser(user: User) {
            viewModelScope.launch {
                repository.saveUser(user)
            }
        }*/
}