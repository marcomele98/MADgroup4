package it.polito.madgroup4.view.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.model.User
import it.polito.madgroup4.utility.calculateStartEndTime
import it.polito.madgroup4.utility.formatDate
import it.polito.madgroup4.utility.formatTimestampToString
import it.polito.madgroup4.view.components.ReservationDetails
import it.polito.madgroup4.view.components.ReviewCard
import it.polito.madgroup4.viewmodel.LoadingStateViewModel
import it.polito.madgroup4.viewmodel.ReservationViewModel
import it.polito.madgroup4.viewmodel.Status
import java.time.LocalTime
import java.util.Date


@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterial3Api
@Composable
fun ShowReservation(
    reservationId: String,
    navController: NavController,
    reservations: State<List<ReservationWithCourt>?>,
    reservationVm: ReservationViewModel,
    setSelectedCourt: (String) -> Unit,
    setSelectedSlot: (Int) -> Unit,
    loadingVm: LoadingStateViewModel,
    user: State<User?>,
    users: State<List<User>?>,
    fromLink: Boolean? = false
) {

    val openDialog = remember { mutableStateOf(false) }

    val openDialogLeave = remember { mutableStateOf(false) }

    val openDialogJoinPublic = remember { mutableStateOf(false) }

    val reservation = reservations.value?.find { it.reservation?.id == reservationId }

    val showConfirmed = remember { mutableStateOf(false) }

    val showPending = remember { mutableStateOf(false) }

    if (reservation != null) {
        setSelectedSlot(reservation.reservation?.slotNumber!!)


        val isInThePast =
            reservation.reservation.date.toDate() < formatDate(Date()) || (formatDate(Date()) == formatDate(
                reservation.reservation.date.toDate()
            ) && LocalTime.parse(
                calculateStartEndTime(
                    reservation.playingCourt!!.openingTime!!, reservation.reservation.slotNumber
                ).split("-")[0].trim()
            ).isBefore(
                LocalTime.now()
            ))

        val sport = user.value?.sports?.find { it.name == reservation.reservation.sport }
        var message = ""

        LaunchedEffect(Unit) {
            setSelectedCourt(reservation.playingCourt!!.name!!)
            reservationVm.getAllPlayingCourtsBySportAndDate(
                formatDate(formatTimestampToString(reservation.reservation.date)),
                reservation.playingCourt!!.sport!!
            )
            if (sport == null) {
                message =
                    "The suggested level for this match is ${reservation.reservation.reservationInfo?.suggestedLevel} while you don't have set your level for this sport. Are you sure you want to join this match? "
            } else if (sport.level != reservation.reservation.reservationInfo?.suggestedLevel) {
                message =
                    "The suggested level for this match is ${reservation.reservation.reservationInfo?.suggestedLevel} while your level for this sport is ${sport.level}. Are you sure you want to join this match? "
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {

            if (openDialog.value) {
                AlertDialog(onDismissRequest = {
                    openDialog.value = false
                }, confirmButton = {
                    TextButton(onClick = {
                        loadingVm.setStatus(Status.Loading)
                        reservationVm.deleteReservation(
                            reservation.reservation,
                            loadingVm,
                            "Reservation deleted successfully",
                            "Error while deleting the reservation",
                            "Reservations",
                            "User ${user.value!!.name} ${user.value!!.surname} has deleted the match"
                        )
                        openDialog.value = false
                    }) {
                        Text("Delete")
                    }
                }, dismissButton = {
                    TextButton(onClick = {
                        openDialog.value = false
                    }) {
                        Text("Cancel")
                    }
                }, title = {
                    Text("Delete Reservation")
                }, text = {
                    Text(
                        text = "Are you sure you want to delete your reservation?",
                    )
                }, properties = DialogProperties(
                    dismissOnBackPress = true, dismissOnClickOutside = true
                )
                )
            }

            if (openDialogLeave.value) {
                AlertDialog(onDismissRequest = {
                    openDialogLeave.value = false
                }, confirmButton = {
                    TextButton(onClick = {
                        loadingVm.setStatus(Status.Loading)
                        reservationVm.cancelPartecipationToReservation(
                            reservation.reservation,
                            user.value?.id!!,
                            loadingVm,
                            "Reservation leaved successfully",
                            "Error while leaving the reservation",
                            "Reservations",
                            "User ${user.value!!.name} ${user.value!!.surname} has leaved your reservation"
                        )
                        openDialogLeave.value = false
                    }) {
                        Text("Leave")
                    }
                }, dismissButton = {
                    TextButton(onClick = {
                        openDialogLeave.value = false
                    }) {
                        Text("Cancel")
                    }
                }, title = {
                    Text("Leave Match")
                }, text = {
                    Text(
                        text = "Are you sure you want to leave this match?",
                    )
                }, properties = DialogProperties(
                    dismissOnBackPress = true, dismissOnClickOutside = true
                )
                )
            }

            if (openDialogLeave.value) {

                AlertDialog(onDismissRequest = {
                    openDialogJoinPublic.value = false
                }, confirmButton = {
                    TextButton(onClick = {
                        loadingVm.setStatus(Status.Loading)
                        reservationVm.addInAPublicReservationAndSaveReservation(
                            user.value?.id!!,
                            reservation.reservation,
                            loadingVm,
                            "Reservation joined successfully",
                            "Error while joining the reservation",
                            "Reservations",
                            "User ${user.value!!.name} ${user.value!!.surname} has joined your reservation"
                        )
                    }) {
                        Text("Join")
                    }
                }, dismissButton = {
                    TextButton(onClick = {
                        openDialogJoinPublic.value = false
                    }) {
                        Text("Cancel")
                    }
                }, title = {
                    Text("Join Match")
                }, text = {
                    Text(
                        text = message,
                    )
                }, properties = DialogProperties(
                    dismissOnBackPress = true, dismissOnClickOutside = true
                )
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                LazyColumn(
                    Modifier.fillMaxSize()
                ) {

                    item {

                        ReservationDetails(
                            reservation.playingCourt!!,
                            reservation.reservation.date.toDate(),
                            reservation.reservation.slotNumber,
                            reservation.reservation.price,
                            reservation.reservation?.reservationInfo,
                        )
                    }

                    item {
                        if (reservation.reservation.reservationInfo?.confirmedUsers?.filter { it != user.value?.id }
                                ?.isNotEmpty()!! || reservation.reservation.reservationInfo?.pendingUsers?.isNotEmpty()!! || user.value?.id == reservation.reservation.userId) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "Participants",
                                    fontSize = 23.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontStyle = FontStyle.Italic
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                if (!isInThePast && user.value?.id == reservation.reservation.userId) IconButton(
                                    onClick = { navController.navigate("Add Participants") }) {
                                    Icon(
                                        imageVector = Icons.Outlined.Add,
                                        modifier = Modifier
                                            .size(30.dp)
                                            .alpha(0.6f),
                                        //tint = MaterialTheme.colorScheme.secondary,
                                        contentDescription = null
                                    )
                                }

                            }
                        }
                    }

                    item {
                        if (reservation.reservation.reservationInfo?.confirmedUsers?.filter { it != user.value?.id }
                                ?.isNotEmpty()!!) {
                            Row(
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text(
                                    text = "Confirmed: ${reservation.reservation.reservationInfo?.confirmedUsers?.size}",
                                    fontSize = 23.sp,
                                    fontStyle = FontStyle.Italic,
                                    fontWeight = FontWeight.Bold,
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = if (!showConfirmed.value) "See All" else "Hide",
                                    modifier = Modifier
                                        .clickable(onClick = {
                                            showConfirmed.value = !showConfirmed.value
                                        })
                                        .padding(start = 8.dp),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }


                    items(reservation.reservation.reservationInfo?.confirmedUsers!!.size) {
                        val user =
                            users.value?.find { user -> user.id == reservation.reservation.reservationInfo?.confirmedUsers!![it] }
                        Column(
                            modifier = Modifier.animateContentSize(
                                animationSpec = tween(
                                    durationMillis = 500, easing = FastOutSlowInEasing
                                )
                            )
                        ) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .then(
                                        if (!showConfirmed.value) Modifier.height(0.dp) else Modifier.height(
                                            30.dp
                                        )
                                    ),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = user?.name!! + " " + user.surname!!,
                                    fontSize = 22.sp,
                                    fontStyle = FontStyle.Italic
                                )
                            }
                        }
                    }

                    item() {
                        if (reservation.reservation.reservationInfo?.pendingUsers?.isNotEmpty()!!) {
                            Spacer(modifier = Modifier.height(15.dp))
                            Row(
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text(
                                    text = "Invited: ${reservation.reservation.reservationInfo?.pendingUsers?.size} ",
                                    fontSize = 23.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontStyle = FontStyle.Italic
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = if (!showPending.value) "See All" else "Hide",
                                    modifier = Modifier
                                        .clickable(onClick = {
                                            showPending.value = !showPending.value
                                        })
                                        .padding(start = 8.dp),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    items(reservation.reservation.reservationInfo?.pendingUsers!!.size) {
                        val user =
                            users.value?.find { user -> user.id == reservation.reservation.reservationInfo?.pendingUsers!![it] }
                        Column(
                            modifier = Modifier.animateContentSize(
                                animationSpec = tween(
                                    durationMillis = 500, easing = FastOutSlowInEasing
                                )
                            )
                        ) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .then(
                                        if (!showPending.value) Modifier.height(0.dp) else Modifier.height(
                                            30.dp
                                        )
                                    ),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = user?.name!! + " " + user.surname!!,
                                    fontSize = 22.sp,
                                    fontStyle = FontStyle.Italic
                                )
                            }
                        }
                    }


                    item {
                        val stuff = reservation.reservation.stuff
                        if (stuff != null) {
                            val filteredStuff = stuff.filter { it.quantity!! > 0 }
                            if (filteredStuff.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(20.dp))
                                Text(
                                    text = "Rented equipment",
                                    fontSize = 23.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontStyle = FontStyle.Italic
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            filteredStuff.forEach { stuff ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${stuff.name} x${stuff.quantity}",
                                        fontSize = 22.sp,

                                        )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        text = "${stuff.quantity?.let { stuff.price?.times(it) }}€",
                                        fontSize = 22.sp,
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }

                    item {
                        val particularRequests = reservation.reservation.particularRequests
                        if (particularRequests != null && particularRequests.trim() != "") {
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "Particular requests",
                                fontSize = 23.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontStyle = FontStyle.Italic
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            ElevatedCard {
                                Text(
                                    text = particularRequests,
                                    fontSize = 18.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                        .padding(10.dp)
                                )
                            }
                        }
                    }




                    item {
                        if (reservation.reservation.review != null) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "Your review",
                                fontSize = 23.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontStyle = FontStyle.Italic
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(12.dp))
                            ) {
                                ReviewCard(
                                    review = reservation.reservation.review!!, showNickname = false
                                )
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(20.dp)) }

                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (!isInThePast && reservation.reservation.userId == user.value?.id) {
                Button(
                    onClick = {
                        openDialog.value = !openDialog.value
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ), modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Delete", color = MaterialTheme.colorScheme.onError)
                }
            } else if (reservation.reservation.review == null && reservation.reservation.userId == user.value?.id) {
                Button(
                    onClick = { navController.navigate("Rate This Playing Court") }, //modificichiamo? in questo momento valutiamo il playing court ma associato alla prenotazione
                    modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(text = "Rate This Playing Court")
                }
            } else if (reservation.reservation.reservationInfo?.status == "Invited") {

                Column() {
                    Text(
                        text = "You have been invited to play",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(CenterHorizontally),
                        fontSize = 22.sp,
                        fontStyle = FontStyle.Italic
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row() {
                        Button(
                            onClick = {
                                loadingVm.setStatus(Status.Loading)
                                reservationVm.rejectAndSaveReservationInvitation(
                                    reservation.reservation,
                                    user.value?.id!!,
                                    loadingVm,
                                    "Invite rejected successfully",
                                    "Error while rejecting the invite",
                                    "Reservations",
                                    "User ${user.value!!.name} ${user.value!!.surname} has rejected your invite"
                                )
                            }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text(text = "Decline", color = MaterialTheme.colorScheme.onError)
                        }

                        Spacer(modifier = Modifier.padding(8.dp))
                        Button(
                            onClick = {
                                loadingVm.setStatus(Status.Loading)
                                reservationVm.acceptAndSaveReservationInvitation(
                                    reservation.reservation,
                                    user.value?.id!!,
                                    loadingVm,
                                    "Invite accepted successfully",
                                    "Error while accepting the invite",
                                    "Reservations",
                                    "User ${user.value!!.name} ${user.value!!.surname} has accepted your invite"
                                )
                            }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary
                            )
                        ) {
                            Text(text = "Accept", color = MaterialTheme.colorScheme.onTertiary)
                        }
                    }
                }
            } else if (reservation.reservation.reservationInfo?.public == true && !reservation.reservation.reservationInfo?.confirmedUsers?.contains(
                    user.value?.id!!
                )!!
            ) {
                Button(
                    onClick = {
                        if(message == "") {
                            loadingVm.setStatus(Status.Loading)
                            reservationVm.addInAPublicReservationAndSaveReservation(
                                user.value?.id!!,
                                reservation.reservation,
                                loadingVm,
                                "Reservation joined successfully",
                                "Error while joining the reservation",
                                "Reservations",
                                "User ${user.value!!.name} ${user.value!!.surname} has joined your reservation"
                            )
                        } else {
                            openDialogJoinPublic.value = !openDialogJoinPublic.value
                        }
                    }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(text = "Join The Match")
                }
            } else if (!reservation.reservation.reservationInfo?.confirmedUsers?.contains(
                    user.value?.id!!
                )!! && fromLink == true
            ) {
                Button(
                    onClick = {
                        loadingVm.setStatus(Status.Loading)
                        reservationVm.joinFromLink(
                            user.value?.id!!,
                            reservation.reservation,
                            loadingVm,
                            "Reservation joined successfully",
                            "Error while joining the reservation",
                            "Reservations",
                            "User ${user.value!!.name} ${user.value!!.surname} has joined your reservation"
                        )
                    }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(text = "Join The Match")
                }
            } else if (!isInThePast && reservation.reservation.reservationInfo?.confirmedUsers!!.contains(
                    user.value?.id
                ) && user.value?.id != reservation.reservation.userId
            ) {

                Button(
                    onClick = {
                        openDialogLeave.value = !openDialogLeave.value
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ), modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Leave The Match", color = MaterialTheme.colorScheme.onError)
                }

            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    } else {
        LoadingScreen()
    }
}