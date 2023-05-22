package it.polito.madgroup4.view.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.utility.calculateAvailableSlot
import it.polito.madgroup4.view.components.SlotSelector
import it.polito.madgroup4.viewmodel.ReservationViewModel

@Composable
fun EditReservation(
    reservationVm: ReservationViewModel,
    reservation: ReservationWithCourt,
    selectedSlot: Int,
    setSelectedSlot: (Int) -> Unit,
    navController: NavController
) {

    val list = calculateAvailableSlot(reservationVm, reservation)

    if(selectedSlot == -1)
        setSelectedSlot(reservation.reservation!!.slotNumber)

    SlotSelector(
        reservation = reservation.reservation,
        date = reservation.reservation!!.date,
        selectedSlot = selectedSlot,
        slots = list,
        onClick = {
            setSelectedSlot(it)
            navController.navigate("Confirm Changes")
        },
    )

}




