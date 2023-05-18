package it.polito.madgroup4.view.screens

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.provider.MediaStore
import kotlinx.coroutines.launch
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat


@Composable
fun CameraScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Immagine catturata con successo
            val imageUri = result.data?.data
            // Puoi gestire l'immagine come desiderato
        } else {
            // L'utente ha annullato o si è verificato un errore nella cattura dell'immagine
        }
    }

    requestCameraPermission {
        // I permessi sono stati concessi, puoi avviare l'intent per la fotocamera
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(context.packageManager) != null) {
            coroutineScope.launch {
                val result = takePictureLauncher.launch(takePictureIntent)
            }
        }
    }
}


@Composable
fun requestCameraPermission(onPermissionGranted: () -> Unit) {
    val context = LocalContext.current
    var hasPermission = remember { mutableStateOf(false) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasPermission.value = isGranted
        if (isGranted) {
            onPermissionGranted()
        }
    }

    if (!hasPermission.value) {
        val permission = Manifest.permission.CAMERA
        val isPermissionGranted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

        if (!isPermissionGranted) {
            requestPermissionLauncher.launch(permission)
        }
    } else {
        onPermissionGranted()
    }
}
