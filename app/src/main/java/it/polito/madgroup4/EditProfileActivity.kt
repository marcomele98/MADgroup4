package it.polito.madgroup4

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.provider.MediaStore
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream


class EditProfileActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etNickname: EditText
    private lateinit var etPhone: EditText
    private lateinit var etMail: EditText
    private lateinit var etGender: EditText
    private lateinit var etBirthdate: EditText
    private var imageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        etName = findViewById(R.id.name)
        etNickname = findViewById(R.id.nickname)
        etPhone = findViewById(R.id.phone)
        etMail = findViewById(R.id.email)
        etGender = findViewById(R.id.gender)
        etBirthdate = findViewById(R.id.birthdate)

        // deserialize the object from json format
        val sharedPref = getSharedPreferences("USER", Context.MODE_PRIVATE)
        val profile: Profile = Profile.getFromPreferences(sharedPref)
        etName.setText(profile.name)
        etNickname.setText(profile.nickname)
        etPhone.setText(profile.phone)
        etMail.setText(profile.email)
        etGender.setText(profile.gender)
        etBirthdate.setText(profile.birthdate)

        profile.imageUri?.let {
            imageUri = Uri.parse(it)
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
                        openGalleryForResult()

                    }
                }
            }
            builder.show()
        }
    }

    fun save_propic_internally(image: Bitmap): Uri? {
        // Check for permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permission if not granted
            println("Permission not granted")
            return null
        } else {
            val filename =  "img_${SystemClock.uptimeMillis()}"+ ".jpeg"
            val outputStream = openFileOutput(filename, Context.MODE_PRIVATE)
            val byteArrayOutputStream = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            outputStream.write(byteArray)
            outputStream.close()

            return Uri.fromFile(getFileStreamPath(filename))

        }
    }


    override fun onPause() {
        super.onPause()
        val sharedPref = getSharedPreferences("USER", Context.MODE_PRIVATE) ?: return
        val profile = Profile(etName.text.toString(),
            etNickname.text.toString(),
            etPhone.text.toString(),
            etMail.text.toString(),
            etGender.text.toString(),
            etBirthdate.text.toString(),
            imageUri.toString())
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
        if (requestCode == 2) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGalleryForResult()
            }
        }
    }

    private fun hasGalleryPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("tv_name", etName.text.toString())
        outState.putString("tv_nickname", etNickname.text.toString())
        outState.putString("tv_phone", etPhone.text.toString())
        outState.putString("tv_mail", etMail.text.toString())
        outState.putString("tv_gender", etGender.text.toString())
        outState.putString("tv_birthdate", etBirthdate.text.toString())

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        etName.setText(savedInstanceState.getString("tv_name"))
        etNickname.setText(savedInstanceState.getString("tv_nickname"))
        etPhone.setText(savedInstanceState.getString("tv_phone"))
        etMail.setText(savedInstanceState.getString("tv_mail"))
        etGender.setText(savedInstanceState.getString("tv_gender"))
        etBirthdate.setText(savedInstanceState.getString("tv_birthdate"))
    }

    private fun openCameraForResult() {
        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //if(takePicture.resolveActivity(packageManager) != null)
        if (!hasCameraPermission()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ),
                1
            )
        } else {
            cameraLauncher.launch(takePicture)
        }

    }

    private fun openGalleryForResult() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (!hasGalleryPermission()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                ),
                2
            )
        } else {
            galleryLauncher.launch(galleryIntent)
        }
    }

    private var cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val bitmap = data?.extras?.get("data") as Bitmap
                if(bitmap != null) {
                    imageUri = save_propic_internally(bitmap)
                    val profile_image = findViewById<android.widget.ImageView>(R.id.profile_image)
                    profile_image.setImageURI(imageUri)
                }
            }
        }

    private var galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imageUri = result.data?.data
                val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                imageUri = save_propic_internally(imageBitmap)
                val profile_image = findViewById<android.widget.ImageView>(R.id.profile_image)
                profile_image.setImageURI(imageUri)
            }
        }
}







