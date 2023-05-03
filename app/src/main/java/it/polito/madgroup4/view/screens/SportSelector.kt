package it.polito.madgroup4.view.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.madgroup4.utility.imageSelector

@Composable
fun SportSelector(
    sports: List<String>,
    navController: NavController,
    setSelectedSport: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
    )

    {
        items(sports.size) { index ->
            //ReservationCard(reservations.value[index], navController, setReservation)
            ElevatedCard(
                modifier = Modifier
                    .padding(bottom = 10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable {
                                setSelectedSport(sports[index])
                                navController.popBackStack()
                            }
                    ) {
                        Icon(
                            imageSelector(sports[index]),
                            modifier = Modifier
                                .size(30.dp),
                            contentDescription = "Reservations"
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = sports[index],
                            fontSize = 30.sp
                        )
                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                        )
                    }
                }
            }
        }
    }
}