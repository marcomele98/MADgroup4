package it.polito.madgroup4.view.screens

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import it.polito.madgroup4.R
import it.polito.madgroup4.model.User
import it.polito.madgroup4.utility.rotateBitmap
import it.polito.madgroup4.utility.saveProPicInternally
import it.polito.madgroup4.utility.uriToBitmap
import it.polito.madgroup4.viewmodel.UserViewModel


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun EditProfile(
    setTopBarAction: (() -> Unit) -> Unit,
    user: State<User?>,
    userVm: UserViewModel,
    navController: NavController,
) {

    val (editedUser, setEditUser) = remember { mutableStateOf(user.value) }



    val context = LocalContext.current

    val openDialog = remember { mutableStateOf(false) }

    val cameraPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
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
            editedUser?.name,
            "Name",
            { it: String -> setEditUser(editedUser?.copy(name = it)) },
            KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
        ),
        listOf(
            null,
            editedUser?.surname,
            "Surname",
            { it: String -> setEditUser(editedUser?.copy(surname = it)) },
            KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
        ),
        listOf(
            null,
            editedUser?.nickname,
            "Nickname",
            { it: String -> setEditUser(editedUser?.copy(nickname = it)) },
            KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
        ),
//        Pair(Icons.Default.Phone, editedUser.phone),
        listOf(
            Icons.Default.Email,
            editedUser?.email,
            "Email",
            { it: String -> setEditUser(editedUser?.copy(email = it)) },
            KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Email)
        )
        ,
//        Pair(Icons.Default.Cake, formatter.format(editedUser.birthday)),
//        Pair(Icons.Default.Transgender, editedUser.gender)
    )


    var imageUri by remember {
        mutableStateOf<Uri?>(editedUser?.photo?.toUri())
    }

    var editImageUri by remember {
        mutableStateOf<Uri?>(editedUser?.photo?.toUri())
    }


    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                editImageUri = result.data?.data
                var uri = userVm.uploadImage(editImageUri.toString())
                val imageBitmap = uriToBitmap(editImageUri!!, context)
                val rotatedBitmap = rotateBitmap(imageBitmap!!, context, editImageUri!!)
                imageUri = saveProPicInternally(rotatedBitmap!!, context)
                setEditUser(editedUser?.copy(photo = imageUri.toString()))
                //setEditedUser( editedUser.copy(photo = uri.toString()))
            }
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            editImageUri = imageUri
            var uri = userVm.uploadImage(editImageUri.toString())
            val imageBitmap = uriToBitmap(editImageUri!!, context)
            val rotatedBitmap = rotateBitmap(imageBitmap!!, context, editImageUri!!)
            imageUri = saveProPicInternally(rotatedBitmap!!, context)
            setEditUser(editedUser?.copy(photo = imageUri.toString()))
            //setEditedUser( editedUser.copy(photo = uri.toString()))
        }
    )

    fun launchCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        imageUri =
            context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            )
        cameraLauncher.launch(imageUri)
    }

    val precedentCameraPermission by remember { mutableStateOf(cameraPermissionState.allPermissionsGranted) }

    LaunchedEffect(cameraPermissionState.allPermissionsGranted) {
        if (cameraPermissionState.allPermissionsGranted != precedentCameraPermission)
            launchCamera()
    }

    fun launchGallery() {
        val galleryIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(galleryIntent)
    }

    val precedentGalleryPermission by remember { mutableStateOf(galleryPermissionState.allPermissionsGranted) }
    LaunchedEffect(galleryPermissionState.allPermissionsGranted) {
        if (galleryPermissionState.allPermissionsGranted != precedentGalleryPermission)
            launchGallery()
    }

    LaunchedEffect(editedUser){
        if(editedUser != null){
            setTopBarAction {
                userVm.saveUser(editedUser)
                navController.navigate("Profile")
            }
        }
    }

    if (openDialog.value) {
        AlertDialog(onDismissRequest = {
            openDialog.value = false
        }, confirmButton = {
            TextButton(onClick = {
                if (cameraPermissionState.allPermissionsGranted) {
                    launchCamera()
                    openDialog.value = false
                } else {
                    cameraPermissionState.launchMultiplePermissionRequest()
                    openDialog.value = false
                }
            }) {
                Text("Take a photo")
            }
        }, dismissButton = {
            TextButton(onClick = {
                if (galleryPermissionState.allPermissionsGranted) {
                    launchGallery()
                    openDialog.value = false
                } else {
                    galleryPermissionState.launchMultiplePermissionRequest()
                    openDialog.value = false
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
                if (editImageUri != null && editedUser?.photo != "") {
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
                        onClick = contactItems[index][3] as (String) -> Unit,
                        keyboardOptions = contactItems[index][4] as KeyboardOptions,
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactItem1(
    icon: ImageVector?,
    text: String,
    label: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    onClick: (String) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            leadingIcon = if (icon != null) {
                { Icon(icon, contentDescription = null) }
            } else null,
            value = text,
            onValueChange = { onClick(it) },
            label = { Text(text = label) },
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            modifier = Modifier.fillMaxWidth()
        )
    }
    Spacer(modifier = Modifier.height(10.dp))
}






