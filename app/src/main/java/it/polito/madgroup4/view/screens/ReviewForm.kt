package it.polito.madgroup4.view.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
import it.polito.madgroup4.utility.formatDateToTimestamp
import it.polito.madgroup4.utility.imageSelector
import it.polito.madgroup4.viewmodel.LoadingStateViewModel
import it.polito.madgroup4.viewmodel.ReservationViewModel
import it.polito.madgroup4.viewmodel.Status
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ReviewForm(
    reservationVm: ReservationViewModel,
    navController: NavController,
    loadingVm: LoadingStateViewModel,
    setTopBarAction: (() -> Unit) -> Unit,
    reservationId: String,
    reservations: State<List<ReservationWithCourt>?>,
    nickname: String
) {

    val (service, setService) = remember { mutableStateOf(0.toFloat()) }
    val (structure, setStructure) = remember { mutableStateOf(0.toFloat()) }
    val (cleaning, setCleaning) = remember { mutableStateOf(0.toFloat()) }
    var comment by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    val reservation = reservations.value?.find { it.reservation?.id == reservationId }


    val review: Review = Review(
        courtName = reservation?.playingCourt!!.name!!,
        userId = nickname,
        title = "",
        serviceRating = 0f,
        structureRating = 0f,
        cleaningRating = 0f,
        text = "",
        date = formatDateToTimestamp(Date()),
    )


    LaunchedEffect(service, structure, cleaning, comment, title) {
        setTopBarAction {
            // I voti sono tutti opzionali (ma almeno uno deve esserci), nel caso uno non venga inserito non deve essere conteggiato nella media della review
            keyboardController?.hide()
            val atLeastOnRating = structure != 0f || cleaning != 0f || service != 0f
            if (comment.trim() != "")
                review.text = comment

            review.serviceRating = service
            review.structureRating = structure
            review.cleaningRating = cleaning


            if (title.trim() == "") {
                loadingVm.setStatus(Status.Error("You have to insert a title", null))
            } else if (!atLeastOnRating) {
                loadingVm.setStatus(Status.Error("You have to insert at least one rating", null))
            } else {
                review.title = title
                reservation.reservation?.review = review
                loadingVm.setStatus(Status.Loading)
                reservationVm.saveReservation(
                    reservation.reservation!!,
                    loadingVm,
                    "Review saved successfully",
                    "Error while saving review",
                    "Reservation Details"
                )
                navController.popBackStack()
            }
            //setReview(review)
        }
    }

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

            OutlinedTextField(
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

            Spacer(modifier = Modifier.height(25.dp))

            CostumeRatingBar(text = "Structure", rating = structure, setRating = setStructure)

            Spacer(modifier = Modifier.height(15.dp))

            CostumeRatingBar(text = "Cleaning", rating = cleaning, setRating = setCleaning)

            Spacer(modifier = Modifier.height(15.dp))

            CostumeRatingBar(text = "Service", service, setService)

            Spacer(modifier = Modifier.height(25.dp))

            OutlinedTextField(
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
            modifier = Modifier.weight(1f),
            text = text,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (rating == 0f) 0.5f else 1f),
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.width(10.dp))
        RatingBar(value = rating,
            config = RatingBarConfig().style(RatingBarStyle.Normal)
                .activeColor(MaterialTheme.colorScheme.primary)
                .inactiveColor(MaterialTheme.colorScheme.surfaceVariant)
                .numStars(5).size(35.dp).padding(0.dp),
            onValueChange = {
                setRating(it)
            },
            onRatingChanged = { setRating(it) })
    }
}



