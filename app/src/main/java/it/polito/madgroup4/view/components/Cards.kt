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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarConfig
import com.gowtham.ratingbar.RatingBarStyle
import com.gowtham.ratingbar.StepSize
import it.polito.madgroup4.model.Achievement
import it.polito.madgroup4.model.PlayingCourt
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.model.Review
import it.polito.madgroup4.model.Sport
import it.polito.madgroup4.utility.calculateStartEndTime
import it.polito.madgroup4.utility.formatTimestampToString
import it.polito.madgroup4.utility.imageSelector


@Composable
fun ReservationCard(
    reservation: ReservationWithCourt,
    setReservation: (ReservationWithCourt) -> Unit,
    navController: NavController
) {

    val startEndTime = calculateStartEndTime(
        reservation.playingCourt!!.openingTime!!, reservation.reservation!!.slotNumber
    )

    ElevatedCard(modifier = Modifier
        .padding(bottom = 10.dp)
        .fillMaxWidth()
        .clickable {
            setReservation(reservation)
            navController.navigate("Reservation Details")
        }) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
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
                text = reservation.playingCourt.name!!, fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = startEndTime, fontSize = 18.sp
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

    ElevatedCard(modifier = Modifier
        .padding(bottom = 10.dp)
        .fillMaxWidth()
        .clickable(enabled) {
            onClick()
        }) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageSelector(playingCourt.sport!!), contentDescription = "Reservations"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = playingCourt.name!!, fontWeight = FontWeight.Bold, fontSize = 22.sp
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
                text = playingCourt.price.toString() + " €/h", fontSize = 18.sp
            )

        }

    }
}


@Composable
fun SportCardSelector(
    sport: String,
    navController: NavController,
) {

    OutlinedCard(modifier = Modifier
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
                    imageSelector(sport), contentDescription = "Reservations"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = sport, fontWeight = FontWeight.Bold, fontSize = 22.sp
                )
                Spacer(
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

}

@Composable
fun ReviewCard(
    review: Review,
) {

    ElevatedCard(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth()
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

            //TODO: aggiungi l'autore della recensione

            if (review.structureRating ?: 0f > 0f) {
                Evaluation(stars = review.structureRating ?: 0f, label = "structure ")
            }
            if (review.cleaningRating ?: 0f > 0f) {
                Evaluation(stars = review.cleaningRating ?: 0f, label = "cleaning ")
            }
            if (review.serviceRating ?: 0f > 0f) {
                Evaluation(stars = review.serviceRating ?: 0f, label = "service ")
            }
            Spacer(modifier = Modifier.height(5.dp))
            if (review.text?.trim() ?: "" != "") {
                Text(
                    text = "\"${review.text!!}\"",
                    fontSize = 18.sp,
                    fontStyle = FontStyle.Italic
                )
            }

        }
    }
}

@Composable
fun Evaluation(
    stars: Float,
    label: String
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            modifier = Modifier.width(80.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))

        RatingBar(value = stars,
            onValueChange = {},
            config = RatingBarConfig().style(RatingBarStyle.Normal)
                .activeColor(MaterialTheme.colorScheme.primary)
                .inactiveColor(MaterialTheme.colorScheme.surfaceVariant)
                .stepSize(StepSize.HALF).numStars(5).size(20.dp).padding(0.dp),
            onRatingChanged = {})
    }

    Spacer(modifier = Modifier.height(8.dp))

}

@Composable
fun SportCard(sport: Sport, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageSelector(sport.name!!), contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = sport.name, fontWeight = FontWeight.Bold, fontSize = 22.sp
                )
                Spacer(
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = sport.level!!,
                fontSize = 18.sp,
                fontStyle = FontStyle.Italic,
                //color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text =
                if (sport.achievements.isEmpty())
                    "No achievements reached"
                else
                    "Achievements reached: ${sport.achievements.size}",
                fontSize = 18.sp,
            )
        }
    }
}

@Composable
fun AchievementCard(
    achievement: Achievement,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = achievement.title!!,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = formatTimestampToString(achievement?.date!!),
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(5.dp))
            if (achievement.description?.trim() ?: "" != "") {
                Text(
                    text = "\"${achievement.description!!}\"",
                    fontSize = 18.sp,
                    fontStyle = FontStyle.Italic
                )
            }

        }
    }
}