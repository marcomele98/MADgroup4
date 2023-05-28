package it.polito.madgroup4.view.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.madgroup4.model.CourtWithSlots
import it.polito.madgroup4.model.Reservation
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.model.Stuff
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
    playingCourt: String? = null,
    reservationDate: LocalDate,
    reservationTimeSlot: Int,
    setSelectedSlot: (Int) -> Unit,
    setTopBarAction: (() -> Unit) -> Unit,
    reservationId: String? = null,
    reservations: State<List<ReservationWithCourt>?>? = null,
    courtsWithSlots: State<List<CourtWithSlots>?>,
) {

    val courtWithSlots = courtsWithSlots.value?.find { it.playingCourt?.name == playingCourt }


    var reservation: ReservationWithCourt? = if (reservationId == null) {
        ReservationWithCourt(
            Reservation(
                courtName = playingCourt!!,
                slotNumber = reservationTimeSlot,
                userId = userVm.user.value!!.id!!,
                price = courtWithSlots?.playingCourt?.price!!,
                stuff = courtWithSlots.playingCourt?.stuff as MutableList<Stuff>,
                date = formatDateToTimestamp(
                    SimpleDateFormat("dd/MM/yyyy").parse(
                        SimpleDateFormat("dd/MM/yyyy").format(
                            Date.valueOf(
                                reservationDate.toString()
                            )
                        )
                    )
                )
            ), courtWithSlots?.playingCourt
        )
    } else {
        reservations?.value?.find { it.reservation?.id == reservationId }!!.copy()
    }

    val initialSlot = reservation?.reservation?.slotNumber

    var price by remember { mutableStateOf(reservation?.reservation?.price!!) }

    var text by remember { mutableStateOf(reservation?.reservation?.particularRequests ?: "") }

    setTopBarAction {
        loadingVm.setStatus(Status.Loading)
        reservation?.reservation?.slotNumber = reservationTimeSlot
        reservation?.reservation?.price = price
        if (text.trim() != "") reservation?.reservation?.particularRequests = text
        if (courtWithSlots?.slots?.get(
                reservationTimeSlot
            )?.isBooked == false || (reservation!!.reservation!!.slotNumber == initialSlot && reservationId != null)
        ) {
            reservationVm.saveReservation(
                reservation?.reservation!!,
                loadingVm,
                "Reservation confirmed successfully",
                "Error while saving the reservation"
            )
        } else {
            setSelectedSlot(-1)
            loadingVm.setStatus(Status.Error("Slot already booked", "Select A Time Slot"))
        }
        setSelectedSlot(-1)
    }

    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)

    ) {

        item {
            ReservationDetails(
                playingCourt = reservation?.playingCourt!!,
                reservationDate = reservation.reservation?.date!!.toDate(),
                reservationTimeSlot = reservationTimeSlot,
                price = price,
            )
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        item {
            Text(
                text = "Rent equipment",
                fontSize = 23.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontStyle = FontStyle.Italic
            )
        }
        item { Spacer(modifier = Modifier.height(8.dp)) }

        items(reservation?.reservation?.stuff?.size ?: 0) {
            val name = reservation?.reservation?.stuff?.get(it)?.name
            val itemPrice = reservation?.reservation?.stuff?.get(it)?.price
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "${name!!}  ${itemPrice!!}€", fontSize = 22.sp)
                Spacer(modifier = Modifier.weight(1f))
                NumberButton(initialQuantity = reservation?.reservation?.stuff?.get(it)?.quantity!!,
                    onNumberChange = { n ->
                        reservation!!.reservation!!.stuff[it].quantity = n
                        price =
                            reservation.playingCourt?.price!! + (0 + reservation!!.reservation!!.stuff.sumOf { item -> item.price!! * item.quantity!!.toDouble() })
                    })
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        item { Spacer(modifier = Modifier.height(10.dp)) }


        item {
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
                placeholder = { Text(text = "Add your particular requests") },
            )
        }
    }
}

@Composable
fun NumberButton(initialQuantity: Int, onNumberChange: (Int) -> Unit) {
    var number by remember { mutableStateOf(initialQuantity) }

    IconButton(onClick = { if (number != 0) number-- }, Modifier.size(25.dp)) {
        Icon(Icons.Filled.Remove, contentDescription = "Decrement")
    }

    Text(text = number.toString(), modifier = Modifier.padding(horizontal = 8.dp), fontSize = 22.sp)

    IconButton(onClick = { number++ }, Modifier.size(25.dp)) {
        Icon(Icons.Filled.Add, contentDescription = "Increment")
    }

    onNumberChange(number)
}
