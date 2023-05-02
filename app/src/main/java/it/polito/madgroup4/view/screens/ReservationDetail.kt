package it.polito.madgroup4.view.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.viewmodel.ReservationViewModel

@Composable
public fun ReservationDetail(
    reservation: ReservationWithCourt,
    vm: ReservationViewModel,
    navController: NavController
) {
    vm.getSlotsByCourtIdAndDate(
        reservation.playingCourt!!.id,
        reservation.reservation!!.date
    )
    Box(Modifier.fillMaxSize()) {
        Text(
            reservation.playingCourt!!.name,
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.headlineLarge
        )
        Button(
            onClick = {
                navController.navigate("EditReservation")
            },
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(text = "Edit")
        }

        Button(
            onClick = {
                vm.deleteReservation(reservation.reservation!!)
                navController.navigate("Reservations")
            },
            modifier = Modifier.padding(100.dp)
        ) {
            Text(text = "Delete")
        }

    }
}

