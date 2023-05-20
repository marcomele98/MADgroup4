package it.polito.madgroup4.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    private val viewModelScope = CoroutineScope(Dispatchers.Main)
    /*    private var _user = MutableLiveData<User>().apply { value = null }
        val user: LiveData<User> = _user*/

    private var _user = MutableLiveData<User>().apply { value = null }
    val user: LiveData<User> = _user


    private val db = Firebase.firestore

    private var storage = Firebase.storage("gs://madgroup4-5de93")
    var storageRef = storage.reference

    fun getUser() {
        val userFire = db
            .collection("users")
            .document("48JnBn7vpjvj0minb62P")
            .get()
            .addOnSuccessListener { res ->
                _user.value =
                    res.toObject(User::class.java)
                //use it as needed
            }
            .addOnFailureListener { it ->
                Log.i("test", "$it")
            }
    }

    fun saveUser(editedUser: User) {
        db.collection("users")
            .document("48JnBn7vpjvj0minb62P")
            .set(editedUser, SetOptions.merge()).addOnSuccessListener {
                Log.i("test", "User $it saved succesfully")
            }
            .addOnFailureListener{
                Log.i("test", "$it")
            }
    }

    fun uploadImage(photoString: String): Uri {
        // Defining the child of storageReference
        val ref = storageRef.child(
            "images/"
                    + "48JnBn7vpjvj0minb62P"
        )
        Log.i("test_vm","after ref ${ref.toString()}")
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
                Log.i("test_vm","after ref ${ref.downloadUrl.toString()}")
                ref.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i("test_vm", "salvata immagine " + ref + " " + Uri.parse(photoString))
                    downloadUri = task.result
                } else {
                    Log.i("test_vm","Errore")
                }
            }.addOnFailureListener{e ->
                Log.i("test","Error adding document",e)
            }
        }
        Log.i("test_vm","after ref ${ref.downloadUrl.toString()}")
        return downloadUri
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