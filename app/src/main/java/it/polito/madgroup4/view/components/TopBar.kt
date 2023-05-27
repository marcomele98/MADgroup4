package it.polito.madgroup4.view.components

import android.content.Context
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.madgroup4.model.Profile
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.model.Sport
import it.polito.madgroup4.model.User
import it.polito.madgroup4.utility.calculateStartEndTime
import it.polito.madgroup4.utility.formatDate
import it.polito.madgroup4.utility.imageSelector
import it.polito.madgroup4.viewmodel.UserViewModel
import java.time.LocalTime
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    reservation: ReservationWithCourt? = null,
    navController: NavController,
    user: State<User?>,
    topBarAction: () -> Unit = {},
    favoriteSport: Int? = null,
) {

    var isInThePast = false
    if (reservation!!.reservation != null) {
        isInThePast = reservation.reservation!!.date < formatDate(Date())
                || (formatDate(Date()) == reservation.reservation.date
                && LocalTime.parse(
            calculateStartEndTime(
                reservation.playingCourt!!.openingTime!!,
                reservation.reservation.slotNumber
            ).split("-")[0].trim()
        ).isBefore(
            LocalTime.now()
        ))
    }

    CenterAlignedTopAppBar(
        title = {
            if (title != "Your Sport")
                Text(text = title)
            else
                Row(
                    verticalAlignment = CenterVertically,
                ) {
                    //Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageSelector(user?.value?.sports?.get(favoriteSport!!)?.name!!),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = user?.value?.sports?.get(favoriteSport!!)?.name!!
                    )
                    //Spacer(modifier = Modifier.weight(1f))
                }
        },
        navigationIcon = {
            if (title != "Reservations" && title != "Profile") {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Back",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        },
        actions = {
            if (title == "Reservation Details" && !isInThePast) {
                IconButton(onClick = { navController.navigate("Edit Reservation") }) {
                    Icon(
                        Icons.Outlined.Edit,
                        contentDescription = "Edit"
                    )
                }
            } else if (title == "Profile") {
                IconButton(onClick = { navController.navigate("Edit Profile") }) {
                    Icon(
                        Icons.Outlined.Edit,
                        contentDescription = "Edit Profile"
                    )
                }
            } else if (
                title == "Edit Profile" ||
                title == "Add Sport" ||
                title == "Confirm Reservation" ||
                title == "Rate This Playing Court" ||
                title == "Confirm Changes" ||
                title == "Edit Your Level" ||
                title == "Create Achievement" ||
                title == "Sign Up"
            ) {
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