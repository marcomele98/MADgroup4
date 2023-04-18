package it.polito.madgroup4

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Nickname
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.google.gson.Gson
import dagger.hilt.android.HiltAndroidApp

class ShowProfileActivity : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvNickname: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvMail: TextView
    private lateinit var tvGender: TextView
    private lateinit var tvBirthdate: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvName = findViewById(R.id.name)
        tvNickname = findViewById(R.id.nickname)
        tvPhone = findViewById(R.id.phone)
        tvMail = findViewById(R.id.email)
        tvGender = findViewById(R.id.gender)
        tvBirthdate = findViewById(R.id.birthdate)

        val sharedPref = getSharedPreferences("USER", Context.MODE_PRIVATE)
        val profile: Profile = Profile.getFromPreferences(sharedPref)
        tvName.text = profile.name
        tvNickname.text = "@"+profile.nickname
        tvPhone.text = profile.phone
        tvMail.text = profile.email
        tvGender.text = profile.gender
        tvBirthdate.text = profile.birthdate

        profile.imageUri?.let {
            findViewById<ImageView>(R.id.profile_image).setImageURI(Uri.parse(it))
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        //menuInflater.inflate(R.menu.dropdown_menu, menu)   for gender
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item -> {
                intent = Intent(this, EditProfileActivity::class.java)
                startActivity(intent)
                true
            }
            /* starting working for dropdown menu for choosing the gender
            R.id.menu_m -> {
                item.isChecked = true
                true
            }
            R.id.menu_f -> {
                item.isChecked = true
                true
            }
            R.id.menu_o -> {
                item.isChecked = true
                true
            } */
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        val sharedPref = getSharedPreferences("USER", Context.MODE_PRIVATE)
        val profile: Profile = Profile.getFromPreferences(sharedPref)
        tvName.text = profile.name
        tvNickname.text = "@"+profile.nickname
        tvPhone.text = profile.phone
        tvMail.text = profile.email
        tvGender.text = profile.gender
        tvBirthdate.text = profile.birthdate

        profile.imageUri?.let {
            findViewById<ImageView>(R.id.profile_image).setImageURI(Uri.parse(it))
        }
    }
}