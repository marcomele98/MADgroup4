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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.boguszpawlowski.composecalendar.SelectableWeekCalendar
import io.github.boguszpawlowski.composecalendar.rememberSelectableWeekCalendarState
import io.github.boguszpawlowski.composecalendar.week.Week
import it.polito.madgroup4.model.PlayingCourt
import it.polito.madgroup4.utility.CourtWithSlots
import it.polito.madgroup4.utility.formatDate
import it.polito.madgroup4.utility.getWeekdaysStartingOnSunday
import it.polito.madgroup4.view.components.DaysOfWeekHeader
import it.polito.madgroup4.view.components.MyDay
import it.polito.madgroup4.view.components.PlayingCourtCard
import it.polito.madgroup4.view.components.SlotSelector
import it.polito.madgroup4.view.components.SportCardSelector
import it.polito.madgroup4.view.components.WeekHeader
import it.polito.madgroup4.viewmodel.ReservationViewModel
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.Date

@Composable
fun CreateReservation(
    reservationVm: ReservationViewModel,
    date: LocalDate,
    selectedSport: String,
    setDate: (LocalDate) -> Unit,
    setSelectedSlot: (Int) -> Unit,
    setSelectedCourt: (CourtWithSlots) -> Unit,
    navController: NavController,
    setSelectedDate: (LocalDate) -> Unit,
) {

    val calendarState = rememberSelectableWeekCalendarState(
        initialSelection = listOf(date),
        initialWeek = Week(getWeekdaysStartingOnSunday(date, DayOfWeek.SUNDAY))
    )

    val allReservations = reservationVm.allRes.observeAsState().value

    if (calendarState.selectionState.selection.isEmpty()) calendarState.selectionState.selection =
        listOf(LocalDate.now())

    if (calendarState.selectionState.selection[0] != date) setDate(calendarState.selectionState.selection[0])

    setSelectedDate(date)

    setSelectedSlot(-1)

    val formatter = SimpleDateFormat("dd/MM/yyyy")

    reservationVm.getAllPlayingCourtsBySportAndDate(
        formatter.parse(formatter.format(java.sql.Date.valueOf(date.toString()))), selectedSport
    )

    val playingCourts = reservationVm.playingCourts.observeAsState(initial = emptyList())

    var filteredCourts by remember {
        mutableStateOf(emptyList<CourtWithSlots>())
    }

    filteredCourts = (playingCourts.value.filter {
        it.slots?.any { slot ->
            !(slot.isBooked || (formatDate(date) == formatDate(Date()) && LocalTime.parse(
                slot.time.split(
                    "-"
                )[0].trim()
            ).isBefore(
                LocalTime.now()
            )))
        } ?: false
    })

    val onClick = { index: Int ->
        setSelectedCourt(filteredCourts[index])
        navController.navigate("Select A Time SLot")
    }

    Column(
        Modifier.padding(horizontal = 16.dp)
    ) {

        Row() {
            SportCardSelector(sport = selectedSport, navController = navController)
        }
        SelectableWeekCalendar(
            calendarState = calendarState,
            weekHeader = { WeekHeader(weekState = it) },
            daysOfWeekHeader = {
                DaysOfWeekHeader(daysOfWeek = it)
            },
            dayContent = { dayState ->
                MyDay(
                    isActive = !dayState.date.isBefore(LocalDate.now()),
                    state = dayState,
                    reservations = allReservations?.firstOrNull() {
                        it.date.toInstant().atZone(ZoneId.systemDefault())
                            .toLocalDate() == dayState.date
                    },
                )
            },
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredCourts.isEmpty()) {
            Text(
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.error,
                text = "No available courts for the selected date and sport.",
                modifier = Modifier.padding(16.dp)
            )
        } else {
            PlayingCourtList(
                playingCourts = filteredCourts.map { it.playingCourt!! },
                onClick = onClick
            )
        }

    }
}

@Composable
fun PlayingCourtList(
    playingCourts: List<PlayingCourt>,
    onClick: (Int) -> Unit,
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
    ) {
        LazyColumn(Modifier.fillMaxSize()) {
            items(playingCourts.size) { index ->
                PlayingCourtCard(
                    playingCourts[index],
                    onClick = {
                        onClick(index)
                    }
                )
            }
        }
    }

}

@Composable
fun SlotSelectionReservation(
    date: LocalDate,
    selectedCourt: CourtWithSlots,
    selectedSlot: Int,
    setSelectedSlot: (Int) -> Unit,
    navController: NavController
) {

    SlotSelector(selectedSlot = selectedSlot,
        slots = selectedCourt.slots!!,
        date = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()),
        onClick = {
            if (!selectedCourt.slots!![it].isBooked) {
                setSelectedSlot(selectedCourt.slots!![it].slotNumber)
                navController.navigate("Confirm Reservation")
            }
        }
    )

}







