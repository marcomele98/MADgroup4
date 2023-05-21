package it.polito.madgroup4.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import it.polito.madgroup4.model.PlayingCourt
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.model.Review
import it.polito.madgroup4.model.User
import it.polito.madgroup4.utility.CourtWithSlots
import it.polito.madgroup4.view.components.BottomNavBar
import it.polito.madgroup4.view.components.FloatingFab
import it.polito.madgroup4.view.components.TopBar
import it.polito.madgroup4.view.screens.AddSport
import it.polito.madgroup4.view.screens.CameraScreen
import it.polito.madgroup4.view.screens.Courts
import it.polito.madgroup4.view.screens.CreateAchievement
import it.polito.madgroup4.view.screens.CreateReservation
import it.polito.madgroup4.view.screens.EditProfile
import it.polito.madgroup4.view.screens.EditReservation
import it.polito.madgroup4.view.screens.LevelSelector
import it.polito.madgroup4.view.screens.Profile
import it.polito.madgroup4.view.screens.ReservationConfirmation
import it.polito.madgroup4.view.screens.Reservations
import it.polito.madgroup4.view.screens.ReviewForm
import it.polito.madgroup4.view.screens.ReviewList
import it.polito.madgroup4.view.screens.ShowCourt
import it.polito.madgroup4.view.screens.ShowFavouriteSport
import it.polito.madgroup4.view.screens.ShowReservation
import it.polito.madgroup4.view.screens.SlotSelectionReservation
import it.polito.madgroup4.view.screens.SportSelector
import it.polito.madgroup4.viewmodel.LoadingStateViewModel
import it.polito.madgroup4.viewmodel.ReservationViewModel
import it.polito.madgroup4.viewmodel.ReviewViewModel
import it.polito.madgroup4.viewmodel.Status
import it.polito.madgroup4.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate


class SnackbarVisualsWithError(
    override val message: String, val isError: Boolean
) : SnackbarVisuals {
    override val actionLabel: String
        get() = if (isError) "Error" else "OK"
    override val withDismissAction: Boolean
        get() = false
    override val duration: SnackbarDuration
        get() = SnackbarDuration.Short
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navigation(
    reservationVm: ReservationViewModel,
    reviewVm: ReviewViewModel,
    userVm: UserViewModel,
    loadingVm: LoadingStateViewModel,

    userId: String,

    reservation: ReservationWithCourt,
    setReservationWithCourt: (ReservationWithCourt) -> Unit,

    sports: List<String>,
    remainingSports: List<String>,

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

    user: State<User?>,

    favouriteSport: Int?,
    setFavoriteSport: (Int) -> Unit,

    selectedLevel: String,
    setSelectedLevel: (String) -> Unit,

    selectedDate: LocalDate,
    setSelectedDate: (LocalDate) -> Unit,

    ) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val (topBarAction, setTopBarAction) = remember { mutableStateOf<() -> Unit>({}) }


    val loading by loadingVm.status.observeAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(loading) {
        when (loading) {
            is Status.Loading -> println("Loading...")
            is Status.Error -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        SnackbarVisualsWithError(
                            (loading as Status.Error).message, isError = true
                        )
                    )
                    loadingVm.setStatus(Status.Loading)

                }/*if((status as Status.Error).nextRoute != null)
                    navController.navigate((status as Status.Error).nextRoute!!)*/
            }

            is Status.Success -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        SnackbarVisualsWithError(
                            (loading as Status.Success).message, isError = false
                        )
                    )
                    loadingVm.setStatus(Status.Loading)
                }/*if((status as Status.Success).nextRoute != null)
                    navController.navigate((status as Status.Success).nextRoute!!)*/
            }

            else -> {}
        }
    }

    Scaffold(bottomBar = {
        BottomNavBar(navController = navController)
    },
        topBar = {
            TopBar(
                title = navBackStackEntry?.destination?.route ?: "",
                reservation = reservation,
                navController = navController,
                topBarAction = topBarAction,
                user = user,
                favoriteSport = favouriteSport,
            )
        },

        floatingActionButton = {
            if (navBackStackEntry?.destination?.route == "Reservations") FloatingFab(
                navController,
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        snackbarHost = { SnackbarHost(snackbarHostState) }) {
        Box(Modifier.padding(it)) {
            NavHost(navController = navController, startDestination = "Reservations") {

                composable("Profile") {
                    Profile(user, setFavoriteSport, navController, userVm, setSelectedLevel, setSelectedSport, remainingSports)
                }

                composable("Edit Profile") {
                    EditProfile(setTopBarAction, user, userVm, navController, loadingVm)
                }

                composable("Camera") {
                    CameraScreen()
                }

                composable("Create Reservation") {
                    CreateReservation(
                        reservationVm,
                        creationDate,
                        selectedSport,
                        setCreationDate,
                        setSelectedSlot,
                        setSelectedCourt,
                        navController,
                        setSelectedDate
                    )
                }

                composable("Reservations") {
                    Reservations(
                        reservationVm,
                        userId,
                        selectedDate,
                        setSelectedDate,
                        setReservationWithCourt,
                        navController,
                        setCreationDate,
                    )
                }

                composable("Edit Reservation") {
                    EditReservation(
                        reservationVm, reservation, selectedSlot, setSelectedSlot, navController
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
                        loadingVm = loadingVm,
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
                        loadingVm = loadingVm,
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
                        navController = navController,
                        loadingVm = loadingVm
                    )
                }

                composable("Reviews") {
                    ReviewList(
                        reviews = reviews, modifier = Modifier.padding(16.dp)
                    )
                }

                composable("Your Sport") {
                    ShowFavouriteSport(favouriteSport!!, user, userVm, navController, loadingVm)
                }

                composable("Create Achievement") {
                    CreateAchievement(userVm, favouriteSport!!, user, loadingVm, navController)
                }

                composable("Select Level") {
                    LevelSelector(
                        favouriteSport!!,
                        navController,
                        selectedLevel,
                        setSelectedLevel,
                        setTopBarAction,
                        userVm,
                        user,
                        loadingVm
                    )
                }

                composable("Add Sport") {
                    AddSport(userVm, loadingVm, navController, selectedSport, setFavoriteSport)
                }

                composable("Select New Sport") {
                    SportSelector(
                        sports = remainingSports,
                        setSelectedSport = setSelectedSport,
                        navController = navController
                    )
                }

            }
        }
    }
}





