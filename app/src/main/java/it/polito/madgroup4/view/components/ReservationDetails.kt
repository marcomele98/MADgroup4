package it.polito.madgroup4.view.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.madgroup4.model.Court
import it.polito.madgroup4.model.ReservationInfo
import it.polito.madgroup4.utility.calculateStartEndTime
import it.polito.madgroup4.utility.imageSelector
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun ReservationDetails(
    playingCourt: Court,
    reservationDate: Date,
    reservationTimeSlot: Int,
    price: Double,
    reservationInfo: ReservationInfo? = null,
    selectedLevel: String? = null,
) {

    val startEndTime = calculateStartEndTime(
        playingCourt.openingTime!!,
        reservationTimeSlot
    )

    val formatter = SimpleDateFormat("dd/MM/yyyy")

    LaunchedEffect(Unit) {
        println("${reservationInfo}}")
    }

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

        ReservationElement(
            icon = Icons.Default.LocationOn,
            text = playingCourt.address + ", " + playingCourt.city + " (" + playingCourt.province + ")",
            description = "Location"
        )

        Spacer(modifier = Modifier.height(20.dp))

        ReservationElement(
            icon = Icons.Default.Euro,
            text = price.toString(),
            description = "Price"
        )

        Spacer(modifier = Modifier.height(20.dp))

        ReservationElement(
            icon = Icons.Default.DateRange,
            text = formatter.format(reservationDate),
            description = "DateRange"
        )

        Spacer(modifier = Modifier.height(20.dp))

        ReservationElement(
            icon = Icons.Default.Schedule,
            text = startEndTime, description = "Schedule"
        )


        if (reservationInfo?.public == true) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Public match details",
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontStyle = FontStyle.Italic
            )
            Spacer(modifier = Modifier.height(8.dp))


            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Available outsider places: ",
                    fontSize = 22.sp,

                    )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${reservationInfo.totalAvailable}",
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Suggested Level: ",
                    fontSize = 22.sp,

                    )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${selectedLevel ?: reservationInfo.suggestedLevel}",
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                )
            }

        }
    }
}


@Composable
fun ReservationElement(
    icon: ImageVector, text: String, description: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(icon, contentDescription = description)
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            fontSize = 22.sp
        )
    }
}
