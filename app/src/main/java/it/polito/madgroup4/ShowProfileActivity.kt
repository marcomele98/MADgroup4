package it.polito.madgroup4

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.Gson

class ShowProfileActivity : AppCompatActivity() {

    private lateinit var tvName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvName = findViewById(R.id.name)

        val sharedPref = getSharedPreferences("USER", Context.MODE_PRIVATE)
        val profile: Profile = Profile.getFromPreferences(sharedPref)
        tvName.text = profile.name

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

        profile.imageUri?.let {
            findViewById<ImageView>(R.id.profile_image).setImageURI(Uri.parse(it))
        }
    }
}