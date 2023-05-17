package it.polito.madgroup4.view.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarConfig
import com.gowtham.ratingbar.RatingBarStyle
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.model.Review
import it.polito.madgroup4.utility.formatDate
import it.polito.madgroup4.utility.imageSelector
import it.polito.madgroup4.viewmodel.ReviewViewModel
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewForm(
    reviewVm: ReviewViewModel,
    userId: Long,
    reservation: ReservationWithCourt,
    navController: NavController,
    review: Review = Review(
        courtId = reservation.playingCourt!!.id,
        userId = userId,
        title = "",
        serviceRating = 0f,
        structureRating = 0f,
        cleaningRating = 0f,
        text = "",
        date = formatDate(Date()),
        reservationId = reservation.reservation!!.id
    )
) {

    val (service, setService) = remember { mutableStateOf(0.toFloat()) }
    val (structure, setStructure) = remember { mutableStateOf(0.toFloat()) }
    val (cleaning, setCleaning) = remember { mutableStateOf(0.toFloat()) }
    var comment by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }

    Column(
        Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = reservation.playingCourt!!.name!!,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageSelector(reservation.playingCourt!!.sport!!),
                contentDescription = "Court",
                modifier = Modifier.size(35.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(
        ) {

            TextField(
                value = title,
                //supportingText = { Text(text = "Max 50 characters") },
                onValueChange = { title = it },
                modifier = Modifier
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Sentences
                ),
                singleLine = false,
                maxLines = 2,
                label = { Text(text = "Title") },
                placeholder = { Text(text = "Add a title") },
            )

            Spacer(modifier = Modifier.height(20.dp))

            CostumeRatingBar(text = "Structure", rating = structure, setRating = setStructure)

            Spacer(modifier = Modifier.height(20.dp))

            CostumeRatingBar(text = "Cleaning", rating = cleaning, setRating = setCleaning)

            Spacer(modifier = Modifier.height(20.dp))

            CostumeRatingBar(text = "Service", service, setService)

            Spacer(modifier = Modifier.height(20.dp))

            TextField(
                value = comment,
                /*supportingText = { Text(text = "Max 200 characters") },*/
                onValueChange = { comment = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Sentences
                ),
                singleLine = false,
                maxLines = 5,
                label = { Text(text = "Comments") },
                placeholder = { Text(text = "Add comments") },
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(
            contentColor = MaterialTheme.colorScheme.onPrimary
        ), onClick = {
            // I voti sono tutti opzionali (ma almeno uno deve esserci), nel caso uno non venga inserito non deve essere conteggiato nella media della review
            var numFields = 0

            if (cleaning != 0f) {
                review.cleaningRating = cleaning
                review.score += cleaning
                numFields++
            }
            if (service != 0f) {
                review.serviceRating = service
                review.score += service
                numFields++
            }
            if (structure != 0f) {
                review.structureRating = structure
                review.score += structure
                numFields++
            }
            if (comment.trim() != "")
                review.text = comment

            if (numFields != 0 && title.trim() != "") {
                review.title = title
                review.averageRating = review.score / numFields
                reviewVm.saveReview(review)
            } //TODO: else mostra un toast per notificare che non è stato inserito un titolo o un voto
            //setReview(review)
            navController.popBackStack()
        }, content = { Text("Submit Review") })
    }
}

@Composable
fun CostumeRatingBar(
    text: String,
    rating: Float,
    setRating: (Float) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.weight(1f), text = text, fontSize = 22.sp
        )
        Spacer(modifier = Modifier.width(10.dp))
        RatingBar(value = rating,
            config = RatingBarConfig().style(RatingBarStyle.Normal)
                .activeColor(MaterialTheme.colorScheme.primary)
                .inactiveColor(MaterialTheme.colorScheme.surfaceVariant)
                .numStars(5).size(35.dp).padding(6.dp),
            onValueChange = {
                setRating(it)
            },
            onRatingChanged = { setRating(it) })
    }
}



