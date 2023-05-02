package it.polito.madgroup4.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.utility.CourtWithSlots
import it.polito.madgroup4.view.components.BottomNavBar
import it.polito.madgroup4.view.components.FloatingFab
import it.polito.madgroup4.view.components.TopBar
import it.polito.madgroup4.view.screens.EditReservation
import it.polito.madgroup4.view.screens.ReservationConfirmation
import it.polito.madgroup4.view.screens.ShowReservation
import it.polito.madgroup4.view.screens.SportSelector
import it.polito.madgroup4.viewmodel.ReservationViewModel
import java.time.LocalDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation(
    vm: ReservationViewModel,
    reservation: ReservationWithCourt,
    setReservationWithCourt: (ReservationWithCourt) -> Unit,
    sports: List<String>,
    selectedSport: String,
    setSelectedSport: (String) -> Unit,
    creationDate: LocalDate,
    setCreationDate: (LocalDate) -> Unit,
    selectedCourt: CourtWithSlots,
    setSelectedCourt: (CourtWithSlots) -> Unit,
    selectedSlot: Int,
    setSelectedSlot: (Int) -> Unit,
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    if (navBackStackEntry?.destination?.route == "Profile") {
        navController.popBackStack()
    }
    Scaffold(
        bottomBar = {
            BottomNavBar(navController = navController)
        },
        topBar = {
            TopBar(navBackStackEntry?.destination?.route ?: "", navController = navController)
        },

        floatingActionButton = {
            if (navBackStackEntry?.destination?.route == "Reservations")
                FloatingFab(navController)
        },
        floatingActionButtonPosition = FabPosition.End
    ) {
        Box(Modifier.padding(it)) {
            NavHost(navController = navController, startDestination = "Reservations") {
                composable("Profile") {
                    Profile()
                }
                composable("Create Reservation") {
                    CreateReservation(
                        vm,
                        navController,
                        selectedSport,
                        creationDate,
                        setCreationDate,
                        selectedCourt,
                        setSelectedCourt
                    )
                }

                composable("Reservations") {
                    Reservations(vm, navController, setReservationWithCourt)
                }

                composable("EditReservation") {
                    EditReservation(reservation, vm, navController)
                }
                composable("ReservationDetails") {
                    ShowReservation(reservation, vm, navController)
                }
                composable("Select Sport") {
                    SportSelector(
                        sports = sports,
                        navController = navController,
                        setSelectedSport = setSelectedSport
                    )
                }
                composable("Select A Time Slot") {
                    SlotSelectionReservation(
                        vm = vm,
                        navController = navController,
                        selectedCourt = selectedCourt,
                        setSelectedSlot = setSelectedSlot,
                        selectedSlot = selectedSlot
                    )
                }

                composable("Confirm Your Reservation") {
                    ReservationConfirmation(
                        playingCourt =  selectedCourt,
                        reservationDate = creationDate,
                        reservationTimeSlot = selectedSlot,
                        vm = vm,
                        navController = navController
                    )
                }
            }
        }
    }
}










