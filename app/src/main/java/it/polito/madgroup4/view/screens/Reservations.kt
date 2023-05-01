package it.polito.madgroup4.view

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
import it.polito.madgroup4.view.components.ReservationList
import it.polito.madgroup4.viewmodel.ReservationViewModel
import java.time.LocalDate

@Composable
public fun Reservations(
    vm: ReservationViewModel,
    navController: NavController,
    setReservation: (ReservationWithCourt) -> Unit
) {

    val calendarState = rememberSelectableCalendarState()
    val allReservations = vm.allRes.observeAsState().value

    Column(
        Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)
    ) {
        val date = if (calendarState.selectionState.selection.isEmpty()) {
            LocalDate.now()
        } else {
            calendarState.selectionState.selection[0]
        }
        Calendar(vm = vm, calendarState, allReservations)
        Spacer(modifier = Modifier.size(10.dp))
        ReservationList(date = date.toString(), vm = vm, navController, setReservation)
    }
}

