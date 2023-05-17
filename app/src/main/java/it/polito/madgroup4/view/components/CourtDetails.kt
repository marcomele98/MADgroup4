package it.polito.madgroup4.view.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarConfig
import com.gowtham.ratingbar.RatingBarStyle
import com.gowtham.ratingbar.StepSize
import it.polito.madgroup4.model.PlayingCourt
import it.polito.madgroup4.model.Review
import it.polito.madgroup4.utility.floatEquals
import it.polito.madgroup4.utility.imageSelector
import java.lang.Math.abs

@Composable
fun CourtDetails(
    playingCourt: PlayingCourt,
    reviews: List<Review>,
    onClick: () -> Unit,
    //onClick2: (Int) -> Unit,
) {
    val avgVal: Float = reviews.flatMap {
        listOf(
            it.cleaningRating, it.serviceRating, it.structureRating
        )
    }.filter { kotlin.math.abs(it ?: 0f - 0f) > 0.0001f }
        .fold(mutableListOf(0f, 0f)) { sum, value ->
            sum[0] += 1f
            sum[1] += value!!
            sum
        }.reduce { sum, value -> if (floatEquals(sum, 0f)) 0f else value / sum }


    val numReview = reviews.size


    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = playingCourt.name!!, fontWeight = FontWeight.Bold, fontSize = 30.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageSelector(playingCourt.sport!!),
                contentDescription = "Court",
                modifier = Modifier.size(35.dp)
            )
        }
        Spacer(modifier = Modifier.height(15.dp))

        AvgReviews(numberOfStars = avgVal, numberOfReviews = numReview, onClick = onClick)

        Spacer(modifier = Modifier.height(30.dp))

        //TODO: lascio tutto sulla stessa riga? potrebbe essere troppo lunga
        Element(
            icon = Icons.Default.LocationOn,
            description = "Location",
            text = playingCourt.address + ", " + playingCourt.city + " (" + playingCourt.province + ")"
        )

        Element(
            icon = Icons.Default.Euro, description = "Price", text = playingCourt.price.toString()
        )

        Element(
            icon = Icons.Default.DateRange,
            text = "${playingCourt.openingTime} - ${playingCourt.closingTime}",
            description = "times"
        )

        if (playingCourt.phone != null)
            Element(
                icon = Icons.Default.Call,
                text = playingCourt.phone,
                description = "phone"
            )

        if (playingCourt.email != null)
            Element(
                icon = Icons.Default.Mail,
                text = playingCourt.email,
                description = "email"
            )

    }
    Spacer(modifier = Modifier.height(20.dp))
}


@Composable
fun Element(icon: ImageVector, text: String, description: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
    ) {
        Icon(icon, description)
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text, fontSize = 22.sp
        )
    }
    Spacer(modifier = Modifier.height(20.dp))
}


@Composable
fun AvgReviews(
    numberOfStars: Float, numberOfReviews: Int, onClick: () -> Unit
) {

    if (numberOfReviews != 0) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            RatingBar(value = numberOfStars,
                onValueChange = {},
                config = RatingBarConfig().style(RatingBarStyle.Normal)
                    .activeColor(MaterialTheme.colorScheme.primary)
                    .inactiveColor(MaterialTheme.colorScheme.surfaceVariant).stepSize(StepSize.HALF)
                    .numStars(5).size(30.dp).padding(0.dp),
                onRatingChanged = {})

            Text(
                text = "($numberOfReviews reviews)",
                modifier = Modifier.padding(start = 4.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
            )

            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "See All",
                modifier = Modifier
                    .clickable(onClick = onClick)
                    .padding(start = 8.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    } else {
        Text(
            text = "Still no reviews for this court yet",
            modifier = Modifier.padding(start = 4.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.primary
        )
    }
}