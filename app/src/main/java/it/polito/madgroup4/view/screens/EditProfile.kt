package it.polito.madgroup4.view.screens

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import it.polito.madgroup4.R
import it.polito.madgroup4.model.User
import it.polito.madgroup4.utility.formatDate
import java.io.File
import java.text.SimpleDateFormat


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val user = remember {
        User(
            name = "Marco",
            surname = "Mele",
            nickname = "marcomele98",
            email = "marcomele98@gmail.com",
            phone = "3334545451",
            gender = "Male",
            birthday = formatDate("01/01/1998"),
        )
    }

    var editedUser by remember { mutableStateOf(user) }

    val formatter = SimpleDateFormat("dd/MM/yyyy")

    val contactItems = listOf(
        listOf(
            null,
            editedUser.name,
            "Name",
            { it: String -> editedUser = editedUser.copy(name = it) }),
        listOf(
            null,
            editedUser.surname,
            "Surname",
            { it: String -> editedUser = editedUser.copy(surname = it) }),
        listOf(
            null,
            editedUser.nickname,
            "Nickname",
            { it: String -> editedUser = editedUser.copy(nickname = it) }),
//        Pair(Icons.Default.Phone, editedUser.phone),
        listOf(
            Icons.Default.Email,
            editedUser.email,
            "Email",
            { it: String -> editedUser = editedUser.copy(email = it) }),
//        Pair(Icons.Default.Cake, formatter.format(editedUser.birthday)),
//        Pair(Icons.Default.Transgender, editedUser.gender)
    )

    var hasImage by remember {
        mutableStateOf(false)
    }
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }



    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            hasImage = uri != null
            imageUri = uri
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
           println(success)
            println(imageUri)
        }
    )

    val context = LocalContext.current

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {

        Column(
            Modifier.fillMaxWidth()
        ) {


            Box(Modifier.align(Alignment.CenterHorizontally)) {
                Image(
                    painter = painterResource(R.drawable.profile),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                )

                SmallFloatingActionButton(
                    onClick = {
                        val values = ContentValues()
                        values.put(MediaStore.Images.Media.TITLE, "New Picture")
                        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
                        imageUri =
                            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
/*                        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, editImageUri)*/
                        cameraLauncher.launch(imageUri)
                    },
                    modifier = Modifier.align(Alignment.BottomEnd),
                    shape = CircleShape,
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Picture",
                        tint = MaterialTheme.colorScheme.primary
                    )

                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            LazyColumn(modifier = Modifier.fillMaxWidth()) {

                items(contactItems.size) { index ->
                    ContactItem1(
                        icon = contactItems[index][0] as ImageVector?,
                        text = contactItems[index][1] as String,
                        label = contactItems[index][2] as String,
                        onClick = contactItems[index][3] as (String) -> Unit
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactItem1(icon: ImageVector?, text: String, label: String, onClick: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            leadingIcon = if (icon != null) {
                { Icon(icon, contentDescription = null) }
            } else null,
            value = text,
            onValueChange = { onClick(it) },
            label = { Text(text = label) },
            modifier = Modifier.fillMaxWidth()
        )
    }
    Spacer(modifier = Modifier.height(10.dp))
}


class ComposeFileProvider : FileProvider(
) {
    companion object {
        fun getImageUri(context: Context): Uri {
            val directory = File(context.cacheDir, "images")
            directory.mkdirs()
            val file = File.createTempFile(
                "selected_image_",
                ".jpg",
                directory,
            )
            val authority = context.packageName + ".fileprovider"
            return getUriForFile(
                context,
                authority,
                file,
            )
        }
    }
}


private fun openCameraForResult(context: Context) {
    //if(takePicture.resolveActivity(packageManager) != null)
/*    if (!hasCameraPermission()) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ),
            1
        )*/
   /* } else {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        editImageUri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, editImageUri)
        cameraLauncher.launch(takePicture)
    }*/


}

var editImageUri: Uri? = null

/*
private var cameraLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {

            val imageBitmap = uriToBitmap(editImageUri!!)
            val rotatedBitmap = rotateBitmap(imageBitmap!!)
            imageUri = save_propic_internally(rotatedBitmap!!)
            val profile_image = findViewById<ImageView>(R.id.profile_image)
            profile_image.setImageURI(imageUri)
//                }
        }
    }
*/


/*

private fun hasCameraPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
            &&
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
}*/
