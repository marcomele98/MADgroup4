package it.polito.madgroup4.view

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.Uri
import android.util.Log
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import it.polito.madgroup4.model.Court
import it.polito.madgroup4.model.CourtWithSlots
import it.polito.madgroup4.model.LevelEnum
import it.polito.madgroup4.model.ReservationInfo
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.model.Review
import it.polito.madgroup4.model.Stuff
import it.polito.madgroup4.model.User
import it.polito.madgroup4.view.components.BottomNavBar
import it.polito.madgroup4.view.components.FloatingFab
import it.polito.madgroup4.view.components.ReservationList
import it.polito.madgroup4.view.components.TopBar
import it.polito.madgroup4.view.screens.AddParticipants
import it.polito.madgroup4.view.screens.AddSport
import it.polito.madgroup4.view.screens.AdditionalInfo
import it.polito.madgroup4.view.screens.Courts
import it.polito.madgroup4.view.screens.CreateAchievement
import it.polito.madgroup4.view.screens.CreateReservation
import it.polito.madgroup4.view.screens.EditLevelSelector
import it.polito.madgroup4.view.screens.EditProfile
import it.polito.madgroup4.view.screens.EditReservation
import it.polito.madgroup4.view.screens.EditReservationConfirmation
import it.polito.madgroup4.view.screens.Explore
import it.polito.madgroup4.view.screens.FirstLogin
import it.polito.madgroup4.view.screens.LevelSelector
import it.polito.madgroup4.view.screens.LoadingScreen
import it.polito.madgroup4.view.screens.NoConnectivity
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
    userVm: UserViewModel,
    loadingVm: LoadingStateViewModel,

    reservation: String,
    setReservationWithCourt: (String) -> Unit,

    sports: List<String>,
    remainingSports: List<String>,
    setRemainingSports: (List<String>) -> Unit,

    selectedSport: String,

    setSelectedSport: (String) -> Unit,
    creationDate: LocalDate,

    setCreationDate: (LocalDate) -> Unit,
    selectedCourt: String,

    setSelectedCourt: (String) -> Unit,
    selectedSlot: Int,

    setSelectedSlot: (Int) -> Unit,
    showedCourt: Court,

    setShowedCourt: (Court) -> Unit,
    reviews: List<Review>,

    setReviews: (List<Review>) -> Unit,

    user: State<User?>,
    favouriteSport: Int?,

    setFavoriteSport: (Int) -> Unit,
    selectedLevel: String,

    setSelectedLevel: (String) -> Unit,
    selectedDate: LocalDate,
    setSelectedDate: (LocalDate) -> Unit,
    connectivity: Boolean,
    activity: ReservationActivityCompose,
    reservations: State<List<ReservationWithCourt>?>,
    courtsWithSlots: State<List<CourtWithSlots>?>,
    stuff: List<Stuff>,
    setStuff: (List<Stuff>) -> Unit,
    reservationInfo: ReservationInfo,
    setReservationInfo: (ReservationInfo) -> Unit,
    sharedReservations: State<List<ReservationWithCourt>?>,
    users: State<List<User>?>,

    ) {


    val navController = rememberAnimatedNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val (topBarAction, setTopBarAction) = remember { mutableStateOf<() -> Unit>({}) }

    val linkReservations = reservationVm.linkReservations.observeAsState(initial = emptyList())

    val (fromLink, setfromLink) = remember { mutableStateOf(false) }

    val loading by loadingVm.status.observeAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val coroutineScope = rememberCoroutineScope()

    var screen by remember { mutableStateOf("Reservations") }


    LaunchedEffect(Unit) {
        if (activity.intent?.extras != null) {
            val value = activity.intent?.extras?.getString("screen")
            if (value != null) {
                setReservationWithCourt(
                    activity.intent?.extras?.getString("reservationId").toString()
                )
                screen = value.toString()

            }
        }

        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(activity.intent)
            .addOnSuccessListener { pendingDynamicLinkData ->
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                }
                if (deepLink != null) {
                    if (deepLink!!.getQueryParameter("reservationId") != null) {
                        val res = deepLink!!.getQueryParameter("reservationId")
                        if (res != null) {
                            setReservationWithCourt(res)
                            setfromLink(true)
                            reservationVm.getReservationsById(res)
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.i("ERROR", "ERROR")
            }

    }

    if (fromLink) {
        screen = "Join Reservation"
    }
    setfromLink(false)


    LaunchedEffect(loading) {
        when (loading) {
            is Status.Loading -> navController.navigate("Loading")
            is Status.Error -> {
                coroutineScope.launch {
                    if ((loading as Status.Error).nextRoute != null && (loading as Status.Error).nextRoute != "back")
                        navController.navigate((loading as Status.Error).nextRoute!!)
                    else if ((loading as Status.Error).nextRoute == "back") {
                        navController.popBackStack()
                    }
                    snackbarHostState.showSnackbar(
                        SnackbarVisualsWithError(
                            "Error: ${(loading as Status.Error).message}"
                        )
                    )
                    loadingVm.setStatus(Status.Running)

                }
            }

            is Status.Success -> {
                if ((loading as Status.Success).nextRoute != null) navController.navigate((loading as Status.Success).nextRoute!!)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        SnackbarVisualsWithError(
                            (loading as Status.Success).message
                        )
                    )
                    loadingVm.setStatus(Status.Running)
                }
            }

            else -> {}
        }
    }

    var isConnectionAvailable by remember { mutableStateOf(connectivity) }
    // Register a network callback to monitor internet connectivity changes
    val connectivityManager =
        activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val networkCallback = remember {
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
                val hasInternetCapability =
                    networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        ?: false
                if (hasInternetCapability) {
                    isConnectionAvailable = true
                }
            }

            override fun onLost(network: Network) {
                isConnectionAvailable = false
            }
        }
    }

    DisposableEffect(Unit) {
        val networkRequest =
            NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        onDispose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }

    if (!isConnectionAvailable) {
        NoConnectivity()
    } else {

        Scaffold(bottomBar = {
            if (navBackStackEntry?.destination?.route != "Loading" && navBackStackEntry?.destination?.route != "No Connectivity" && navBackStackEntry?.destination?.route != "Welcome" && navBackStackEntry?.destination?.route != "Complete Your Profile") BottomNavBar(
                navController = navController
            )
        },
            topBar = {
                if (navBackStackEntry?.destination?.route != "Loading" && navBackStackEntry?.destination?.route != "No Connectivity") TopBar(
                    title = navBackStackEntry?.destination?.route ?: "",
                    reservations = reservations,
                    reservationId = reservation,
                    navController = navController,
                    topBarAction = topBarAction,
                    user = user,
                    favoriteSport = favouriteSport,
                    setReservationInfo = setReservationInfo,
                    setSelectedLevel = setSelectedLevel,
                )
            },

            floatingActionButton = {
                if (navBackStackEntry?.destination?.route == "Reservations") FloatingFab(
                    navController,
                    setReservationInfo,
                    setSelectedLevel
                )
            },
            floatingActionButtonPosition = FabPosition.End,
            snackbarHost = { SnackbarHost(snackbarHostState) }) { it ->
            Box(Modifier.padding(it)) {
                AnimatedNavHost(
                    navController = navController,
                    startDestination = if (user.value?.nickname != null) screen else "Welcome"
                ) {

                    fun animatedComposable(
                        route: String,
                        content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
                    ) {
                        composable(route, content = content, enterTransition = { scaleIn() },
                            exitTransition = {
                                scaleOut(animationSpec = tween(50))
                            }, popEnterTransition = {
                                slideIntoContainer(
                                    AnimatedContentScope.SlideDirection.Left,
                                    animationSpec = tween(300)
                                )

                            }, popExitTransition = {
                                slideOutOfContainer(
                                    AnimatedContentScope.SlideDirection.Left,
                                    animationSpec = tween(300)
                                )

                            })
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
                        EditProfile(setTopBarAction, user, userVm, loadingVm)
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
                            setSelectedDate,
                        )
                    }

                    animatedComposable("Reservations") {
                        Reservations(
                            reservations.value,
                            selectedDate,
                            setSelectedDate,
                            setReservationWithCourt,
                            navController,
                            setCreationDate,
                        )
                    }

                    animatedComposable("Update time slot") {
                        EditReservation(
                            reservation,
                            selectedSlot,
                            setSelectedSlot,
                            navController,
                            reservations,
                            selectedCourt,
                            courtsWithSlots
                        )
                    }

                    animatedComposable("Reservation Details") {
                        ShowReservation(
                            reservation,
                            navController,
                            reservations,
                            reservationVm,
                            setSelectedCourt,
                            setSelectedSlot,
                            loadingVm,
                            user,
                            users
                        )
                    }

                    animatedComposable("Confirm Reservation") {
                        ReservationConfirmation(
                            reservationVm = reservationVm,
                            userVm = userVm,
                            loadingVm = loadingVm,
                            playingCourt = selectedCourt,
                            reservationDate = creationDate,
                            reservationTimeSlot = selectedSlot,
                            setSelectedSlot = setSelectedSlot,
                            setTopBarAction = setTopBarAction,
                            courtsWithSlots = courtsWithSlots,
                            stuff = stuff,
                            setStuff = setStuff,
                            reservationInfo = reservationInfo,
                            selectedLevel = selectedLevel,
                        )
                    }

                    animatedComposable("Edit Reservation") {
                        EditReservationConfirmation(
                            reservationVm = reservationVm,
                            userVm = userVm,
                            loadingVm = loadingVm,
                            reservationTimeSlot = selectedSlot,
                            setSelectedSlot = setSelectedSlot,
                            setTopBarAction = setTopBarAction,
                            courtsWithSlots = courtsWithSlots,
                            reservationId = reservation,
                            selectedLevel = selectedLevel,
                            playingCourt = selectedCourt,
                            reservations = reservations
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
                        SportSelector(sports = sports,
                            setSelectedSport = setSelectedSport,
                            navController = navController,
                            unselectable = user.value?.sports?.map { it.name!! } ?: listOf())
                    }

                    animatedComposable("Select A Time Slot") {
                        SlotSelectionReservation(
                            date = creationDate,
                            selectedCourtName = selectedCourt,
                            courtsWithSlots = courtsWithSlots,
                            selectedSlot = selectedSlot,
                            setSelectedSlot = setSelectedSlot,
                            navController = navController
                        )
                    }

                    animatedComposable("Playing Courts") {
                        Courts(
                            courtVm = reservationVm,
                            selectedSport = selectedSport,
                            setShowedCourt = setShowedCourt,
                            navController = navController
                        )
                    }

                    animatedComposable("Playing Court Details") {
                        ShowCourt(
                            reservationVm = reservationVm,
                            playingCourt = showedCourt,
                            setReviews = setReviews,
                            navController = navController,
                        )
                    }

                    animatedComposable("Rate This Playing Court") {
                        ReviewForm(
                            reservationVm = reservationVm,
                            reservationId = reservation,
                            reservations = reservations,
                            navController = navController,
                            loadingVm = loadingVm,
                            setTopBarAction = setTopBarAction,
                            nickname = user.value?.nickname ?: ""
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
                            setSelectedLevel,
                        )
                    }

                    animatedComposable("Create Achievement") {
                        CreateAchievement(
                            userVm, favouriteSport!!, user, loadingVm, setTopBarAction
                        )
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
                            favouriteSport!!, setTopBarAction, userVm, user, loadingVm
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

                    animatedComposable("Complete Your Profile") {
                        EditProfile(
                            setTopBarAction, user, userVm, loadingVm, signUp = true
                        )
                    }

                    animatedComposable("Welcome") {
                        FirstLogin(
                            navController,
                        )
                    }


                    animatedComposable("Invites") {
                        ReservationList(
                            reservations = reservations.value?.filter { it.reservation?.reservationInfo?.status == "Invited" },
                            setReservation = setReservationWithCourt,
                            navController = navController,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(top = 10.dp),
                            text = "No invites yet",
                            nextRoute = "Reservation Details",
                            date = true
                        )
                    }


                    animatedComposable("Reviewable") {
                        ReservationList(
                            reservations = reservations.value?.filter { it.reservation?.reservationInfo?.status == "Reviewable" },
                            setReservation = setReservationWithCourt,
                            navController = navController,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(top = 10.dp),
                            text = "No reviewable reservations",
                            nextRoute = "Reservation Details",
                            date = true
                        )
                    }

                    animatedComposable("Additional Info") {
                        AdditionalInfo(
                            playingCourt = selectedCourt,
                            courtsWithSlots = courtsWithSlots,
                            navController = navController,
                            stuff = stuff,
                            setStuff = setStuff,
                            reservationInfo = reservationInfo,
                            setReservationInfo = setReservationInfo,
                            selectedLevel = selectedLevel,
                        )
                    }

                    animatedComposable("Explore") {
                        Explore(
                            reservations = sharedReservations.value,
                            setReservation = setReservationWithCourt,
                            navController = navController,
                            selectedSport = selectedSport,
                        )
                    }

                    animatedComposable("Public Match Details") {
                        ShowReservation(
                            reservation,
                            navController,
                            sharedReservations,
                            reservationVm,
                            setSelectedCourt,
                            setSelectedSlot,
                            loadingVm,
                            user,
                            users,
                        )
                    }

                    animatedComposable("Add Participants") {
                        AddParticipants(
                            reservationId = reservation,
                            reservations = reservations,
                            owner = user,
                            users = users,
                            reservationVm = reservationVm,
                            loadingVm = loadingVm,
                            context = activity
                        )
                    }

                    animatedComposable("Join Reservation") {
                        ShowReservation(
                            reservation,
                            navController,
                            linkReservations,
                            reservationVm,
                            setSelectedCourt,
                            setSelectedSlot,
                            loadingVm,
                            user,
                            users,
                            fromLink = true
                        )
                    }
                }
            }
        }
    }
}






