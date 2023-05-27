package it.polito.madgroup4.view.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
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
import it.polito.madgroup4.model.Court
import it.polito.madgroup4.model.Reservation
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.utility.formatDateToTimestamp
import it.polito.madgroup4.view.components.ReservationDetails
import it.polito.madgroup4.viewmodel.LoadingStateViewModel
import it.polito.madgroup4.viewmodel.ReservationViewModel
import it.polito.madgroup4.viewmodel.Status
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
    playingCourt: Court? = null,
    reservationDate: LocalDate,
    reservationTimeSlot: Int,
    setSelectedSlot: (Int) -> Unit,
    navController: NavController,
    setTopBarAction: (() -> Unit) -> Unit,
    reservationId: String? = null,
    reservations: State<List<ReservationWithCourt>?>? = null
) {

    val reservation: ReservationWithCourt? = if (reservationId == null) {
        ReservationWithCourt(
            Reservation(
                courtName = playingCourt!!.name!!,
                slotNumber = reservationTimeSlot,
                userId = userVm.user.value!!.id!!,
                date = formatDateToTimestamp(
                    SimpleDateFormat("dd/MM/yyyy").parse(
                        SimpleDateFormat("dd/MM/yyyy").format(
                            Date.valueOf(
                                reservationDate.toString()
                            )
                        )
                    )
                )
            ),
            playingCourt
        )
    } else {
        reservations?.value?.find { it.reservation?.id == reservationId }!!.copy()
    }


    var text by remember { mutableStateOf(reservation?.reservation?.particularRequests ?: "") }

    setTopBarAction {
        loadingVm.setStatus(Status.Loading)
        reservation?.reservation?.slotNumber = reservationTimeSlot
        if (text.trim() != "")
            reservation?.reservation?.particularRequests = text
        reservationVm.saveReservation(
            reservation?.reservation!!,
            loadingVm,
            "Reservation confirmed successfully",
            "Error while saving the reservation"
        )
        setSelectedSlot(-1)
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)

    ) {

        ReservationDetails(
            playingCourt = reservation?.playingCourt!!,
            reservationDate = reservation.reservation?.date!!.toDate(),
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