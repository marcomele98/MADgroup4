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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.view.components.ReservationDetails
import it.polito.madgroup4.viewmodel.ReservationViewModel


@ExperimentalMaterial3Api
@Composable
fun ShowReservation(
    reservation: ReservationWithCourt, vm: ReservationViewModel, navController: NavController
) {
    val openDialog = remember { mutableStateOf(false) }
    vm.getSlotsByCourtIdAndDate(
        reservation.playingCourt!!.id, reservation.reservation!!.date
    )
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
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
            reservation.reservation.slotNumber
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                openDialog.value = !openDialog.value
            }, colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            ), modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Delete")
        }
    }
}