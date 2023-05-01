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
import it.polito.madgroup4.viewmodel.ReservationViewModel


@Composable
public fun EditReservation(
    reservation: ReservationWithCourt,
    vm: ReservationViewModel,
    navController: NavController
) {

    val (selected, setSelected) = remember {
        mutableStateOf(reservation.reservation!!.slotNumber)
    }

    val list =
        calculateAvailableSlot(vm, reservation)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(128.dp),

            // content padding
            contentPadding = PaddingValues(
                start = 12.dp,
                top = 16.dp,
                end = 12.dp,
                bottom = 16.dp
            ),
            content = {
                items(list.size) { index ->
                    Card(
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable {
                                if (!list[index].isBooked || list[index].slotNumber == reservation.reservation!!.slotNumber) {
                                    setSelected(list[index].slotNumber)
                                }
                            }
                            .fillMaxWidth(),
                        //elevation = 8.dp,
                        /*backgroundColor = if (list[index].slotNumber == selected) {
                            Color.Red
                        } else if (list[index].isBooked && list[index].slotNumber != reservation.reservation!!.slotNumber) {
                            Color.Gray
                        } else {
                            Color.White
                        },*/

                    ) {
                        Text(
                            text = list[index].time,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }


            }
        )
        Row(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd)
        ) {
            Button(
                onClick = {
                    reservation.reservation!!.slotNumber = selected;
                    vm.saveReservation(reservation.reservation)
                    navController.navigate("Home")
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(text = "Save")
            }
        }
    }

}
