package it.polito.madgroup4.view.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.utility.calculateStartEndTime
import it.polito.madgroup4.utility.formatDate
import it.polito.madgroup4.view.components.ReservationDetails
import it.polito.madgroup4.view.components.ReviewCard
import it.polito.madgroup4.viewmodel.ReservationViewModel
import it.polito.madgroup4.viewmodel.ReviewViewModel
import it.polito.madgroup4.viewmodel.UserViewModel
import java.time.LocalTime
import java.util.Date


@ExperimentalMaterial3Api
@Composable
fun ShowReservation(
    reservation: ReservationWithCourt,
    vm: ReservationViewModel,
    reviewVm: ReviewViewModel,
    navController: NavController,
    userVm: UserViewModel,
) {

    val openDialog = remember { mutableStateOf(false) }

    vm.getSlotsByCourtIdAndDate(
        reservation.playingCourt!!.id, reservation.reservation!!.date, userVm.user.value!!.id
    )

    reviewVm.getReviewByReservationId(reservation.reservation.id)

    val review = reviewVm.review.observeAsState(initial = null)

    println("review: $review")

    val isInThePast = reservation.reservation.date < formatDate(Date())
            || (formatDate(Date()) == reservation.reservation.date
            && LocalTime.parse(
        calculateStartEndTime(
            reservation.playingCourt.openingTime!!,
            reservation.reservation.slotNumber
        ).split("-")[0].trim()
    ).isBefore(
        LocalTime.now()
    ))

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
    ) {
        if (openDialog.value) {
            AlertDialog(onDismissRequest = {
                openDialog.value = false
            }, confirmButton = {
                TextButton(onClick = {
                    vm.deleteReservation(reservation.reservation)
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

        ReservationDetails(
            reservation.playingCourt,
            reservation.reservation.date,
            reservation.reservation.slotNumber,
            reservation.reservation.particularRequests
        )
        if(review.value != null){
            ReviewCard(review = review.value!!, onClick = {
                navController.navigate("Show Review")
            })
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
        } else if (review.value == null){
            Button(
                onClick = { navController.navigate("Rate This Playing Court") },
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