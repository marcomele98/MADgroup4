package it.polito.madgroup4.view.components

import android.content.Context
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.polito.madgroup4.model.Profile
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.model.User
import it.polito.madgroup4.utility.calculateStartEndTime
import it.polito.madgroup4.utility.formatDate
import java.time.LocalTime
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    reservation: ReservationWithCourt? = null,
    navController: NavController,
    editedUser: User,
    context: Context
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
            Text(text = title)
        },
        navigationIcon = {
            if (title != "Reservations") {
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
            } else if (title == "Edit Profile") {
                IconButton(onClick = {
                    val sharedPref =
                        context.getSharedPreferences("USER", Context.MODE_PRIVATE) ?: null
                    val profile = Profile(
                        editedUser.name!!,
                        editedUser.surname!!,
                        editedUser.nickname!!,
                        editedUser.email!!,
                        editedUser.photo
                    )
                    if (sharedPref != null)
                        profile.saveToPreferences(sharedPref)

                    navController.navigate("Profile")
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