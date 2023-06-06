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
fun ReservationConfirmation(
    reservationVm: ReservationViewModel,
    userVm: UserViewModel,
    loadingVm: LoadingStateViewModel,
    playingCourt: String,
    reservationDate: LocalDate,
    reservationTimeSlot: Int,
    setSelectedSlot: (Int) -> Unit,
    setTopBarAction: (() -> Unit) -> Unit,
    courtsWithSlots: State<List<CourtWithSlots>?>,
    stuff: List<Stuff>,
    reservationInfo: ReservationInfo,
    selectedLevel: String,
    setStuff: (List<Stuff>) -> Unit,
) {

    val courtWithSlots = courtsWithSlots.value?.find { it.playingCourt?.name == playingCourt }

    var reservation: ReservationWithCourt? =
        ReservationWithCourt(
            Reservation(
                courtName = playingCourt!!,
                slotNumber = reservationTimeSlot,
                userId = userVm.user.value!!.id!!,
                price = courtWithSlots?.playingCourt?.price!! + (0 + stuff.sumOf { item -> item.price!! * item.quantity!!.toDouble() }),
                stuff = stuff as MutableList<Stuff>,
                date = formatDateToTimestamp(
                    SimpleDateFormat("dd/MM/yyyy").parse(
                        SimpleDateFormat("dd/MM/yyyy").format(
                            Date.valueOf(
                                reservationDate.toString()
                            )
                        )
                    )
                ),
                reservationInfo = reservationInfo.copy(
                    confirmedUsers = mutableListOf(userVm.user.value!!.id!!),
                    suggestedLevel = selectedLevel
                ),
                sport = courtWithSlots.playingCourt?.sport!!
            ), courtWithSlots.playingCourt
        )


    val price by remember { mutableStateOf(reservation?.reservation?.price!!) }

    var text by remember { mutableStateOf(reservation?.reservation?.particularRequests ?: "") }

    LaunchedEffect(reservation) {
        setTopBarAction {
            loadingVm.setStatus(Status.Loading)
            reservation?.reservation?.slotNumber = reservationTimeSlot
            reservation?.reservation?.price = price
            reservation?.reservation?.stuff = stuff.toMutableList()
            if (text.trim() != "") reservation?.reservation?.particularRequests = text
            reservationVm.saveReservation(
                reservation?.reservation!!,
                loadingVm,
                "Reservation confirmed successfully",
                "Error while saving the reservation",
            )
            setSelectedSlot(-1)
            setStuff(mutableListOf())
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
            )
        }

        item {
            if (reservation?.reservation?.reservationInfo?.public == true) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Public match details",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontStyle = FontStyle.Italic
                )
                Spacer(modifier = Modifier.height(8.dp))


                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Available outsider places: ",
                        fontSize = 20.sp,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${reservation?.reservation?.reservationInfo!!.totalAvailable}",
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Suggested Level: ",
                        fontSize = 20.sp,

                        )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${reservation?.reservation?.reservationInfo!!.suggestedLevel}",
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    )
                }

            }

        }


        item {
            if (stuff.any { it.quantity!! > 0 }) {
                val filteredStuff = stuff.filter { it.quantity!! > 0 }
                if (filteredStuff.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Rented equipment",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontStyle = FontStyle.Italic
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                filteredStuff.forEach { stuff ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${stuff.name} x${stuff.quantity}",
                            fontSize = 20.sp,

                            )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "${stuff.quantity?.let { stuff.price?.times(it) }}€",
                            fontSize = 20.sp,
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }


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





