package it.polito.madgroup4.view.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import it.polito.madgroup4.model.PlayingCourt
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.utility.calculateStartEndTime
import it.polito.madgroup4.utility.formatDate
import it.polito.madgroup4.view.components.CourtDetails
import it.polito.madgroup4.view.components.ReservationDetails
import it.polito.madgroup4.viewmodel.ReservationViewModel
import java.time.LocalTime
import java.util.Date


@ExperimentalMaterial3Api
@Composable
fun ShowCourt(
    playingCourt: PlayingCourt,
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        CourtDetails(
            playingCourt,
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}