package it.polito.madgroup4.view.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.utility.calculateStartEndTime
import it.polito.madgroup4.utility.formatDate
import it.polito.madgroup4.utility.formatTimestampToString
import it.polito.madgroup4.view.components.ReservationDetails
import it.polito.madgroup4.viewmodel.LoadingStateViewModel
import it.polito.madgroup4.viewmodel.ReservationViewModel
import it.polito.madgroup4.viewmodel.UserViewModel
import java.time.LocalTime
import java.util.Date


@ExperimentalMaterial3Api
@Composable
fun ShowReservation(
    reservationId: String,
    navController: NavController,
    reservations: State<List<ReservationWithCourt>?>,
    reservationVm: ReservationViewModel,
    setSelectedCourt: (String) -> Unit,
) {

    val openDialog = remember { mutableStateOf(false) }

    val reservation = reservations.value?.find { it.reservation?.id == reservationId }

    val isInThePast = reservation?.reservation?.date!!.toDate() < formatDate(Date())
            || (formatDate(Date()) == formatDate(reservation.reservation.date.toDate())
            && LocalTime.parse(
        calculateStartEndTime(
            reservation.playingCourt!!.openingTime!!,
            reservation.reservation.slotNumber
        ).split("-")[0].trim()
    ).isBefore(
        LocalTime.now()
    ))

    LaunchedEffect(Unit){
        setSelectedCourt(reservation.playingCourt!!.name!!)
        reservationVm.getAllPlayingCourtsBySportAndDate(
            formatDate(formatTimestampToString(reservation.reservation.date)), reservation.playingCourt!!.sport!!
        )
    }

    Column( modifier = Modifier.fillMaxWidth()) {

        if (openDialog.value) {
            AlertDialog(onDismissRequest = {
                openDialog.value = false
            }, confirmButton = {
                TextButton(onClick = {
//                    reservationVm.deleteReservation(reservation.reservation, loadingVm, "Reservation deleted successfully", "Error while deleting the reservation")
                    openDialog.value = false
                    navController.navigate("Reservations")
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

        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        ) {

            item {

                ReservationDetails(
                    reservation.playingCourt!!,
                    reservation.reservation.date.toDate(),
                    reservation.reservation.slotNumber,
                    reservation.reservation.particularRequests,
                    reservation.reservation.price,
                    reservation.reservation.stuff,
                )

                if (reservation.reservation.review != null) {
                    Spacer(modifier = Modifier.height(30.dp))
                    ReviewList(
                        reviews = listOf(reservation.reservation.review!!),
                        showNickname = false
                    )
                }

            }
        }

        Spacer(modifier = Modifier.weight(1f))

        if (!isInThePast) {
            Button(
                onClick = {
                    openDialog.value = !openDialog.value
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ), modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Delete")
            }
        } else if (reservation.reservation.review == null) {
            Button(
                onClick = { navController.navigate("Rate This Playing Court") }, //modificichiamo? in questo momento valutiamo il playing court ma associato alla prenotazione
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(text = "Rate This Playing Court")
            }
        }
    }
}