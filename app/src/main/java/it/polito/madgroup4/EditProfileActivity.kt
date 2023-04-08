package it.polito.madgroup4

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson


class EditProfileActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private var imageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        etName = findViewById(R.id.name)
        // deserialize the object from json format
        val sharedPref = getSharedPreferences("USER", Context.MODE_PRIVATE)
        val profile: Profile = Profile.getFromPreferences(sharedPref)
        etName.setText(profile.name)
        profile.imageUri?.let {
           findViewById<ImageView>(R.id.profile_image).setImageURI(Uri.parse(it))
        }
        val imageButton = findViewById<android.widget.ImageButton>(R.id.camera_button)
        imageButton.setOnClickListener {
            val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery")
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Choose an option")
            builder.setItems(options) { _, item ->
                when (options[item]) {
                    "Take Photo" -> {
                        openCameraForResult()
                    }
                    "Choose from Gallery" -> {
                        openGallery()

                    }
                }
            }
            builder.show()
        }
    }

    override fun onPause() {
        super.onPause()
        val sharedPref = getSharedPreferences("USER", Context.MODE_PRIVATE) ?: return
        val profile = Profile(etName.text.toString(), imageUri.toString())
        profile.saveToPreferences(sharedPref)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCameraForResult()
            }
        }
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("tv_name", etName.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        etName.setText(savedInstanceState.getString("tv_name"))
    }

    private fun openCameraForResult() {
        val values = ContentValues()
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
//        if(takePicture.resolveActivity(packageManager) != null)
        if (!hasCameraPermission()) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA), 1
            )
        } else {
            cameraLauncher.launch(takePicture)
        }

    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(galleryIntent)
    }

    private var cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val profile_image = findViewById<android.widget.ImageView>(R.id.profile_image)
                profile_image.setImageURI(imageUri)
            }
        }

    private var galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val profile_image = findViewById<android.widget.ImageView>(R.id.profile_image)
                imageUri = result.data?.data
                profile_image.setImageURI(imageUri)
            }
        }
}







