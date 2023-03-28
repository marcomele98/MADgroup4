package it.polito.madgroup4

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest

class EditProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        val imageButton = findViewById<android.widget.ImageButton>(R.id.camera_button)
        imageButton.setOnClickListener {
            val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery")
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Choose an option")
            builder.setItems(options) { _, item ->
                when (options[item]) {
                    "Take Photo" -> {
                        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        if (takePicture.resolveActivity(packageManager) != null) if (!hasCameraPermission()) {
                            ActivityCompat.requestPermissions(
                                this, arrayOf(Manifest.permission.CAMERA), 1
                            )
                        } else {
                            startActivity(takePicture)
                        }

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
                startActivity(takePicture)
            }
        }
    }

    fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }


}

