package it.polito.madgroup4.view.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.polito.madgroup4.model.PlayingCourt
import it.polito.madgroup4.view.components.SportCard
import it.polito.madgroup4.viewmodel.ReservationViewModel

@Composable
fun Courts(
    navController: NavController,
    vm: ReservationViewModel,
    selectedSport: String,
    setShowedCourt: (PlayingCourt) -> Unit,
) {

    vm.getAllPlayingCourtBySport(selectedSport)

    val playingCourts = vm.allCourtsBySport.observeAsState(initial = emptyList())

    Column(
        Modifier.padding(horizontal = 16.dp)
    ) {
        Row(
            Modifier.padding(bottom = 16.dp)
        ) {
            SportCard(sport = selectedSport, navController = navController)
        }
        PlayingCourtList(
            playingCourts = playingCourts.value,
            onClick = { setShowedCourt(playingCourts.value[it]); navController.navigate("Playing Court Details") })
    }

}