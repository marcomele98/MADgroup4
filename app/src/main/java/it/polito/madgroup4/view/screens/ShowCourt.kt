package it.polito.madgroup4.view.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.polito.madgroup4.model.Court
import it.polito.madgroup4.model.Review
import it.polito.madgroup4.view.components.CourtDetails
import it.polito.madgroup4.viewmodel.ReservationViewModel


@ExperimentalMaterial3Api
@Composable
fun ShowCourt(
    reservationVm: ReservationViewModel,
    playingCourt: Court,
    setReviews: (List<Review>) -> Unit,
    navController: NavController,
) {

    reservationVm.getAllReviewsByCourtName(playingCourt.name!!)

    val reviews = reservationVm.reviews.observeAsState(initial = emptyList())

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
    ) {
        CourtDetails(
            playingCourt,
            reviews = reviews.value,
            onClick = { setReviews(reviews.value); navController.navigate("Reviews") }
        )
    }
}