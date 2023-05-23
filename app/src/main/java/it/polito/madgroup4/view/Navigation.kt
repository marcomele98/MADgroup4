package it.polito.madgroup4.view

import android.widget.Toast
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import it.polito.madgroup4.model.LevelEnum
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
import it.polito.madgroup4.view.screens.EditLevelSelector
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
import it.polito.madgroup4.view.screens.SplashScreen
import it.polito.madgroup4.view.screens.SportSelector
import it.polito.madgroup4.viewmodel.LoadingStateViewModel
import it.polito.madgroup4.viewmodel.ReservationViewModel
import it.polito.madgroup4.viewmodel.ReviewViewModel
import it.polito.madgroup4.viewmodel.Status
import it.polito.madgroup4.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate


class SnackbarVisualsWithError(
    override val message: String
) : SnackbarVisuals {
    override val actionLabel: String
        get() = "OK"
    override val withDismissAction: Boolean
        get() = false
    override val duration: SnackbarDuration
        get() = SnackbarDuration.Short
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
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
    setRemainingSports: (List<String>) -> Unit,

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
    val navController = rememberAnimatedNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val (topBarAction, setTopBarAction) = remember { mutableStateOf<() -> Unit>({}) }


    val loading by loadingVm.status.observeAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(loading) {
        when (loading) {
            is Status.Loading -> navController.navigate("Loading")
            is Status.Error -> {
                coroutineScope.launch {
                    if((loading as Status.Error).nextRoute != null)
                        navController.navigate((loading as Status.Error).nextRoute!!)
                    snackbarHostState.showSnackbar(
                        SnackbarVisualsWithError(
                            "Error: ${(loading as Status.Error).message}"
                        )
                    )
                    loadingVm.setStatus(Status.Running)

                }/*if((status as Status.Error).nextRoute != null)
                    navController.navigate((status as Status.Error).nextRoute!!)*/
            }

            is Status.Success -> {
                if((loading as Status.Success).nextRoute != null)
                    navController.navigate((loading as Status.Success).nextRoute!!)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        SnackbarVisualsWithError(
                            (loading as Status.Success).message
                        )
                    )
                    loadingVm.setStatus(Status.Running)
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
        snackbarHost = { SnackbarHost(snackbarHostState) }) { it ->
        Box(Modifier.padding(it)) {
            AnimatedNavHost(navController = navController, startDestination = "Reservations") {

                fun animatedComposable(
                    route: String,
                    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
                ) {
                    composable(
                        route,
                        content = content,
                        enterTransition = {
                            slideIntoContainer(
                                AnimatedContentScope.SlideDirection.Up,
                                animationSpec = tween(300)
                            )
                        },
                        exitTransition = {

                            slideOutOfContainer(
                                AnimatedContentScope.SlideDirection.Up,
                                animationSpec = tween(300)
                            )
                        },
                        popEnterTransition = {

                            slideIntoContainer(
                                AnimatedContentScope.SlideDirection.Up,
                                animationSpec = tween(300)
                            )

                        },
                        popExitTransition = {

                            slideOutOfContainer(
                                AnimatedContentScope.SlideDirection.Up,
                                animationSpec = tween(300)
                            )

                        }
                    )
                }

                composable(route = "splash") {
                    SplashScreen(
                        valid = true,
                        onStart = { },
                        onSplashEndedValid = {
                            navController.navigate("Reservations") {
                                popUpTo("splash") { inclusive = true }
                            }
                        },
                        onSplashEndedInvalid = {
                            println("suca")
                        }
                    )
                }

                animatedComposable("Profile") {
                    Profile(
                        user,
                        setFavoriteSport,
                        navController,
                        userVm,
                        setSelectedLevel,
                        setSelectedSport,
                        remainingSports,
                        setRemainingSports,
                        sports
                    )
                }

                animatedComposable("Edit Profile") {
                    EditProfile(setTopBarAction, user, userVm, navController, loadingVm)
                }

                animatedComposable("Camera") {
                    CameraScreen()
                }

                animatedComposable("Create Reservation") {
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

                animatedComposable("Reservations") {
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

                animatedComposable("Edit Reservation") {
                    EditReservation(
                        reservationVm,
                        reservation,
                        selectedSlot,
                        setSelectedSlot,
                        navController
                    )
                }

                animatedComposable("Reservation Details") {
                    ShowReservation(
                        reservationVm,
                        reviewVm,
                        userVm,
                        reservation,
                        navController,
                        loadingVm
                    )
                }

                animatedComposable("Confirm Reservation") {
                    ReservationConfirmation(
                        reservationVm = reservationVm,
                        userVm = userVm,
                        loadingVm = loadingVm,
                        playingCourt = selectedCourt.playingCourt!!,
                        reservationDate = creationDate,
                        reservationTimeSlot = selectedSlot,
                        setSelectedSlot = setSelectedSlot,
                        navController = navController,
                        setTopBarAction = setTopBarAction,
                    )
                }

                animatedComposable("Confirm Changes") {
                    ReservationConfirmation(
                        reservationVm = reservationVm,
                        userVm = userVm,
                        loadingVm = loadingVm,
                        playingCourt = reservation.playingCourt!!,
                        reservationDate = creationDate,
                        reservationTimeSlot = selectedSlot,
                        setSelectedSlot = setSelectedSlot,
                        navController = navController,
                        reservation = reservation.reservation!!,
                        setTopBarAction = setTopBarAction
                    )
                }

                animatedComposable("Select Sport") {
                    SportSelector(
                        sports = sports,
                        setSelectedSport = setSelectedSport,
                        navController = navController
                    )
                }

                animatedComposable("Select Your Sport") {
                    SportSelector(
                        sports = sports,
                        setSelectedSport = setSelectedSport,
                        navController = navController,
                        unselectable = user.value?.sports?.map { it.name!! } ?: listOf()
                    )
                }

                animatedComposable("Select A Time Slot") {
                    SlotSelectionReservation(
                        date = creationDate,
                        selectedCourt = selectedCourt,
                        selectedSlot = selectedSlot,
                        setSelectedSlot = setSelectedSlot,
                        navController = navController
                    )
                }

                animatedComposable("Playing Courts") {
                    Courts(
                        reservationVm = reservationVm,
                        selectedSport = selectedSport,
                        setShowedCourt = setShowedCourt,
                        navController = navController
                    )
                }

                animatedComposable("Playing Court Details") {
                    ShowCourt(
                        reviewVm = reviewVm,
                        playingCourt = showedCourt,
                        setReviews = setReviews,
                        navController = navController
                    )
                }

                animatedComposable("Rate This Playing Court") {
                    ReviewForm(
                        reviewVm = reviewVm,
                        userId = userId,
                        reservation = reservation,
                        navController = navController,
                        loadingVm = loadingVm,
                        setTopBarAction = setTopBarAction
                    )
                }

                animatedComposable("Reviews") {
                    ReviewList(
                        reviews = reviews, modifier = Modifier.padding(16.dp)
                    )
                }

                animatedComposable("Your Sport") {
                    ShowFavouriteSport(
                        favouriteSport!!,
                        user,
                        userVm,
                        navController,
                        loadingVm,
                        setSelectedLevel
                    )
                }

                animatedComposable("Create Achievement") {
                    CreateAchievement(userVm, favouriteSport!!, user, loadingVm, navController)
                }

                animatedComposable("Select Your Level") {
                    LevelSelector(
                        levels = LevelEnum.values().map { l -> l.name },
                        setSelectedLevel = setSelectedLevel,
                        navController = navController
                    )
                }

                animatedComposable("Edit Your Level") {
                    EditLevelSelector(
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



                animatedComposable("Add Sport") {
                    AddSport(
                        userVm,
                        loadingVm,
                        navController,
                        selectedSport,
                        selectedLevel,
                        setTopBarAction
                    )
                }

                animatedComposable("Select New Sport") {
                    SportSelector(
                        sports = remainingSports,
                        setSelectedSport = setSelectedSport,
                        navController = navController
                    )
                }

                animatedComposable("Loading") {
                    LoadingScreen()
                }

            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center)
        )
    }
}





