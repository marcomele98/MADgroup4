package it.polito.madgroup4.view.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.boguszpawlowski.composecalendar.SelectableWeekCalendar
import io.github.boguszpawlowski.composecalendar.rememberSelectableWeekCalendarState
import io.github.boguszpawlowski.composecalendar.week.Week
import it.polito.madgroup4.utility.CourtWithSlots
import it.polito.madgroup4.utility.getWeekdaysStartingOnSunday
import it.polito.madgroup4.view.components.DaysOfWeekHeader
import it.polito.madgroup4.view.components.MyDay
import it.polito.madgroup4.view.components.PlayingCourtCard
import it.polito.madgroup4.view.components.SlotSelector
import it.polito.madgroup4.view.components.SportCard
import it.polito.madgroup4.view.components.WeekHeader
import it.polito.madgroup4.viewmodel.ReservationViewModel
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun CreateReservation(
    vm: ReservationViewModel,
    navController: NavController,
    selectedSport: String,
    date: LocalDate,
    setDate: (LocalDate) -> Unit,
    setSelectedCourt: (CourtWithSlots) -> Unit,
) {
    val calendarState = rememberSelectableWeekCalendarState(
        initialSelection = listOf(date),
        initialWeek = Week(getWeekdaysStartingOnSunday(date, DayOfWeek.SUNDAY))
    )
    val allReservations = vm.allRes.observeAsState().value

    if(calendarState.selectionState.selection[0] != date)
        setDate(calendarState.selectionState.selection[0])

    Column(
        Modifier
            .padding(horizontal = 16.dp)
    ) {
        Row(
            Modifier
                .padding(vertical = 16.dp)
        ) {
            SportCard(sport = selectedSport, navController = navController)
        }
        SelectableWeekCalendar(
            calendarState = calendarState,
            weekHeader = { WeekHeader(weekState = it) },
            daysOfWeekHeader = {
                DaysOfWeekHeader(daysOfWeek = it)
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
        )
        Spacer(modifier = Modifier.height(16.dp))
        PlayingCourtList(
            date = date.toString(),
            sport = selectedSport,
            vm = vm,
            navController = navController,
            setSelectedCourt = setSelectedCourt
        )
    }
}


@Composable
fun PlayingCourtList(
    date: String,
    sport: String,
    vm: ReservationViewModel,
    navController: NavController,
    setSelectedCourt: (CourtWithSlots) -> Unit
) {

    val formatter = SimpleDateFormat("dd/MM/yyyy")

    vm.getAllPlayingCourtsBySportAndDate(
        formatter.parse(formatter.format(java.sql.Date.valueOf(date))),
        sport
    )
    val playingCourts = vm.playingCourts.observeAsState(initial = emptyList())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
    ) {
        LazyColumn(Modifier.fillMaxSize()) {
            items(playingCourts.value.size) { index ->
                PlayingCourtCard(playingCourts.value[index], onClick = {
                    setSelectedCourt(it)
                    navController.navigate("Select A Time SLot")
                })
            }
        }
    }
}


@Composable
fun SlotSelectionReservation(
    navController: NavController,
    selectedCourt: CourtWithSlots,
    selectedSlot: Int,
    setSelectedSlot: (Int) -> Unit,
) {

    SlotSelector(
        slots = selectedCourt.slots!!,
        onClick = {
            if (!selectedCourt.slots!![it].isBooked) {
                setSelectedSlot(selectedCourt.slots!![it].slotNumber)
                navController.navigate("Confirm Reservation")
            }
        }
    )

}







