package it.polito.madgroup4.view.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.madgroup4.model.PlayingCourt
import it.polito.madgroup4.model.Reservation
import it.polito.madgroup4.utility.CourtWithSlots
import it.polito.madgroup4.utility.calculateStartEndTime
import it.polito.madgroup4.utility.imageSelector
import it.polito.madgroup4.view.components.PlayingCourtCard
import it.polito.madgroup4.viewmodel.ReservationViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date

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

    val startEndTime = calculateStartEndTime(
        playingCourt.playingCourt!!.openingTime,
        reservationTimeSlot
    )


    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageSelector(playingCourt.playingCourt!!.sport),
                contentDescription = "Reservations"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = playingCourt.playingCourt!!.name,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
            Spacer(
                modifier = Modifier
                    .weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = playingCourt.playingCourt!!.address + ", " + playingCourt.playingCourt!!.city + " (" + playingCourt.playingCourt!!.province + ")",
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = playingCourt.playingCourt!!.price.toString() + " €/h",
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = formatter.format(java.sql.Date.valueOf(reservationDate.toString())),
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = startEndTime,
            fontSize = 18.sp
        )

        Button(
            onClick = {
                vm.saveReservation(reservation)
                navController.navigate("Reservations")
            },
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(text = "Save")
        }

    }


}