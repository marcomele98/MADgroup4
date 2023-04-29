package it.polito.madgroup4.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import it.polito.madgroup4.R
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.viewmodel.ReservationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navbar(
    vm: ReservationViewModel,
    reservation: ReservationWithCourt,
    setReservationWithCourt: (ReservationWithCourt) -> Unit
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomAppBar() {
                /*IconButton(
                    onClick = {
                        navController.navigate("Home")
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Home, "Home")
                }*/
                IconButton(
                    onClick = { navController.navigate("Home") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.DateRange, "Home")
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
                    // Aggiungi qui la logica del click sul FAB
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
            NavHost(navController = navController, startDestination = "Home") {
                composable("Profile") {
                    Profile()
                }

                composable("Home") {
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









