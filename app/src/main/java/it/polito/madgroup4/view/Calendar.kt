package it.polito.madgroup4.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.github.boguszpawlowski.composecalendar.SelectableCalendar
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.R
import it.polito.madgroup4.utility.calculateAvailableSlot
import it.polito.madgroup4.viewmodel.ReservationViewModel
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalDate
import it.polito.madgroup4.utility.calculateStartEndTime

@Composable
fun SelectableCalendarSample(
    vm: ReservationViewModel,
    navController: NavController,
    setReservation: (ReservationWithCourt) -> Unit
) {
    val calendarState = rememberSelectableCalendarState()
    Column(

    ) {
        SelectableCalendar(calendarState = calendarState)
        Spacer(modifier = Modifier.height(16.dp))
        val date = if (calendarState.selectionState.selection.isEmpty()) {
            LocalDate.now().toString()
        } else {
            calendarState.selectionState.selection[0].toString()
        }
        ReservationList(date = date, vm = vm, navController, setReservation)

    }
}

@Composable
fun ReservationList(
    date: String,
    vm: ReservationViewModel,
    navController: NavController,
    setReservation: (ReservationWithCourt) -> Unit
) {

    val formatter = SimpleDateFormat(
        "dd/MM/yyyy"
    )

    vm.getReservationsByDate(formatter.parse(formatter.format(Date.valueOf(date))))
    val reservations = vm.reservations.observeAsState(initial = emptyList())

    println(reservations.value)

    LazyColumn(Modifier.fillMaxSize()) {
        items(reservations.value.size) { index ->
            ReservationCard(reservations.value[index], navController, setReservation)
            Column {
            }
        }
    }
}

@Composable
fun ReservationCard(
    reservation: ReservationWithCourt,
    navController: NavController,
    setReservation: (ReservationWithCourt) -> Unit,
) {

    var paint = painterResource(id = R.drawable.baseline_sports_soccer_24)

    if (reservation.playingCourt?.sport == "Tennis") {
        paint = painterResource(id = R.drawable.baseline_sports_tennis_24)
    } else if (reservation.playingCourt?.sport == "Football") {
        paint = painterResource(id = R.drawable.baseline_sports_soccer_24)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = 4.dp,
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                setReservation(reservation);
                navController.navigate("ReservationDetails")
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
                        painter = paint,
                        contentDescription = "lucy pic",
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = reservation.playingCourt!!.name,
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
            Column() {
                SlotCard(
                    slotId = reservation.reservation!!.slotNumber,
                    startTime = reservation.playingCourt!!.openingTime
                )
            }
        }
    }
}


@Composable
fun SlotCard(slotId: Int, startTime: String) {
    val startEndTime = calculateStartEndTime(startTime, slotId)
    Card() {
        Row {
            Text(
                text = startEndTime,
                style = MaterialTheme.typography.h6,
            )
        }
    }
}


@Composable
fun ReservationDetail(
    reservation: ReservationWithCourt,
    vm: ReservationViewModel,
    navController: NavController
) {
    Box(Modifier.fillMaxSize()) {
        Text(
            reservation.playingCourt!!.name,
            modifier = Modifier.align(Alignment.Center),
            style = androidx.compose.material3.MaterialTheme.typography.headlineLarge
        )
        Button(
            onClick = {
                navController.navigate("EditReservation")
            },
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(text = "Edit")
        }

        Button(
            onClick = {
                vm.deleteReservation(reservation.reservation!!)
                navController.navigate("Home")
            },
            modifier = Modifier.padding(100.dp)
        ) {
            Text(text = "Delete")
        }

    }
}


@Composable
fun EditReservation(
    reservation: ReservationWithCourt,
    vm: ReservationViewModel,
    navController: NavController
) {

    val (selected, setSelected) = remember {
        mutableStateOf(reservation.reservation!!.slotNumber)
    }

    val list =
        calculateAvailableSlot(vm, reservation)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(128.dp),

            // content padding
            contentPadding = PaddingValues(
                start = 12.dp,
                top = 16.dp,
                end = 12.dp,
                bottom = 16.dp
            ),
            content = {
                items(list.size) { index ->

                    var color = Color.White;
                    if (list[index].slotNumber == selected) {
                        color = Color.Red
                    } else if (list[index].isBooked && list[index].slotNumber != reservation.reservation!!.slotNumber) {
                        color = Color.Gray
                    }

                    Card(
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable {
                                if (!list[index].isBooked || list[index].slotNumber == reservation.reservation!!.slotNumber) {
                                    setSelected(list[index].slotNumber)
                                }
                            }
                            .fillMaxWidth(),
                        elevation = 8.dp,
                        backgroundColor = color
                    ) {
                        Text(
                            text = list[index].time,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }


            }
        )
        Row(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd)
        ) {
            Button(
                onClick = {
                    reservation.reservation!!.slotNumber = selected;
                    vm.saveReservation(reservation.reservation)
                    navController.navigate("Home")
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(text = "Save")
            }
        }
    }

}


