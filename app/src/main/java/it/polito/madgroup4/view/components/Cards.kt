package it.polito.madgroup4.view.components

import android.graphics.Bitmap
import android.provider.ContactsContract.DisplayPhoto
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarConfig
import com.gowtham.ratingbar.RatingBarStyle
import com.gowtham.ratingbar.StepSize
import it.polito.madgroup4.R
import it.polito.madgroup4.model.Achievement
import it.polito.madgroup4.model.Court
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.model.Review
import it.polito.madgroup4.model.Sport
import it.polito.madgroup4.utility.calculateStartEndTime
import it.polito.madgroup4.utility.formatTimestampToString
import it.polito.madgroup4.utility.imageSelector


@Composable
fun ReservationCard(
    reservation: ReservationWithCourt,
    setReservation: (String) -> Unit,
    navController: NavController,
    nextRoute: String
) {

    val startEndTime = calculateStartEndTime(
        reservation.playingCourt!!.openingTime!!, reservation.reservation!!.slotNumber
    )

    ElevatedCard(modifier = Modifier
        .padding(bottom = 10.dp)
        .fillMaxWidth()
        .clickable {
            setReservation(reservation.reservation.id!!)
            navController.navigate(nextRoute)
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
                    fontSize = 20.sp
                )
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )

                val status = reservation.reservation.reservationInfo?.status

                if (status != "Confirmed") {
                    Spacer(modifier = Modifier.height(10.dp))

                    status?.let {
                        Text(
                            text = it,
                            fontSize = 18.sp,
                            color = if (status == "Invited") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }


            if (reservation?.reservation?.reservationInfo?.suggestedLevel != null && reservation.reservation.reservationInfo?.public == true) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = reservation?.reservation?.reservationInfo?.suggestedLevel!!,
                    fontSize = 18.sp
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
    playingCourt: Court, onClick: () -> Unit, enabled: Boolean = true, photo: Bitmap? = null
) {

    ElevatedCard(modifier = Modifier
        .padding(bottom = 10.dp)
        .fillMaxWidth()
        .clickable(enabled) {
            onClick()
        }) {


        if (photo != null) {
            Image(
                bitmap = photo!!.asImageBitmap(),
                contentDescription = "Court",
                Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .align(Alignment.CenterHorizontally)
                    .fillMaxSize()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageSelector(playingCourt.sport!!), contentDescription = "Court"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = playingCourt.name!!, fontWeight = FontWeight.Bold, fontSize = 20.sp
                )
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (playingCourt.reviewNumber != null && playingCourt.reviewNumber!! > 0) {
                Evaluation(
                    stars = playingCourt.review ?: 0f,
                    label = "",
                    reviews = playingCourt.reviewNumber!!
                )
            }


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
    sport: String, onClick: () -> Unit
) {

    OutlinedCard(modifier = Modifier
        .padding(bottom = 10.dp)
        .clickable {
            onClick()
        }) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageSelector(sport), contentDescription = "Sport"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = sport, fontWeight = FontWeight.Bold, fontSize = 20.sp
                )
                Spacer(
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Outlined.KeyboardArrowDown, contentDescription = "DownArrow"
                )
            }
        }
    }

}

@Composable
fun LevelCardSelector(
    level: String, onClick: () -> Unit
) {

    OutlinedCard(modifier = Modifier
        .padding(bottom = 10.dp)
        .clickable {
            onClick()
        }) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = level, fontWeight = FontWeight.Bold, fontSize = 20.sp
                )
                Spacer(
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Outlined.KeyboardArrowDown, contentDescription = "DownArrow"
                )
            }
        }
    }

}

@Composable
fun ReviewCard(
    review: Review, showNickname: Boolean
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
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = review.title, fontWeight = FontWeight.Bold, fontSize = 20.sp
                )
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
            if (showNickname) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "@" + review.userId,
                        fontSize = 16.sp,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

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
                    text = "\"${review.text!!}\"", fontSize = 18.sp, fontStyle = FontStyle.Italic
                )
            }

        }
    }
}

@Composable
fun Evaluation(
    stars: Float, label: String, reviews: Int? = null
) {

    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
    ) {
        if (label != "") {
            Text(
                text = label,
                modifier = Modifier.width(80.dp),
                fontSize = 18.sp,
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        RatingBar(value = stars,
            onValueChange = {},
            config = RatingBarConfig().style(RatingBarStyle.Normal)
                .activeColor(MaterialTheme.colorScheme.primary)
                .inactiveColor(MaterialTheme.colorScheme.surfaceVariant).stepSize(StepSize.HALF)
                .numStars(5).size(20.dp).padding(0.dp),
            onRatingChanged = {})
        if (reviews != null) {
            Text(
                text = "($reviews reviews)",
                modifier = Modifier.padding(start = 4.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Light,
            )
        }
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
                    text = sport.name, fontWeight = FontWeight.Bold, fontSize = 20.sp
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
                text = if (sport.achievements.isEmpty()) "No achievements reached"
                else "Achievements reached: ${sport.achievements.size}",
                fontSize = 18.sp,
            )
        }
    }
}

@Composable
fun AchievementCard(
    achievement: Achievement,
    onDelete: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .padding(top = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 16.dp, end = 4.dp)
            ) {
                Text(
                    text = achievement.title!!, fontWeight = FontWeight.Bold, fontSize = 20.sp
                )
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                )
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Outlined.Delete, contentDescription = null)
                }
            }
            Text(
                text = formatTimestampToString(achievement?.date!!),
                fontSize = 18.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            if (achievement.description?.trim() ?: "" != "") {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "\"${achievement.description!!}\"",
                    fontSize = 18.sp,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

        }
    }
}