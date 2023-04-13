package it.polito.madgroup4

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import android.net.Uri
import android.provider.ContactsContract.CommonDataKinds.Nickname

class Profile(
    var name: String = "Marco Mele",
    var nickname: String = "marcomele98",
    var phone: String = "3333333333",
    var email: String = "marco@polito.it",
    var gender: String = "Male",
    var birthdate: String = "13-09-1998",
    var imageUri: String? = null
) {
    companion object {
        fun getFromPreferences(sharedPreferences: SharedPreferences): Profile {
            val json: String? = sharedPreferences.getString("profile", "")
            return (Gson().fromJson(json, Profile::class.java) ?: Profile())
                .apply {
                    if (imageUri == "null") imageUri = null
                }
        }
    }

    fun saveToPreferences(sharedPreferences: SharedPreferences) {
        // serialize the object in json format
        val json = Gson().toJson(this)
        // save the json string in the shared preferences
        with(sharedPreferences.edit()) {
            putString("profile", json)
            apply()
        }
    }

}