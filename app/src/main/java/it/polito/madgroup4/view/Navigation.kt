package it.polito.madgroup4.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import it.polito.madgroup4.model.PlayingCourt
import it.polito.madgroup4.model.Profile
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.model.Review
import it.polito.madgroup4.model.User
import it.polito.madgroup4.utility.CourtWithSlots
import it.polito.madgroup4.view.components.BottomNavBar
import it.polito.madgroup4.view.components.FloatingFab
import it.polito.madgroup4.view.components.TopBar
import it.polito.madgroup4.view.screens.CameraScreen
import it.polito.madgroup4.view.screens.Courts
import it.polito.madgroup4.view.screens.CreateReservation
import it.polito.madgroup4.view.screens.EditReservation
import it.polito.madgroup4.view.screens.Profile
import it.polito.madgroup4.view.screens.EditProfile
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
    reservationVm: ReservationViewModel,
    reviewVm: ReviewViewModel,
    userVm: UserViewModel,

    userId: Long,

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

    reviews: List<Review>,
    setReviews: (List<Review>) -> Unit,

    editedUser: User,
    setEditedUser: (User) -> Unit,
    ) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            BottomNavBar(navController = navController)
        },
        topBar = {
            TopBar(
                title = navBackStackEntry?.destination?.route ?: "",
                reservation = reservation,
                navController = navController,
                editedUser = editedUser,
                context = context,
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
                    Profile(setEditedUser)
                }

                composable("Create Reservation") {
                    CreateReservation(
                        reservationVm,
                        creationDate,
                        selectedSport,
                        setCreationDate,
                        setSelectedSlot,
                        setSelectedCourt,
                        navController
                    )
                }

                composable("Reservations") {
                    Reservations(
                        reservationVm,
                        userId,
                        creationDate,
                        setCreationDate,
                        setReservationWithCourt,
                        navController
                    )
                }

                composable("Edit Reservation") {
                    EditReservation(
                        reservationVm,
                        reservation,
                        selectedSlot,
                        setSelectedSlot,
                        navController
                    )
                }

                composable("Reservation Details") {
                    ShowReservation(
                        reservationVm,
                        reviewVm,
                        userVm,
                        reservation,
                        navController,
                    )
                }

                composable("Confirm Reservation") {
                    ReservationConfirmation(
                        reservationVm = reservationVm,
                        userVm = userVm,
                        playingCourt = selectedCourt.playingCourt!!,
                        reservationDate = creationDate,
                        reservationTimeSlot = selectedSlot,
                        setSelectedSlot = setSelectedSlot,
                        navController = navController,
                    )
                }

                composable("Confirm Changes") {
                    ReservationConfirmation(
                        playingCourt = reservation.playingCourt!!,
                        reservationDate = creationDate,
                        reservationTimeSlot = selectedSlot,
                        setSelectedSlot = setSelectedSlot,
                        reservationVm = reservationVm,
                        userVm = userVm,
                        navController = navController,
                        reservation = reservation.reservation!!,
                    )
                }

                composable("Select Sport") {
                    SportSelector(
                        sports = sports,
                        setSelectedSport = setSelectedSport,
                        navController = navController
                    )
                }
                composable("Select A Time Slot") {
                    SlotSelectionReservation(
                        date = creationDate,
                        selectedCourt = selectedCourt,
                        selectedSlot = selectedSlot,
                        setSelectedSlot = setSelectedSlot,
                        navController = navController
                    )
                }

                composable("Playing Courts") {
                    Courts(
                        reservationVm = reservationVm,
                        selectedSport = selectedSport,
                        setShowedCourt = setShowedCourt,
                        navController = navController
                    )
                }

                composable("Playing Court Details") {
                    ShowCourt(
                        reviewVm = reviewVm,
                        playingCourt = showedCourt,
                        setReviews = setReviews,
                        navController = navController
                    )
                }

                composable("Rate This Playing Court") {
                    ReviewForm(
                        reviewVm = reviewVm,
                        userId = userId,
                        reservation = reservation,
                        navController = navController
                    )
                }

                composable("Reviews") {
                    ReviewList(
                        reviews = reviews,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                composable("Edit Profile") {
                    EditProfile(navController, editedUser, setEditedUser)
                }

                composable("Camera") {
                    CameraScreen()
                }
            }
        }
    }
}










