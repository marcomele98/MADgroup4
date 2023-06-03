package it.polito.madgroup4.view.screens


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import it.polito.madgroup4.view.ReservationActivityCompose
import kotlinx.coroutines.delay

@Composable
fun ShareOrCopyInviteLink(
    context: ReservationActivityCompose, text: String
) {

    var isSnackbarVisible by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }


    Row {
        IconButton(
            onClick = {
                var message = "Join my Reservation in CUS Torino App \n \n $text"
                val i = Intent(Intent.ACTION_SEND)
                i.type = "text/plain"
                i.putExtra(Intent.EXTRA_TEXT, message)
                try {
                    context.startActivity(i)
                } catch (e: Exception) {
                    Log.e("Error", "Error while sending invite")
                }
            }, modifier = Modifier.padding(10.dp)
        ) {
            Icon(
                Icons.Outlined.Share, modifier = Modifier.size(30.dp), contentDescription = "Share"
            )
        }

        IconButton(
            onClick = {
                val clipboardManager =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                var textToCopy = "Join my Reservation in CUS Torino App \n \n $text"

                val clipData = ClipData.newPlainText("Text", textToCopy)
                clipboardManager.setPrimaryClip(clipData)

                isSnackbarVisible = true
                snackbarMessage = "Text copied on clipboard"
            }, modifier = Modifier.padding(10.dp)
        ) {
            // on below line adding a text for our button.
            Icon(
                Icons.Default.ContentCopy,
                modifier = Modifier.size(30.dp),
                contentDescription = "Copy To Clipboard"
            )
        }
        if (isSnackbarVisible) {
            Snackbar(
                action = {},
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            ) {
                Text(snackbarMessage)
            }

            LaunchedEffect(isSnackbarVisible) {
                if (isSnackbarVisible) {
                    delay(2000) // Mostra lo Snackbar per 2 secondi
                    isSnackbarVisible = false
                }
            }
        }
    }
}