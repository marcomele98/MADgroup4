package it.polito.madgroup4.view.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarConfig
import com.gowtham.ratingbar.RatingBarStyle
import com.gowtham.ratingbar.StepSize
import it.polito.madgroup4.model.Review
import it.polito.madgroup4.viewmodel.ReviewViewModel

@Composable
fun ReviewDetails(
    showedReview: Review,
    reviewVm: ReviewViewModel,
    navController: NavController,
) {

    val openDialog = remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {

        if (openDialog.value) {
            AlertDialog(onDismissRequest = {
                openDialog.value = false
            }, confirmButton = {
                TextButton(onClick = {
                    reviewVm.deleteReview(showedReview)
                    openDialog.value = false
                    navController.navigate("Reservations")
                }) {
                    Text("Delete")
                }
            }, dismissButton = {
                TextButton(onClick = {
                    openDialog.value = false
                }) {
                    Text("Cancel")
                }
            }, title = {
                Text("Delete Review")
            }, text = {
                Text(
                    text = "Are you sure you want to delete your review?",
                )
            }, properties = DialogProperties(
                dismissOnBackPress = true, dismissOnClickOutside = true
            )
            )
        }

        Text(
            text = "Title: " + showedReview.title,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.ManageAccounts, contentDescription = "Service")
            Spacer(modifier = Modifier.width(10.dp))
            RatingBar(value = showedReview.serviceRating ?: 0f,
                onValueChange = {},
                config = RatingBarConfig().style(RatingBarStyle.Normal)
                    .activeColor(MaterialTheme.colorScheme.primary)
                    .inactiveColor(MaterialTheme.colorScheme.surfaceVariant).stepSize(StepSize.HALF)
                    .numStars(5).size(35.dp).padding(6.dp),
                onRatingChanged = {})
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Apartment, contentDescription = "Structure")
            Spacer(modifier = Modifier.width(10.dp))
            RatingBar(value = showedReview.structureRating ?: 0f,
                onValueChange = {},
                config = RatingBarConfig().style(RatingBarStyle.Normal)
                    .activeColor(MaterialTheme.colorScheme.primary)
                    .inactiveColor(MaterialTheme.colorScheme.surfaceVariant).stepSize(StepSize.HALF)
                    .numStars(5).size(35.dp).padding(6.dp),
                onRatingChanged = {})
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.CleaningServices, contentDescription = "Cleaning")
            Spacer(modifier = Modifier.width(10.dp))
            RatingBar(value = showedReview.cleaningRating ?: 0f,
                onValueChange = {},
                config = RatingBarConfig().style(RatingBarStyle.Normal)
                    .activeColor(MaterialTheme.colorScheme.primary)
                    .inactiveColor(MaterialTheme.colorScheme.surfaceVariant).stepSize(StepSize.HALF)
                    .numStars(5).size(35.dp).padding(6.dp),
                onRatingChanged = {})
        }

        if (showedReview.text != "") {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Notes: " + showedReview.text,
                fontSize = 20.sp,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        //TODO: o la levo o controllo che l'utente sia l'host
        Button(
            onClick = {
                openDialog.value = !openDialog.value
            }, colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            ), modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Delete")
        }

    }
}