package it.polito.madgroup4.view.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.utility.CourtWithSlots
import it.polito.madgroup4.utility.calculateStartEndTime
import it.polito.madgroup4.utility.imageSelector


@Composable
fun ReservationCard(
    reservation: ReservationWithCourt,
    navController: NavController,
    setReservation: (ReservationWithCourt) -> Unit
) {
    val startEndTime = calculateStartEndTime(
        reservation.playingCourt!!.openingTime,
        reservation.reservation!!.slotNumber
    )
    ElevatedCard(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth()
            .clickable {
                setReservation(reservation);
                navController.navigate("ReservationDetails")
            }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageSelector(reservation.playingCourt!!.sport),
                    contentDescription = "Reservations"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = reservation.playingCourt.sport,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${reservation.playingCourt?.name}",
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))


            Text(
                text = startEndTime,
                fontSize = 18.sp
            )
        }
    }
}


@Composable
fun PlayingCourtCard(
    playingCourt: CourtWithSlots,
    onClick: (CourtWithSlots) -> Unit,
    enabled: Boolean = true
) {

    ElevatedCard(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth()
            .clickable(enabled) {
                onClick(playingCourt);
            }
    ) {
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
                        .fillMaxHeight()
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
        }
    }
}


@Composable
fun SportCard(
    sport: String,
    navController: NavController,
) {
    OutlinedCard(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .clickable {
                navController.navigate("Select Sport")
            }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageSelector(sport),
                    contentDescription = "Reservations"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = sport,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                )
            }
        }
    }
}