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
    val initialSlot = reservation.reservation!!.slotNumber

    SlotSelector(slots = list, onClick = {
        reservation.reservation!!.slotNumber = it
        if(reservation.reservation.slotNumber != initialSlot)
            reservation.reservation.particularRequests = ""
        navController.navigate("Confirm Changes")
    })

}




