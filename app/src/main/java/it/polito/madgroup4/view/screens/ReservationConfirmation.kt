package it.polito.madgroup4.view.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.polito.madgroup4.model.Reservation
import it.polito.madgroup4.utility.CourtWithSlots
import it.polito.madgroup4.utility.calculateStartEndTime
import it.polito.madgroup4.view.components.ReservationDetails
import it.polito.madgroup4.viewmodel.ReservationViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate

@Composable
fun ReservationConfirmation(
    playingCourt: CourtWithSlots,
    reservationDate: LocalDate,
    reservationTimeSlot: Int,
    vm: ReservationViewModel,
    navController: NavController
) {

    val formatter = SimpleDateFormat("dd/MM/yyyy")
    val reservation = Reservation(
        courtId = playingCourt.playingCourt!!.id,
        slotNumber = reservationTimeSlot,
        date = formatter.parse(formatter.format(java.sql.Date.valueOf(reservationDate.toString())))
    )


    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        ReservationDetails(
            playingCourt = playingCourt,
            reservationDate = reservation.date!!,
            reservationTimeSlot = reservationTimeSlot
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                vm.saveReservation(reservation)
                navController.navigate("Reservations")
            },
            modifier = Modifier
                .padding(start = 8.dp)
                .fillMaxWidth()
        ) {
            Text(text = "Confirm")
        }


    }


}