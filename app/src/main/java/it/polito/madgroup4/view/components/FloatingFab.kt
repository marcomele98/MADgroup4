package it.polito.madgroup4.view.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import it.polito.madgroup4.model.ReservationInfo

@Composable
fun FloatingFab(
    navController: NavHostController,
    setReservationInfo: (ReservationInfo) -> Unit,
    setSelectedLevel: (String) -> Unit
) {
    FloatingActionButton(
        onClick = {
            setReservationInfo(
                ReservationInfo(
                    public = false,
                    totalAvailable = 0,
                )
            )
            setSelectedLevel("BEGINNER")
            navController.navigate("Create Reservation")
        }
    ) {
        Icon(Icons.Filled.Add, contentDescription = "Adding")
    }
}