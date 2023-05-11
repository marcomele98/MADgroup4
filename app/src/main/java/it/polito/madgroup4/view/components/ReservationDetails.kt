package it.polito.madgroup4.view.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.madgroup4.model.PlayingCourt
import it.polito.madgroup4.utility.calculateStartEndTime
import it.polito.madgroup4.utility.imageSelector
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun ReservationDetails(
    playingCourt: PlayingCourt,
    reservationDate: Date,
    reservationTimeSlot: Int,
    particularRequests: String? = null
) {

    val startEndTime = calculateStartEndTime(
        playingCourt.openingTime!!,
        reservationTimeSlot
    )

    val formatter = SimpleDateFormat("dd/MM/yyyy")

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = playingCourt.name!!,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageSelector(playingCourt.sport!!),
                contentDescription = "Reservations",
                modifier = Modifier
                    .size(35.dp)
            )
        }
        Spacer(modifier = Modifier.height(30.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = "Location")
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = playingCourt.address + ", " + playingCourt.city + " (" + playingCourt.province + ")",
                fontSize = 22.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Euro, contentDescription = "Location")
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = playingCourt.price.toString(),
                fontSize = 22.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.DateRange, contentDescription = "Location")
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = formatter.format(reservationDate),
                fontSize = 22.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Schedule, contentDescription = "Location")
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = startEndTime,
                fontSize = 22.sp
            )
        }
        if (particularRequests != null && particularRequests.trim() != "") {
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Particular requests:", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Card {
                Text(
                    text = particularRequests!!,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(10.dp)
                )
            }
        }
    }
}
