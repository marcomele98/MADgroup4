package it.polito.madgroup4.view.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.utility.calculateAvailableSlot
import it.polito.madgroup4.view.components.SlotSelector
import it.polito.madgroup4.viewmodel.ReservationViewModel


@Composable
fun EditReservation(
    reservation: ReservationWithCourt,
    vm: ReservationViewModel,
    navController: NavController
) {
    val list =
        calculateAvailableSlot(vm, reservation)

    SlotSelector(
        slots = list,
        onClick = {
            reservation.reservation!!.slotNumber = it
            navController.navigate("Confirm Changes")
        },
        selectedSlot = reservation.reservation!!.slotNumber
    )

}




