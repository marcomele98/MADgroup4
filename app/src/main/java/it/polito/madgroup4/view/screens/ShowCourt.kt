package it.polito.madgroup4.view.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.polito.madgroup4.model.PlayingCourt
import it.polito.madgroup4.model.Review
import it.polito.madgroup4.view.components.CourtDetails
import it.polito.madgroup4.viewmodel.ReviewViewModel


@ExperimentalMaterial3Api
@Composable
fun ShowCourt(
    playingCourt: PlayingCourt,
    navController: NavController,
    reviewVm: ReviewViewModel,
    setReviews: (List<Review>) -> Unit,
) {
    reviewVm.getAllReviewsByCourtId(playingCourt.id!!)

    val reviews = reviewVm.reviews.observeAsState(initial = emptyList())

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
    ) {
        CourtDetails(
            playingCourt,
            onClick = { setReviews(reviews.value); navController.navigate("Reviews") }
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = { navController.navigate("Rate This Playing Court") },
            modifier = Modifier
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(text = "Rate This Playing Court")
        }
    }
}