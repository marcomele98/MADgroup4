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
import it.polito.madgroup4.model.PlayingCourt
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.model.Review
import it.polito.madgroup4.utility.CourtWithSlots
import it.polito.madgroup4.view.components.BottomNavBar
import it.polito.madgroup4.view.components.FloatingFab
import it.polito.madgroup4.view.components.Profile
import it.polito.madgroup4.view.components.TopBar
import it.polito.madgroup4.view.screens.Courts
import it.polito.madgroup4.view.screens.CreateReservation
import it.polito.madgroup4.view.screens.EditReservation
import it.polito.madgroup4.view.screens.ReservationConfirmation
import it.polito.madgroup4.view.screens.Reservations
import it.polito.madgroup4.view.screens.ReviewForm
import it.polito.madgroup4.view.screens.ReviewList
import it.polito.madgroup4.view.screens.ShowCourt
import it.polito.madgroup4.view.screens.ShowReservation
import it.polito.madgroup4.view.screens.SlotSelectionReservation
import it.polito.madgroup4.view.screens.SportSelector
import it.polito.madgroup4.viewmodel.ReservationViewModel
import it.polito.madgroup4.viewmodel.ReviewViewModel
import it.polito.madgroup4.viewmodel.UserViewModel
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
    showedCourt: PlayingCourt,
    setShowedCourt: (PlayingCourt) -> Unit,
    userVm: UserViewModel,
    userId: Long,
    reviewVm: ReviewViewModel,
    reviews: List<Review>,
    setReviews: (List<Review>) -> Unit,
    showedReview: Review,
    setShowedReview: (Review) -> Unit
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
            TopBar(
                navBackStackEntry?.destination?.route ?: "",
                navController = navController,
                reservation = reservation
            )
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
                        setSelectedCourt,
                        setSelectedSlot
                    )
                }

                composable("Reservations") {
                    Reservations(
                        vm,
                        navController,
                        setReservationWithCourt,
                        creationDate,
                        setCreationDate,
                        userVm,
                        userId
                    )
                }

                composable("Edit Reservation") {
                    EditReservation(reservation, vm, navController, selectedSlot, setSelectedSlot)
                }
                composable("Reservation Details") {
                    ShowReservation(
                        reservation,
                        vm,
                        reviewVm,
                        navController,
                        userVm
                    )
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
                        navController = navController,
                        selectedCourt = selectedCourt,
                        setSelectedSlot = setSelectedSlot,
                        selectedSlot = selectedSlot,
                        date = creationDate,
                    )
                }

                composable("Confirm Reservation") {
                    ReservationConfirmation(
                        playingCourt = selectedCourt.playingCourt!!,
                        reservationDate = creationDate,
                        reservationTimeSlot = selectedSlot,
                        setSelectedSlot = setSelectedSlot,
                        vm = vm,
                        userVm = userVm,
                        navController = navController,
                    )
                }

                composable("Confirm Changes") {
                    ReservationConfirmation(
                        playingCourt = reservation.playingCourt!!,
                        reservationDate = creationDate,
                        reservationTimeSlot = selectedSlot,
                        setSelectedSlot = setSelectedSlot,
                        vm = vm,
                        userVm = userVm,
                        navController = navController,
                        reservation = reservation.reservation!!,
                    )
                }

                composable("Playing Courts") {
                    Courts(
                        navController = navController,
                        vm = vm,
                        selectedSport = selectedSport,
                        setShowedCourt = setShowedCourt,
                    )
                }

                composable("Playing Court Details") {
                    ShowCourt(playingCourt = showedCourt,
                        navController = navController,
                        reviewVm = reviewVm,
                        setReviews = setReviews,
                        )
                }

                composable("Rate This Playing Court") {
                    ReviewForm(reservation = reservation,  userId = userId, reviewVm = reviewVm, navController = navController)
                }

                composable("Reviews") {
                    ReviewList(reviews= reviews)
                }
            }
        }
    }
}










