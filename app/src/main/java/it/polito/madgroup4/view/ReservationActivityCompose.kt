package it.polito.madgroup4.view

import android.content.Context
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import it.polito.madgroup4.model.LevelEnum
import it.polito.madgroup4.model.PlayingCourt
import it.polito.madgroup4.model.Reservation
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.model.Review
import it.polito.madgroup4.utility.CourtWithSlots
import it.polito.madgroup4.view.ui.theme.MADgroup4Theme
import it.polito.madgroup4.viewmodel.LoadingStateViewModel
import it.polito.madgroup4.viewmodel.ReservationViewModel
import it.polito.madgroup4.viewmodel.ReviewViewModel
import it.polito.madgroup4.viewmodel.SplashViewModel
import it.polito.madgroup4.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date


@AndroidEntryPoint
class ReservationActivityCompose : ComponentActivity() {

    val reservationVm by viewModels<ReservationViewModel>()

    val userVm by viewModels<UserViewModel>()

    private val reviewVm by viewModels<ReviewViewModel>()

    val loadingVm by viewModels<LoadingStateViewModel>()

    private val splashViewModel by viewModels<SplashViewModel>()


    val playingCourt = PlayingCourt(
        1,
        "Campo 1",
        10.0,
        "8:30",
        "20:30",
        "Tennis",
        "Via Filippo Turati, 7",
        "Torino",
        "TO",
        "3333333333",
        "campo1@gmail.com"
    )
    val playingCourt2 = PlayingCourt(
        2,
        "Campo 2",
        10.0,
        "8:30",
        "20:30",
        "Football",
        "Corso Francia",
        "Torino",
        "TO",
        "3333333334",
        "campo2@gmail.com"
    )
    val playingCourt3 = PlayingCourt(
        3,
        "Campo 3",
        10.0,
        "8:30",
        "20:30",
        "Tennis",
        "Via Marconi",
        "Torino",
        "TO",
        "3333333335",
        "campo3@gmail.com"
    )


    val formatter = SimpleDateFormat("dd/MM/yyyy")
    val reservation =
        Reservation(1, 1, "francesco@gmail.com", 1, formatter.parse(formatter.format(Date())))
    val reservation2 =
        Reservation(2, 2, "francesco@gmail.com", 2, formatter.parse(formatter.format(Date())))
    val reservation3 =
        Reservation(3, 1, "marco@gmail.com", 2, formatter.parse(formatter.format(Date())))
    val reservation4 = Reservation(4, 1, "francesco@gmail.com", 3, formatter.parse("11/05/2023"))
    val reservation5 = Reservation(5, 2, "marco@gmail.com", 10, formatter.parse("21/05/2023"))



    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition{splashViewModel.isLoading.value}

        super.onCreate(savedInstanceState)
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        reservationVm.savePlayingCourt(playingCourt)
        reservationVm.savePlayingCourt(playingCourt2)
        reservationVm.savePlayingCourt(playingCourt3)
        reservationVm.saveReservationUtility(reservation)
        reservationVm.saveReservationUtility(reservation2)
        reservationVm.saveReservationUtility(reservation3)
        reservationVm.saveReservationUtility(reservation4)
        reservationVm.saveReservationUtility(reservation5)

        val connectivity = isNetworkAvailable(this)

        setContent {
                MADgroup4Theme {
                    Surface(
                        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface
                    ) {
                        MainScreen(reservationVm, userVm, reviewVm, loadingVm, connectivity, this)
                    }
                }

        }
    }
}

fun isNetworkAvailable(context: Context): Boolean {
    val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (connectivity != null) {
        val info = connectivity.allNetworkInfo
        if (info != null) {
            for (i in info.indices) {
                Log.i("Class", info[i].state.toString())
                if (info[i].state == NetworkInfo.State.CONNECTED) {
                    return true
                }
            }
        }
    }
    return false
}


@Composable
fun MainScreen(
    reservationVm: ReservationViewModel,
    userVm: UserViewModel,
    reviewVm: ReviewViewModel,
    loadingVm: LoadingStateViewModel,
    connectivity: Boolean,
    activity: ReservationActivityCompose,
) {

    var user = userVm.user.observeAsState()

    val (reservation, setReservation) = remember {
        mutableStateOf(ReservationWithCourt(null, null))
    }

    val sports =
        listOf("Tennis", "Football", "Basketball", "Volleyball", "Baseball", "Rugby", "Hockey")
    val (remainingSports, setRemainingSports) = remember {
        mutableStateOf(sports.minus((user.value?.sports?.map { it.name!! } ?: emptyList()).toSet()))
    }

    val (selectedSport, setSelectedSport) = remember { mutableStateOf(sports[0]) }
    val (creationDate, setCreationDate) = remember { mutableStateOf(LocalDate.now()) }
    val (selectedDate, setSelectedDate) = remember { mutableStateOf(LocalDate.now()) }
    val (selectedCourt, setSelectedCourt) = remember { mutableStateOf(CourtWithSlots(null, null)) }
    val (selectedSlot, setSelectedSlot) = remember { mutableStateOf(-1) }
    val (showedCourt, setShowedCourt) = remember { mutableStateOf(PlayingCourt()) }
    val (reviews, setReviews) = remember { mutableStateOf(listOf<Review>()) }
    val (favoriteSport, setFavoriteSport) = remember { mutableStateOf<Int?>(null) }
    val (selectedLevel, setSelectedLevel) = remember { mutableStateOf(LevelEnum.BEGINNER.name) }


    //TODO: prendo l'id dalle preferences
    val userId: String = "francesco@gmail.com"


    reservationVm.getAllReservations(userId)

    Navigation(
        reservationVm,
        reviewVm,
        userVm,
        loadingVm,
        userId,
        reservation,
        setReservation,
        sports,
        remainingSports,
        setRemainingSports,
        selectedSport,
        setSelectedSport,
        creationDate,
        setCreationDate,
        selectedCourt,
        setSelectedCourt,
        selectedSlot,
        setSelectedSlot,
        showedCourt,
        setShowedCourt,
        reviews,
        setReviews,
        user,
        favoriteSport,
        setFavoriteSport,
        selectedLevel,
        setSelectedLevel,
        selectedDate,
        setSelectedDate,
        connectivity,
        activity
    )

}