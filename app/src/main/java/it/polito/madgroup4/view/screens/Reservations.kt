package it.polito.madgroup4.view.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.view.components.Calendar
import it.polito.madgroup4.view.components.ReservationList
import it.polito.madgroup4.viewmodel.ReservationViewModel
import java.time.LocalDate

@Composable
fun Reservations(
    reservationVm: ReservationViewModel,
    userId: String,
    selectedDate: LocalDate,
    setSelectedDate: (LocalDate) -> Unit,
    setReservation: (ReservationWithCourt) -> Unit,
    navController: NavController,
    setCreationDate: (LocalDate) -> Unit
) {

    val calendarState = rememberSelectableCalendarState()
    val allReservations = reservationVm.allRes.observeAsState().value

    if (calendarState.selectionState.selection.isEmpty()) {
        calendarState.selectionState.selection = listOf(selectedDate)
        setSelectedDate(selectedDate)
    } else {
        setSelectedDate(calendarState.selectionState.selection[0])
    }

    if(!(selectedDate.isBefore(LocalDate.now()))) {
        setCreationDate(selectedDate)
    } else {
        setCreationDate(LocalDate.now())
    }


    Column(
        Modifier.padding(start = 16.dp, end = 16.dp)
    ) {
        Calendar(calendarState, allReservations)
        Spacer(modifier = Modifier.size(10.dp))
        ReservationList(
            reservationVm = reservationVm,
            date = selectedDate.toString(),
            userId = userId,
            setReservation = setReservation,
            navController = navController
        )
    }

}

