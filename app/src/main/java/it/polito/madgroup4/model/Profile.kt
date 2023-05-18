package it.polito.madgroup4.model

import android.content.SharedPreferences
import com.google.gson.Gson

class Profile(
    var name: String = "Marco",
    var surname: String = "Mele",
    var nickname: String = "marcomele98",
    var email: String = "marco@polito.it",
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