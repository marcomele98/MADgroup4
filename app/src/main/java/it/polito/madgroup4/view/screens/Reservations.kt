package it.polito.madgroup4.view.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.utility.formatDate
import it.polito.madgroup4.view.components.Calendar
import it.polito.madgroup4.view.components.ReservationList
import java.time.LocalDate

@Composable
fun Reservations(
    reservations: List<ReservationWithCourt>?,
    selectedDate: LocalDate,
    setSelectedDate: (LocalDate) -> Unit,
    setReservation: (String) -> Unit,
    navController: NavController,
    setCreationDate: (LocalDate) -> Unit
) {

    val calendarState = rememberSelectableCalendarState()

    if (calendarState.selectionState.selection.isEmpty()) {
        calendarState.selectionState.selection = listOf(selectedDate)
        setSelectedDate(selectedDate)
    } else {
        setSelectedDate(calendarState.selectionState.selection[0])
    }

    if (!(selectedDate.isBefore(LocalDate.now()))) {
        setCreationDate(selectedDate)
    } else {
        setCreationDate(LocalDate.now())
    }

    println(reservations)


    Column(
        Modifier.padding(start = 16.dp, end = 16.dp)
    ) {
        Calendar(calendarState, reservations)
        Spacer(modifier = Modifier.size(10.dp))

        ReservationList(
            reservations = reservations?.filter {
                it.reservation?.date?.toDate()
                    ?.let { it1 -> formatDate(it1) } == formatDate(selectedDate)
            },
            setReservation = setReservation,
            navController = navController
        )

    }

}

