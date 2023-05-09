package it.polito.madgroup4.view.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.gowtham.ratingbar.StepSize
import it.polito.madgroup4.model.PlayingCourt
import it.polito.madgroup4.model.Review
import it.polito.madgroup4.utility.formatDate
import it.polito.madgroup4.utility.imageSelector
import it.polito.madgroup4.viewmodel.ReviewViewModel
import it.polito.madgroup4.viewmodel.UserViewModel
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewForm(
    showedCourt: PlayingCourt,
    userId: Long,
    reviewVm: ReviewViewModel,
    navController: NavController,
    review: Review = Review(
        courtId = showedCourt.id,
        userId = userId,
        serviceRating = 0f,
        structureRating = 0f,
        cleaningRating = 0f,
        text = "",
        date = formatDate(Date()),
    )
) {
    var service by remember { mutableStateOf(0.toFloat()) }
    var structure by remember { mutableStateOf(0.toFloat()) }
    var cleaning by remember { mutableStateOf(0.toFloat()) }
    var comment by remember { mutableStateOf("") }

    Column(
        Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = showedCourt.name!!, fontWeight = FontWeight.Bold, fontSize = 30.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageSelector(showedCourt.sport!!),
                contentDescription = "Court",
                modifier = Modifier.size(35.dp)
            )

        }
        Spacer(modifier = Modifier.height(30.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.weight(1f), text = "Service", fontSize = 22.sp
            )
            Spacer(modifier = Modifier.width(10.dp))
            RatingBar(value = service,
                config = RatingBarConfig().style(RatingBarStyle.Normal)
                    .activeColor(MaterialTheme.colorScheme.primary)
                    .inactiveColor(MaterialTheme.colorScheme.surfaceVariant).stepSize(StepSize.HALF)
                    .numStars(5).size(35.dp).padding(6.dp),
                onValueChange = {
                    service = it
                },
                onRatingChanged = { service = it })
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.weight(1f), text = "Structure", fontSize = 22.sp
            )
            Spacer(modifier = Modifier.width(10.dp))
            RatingBar(value = structure,
                config = RatingBarConfig().style(RatingBarStyle.Normal)
                    .activeColor(MaterialTheme.colorScheme.primary)
                    .inactiveColor(MaterialTheme.colorScheme.surfaceVariant).stepSize(StepSize.HALF)
                    .numStars(5).size(35.dp).padding(6.dp),
                onValueChange = {
                    structure = it
                },
                onRatingChanged = { structure = it })
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.weight(1f), text = "Cleaning", fontSize = 22.sp
            )
            Spacer(modifier = Modifier.width(10.dp))
            RatingBar(value = cleaning,
                config = RatingBarConfig().style(RatingBarStyle.Normal)
                    .activeColor(MaterialTheme.colorScheme.primary)
                    .inactiveColor(MaterialTheme.colorScheme.surfaceVariant).stepSize(StepSize.HALF)
                    .numStars(5).size(35.dp).padding(6.dp),
                onValueChange = {
                    cleaning = it
                },
                onRatingChanged = { cleaning = it })
        }


        Spacer(modifier = Modifier.height(20.dp))


        Text(
            text = "Comments:",
            fontSize = 20.sp,
        )
        Spacer(modifier = Modifier.height(8.dp))

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
        )


        Spacer(modifier = Modifier.weight(1f))

        Button(modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(
            contentColor = MaterialTheme.colorScheme.onPrimary
        ), onClick = {
            review.cleaningRating = cleaning
            review.serviceRating = service
            review.structureRating = structure
            if (comment.trim() != "")
                review.text = comment
            reviewVm.saveReview(review)
            navController.popBackStack()
        }, content = { Text("Submit Review") })
    }
}


