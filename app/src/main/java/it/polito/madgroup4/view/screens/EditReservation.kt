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
    navController: NavController,
    selectedSlot: Int,
    setSelectedSlot: (Int)->Unit
) {
    val list =
        calculateAvailableSlot(vm, reservation)

    println(selectedSlot)

    if(selectedSlot == -1)
        setSelectedSlot(reservation.reservation!!.slotNumber)

    SlotSelector(
        slots = list,
        onClick = {
            setSelectedSlot(it)
            navController.navigate("Confirm Changes")
        },
        selectedSlot = selectedSlot,
        reservation = reservation.reservation,
    )

}




