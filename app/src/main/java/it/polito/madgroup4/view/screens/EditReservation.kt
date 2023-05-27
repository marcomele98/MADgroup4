package it.polito.madgroup4.view.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.navigation.NavController
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.utility.calculateAvailableSlot
import it.polito.madgroup4.view.components.SlotSelector
import it.polito.madgroup4.viewmodel.ReservationViewModel

@Composable
fun EditReservation(
    reservationVm: ReservationViewModel,
    reservationId: String,
    selectedSlot: Int,
    setSelectedSlot: (Int) -> Unit,
    navController: NavController,
    reservations: State<List<ReservationWithCourt>?>
) {

    val reservation = reservations.value?.find { it.reservation?.id == reservationId }

    val list = calculateAvailableSlot(reservationVm, reservation!!)

    if(selectedSlot == -1)
        setSelectedSlot(reservation.reservation!!.slotNumber)

    SlotSelector(
        reservation = reservation.reservation,
        date = reservation.reservation!!.date.toDate(),
        selectedSlot = selectedSlot,
        slots = list,
        onClick = {
            setSelectedSlot(it)
            navController.navigate("Confirm Changes")
        },
    )

}




