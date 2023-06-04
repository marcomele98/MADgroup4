package it.polito.madgroup4.view.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.navigation.NavController
import it.polito.madgroup4.model.CourtWithSlots
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.view.components.SlotSelector

@Composable
fun EditReservation(
    reservationId: String,
    selectedSlot: Int,
    setSelectedSlot: (Int) -> Unit,
    navController: NavController,
    reservations: State<List<ReservationWithCourt>?>,
    selectedCourtName: String,
    courtsWithSlots: State<List<CourtWithSlots>?>,
) {

    val reservation = reservations.value?.find { it.reservation?.id == reservationId }

    val selectedCourt = courtsWithSlots.value?.find { it.playingCourt?.name == selectedCourtName }


    SlotSelector(
        reservation = reservation!!.reservation,
        date = reservation!!.reservation!!.date.toDate(),
        selectedSlot = selectedSlot,
        slots = selectedCourt?.slots!!,
        onClick = {
            setSelectedSlot(it)
            navController.navigate("Edit Reservation")
        },
    )

}




