package it.polito.madgroup4.view.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.model.User
import it.polito.madgroup4.utility.calculateStartEndTime
import it.polito.madgroup4.utility.formatDate
import it.polito.madgroup4.utility.imageSelector
import java.time.LocalTime
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    navController: NavController,
    user: State<User?>,
    topBarAction: () -> Unit = {},
    favoriteSport: Int? = null,
    reservations: State<List<ReservationWithCourt>?>,
    reservationId: String,
) {

    val reservation = reservations.value?.find { it.reservation?.id == reservationId }

    var isInThePast = false
    if (reservation?.reservation != null) {
        isInThePast =
            reservation?.reservation?.date!!.toDate() < formatDate(Date()) || (formatDate(Date()) == formatDate(
                reservation.reservation.date.toDate()
            ) && LocalTime.parse(
                calculateStartEndTime(
                    reservation.playingCourt!!.openingTime!!, reservation.reservation.slotNumber
                ).split("-")[0].trim()
            ).isBefore(
                LocalTime.now()
            ))
    }

    CenterAlignedTopAppBar(
        title = {
            if (title != "Your Sport" && title != "Reservations" && title != "Invites" && title != "Reviewable") Text(
                text = title
            )
            else if (title == "Reservations" || title == "Invites" || title == "Reviewable") {
                Row(
                    verticalAlignment = CenterVertically,
                ) {
                    TextButton(
                        onClick = { navController.navigate("Reservations") },
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = "All",
                            color = if (title == "Reservations") MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface,
                            fontSize = 18.sp
                        )
                    }

                    Divider(
                        modifier = Modifier
                            .width(2.dp)
                            .height(25.dp)
                    )

                    TextButton(
                        onClick = { navController.navigate("Reviewable") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Reviewable",
                            color = if (title == "Reviewable") MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface,
                            fontSize = 18.sp
                        )
                    }

                    Divider(
                        modifier = Modifier
                            .width(2.dp)
                            .height(25.dp)
                    )


                    TextButton(
                        onClick = { navController.navigate("Invites") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Invites",
                            color = if (title == "Invites") MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface,
                            fontSize = 18.sp
                        )
                    }

                }

            } else {
                Row(
                    verticalAlignment = CenterVertically,
                ) {
                    Icon(
                        imageSelector(user?.value?.sports?.get(favoriteSport!!)?.name!!),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = user?.value?.sports?.get(favoriteSport!!)?.name!!
                    )
                }
            }
        },

        navigationIcon = {
            if (title != "Reservations" && title != "Profile" && title != "Welcome" && title != "Invites" && title != "Reviewable") {
                IconButton(onClick = {
                    navController.popBackStack();
                }) {
                    Icon(
                        Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Back",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        },
        actions = {
            if (title == "Reservation Details" && !isInThePast && reservation?.reservation?.userId == user.value?.id) {
                IconButton(onClick = { navController.navigate("Edit Reservation") }) {
                    Icon(
                        Icons.Outlined.Edit, contentDescription = "Edit"
                    )
                }
            } else if (title == "Profile") {
                IconButton(onClick = { navController.navigate("Edit Profile") }) {
                    Icon(
                        Icons.Outlined.Edit, contentDescription = "Edit Profile"
                    )
                }
            } else if (title == "Edit Profile" || title == "Add Sport" || title == "Confirm Reservation" || title == "Rate This Playing Court" || title == "Confirm Changes" || title == "Edit Your Level" || title == "Create Achievement" || title == "Complete Your Profile") {
                IconButton(onClick = {
                    topBarAction()
                }) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Done",
                        modifier = Modifier.size(30.dp)
                    )
                }

            }
        },
    )
}