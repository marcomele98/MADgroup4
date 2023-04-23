package it.polito.madgroup4

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.io.FileDescriptor
import java.io.IOException
import java.util.*

@AndroidEntryPoint
class EditProfileActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etNickname: EditText
    private lateinit var etPhone: EditText
    private lateinit var etMail: EditText
    //private lateinit var genderSpinner: Spinner
    private lateinit var etGender: EditText
    private lateinit var etBirthdate: EditText
    private var editImageUri: Uri? = null
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        etName = findViewById(R.id.name)
        etNickname = findViewById(R.id.nickname)
        etPhone = findViewById(R.id.phone)
        etMail = findViewById(R.id.email)
        etBirthdate = findViewById(R.id.birthdate)
        etGender = findViewById(R.id.gender)


        // deserialize the object from json format
        val sharedPref = getSharedPreferences("USER", Context.MODE_PRIVATE)
        val profile: Profile = Profile.getFromPreferences(sharedPref)
        etName.setText(profile.name)
        etNickname.setText(profile.nickname)
        etPhone.setText(profile.phone)
        etMail.setText(profile.email)
        etGender.setText(profile.gender)
        etBirthdate.setText(profile.birthdate)

        fun showGenderDialog() {
            val options = arrayOf<CharSequence>("Male", "Female", "Non-binary", "Prefer not to say")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Select your gender")
            builder.setItems(options) { _, item ->
                val selectedGender = options[item]
                etGender.setText(selectedGender)
            }
            builder.show()
        }


        etGender.setOnClickListener{
           showGenderDialog()
        }

        fun showBirthdayDialog(){
            // on below line we are getting
            // the instance of our calendar.
            val c = Calendar.getInstance()

            // on below line we are getting
            // our day, month and year.
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            // on below line we are creating a
            // variable for date picker dialog.
            val datePickerDialog = DatePickerDialog(
                // on below line we are passing context.
                this, R.style.Theme_DatePicker,
                { view, year, monthOfYear, dayOfMonth ->
                    // on below line we are setting
                    // date to our edit text.
                    val dat = (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                    etBirthdate.setText(dat)
                },
                // on below line we are passing year, month
                // and day for the selected date in our date picker.
                year,
                month,
                day
            )

            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            // at last we are calling show
            // to display our date picker dialog.

            datePickerDialog.show()
        }


        etBirthdate.setOnClickListener {
            showBirthdayDialog()
        }

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
            val filename = "img_${SystemClock.uptimeMillis()}" + ".jpeg"
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
        val profile = Profile(
            etName.text.toString(),
            etNickname.text.toString(),
            etPhone.text.toString(),
            etMail.text.toString(),
            etGender.text.toString(),
            //genderSpinner.selectedItem.toString(),
            etBirthdate.text.toString(),
            imageUri.toString()
        )
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
        ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun openCameraForResult() {
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
            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, "New Picture")
            values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
            editImageUri =
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, editImageUri)
            cameraLauncher.launch(takePicture)
        }

    }

    private fun openGalleryForResult() {
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
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(galleryIntent)
        }
    }

    private var cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                val imageBitmap = uriToBitmap(editImageUri!!)
                val rotatedBitmap = rotateBitmap(imageBitmap!!)
                imageUri = save_propic_internally(rotatedBitmap!!)
                val profile_image = findViewById<ImageView>(R.id.profile_image)
                profile_image.setImageURI(imageUri)
//                }
            }
        }

    private var galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                editImageUri = result.data?.data
                val imageBitmap = uriToBitmap(editImageUri!!)
                val rotatedBitmap = rotateBitmap(imageBitmap!!)
                imageUri = save_propic_internally(rotatedBitmap!!)
                val profile_image = findViewById<ImageView>(R.id.profile_image)
                profile_image.setImageURI(imageUri)
            }
        }

    @SuppressLint("Range")
    fun rotateBitmap(input: Bitmap): Bitmap? {
        val orientationColumn =
            arrayOf(MediaStore.Images.Media.ORIENTATION)
        val cur: Cursor? =
            contentResolver.query(editImageUri!!, orientationColumn, null, null, null)
        var orientation = -1
        if (cur != null && cur.moveToFirst()) {
            orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]))
        }
        Log.d("tryOrientation", orientation.toString() + "")
        val rotationMatrix = Matrix()
        rotationMatrix.setRotate(orientation.toFloat())
        return Bitmap.createBitmap(input, 0, 0, input.width, input.height, rotationMatrix, true)
    }

    private fun uriToBitmap(selectedFileUri: Uri): Bitmap? {
        try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(selectedFileUri, "r")
            val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return image
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

}







