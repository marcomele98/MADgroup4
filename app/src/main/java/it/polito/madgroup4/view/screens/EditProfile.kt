package it.polito.madgroup4.view.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import it.polito.madgroup4.R
import it.polito.madgroup4.model.User
import it.polito.madgroup4.utility.formatDate
import java.io.File
import java.io.FileDescriptor
import java.io.IOException
import java.text.SimpleDateFormat


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
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

    val openDialog = remember { mutableStateOf(false) }

    var isCameraOpen = remember { mutableStateOf(false) }

    val cameraPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    )

    val galleryPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    )


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


    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var editImageUri by remember {
        mutableStateOf<Uri?>(null)
    }


    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                editImageUri = result.data?.data
//                val imageBitmap = uriToBitmap(editImageUri!!, context)
//                val rotatedBitmap = rotateBitmap(imageBitmap!!, context, editImageUri)

            }
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            editImageUri = imageUri
            isCameraOpen.value = false
        }
    )

    if (openDialog.value) {
        AlertDialog(onDismissRequest = {
            openDialog.value = false
        }, confirmButton = {
            TextButton(onClick = {
                if (cameraPermissionState.allPermissionsGranted) {
                    val values = ContentValues()
                    values.put(MediaStore.Images.Media.TITLE, "New Picture")
                    values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
                    imageUri =
                        context.contentResolver.insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            values
                        )
                    cameraLauncher.launch(imageUri)
                    openDialog.value = false
                } else {
                    cameraPermissionState.launchMultiplePermissionRequest()
                }
            }) {
                Text("Take a photo")
            }
        }, dismissButton = {
            TextButton(onClick = {
                if (galleryPermissionState.allPermissionsGranted) {
                    val galleryIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    galleryLauncher.launch(galleryIntent)
                    openDialog.value = false
                } else {
                    galleryPermissionState.launchMultiplePermissionRequest()
                }
            }) {
                Text("Chose from gallery")
            }
        }, title = {
            Text("Choose an option")
        }, properties = DialogProperties(
            dismissOnBackPress = true, dismissOnClickOutside = true
        )
        )
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {

        Column(
            Modifier.fillMaxWidth()
        ) {


            Box(Modifier.align(Alignment.CenterHorizontally)) {
                if (editImageUri != null) {
                    println("imageUri not null")
                    uriToBitmap(editImageUri!!, context)?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                        )
                    }
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(1.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                    )
                }

                SmallFloatingActionButton(
                    onClick = {
                        openDialog.value = true
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

@OptIn(ExperimentalPermissionsApi::class)
private fun openCameraForResult(
    cameraPermissionState: MultiplePermissionsState,
    context: Context,
    imageUri: MutableState<Uri?>,
    cameraLauncher: ActivityResultLauncher<Uri?>,
    openDialog: MutableState<Boolean>
) {
    if (cameraPermissionState.allPermissionsGranted) {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        imageUri.value =
            context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            )
        cameraLauncher.launch(imageUri.value)
        openDialog.value = false
    } else {
        cameraPermissionState.launchMultiplePermissionRequest()
    }
}


private fun uriToBitmap(selectedFileUri: Uri, context: Context): Bitmap? {
    try {
        val parcelFileDescriptor = context.contentResolver.openFileDescriptor(selectedFileUri, "r")
        val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()
        return image
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}

@SuppressLint("Range")
fun rotateBitmap(input: Bitmap, context: Context, editImageUri: Uri): Bitmap? {
    val orientationColumn =
        arrayOf(MediaStore.Images.Media.ORIENTATION)
    val cur: Cursor? =
        context.contentResolver.query(editImageUri!!, orientationColumn, null, null, null)
    var orientation = -1
    if (cur != null && cur.moveToFirst()) {
        orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]))
    }
    Log.d("tryOrientation", orientation.toString() + "")
    val rotationMatrix = Matrix()
    rotationMatrix.setRotate(orientation.toFloat())
    return Bitmap.createBitmap(input, 0, 0, input.width, input.height, rotationMatrix, true)
}


