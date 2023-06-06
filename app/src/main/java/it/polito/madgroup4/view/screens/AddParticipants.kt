package it.polito.madgroup4.view.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Space
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.model.User
import it.polito.madgroup4.view.ReservationActivityCompose
import it.polito.madgroup4.viewmodel.LoadingStateViewModel
import it.polito.madgroup4.viewmodel.ReservationViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AddParticipants(
    reservationId: String,
    reservations: State<List<ReservationWithCourt>?>,
    owner: State<User?>,
    users: State<List<User>?>,
    reservationVm: ReservationViewModel,
    loadingVm: LoadingStateViewModel,
    context: ReservationActivityCompose,
) {

    val reservation = reservations.value?.find { it.reservation?.id == reservationId }

    var search by remember { mutableStateOf("") }

    var filteredUsers by remember { mutableStateOf(emptyList<User>()) }

    val link = reservationVm.generateReservationLink(reservationId)

    var isSnackbarVisible by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    Column {

        LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Invite with link",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontStyle = FontStyle.Italic
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = {
                            var message = "Join my Reservation in CUS Torino App \n \n $link"
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
                            Icons.Outlined.Share,
                            modifier = Modifier.size(25.dp),
                            contentDescription = "Share"
                        )
                    }

                    IconButton(
                        onClick = {
                            val clipboardManager =
                                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                            var textToCopy = "Join my Reservation in CUS Torino App \n \n $link"

                            val clipData = ClipData.newPlainText("Text", textToCopy)
                            clipboardManager.setPrimaryClip(clipData)

                            isSnackbarVisible = true
                            snackbarMessage = "Text copied on clipboard"
                        }, modifier = Modifier.padding(10.dp)
                    ) {
                        // on below line adding a text for our button.
                        Icon(
                            Icons.Default.ContentCopy,
                            modifier = Modifier.size(25.dp),
                            contentDescription = "Copy To Clipboard"
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "Search friends",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontStyle = FontStyle.Italic
                )
                Row() {
                    OutlinedTextField(
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        value = search,
                        onValueChange = {
                            search = it.lowercase().trim();
                            filteredUsers =
                                users.value?.filter { it.name != null && it.id != null && it.surname != null && it.nickname != null }
                                    ?.filter {
                                        it.id != owner.value?.id &&
                                                it.name!!.lowercase().trim()
                                                    .startsWith(search) || it.nickname!!.lowercase()
                                            .trim()
                                            .startsWith(search) || it.surname!!.lowercase().trim()
                                            .startsWith(
                                                search
                                            )
                                    }!!.take(5)
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        singleLine = true,
                        label = { Text(text = "Search name or nickname") },
                        placeholder = { Text(text = "Search name or nickname") },
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }


            items(
                filteredUsers.size
            ) {
                val user = filteredUsers[it]
                Column(
                    modifier = Modifier
                        .animateContentSize(
                            animationSpec = tween(
                                durationMillis = 500,
                                easing = FastOutSlowInEasing
                            )
                        )
                        .animateItemPlacement(
                            animationSpec = tween(
                                durationMillis = 500,
                                easing = FastOutSlowInEasing
                            )
                        )
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (search == "") Modifier.height(0.dp) else Modifier.height(
                                    70.dp
                                )
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = user?.name!! + " " + user.surname!!,
                                fontSize = 20.sp,
                                fontStyle = FontStyle.Italic
                            )
                            Text(text = "@" + user.nickname!!, fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        if (reservation?.reservation?.reservationInfo?.confirmedUsers!!.contains(
                                user.id
                            )
                        )
                            Text(
                                text = "Participant", color = MaterialTheme.colorScheme.tertiary,
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.Bold
                            )
                        else if (reservation?.reservation?.reservationInfo?.pendingUsers!!.contains(
                                user.id
                            )
                        ) {
                            Text(
                                text = "Invited", color = MaterialTheme.colorScheme.primary,
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.Bold
                            )
                        } else
                            Button(onClick = {
                                reservationVm.invite(
                                    user.id!!,
                                    reservation?.reservation!!,
                                    loadingVm,
                                    "User invited successfully",
                                    "Error while inviting the user",
                                    "Add Participants",
                                    "${owner.value?.name} ${owner.value?.surname} invited you to play",
                                    user
                                )
                            }) {
                                Text(text = "Invite")
                            }
                    }
                    Divider(modifier = Modifier.height(if (search == "") 0.dp else 1.dp))
                }
            }


        }

        Spacer(modifier = Modifier.weight(1f))
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