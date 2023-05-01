package it.polito.madgroup4.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.github.boguszpawlowski.composecalendar.SelectableCalendar
import io.github.boguszpawlowski.composecalendar.day.DayState
import io.github.boguszpawlowski.composecalendar.header.MonthState
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import io.github.boguszpawlowski.composecalendar.selection.DynamicSelectionState
import it.polito.madgroup4.model.Reservation
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.utility.calculateAvailableSlot
import it.polito.madgroup4.utility.calculateStartEndTime
import it.polito.madgroup4.utility.imageSelector
import it.polito.madgroup4.viewmodel.ReservationViewModel
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun Calendar(
    vm: ReservationViewModel,
    navController: NavController,
    setReservation: (ReservationWithCourt) -> Unit
) {

    val calendarState = rememberSelectableCalendarState()
    val allReservations = vm.allRes.observeAsState().value

    Column(
        Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)
    ) {
        SelectableCalendar(
            daysOfWeekHeader = {
                DaysOfWeekHeader(daysOfWeek = it)
            },
            monthHeader = {
                MonthHeader(monthState = it)
            },
            dayContent = { dayState ->
                MyDay(
                    state = dayState,
                    reservations = allReservations?.firstOrNull() {
                        it.date.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate() == dayState.date
                    },
                )
            },
            calendarState = calendarState,
            showAdjacentMonths = false
        )
        val date = if (calendarState.selectionState.selection.isEmpty()) {
            LocalDate.now()
        } else {
            calendarState.selectionState.selection[0]
        }
        Spacer(modifier = Modifier.size(10.dp))
        ReservationList(date = date.toString(), vm = vm, navController, setReservation)
    }
}

@Composable
fun MonthHeader(
    monthState: MonthState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            modifier = Modifier.testTag("Decrement"),
            onClick = { monthState.currentMonth = monthState.currentMonth.minusMonths(1) }
        ) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            modifier = Modifier.testTag("MonthLabel"),
            text = monthState.currentMonth.month
                .getDisplayName(TextStyle.FULL, Locale.getDefault())
                .lowercase()
                .replaceFirstChar { it.titlecase() }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = monthState.currentMonth.year.toString()
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            modifier = Modifier.testTag("Increment"),
            onClick = { monthState.currentMonth = monthState.currentMonth.plusMonths(1) }
        ) {
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "forward")
        }
    }
}

@Composable
fun DaysOfWeekHeader(
    daysOfWeek: List<DayOfWeek>,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        daysOfWeek.forEach { dayOfWeek ->
            androidx.compose.material.Text(
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                modifier = modifier
                    .weight(1f)
                    .wrapContentHeight()
            )
        }
    }
}

@Composable
fun MyDay(
    state: DayState<DynamicSelectionState>,
    reservations: Reservation?,
    modifier: Modifier = Modifier,
) {
    val date = state.date
    val selectionState = state.selectionState
    val isSelected =
        if(selectionState.selection.isEmpty() && date == LocalDate.now()){
            true
        }else{
            selectionState.isDateSelected(date)
        }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .clickable {
                    selectionState.onDateSelected(date)
                },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected)
                                MaterialTheme.colorScheme.surfaceVariant
                            else
                                Color.Transparent
                        )
                ){
                    Text(
                        text = date.dayOfMonth.toString(),
                        modifier = Modifier.align(Alignment.Center),
                        color =
                        if(!state.isCurrentDay)
                            contentColorFor(
                                backgroundColor = if (isSelected)
                                    MaterialTheme.colorScheme.surfaceVariant
                                else
                                    MaterialTheme.colorScheme.surface
                            )
                        else
                            MaterialTheme.colorScheme.primary,
                    )
                }


            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(
                        if (reservations != null)
                            MaterialTheme.colorScheme.primary
                        else
                            Color.Transparent
                    )
            )
        }
    }
}


@Composable
fun ReservationList(
    date: String,
    vm: ReservationViewModel,
    navController: NavController,
    setReservation: (ReservationWithCourt) -> Unit
) {

    val formatter = SimpleDateFormat("dd/MM/yyyy")

    vm.getReservationsByDate(formatter.parse(formatter.format(Date.valueOf(date))))
    val reservations = vm.reservations.observeAsState(initial = emptyList())

    println(reservations.value)

    /*LazyColumn(Modifier.fillMaxSize()) {
        items(reservations.value.size) { index ->
            //ReservationCard(reservations.value[index], navController, setReservation)
            ReservationCard("Tennis", reservations.value[index])
        }
    }*/

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
    ) {
        LazyColumn(
            //columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(reservations.value.size) { index ->
                //ReservationCard(reservations.value[index], navController, setReservation)
                ReservationCard(reservations.value[index], navController, setReservation)
                if (index == reservations.value.size - 1) {
                    Spacer(modifier = Modifier.height(70.dp))
                }
            }
        }
    }
}

/*
@Composable
fun ReservationCard(
    reservation: ReservationWithCourt,
    navController: NavController,
    setReservation: (ReservationWithCourt) -> Unit,
) {

    var image = ImageSelector(reservation.playingCourt!!.sport)

    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(vertical = 8.dp)
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
                        painter = image,
                        contentDescription = "sport",
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = reservation.playingCourt!!.name,
                        //s
                        style = MaterialTheme.typography.titleSmall,
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
*/

@Composable
fun SlotCard(slotId: Int, startTime: String) {
    val startEndTime = calculateStartEndTime(startTime, slotId)
    ElevatedCard() {
        Row {
            Text(
                text = startEndTime,
                style = MaterialTheme.typography.titleSmall,
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
    vm.getSlotsByCourtIdAndDate(
        reservation.playingCourt!!.id,
        reservation.reservation!!.date
    )
    Box(Modifier.fillMaxSize()) {
        Text(
            reservation.playingCourt!!.name,
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.headlineLarge
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
                    Card(
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable {
                                if (!list[index].isBooked || list[index].slotNumber == reservation.reservation!!.slotNumber) {
                                    setSelected(list[index].slotNumber)
                                }
                            }
                            .fillMaxWidth(),
                        //elevation = 8.dp,
                        /*backgroundColor = if (list[index].slotNumber == selected) {
                            Color.Red
                        } else if (list[index].isBooked && list[index].slotNumber != reservation.reservation!!.slotNumber) {
                            Color.Gray
                        } else {
                            Color.White
                        },*/

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

@Composable
fun ReservationCard(
    reservation: ReservationWithCourt,
    navController: NavController,
    setReservation: (ReservationWithCourt) -> Unit
) {
    val startEndTime = calculateStartEndTime(
        reservation.playingCourt!!.openingTime,
        reservation.reservation!!.slotNumber
    )
    Card(
        modifier = Modifier
            .padding(bottom = 4.dp)
            .fillMaxWidth()
            .clickable {
                setReservation(reservation);
                navController.navigate("ReservationDetails")
            }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageSelector(reservation.playingCourt!!.sport),
                    contentDescription = "Reservations"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = reservation.playingCourt.sport,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${reservation.playingCourt?.name}",
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))


            Text(
                text = startEndTime,
                fontSize = 18.sp
            )


        }
    }
}


