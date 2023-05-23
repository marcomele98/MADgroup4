package it.polito.madgroup4.view.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.polito.madgroup4.model.PlayingCourt
import it.polito.madgroup4.model.Reservation
import it.polito.madgroup4.view.components.ReservationDetails
import it.polito.madgroup4.viewmodel.LoadingStateViewModel
import it.polito.madgroup4.viewmodel.ReservationViewModel
import it.polito.madgroup4.viewmodel.UserViewModel
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationConfirmation(
    reservationVm: ReservationViewModel,
    userVm: UserViewModel,
    loadingVm: LoadingStateViewModel,
    playingCourt: PlayingCourt,
    reservationDate: LocalDate,
    reservationTimeSlot: Int,
    setSelectedSlot: (Int) -> Unit,
    navController: NavController,
    reservation: Reservation = Reservation(
        courtId = playingCourt.id,
        slotNumber = reservationTimeSlot,
        userId = userVm.user.value!!.email!!,
        date = SimpleDateFormat("dd/MM/yyyy").parse(
            SimpleDateFormat("dd/MM/yyyy").format(
                Date.valueOf(
                    reservationDate.toString()
                )
            )
        )
    ),
    setTopBarAction: (() -> Unit) -> Unit
) {

    var text by remember { mutableStateOf(reservation.particularRequests ?: "") }

    setTopBarAction{
        reservation.slotNumber = reservationTimeSlot
        if (text.trim() != "")
            reservation.particularRequests = text
        reservationVm.saveReservation(reservation, loadingVm, "Reservation confirmed successfully", "Error while saving the reservation" )
        navController.navigate("Reservations")
        setSelectedSlot(-1)
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)

    ) {

        ReservationDetails(
            playingCourt = playingCourt,
            reservationDate = reservation.date,
            reservationTimeSlot = reservationTimeSlot,
            particularRequests = null
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = text,
            /*supportingText = { Text(text = "Max 200 characters") },*/
            onValueChange = { text = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Sentences
            ),
            singleLine = false,
            maxLines = 5,
            label = { Text(text = "Particular requests") },
            placeholder = { Text(text = "Add particular requests") },
        )
    }
}