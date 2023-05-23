package it.polito.madgroup4.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import it.polito.madgroup4.model.Repository
import it.polito.madgroup4.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private val viewModelScope = CoroutineScope(Dispatchers.Main)/*    private var _user = MutableLiveData<User>().apply { value = null }
        val user: LiveData<User> = _user*/

    private var _user = MutableLiveData<User>().apply { value = null }
    val user: LiveData<User> = _user

    private val userListener: ListenerRegistration
    private val db = Firebase.firestore


    private var _userPhoto = MutableLiveData<Bitmap?>().apply { value = null }
    val userPhoto: LiveData<Bitmap?> = _userPhoto


    private var storage = Firebase.storage("gs://madgroup4-5de93.appspot.com")
    var storageRef = storage.reference

    init {
        userListener =
            db.collection("users").document("48JnBn7vpjvj0minb62P")
                .addSnapshotListener { r, e ->
                    _user.value = if (e != null) throw e
                    else r?.toObject(User::class.java)
                }
        val pathReference = storageRef
            .child("images")
            .child("48JnBn7vpjvj0minb62P.jpg")
//        var bitmap: Bitmap? = null
        val localFile = File.createTempFile("images", "jpg")
        pathReference.getFile(localFile).addOnSuccessListener {
            // Local temp file has been created
            _userPhoto.value = (BitmapFactory.decodeFile(localFile.absolutePath))
//            bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
        }.addOnFailureListener {
            // Handle any errors
            Log.i("test", "error", it)
        }

    }

    override fun onCleared() {
        super.onCleared(); userListener.remove(); }


    fun saveUser(
        editedUser: User,
        stateViewModel: LoadingStateViewModel,
        message: String,
        error: String,
        imageBitmap: Bitmap? = null,
        nextRoute: String
    ) {

        fun saveUserDetails() {
            db.collection("users").document("48JnBn7vpjvj0minb62P")
                .set(editedUser, SetOptions.merge())
                .addOnSuccessListener {
                    Log.i("test", "User updated successfully")
                    stateViewModel.setStatus(Status.Success(message, nextRoute))
                }.addOnFailureListener {
                    Log.i("test", "$it")
                    stateViewModel.setStatus(Status.Error(error, nextRoute))
                }
        }
        stateViewModel.setStatus(Status.Loading)
        if (imageBitmap != null) {
            uploadImage(imageBitmap){
                saveUserDetails()
            }
        }else{
            saveUserDetails()
        }
    }


    fun removeAchievement(
        sportName: String,
        achievmentTitle: String,
        stateViewModel: LoadingStateViewModel
    ) {
        var userTmp = _user.value!!
        userTmp.sports?.forEach { sport ->
            if (sport.name == sportName) {
                sport.achievements = sport.achievements.filter { achievement ->
                    achievement.title != achievmentTitle
                }
            }
        }
        saveUser(
            userTmp,
            stateViewModel,
            "Achievement removed successfully",
            "Error while removing achievement",
            null,
            "Your Sport"
        )
    }


    fun uploadImage(photo: Bitmap, then : () -> Unit) {
        val ref = storageRef
            .child("images")
            .child("48JnBn7vpjvj0minb62P.jpg")
        if (photo != null) {
            val baos = ByteArrayOutputStream()
            photo.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            // adding listeners on upload
            // or failure of image
            var uploadTask = ref.putBytes(data)

            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i("test_vm", "Image saved with success")
                    _userPhoto.value = photo
                    then()
                } else {
                    Log.i("test_vm", "Error while saving image")
                }
            }.addOnFailureListener { e ->
                Log.i("test", "Error adding image ", e)
            }
        }
        Log.i("test_vm", "after ref ${ref.downloadUrl}")
    }

    fun getImage(id: String, setBitmap: (Bitmap) -> Unit) {
        val pathReference = storageRef
            .child("images")
            .child("48JnBn7vpjvj0minb62P.jpg")
//        var bitmap: Bitmap? = null
        val localFile = File.createTempFile("images", "jpg")
        pathReference.getFile(localFile).addOnSuccessListener {
            // Local temp file has been created
            setBitmap(BitmapFactory.decodeFile(localFile.absolutePath))
//            bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
        }.addOnFailureListener {
            // Handle any errors
            Log.i("test", "error", it)
        }
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