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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Reviews
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarConfig
import com.gowtham.ratingbar.RatingBarStyle
import com.gowtham.ratingbar.StepSize
import it.polito.madgroup4.model.PlayingCourt
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.model.Review
import it.polito.madgroup4.utility.calculateStartEndTime
import it.polito.madgroup4.utility.imageSelector


@Composable
fun ReservationCard(
    reservation: ReservationWithCourt,
    navController: NavController,
    setReservation: (ReservationWithCourt) -> Unit
) {
    val startEndTime = calculateStartEndTime(
        reservation.playingCourt!!.openingTime!!,
        reservation.reservation!!.slotNumber
    )
    ElevatedCard(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth()
            .clickable {
                setReservation(reservation)
                navController.navigate("Reservation Details")
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
                    imageSelector(reservation.playingCourt.sport!!),
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
                text = reservation.playingCourt.name!!,
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
    playingCourt: PlayingCourt,
    onClick: () -> Unit,
    enabled: Boolean = true
) {

    ElevatedCard(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth()
            .clickable(enabled) {
                onClick()
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
                    imageSelector(playingCourt.sport!!),
                    contentDescription = "Reservations"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = playingCourt.name!!,
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
                text = playingCourt.address + ", " + playingCourt.city + " (" + playingCourt.province + ")",
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = playingCourt.price.toString() + " €/h",
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

@Composable
fun ReviewCard(
    review: Review,
    onClick: () -> Unit,
) {

    ElevatedCard(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth()
            .clickable {
                onClick()
            }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = review.title,
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

            Row() {
                Icon(Icons.Default.Reviews, contentDescription = "Review")
                Spacer(modifier = Modifier.width(10.dp))
                RatingBar(value = review.averageRating?:0f,
                    onValueChange = {},
                    config = RatingBarConfig().style(RatingBarStyle.Normal)
                        .activeColor(MaterialTheme.colorScheme.primary)
                        .inactiveColor(MaterialTheme.colorScheme.surfaceVariant).stepSize(StepSize.HALF)
                        .numStars(5).size(35.dp).padding(6.dp),
                    onRatingChanged = {})
            }
        }
    }
}
