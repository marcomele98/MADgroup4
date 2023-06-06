package it.polito.madgroup4.view.screens

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import it.polito.madgroup4.R
import it.polito.madgroup4.model.User
import it.polito.madgroup4.utility.rotateBitmap
import it.polito.madgroup4.utility.uriToBitmap
import it.polito.madgroup4.viewmodel.LoadingStateViewModel
import it.polito.madgroup4.viewmodel.Status
import it.polito.madgroup4.viewmodel.UserViewModel


@OptIn(ExperimentalPermissionsApi::class, ExperimentalComposeUiApi::class)
@Composable
fun EditProfile(
    setTopBarAction: (() -> Unit) -> Unit,
    user: State<User?>,
    userVm: UserViewModel,
    loadingVm: LoadingStateViewModel,
    signUp: Boolean = false,
) {

    val (editedUser, setEditUser) = remember { mutableStateOf(if (user.value != null) user.value else User()) }
    val (selectedImageInput, setSelectedImageInput) = remember { mutableStateOf("") }

    val context = LocalContext.current

    val openDialog = remember { mutableStateOf(false) }

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
            editedUser?.name ?: "",
            "Name",
            { it: String -> setEditUser(editedUser?.copy(name = it)) },
            KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
        ),
        listOf(
            null,
            editedUser?.surname ?: "",
            "Surname",
            { it: String -> setEditUser(editedUser?.copy(surname = it)) },
            KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
        ),
        listOf(
            null,
            editedUser?.nickname ?: "",
            "Nickname",
            { it: String -> setEditUser(editedUser?.copy(nickname = it)) },
            KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
        ),
    )


    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val userPic = userVm.userPhoto.observeAsState()

    var editImageBitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }

    var editImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    LaunchedEffect(userPic.value) {
        if (userPic.value != null) {
            editImageBitmap = userPic.value
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                editImageUri = result.data?.data
                val imageBitmap = uriToBitmap(editImageUri!!, context)
                editImageBitmap = rotateBitmap(imageBitmap!!, context, editImageUri!!)
                setEditUser(editedUser?.copy(photo = true))
            }
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                editImageUri = imageUri
                val imageBitmap = uriToBitmap(editImageUri!!, context)
                editImageBitmap = rotateBitmap(imageBitmap!!, context, editImageUri!!)
                setEditUser(editedUser?.copy(photo = true))
            }
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

    LaunchedEffect(cameraPermissionState.allPermissionsGranted) {
        if (selectedImageInput == "Camera" && cameraPermissionState.allPermissionsGranted)
            launchCamera()
    }

    fun launchGallery() {
        val galleryIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(galleryIntent)
    }

    LaunchedEffect(galleryPermissionState.allPermissionsGranted) {
        if (selectedImageInput == "Gallery" && galleryPermissionState.allPermissionsGranted)
            launchGallery()
    }

    val keyboardController = LocalSoftwareKeyboardController.current


    LaunchedEffect(editedUser) {
        setTopBarAction {

            keyboardController?.hide()

            if ((editedUser?.name?.trim() ?: "") == "") {
                loadingVm.setStatus(Status.Error("You have to insert a name", null))
            } else if ((editedUser?.surname?.trim() ?: "") == "") {
                loadingVm.setStatus(Status.Error("You have to insert a surname", null))
            } else if ((editedUser?.nickname?.trim() ?: "") == "") {
                loadingVm.setStatus(Status.Error("You have to insert a nickname", null))
            } else {
                loadingVm.setStatus(Status.Loading)
                userVm.saveUser(
                    editedUser!!,
                    loadingVm,
                    "Profile ${if (signUp) "created" else "edited"} successfully",
                    "Error while ${if (signUp) "creating" else "editing"} profile",
                    if (editImageUri != null) rotateBitmap(
                        uriToBitmap(editImageUri!!, context)!!,
                        context,
                        editImageUri!!
                    ) else null,
                    "Profile"
                )
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
                    setSelectedImageInput("Camera")
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
                    setSelectedImageInput("Gallery")
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

    if ((editImageBitmap != null && editedUser?.photo == true) || signUp || editedUser?.photo == null) {

        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {

            Column(
                Modifier.fillMaxWidth()
            ) {

                Spacer(modifier = Modifier.size(10.dp))

                Box(Modifier.align(Alignment.CenterHorizontally)) {
                    if (editedUser?.photo == true && editImageBitmap != null) {
                        Image(
                            bitmap = editImageBitmap!!.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                        )

                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(110.dp)
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
    } else {
        LoadingScreen()
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






