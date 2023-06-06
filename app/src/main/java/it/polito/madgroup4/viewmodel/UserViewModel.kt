package it.polito.madgroup4.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.ktx.storage
import it.polito.madgroup4.model.User
import java.io.ByteArrayOutputStream
import java.io.File

class UserViewModel(reservationVm: ReservationViewModel) : ViewModel() {

    private var _user = MutableLiveData<User>().apply { value = null }
    val user: LiveData<User> = _user

    private var userListener: ListenerRegistration? = null
    private val db = Firebase.firestore
    private val auth = Firebase.auth


    private var _userPhoto = MutableLiveData<Bitmap?>().apply { value = null }
    val userPhoto: LiveData<Bitmap?> = _userPhoto

    private var storage = Firebase.storage("gs://madgroup4-5de93.appspot.com")
    private var storageRef = storage.reference

    private var usersListener: ListenerRegistration? = null

    private var _users = MutableLiveData<List<User>>().apply { value = null }
    val users: LiveData<List<User>> = _users

    init {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            auth.signInAnonymously().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val currentUserId = auth.currentUser!!.uid
                    FirebaseMessaging.getInstance().token.addOnCompleteListener { task2 ->
                        if (task2.isSuccessful) {
                            val token = task2.result
                            // Salva il token nel documento utente nel database Firebase
                            db.collection("users2").document(currentUserId)
                                .set(User(currentUserId, token)).addOnSuccessListener {
                                    createUserListener(currentUserId, reservationVm)
                                }
                        } else {
                            Log.i("test", "error", task2.exception)
                            // Gestisci il fallimento nel recupero del token
                        }
                    }
                } else {
                    Log.i("test", "error", task.exception)
                }
            }
        } else {
            createUserListener(currentUser.uid, reservationVm)
        }
    }

    private fun createUserListener(uid: String, reservationVm: ReservationViewModel) {
        reservationVm.createReservationsListener(uid)

        usersListener = db.collection("users2").addSnapshotListener { r, e ->
            if (e != null) throw e
            else {
                _users.value = r?.toObjects(User::class.java)
            }
        }

        userListener = db.collection("users2").document(uid).addSnapshotListener { r, e ->
            _user.value = if (e != null) throw e
            else r?.toObject(User::class.java)
        }
        val pathReference = storageRef.child("images").child("${uid}.jpg")
        val localFile = File.createTempFile("images", "jpg")
        pathReference.getFile(localFile).addOnSuccessListener {
            // Local temp file has been created
            _userPhoto.value = (BitmapFactory.decodeFile(localFile.absolutePath))
        }.addOnFailureListener {
            // Handle any errors
            Log.i("test", "error", it)
        }
    }


    override fun onCleared() {
        super.onCleared();
        userListener?.remove();
    }


    fun saveUser(
        editedUser: User,
        stateViewModel: LoadingStateViewModel,
        message: String,
        error: String,
        imageBitmap: Bitmap? = null,
        nextRoute: String
    ) {
        fun saveUserDetails() {
            db.collection("users2")
                .whereEqualTo("nickname", "${editedUser.nickname}")
                .get()
                .addOnSuccessListener {
                    if (it.isEmpty || it.documents[0].id == auth.currentUser!!.uid) {
                        db.collection("users2").document(auth.currentUser!!.uid)
                            .set(editedUser, SetOptions.merge()).addOnSuccessListener {
                                Log.i("test", "User updated successfully")
                                stateViewModel.setStatus(Status.Success(message, nextRoute))
                            }.addOnFailureListener {
                                Log.i("test", "$it")
                                stateViewModel.setStatus(Status.Error(error, null))
                            }
                    } else {
                        stateViewModel.setStatus(Status.Error("Nickname already in use", "back"))
                    }
                }
                .addOnFailureListener { exception ->
                    stateViewModel.setStatus(
                        Status.Error(
                            error,
                            nextRoute
                        )
                    )
                    Log.i("test", "Error getting documents: ", exception)
                }
        }
        if (imageBitmap != null) {
            uploadImage(imageBitmap, auth.currentUser!!.uid) {
                saveUserDetails()
            }
        } else {
            saveUserDetails()
        }
    }


    fun removeAchievement(
        sportName: String, achievementId: Int, stateViewModel: LoadingStateViewModel
    ) {
        val userTmp = _user.value!!
        userTmp.sports?.forEach { sport ->
            if (sport.name == sportName) {
                sport.achievements = sport.achievements - sport.achievements[achievementId]
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


    private fun uploadImage(photo: Bitmap, uid: String, then: () -> Unit) {
        val ref = storageRef.child("images").child("${uid}.jpg")
        if (photo != null) {
            val baos = ByteArrayOutputStream()
            photo.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            // adding listeners on upload
            // or failure of image
            val uploadTask = ref.putBytes(data)

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
}