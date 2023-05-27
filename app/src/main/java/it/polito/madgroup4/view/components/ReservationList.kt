package it.polito.madgroup4.view.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.madgroup4.model.ReservationWithCourt

@Composable
fun ReservationList(
    reservations: List<ReservationWithCourt>?,
    setReservation: (String) -> Unit,
    navController: NavController
) {


    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
    ) {
        if(reservations == null) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
        else if (reservations.isEmpty()) {
            Text(
                text = "No reservations for the selected date",
                modifier = Modifier
                    .align(Alignment.Center),
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.primary
            )
        }
        else {
            LazyColumn(
                //columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(reservations.size) { index ->
                    ReservationCard(reservations[index], setReservation, navController)
                    if (index == reservations.size - 1) {
                        Spacer(modifier = Modifier.height(70.dp))
                    }
                }
            }
        }

    }

}
