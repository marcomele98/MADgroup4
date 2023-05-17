package it.polito.madgroup4.view.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.viewmodel.ReservationViewModel
import java.sql.Date
import java.text.SimpleDateFormat

@Composable
fun ReservationList(
    reservationVm: ReservationViewModel,
    userId: Long,
    date: String,
    setReservation: (ReservationWithCourt) -> Unit,
    navController: NavController
) {

    val formatter = SimpleDateFormat("dd/MM/yyyy")

    //TODO: change 1 to userVm.user.value!!.id
    reservationVm.getReservationsByDate(
        formatter.parse(formatter.format(Date.valueOf(date))),
        userId
    )
    val reservations = reservationVm.reservations.observeAsState(initial = emptyList())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
    ) {
        LazyColumn(
            //columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(reservations.value.size) { index ->
                ReservationCard(reservations.value[index], setReservation, navController)
                if (index == reservations.value.size - 1) {
                    Spacer(modifier = Modifier.height(70.dp))
                }
            }
        }
    }

}
