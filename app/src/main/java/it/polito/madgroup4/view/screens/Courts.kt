package it.polito.madgroup4.view.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.polito.madgroup4.model.Court
import it.polito.madgroup4.view.components.SportCardSelector
import it.polito.madgroup4.viewmodel.ReservationViewModel

@Composable
fun Courts(
    selectedSport: String,
    setShowedCourt: (Court) -> Unit,
    navController: NavController,
    courtVm: ReservationViewModel
) {

    val playingCourts = courtVm.allCourts.observeAsState(initial = emptyList())

    println(playingCourts.value)

    Column(
        Modifier.padding(horizontal = 16.dp)
    ) {
        Row(
            Modifier.padding(bottom = 16.dp)
        ) {
            SportCardSelector(
                sport = selectedSport,
                onClick = { navController.navigate("Select Sport") })
        }
        PlayingCourtList(
            playingCourts = playingCourts.value.filter { it.sport == selectedSport },
            onClick = { setShowedCourt(playingCourts.value[it]); navController.navigate("Playing Court Details") },
            messageIfNoCourts = "No courts available for this sport"
        )
    }
}