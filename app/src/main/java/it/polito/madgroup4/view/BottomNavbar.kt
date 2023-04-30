package it.polito.madgroup4.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import it.polito.madgroup4.R
import it.polito.madgroup4.model.PlayingCourt
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.viewmodel.ReservationViewModel

@Composable
fun Navbar(
    vm: ReservationViewModel,
    reservation: ReservationWithCourt,
    setReservationWithCourt: (ReservationWithCourt) -> Unit
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomAppBar(
                elevation = 10.dp
            ) {
                /*IconButton(
                    onClick = {
                        navController.navigate("Home")
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Home, "Home")
                }*/
                IconButton(
                    onClick = { navController.navigate("Reservations") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.DateRange, "Reservations")
                }
                IconButton(
                    onClick = { navController.navigate("Profile") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.AccountCircle, "Home")
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("CreateReservation")
                },
                shape = CircleShape
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Aggiungi")
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true
    ) {
        Box(Modifier.padding(it)) {
            NavHost(navController = navController, startDestination = "Reservations") {
                composable("Profile") {
                    Profile()
                }
                composable("CreateReservation") {
                    CreateReservation(vm, navController)
                }

                composable("Reservations") {
                    SelectableCalendarSample(vm, navController, setReservationWithCourt)
                }

                composable("EditReservation") {

                    EditReservation(reservation, vm, navController)
                }
                composable("ReservationDetails") {
                    ReservationDetail(reservation, vm, navController)
                }
            }
        }
    }
}









