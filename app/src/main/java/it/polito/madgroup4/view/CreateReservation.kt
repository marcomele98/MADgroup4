package it.polito.madgroup4.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.boguszpawlowski.composecalendar.SelectableWeekCalendar
import io.github.boguszpawlowski.composecalendar.rememberSelectableWeekCalendarState
import it.polito.madgroup4.R
import it.polito.madgroup4.model.CourtWithReservations
import it.polito.madgroup4.model.PlayingCourt
import it.polito.madgroup4.utility.ImageSelector
import it.polito.madgroup4.viewmodel.ReservationViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@Composable
fun CreateReservation(vm: ReservationViewModel, navController: NavController) {
    val calendarState = rememberSelectableWeekCalendarState()
    val sports = arrayOf("Tennis", "Football")
    var selectedSport by remember { mutableStateOf(sports[0]) }

    val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.getDefault())
    val date = if (calendarState.selectionState.selection.isEmpty()) {
        LocalDate.now()
    } else {
        calendarState.selectionState.selection[0]
    }

    Column(
        Modifier
            .padding(horizontal = 16.dp)
    ) {
        Row(
            Modifier
                .padding(vertical = 16.dp)
        ) {
            SportSelector(
                sports = sports,
                selectedSport = selectedSport,
                onSportSelected = { selectedSport = it })
        }
        SelectableWeekCalendar(calendarState = calendarState)
        PlayingCourtList(
            date = date.toString(),
            sport = selectedSport,
            vm = vm,
            navController = navController
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SportSelector(sports: Array<String>, selectedSport: String, onSportSelected: (String) -> Unit) {

    var expanded by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {

            TextField(
                value = selectedSport,
                leadingIcon = {
                    Image(
                        modifier = Modifier
                            .size(size = 20.dp),
                        painter = ImageSelector(sport = selectedSport),
                        contentDescription = "sport",
                        contentScale = ContentScale.Crop
                    )
                },
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                sports.forEach { item ->
                    DropdownMenuItem(
                        leadingIcon = {
                            Image(
                                modifier = Modifier
                                    .size(size = 20.dp),
                                painter = if (item == "Tennis") {
                                    painterResource(id = R.drawable.baseline_sports_tennis_24)
                                } else {
                                    painterResource(id = R.drawable.baseline_sports_soccer_24)
                                },
                                contentDescription = "sport",
                                contentScale = ContentScale.Crop
                            )
                        },
                        text = { Text(text = item) },
                        onClick = {
                            onSportSelected(item)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PlayingCourtCard(
    date: String,
    vm: ReservationViewModel,
    playingCourt: CourtWithReservations,
    navController: NavController
) {

    var image = ImageSelector(playingCourt.court.sport)


    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = 4.dp,
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clickable {

            }
            .fillMaxWidth()
    ) {
        Row() {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {

                Row() {
                    Image(
                        modifier = Modifier
                            .size(size = 60.dp),
                        painter = image,
                        contentDescription = "sport",
                        contentScale = ContentScale.Crop
                    )
                    Column() {
                        Text(
                            text = playingCourt.court.name,
                            style = MaterialTheme.typography.h6,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = playingCourt.court.address + ", " + playingCourt.court.city + " (" + playingCourt.court.province + ")",
                            style = MaterialTheme.typography.h6,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = playingCourt.court.price.toString() + "€/h",
                            style = MaterialTheme.typography.h6,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                }
            }
        }
    }
}


@Composable
fun PlayingCourtList(
    date: String,
    sport: String,
    vm: ReservationViewModel,
    navController: NavController
) {

    val formatter = SimpleDateFormat("dd/MM/yyyy")

    vm.getAllPlayingCourtsBySportAndDate(
        formatter.parse(formatter.format(java.sql.Date.valueOf(date))),
        sport
    )
    val playingCourts = vm.playingCourts.observeAsState(initial = emptyList())

/*
    playingCourts.value.forEach {
        it.reservations.map { reservation -> reservation.slotNumber }
    }*/


    LazyColumn(Modifier.fillMaxSize()) {
        items(playingCourts.value.size) { index ->
            PlayingCourtCard(date, vm, playingCourts.value[index], navController)
        }
    }
}




