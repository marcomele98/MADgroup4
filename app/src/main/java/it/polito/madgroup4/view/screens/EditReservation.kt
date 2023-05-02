package it.polito.madgroup4.view.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.utility.calculateAvailableSlot
import it.polito.madgroup4.view.components.SlotSelector
import it.polito.madgroup4.viewmodel.ReservationViewModel


@Composable
public fun EditReservation(
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




