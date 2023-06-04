package it.polito.madgroup4.view.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.madgroup4.model.CourtWithSlots
import it.polito.madgroup4.model.Reservation
import it.polito.madgroup4.model.ReservationInfo
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
fun EditReservationConfirmation(
    reservationVm: ReservationViewModel,
    userVm: UserViewModel,
    loadingVm: LoadingStateViewModel,
    playingCourt: String,
    reservationTimeSlot: Int,
    setSelectedSlot: (Int) -> Unit,
    setTopBarAction: (() -> Unit) -> Unit,
    reservationId: String,
    reservations: State<List<ReservationWithCourt>?>,
    courtsWithSlots: State<List<CourtWithSlots>?>,
    selectedLevel: String
) {

    val courtWithSlots = courtsWithSlots.value?.find { it.playingCourt?.name == playingCourt }

    val reservation = reservations?.value?.find { it.reservation?.id == reservationId }!!.copy()

    val initialSlot = reservation?.reservation?.slotNumber

    /*
    var res: ReservationWithCourt? =
        ReservationWithCourt(
            Reservation(
                courtName = res.reservation!!.courtName,
                slotNumber = reservationTimeSlot, //TODO CHECK
                userId = userVm.user.value!!.id!!,
                price = res.reservation.price,
                stuff = editedStuff ?: mutableListOf(),
                date = res.reservation.date,
                reservationInfo = res.reservation.reservationInfo?.copy(), //TODO SE EDITIAMO VEDIAMO DOPO
                sport = res.reservation.sport,
                particularRequests =  res.reservation.particularRequests
            ), courtWithSlots?.playingCourt
        )
     */

    var editedStuff by remember {
        mutableStateOf(reservation.reservation?.stuff?.map {
            Stuff(
                it.name,
                it.quantity,
                it.price,
                it.maxQuantity
            )
        }?.toMutableList())
    } //TODO quando modifichiamo editstuff bisogna vambiare refresh

    var refresh by remember { mutableStateOf(false) }

    var price by remember { mutableStateOf(reservation?.reservation?.price!!) }

    var text by remember { mutableStateOf(reservation?.reservation?.particularRequests ?: "") }

    LaunchedEffect(refresh, price, text) {
        setTopBarAction {
            loadingVm.setStatus(Status.Loading)

            if (courtWithSlots?.slots?.get(
                    reservationTimeSlot
                )?.isBooked == false || (reservationTimeSlot == initialSlot && reservationId != null)
            ) {
                reservationVm.saveReservation(
                    reservation.reservation!!.copy(
                        slotNumber = reservationTimeSlot,
                        price = price,
                        stuff = editedStuff ?: mutableListOf(),
                        particularRequests = if (text.trim() != "") text.trim() else null
                    ),
                    loadingVm,
                    "Reservation confirmed successfully",
                    "Error while saving the reservation",
                )
            } else {
                setSelectedSlot(-1)
                loadingVm.setStatus(Status.Error("Slot already booked", "Select A Time Slot"))
            }
            setSelectedSlot(-1)
        }
    }


    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {

        item {
            ReservationDetails(
                playingCourt = reservation?.playingCourt!!,
                reservationDate = reservation.reservation?.date!!.toDate(),
                reservationTimeSlot = reservationTimeSlot,
                price = price,
                reservationInfo = reservation.reservation?.reservationInfo,
                selectedLevel = selectedLevel,
            )
        }

        item {
            Text(
                text = "Rent equipment",
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        item { Spacer(modifier = Modifier.height(8.dp)) }

        items(editedStuff!!.size) {
            val name = editedStuff?.get(it)?.name
            val itemPrice = editedStuff?.get(it)?.price
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(text = "${name!!}  ${itemPrice!!}€", fontSize = 22.sp)
                Spacer(modifier = Modifier.weight(1f))
                NumberButton(
                    initialQuantity = editedStuff?.get(it)?.quantity!!,
                    onNumberChange = { n ->
                        editedStuff!![it].quantity = n
                        price = courtWithSlots?.playingCourt?.price!! +
                                (0 + editedStuff!!.sumOf { item -> item.price!! * item.quantity!!.toDouble() })
                        refresh = !refresh
                    },
                    editedStuff!![it].maxQuantity!!
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

        }

        item {
            OutlinedTextField(
                value = text,
                /*supportingText = { Text(text = "Max 200 characters") },*/
                onValueChange = { text = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(bottom = 16.dp),
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





