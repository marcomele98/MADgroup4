package it.polito.madgroup4

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.app.Activity
import android.graphics.Bitmap
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts

class EditProfileActivity : AppCompatActivity() {

    lateinit var et_name: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        et_name = findViewById(R.id.name)
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
                        val pickPhoto =
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        if (pickPhoto.resolveActivity(packageManager) != null) startActivity(
                            pickPhoto
                        )
                    }
                }
            }
            builder.show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                resultLauncher.launch(takePicture)
            }
        }
    }

    fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("tv_name", et_name.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        et_name.setText(savedInstanceState.getString("tv_name"))
    }

    fun openCameraForResult() {
        println("openCameraForResult")
        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePicture.resolveActivity(packageManager) != null)
            println("ciao")
            if (!hasCameraPermission()) {
                println("ciao1")
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA), 1
            )
        } else {
            println("ciao2")
            resultLauncher.launch(takePicture)
        }
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                val profile_image = findViewById<android.widget.ImageView>(R.id.profile_image)
                val imageData: ByteArray? = data?.getByteArrayExtra("data")
                val bitmap = imageData?.let { android.graphics.BitmapFactory.decodeByteArray(it, 0, it.size) }
                profile_image.setImageBitmap(bitmap)
            }
        }

}

