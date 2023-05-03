package it.polito.madgroup4.view.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.view.components.ReservationDetails
import it.polito.madgroup4.viewmodel.ReservationViewModel

@Composable
fun ShowReservation(
    reservation: ReservationWithCourt,
    vm: ReservationViewModel,
    navController: NavController
) {
    vm.getSlotsByCourtIdAndDate(
        reservation.playingCourt!!.id,
        reservation.reservation!!.date
    )
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        ReservationDetails(reservation.playingCourt, reservation.reservation.date, reservation.reservation.slotNumber)
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                vm.deleteReservation(reservation.reservation!!)
                navController.navigate("Reservations")
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Delete")
        }

    }
}

