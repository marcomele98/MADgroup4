package it.polito.madgroup4

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import android.net.Uri

class Profile(var name: String = "Full Name", var imageUri: String? = null) {
    companion object {
        fun getFromPreferences(sharedPreferences: SharedPreferences): Profile {
            val json: String? = sharedPreferences.getString("profile", "")
            return Gson().fromJson(json, Profile::class.java) ?: Profile()
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