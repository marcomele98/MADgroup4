package it.polito.madgroup4.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
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
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    if(navBackStackEntry?.destination?.route == "Profile") {
        navController.popBackStack()
    }

    Scaffold(
        bottomBar = {

            NavigationBar(
                modifier = Modifier.height(75.dp)
            ) {
                NavigationBarItem(
                    selected = navController.currentBackStackEntry?.destination?.route == "Profile",
                    icon = {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile"
                        )
                    },
                    label = { Text("Profile") },
                    onClick = { navController.navigate("Profile") })
                NavigationBarItem(
                    selected = navController.currentBackStackEntry?.destination?.route == "Reservations",
                    icon = {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Reservations"
                        )
                    },
                    label = { Text("Reservations") },
                    onClick = { navController.navigate("Reservations") })
            }


        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("CreateReservation")
                }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Aggiungi")
            }
        },
        floatingActionButtonPosition = FabPosition.End
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
                    Calendar(vm, navController, setReservationWithCourt)
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










