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
import it.polito.madgroup4.model.Court
import it.polito.madgroup4.model.LevelEnum
import it.polito.madgroup4.model.Review
import it.polito.madgroup4.view.ui.theme.MADgroup4Theme
import it.polito.madgroup4.viewmodel.LoadingStateViewModel
import it.polito.madgroup4.viewmodel.ReservationViewModel
import it.polito.madgroup4.viewmodel.SplashViewModel
import it.polito.madgroup4.viewmodel.UserViewModel
import java.time.LocalDate


@AndroidEntryPoint
class ReservationActivityCompose : ComponentActivity() {

    val reservationVm by viewModels<ReservationViewModel>()

    val userVm by viewModels<UserViewModel>()

    val loadingVm by viewModels<LoadingStateViewModel>()

    private val splashViewModel by viewModels<SplashViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { splashViewModel.isLoading.value }

        super.onCreate(savedInstanceState)
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT


        val connectivity = isNetworkAvailable(this)

        setContent {
            MADgroup4Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface
                ) {
                    MainScreen(
                        reservationVm,
                        userVm,
                        loadingVm,
                        connectivity,
                        this
                    )
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
    loadingVm: LoadingStateViewModel,
    connectivity: Boolean,
    activity: ReservationActivityCompose,
) {

    var user = userVm.user.observeAsState()

    val (reservation, setReservation) = remember {
        mutableStateOf("")
    }

    val sports =
        listOf("Tennis", "Football", "Basketball", "Volleyball", "Baseball", "Rugby", "Hockey")
    val (remainingSports, setRemainingSports) = remember {
        mutableStateOf(sports.minus((user.value?.sports?.map { it.name!! } ?: emptyList()).toSet()))
    }

    val (selectedSport, setSelectedSport) = remember { mutableStateOf(sports[0]) }
    val (creationDate, setCreationDate) = remember { mutableStateOf(LocalDate.now()) }
    val (selectedDate, setSelectedDate) = remember { mutableStateOf(LocalDate.now()) }
    val (selectedCourt, setSelectedCourt) = remember { mutableStateOf("") }
    val (selectedSlot, setSelectedSlot) = remember { mutableStateOf(-1) }
    val (showedCourt, setShowedCourt) = remember { mutableStateOf(Court()) }
    val (reviews, setReviews) = remember { mutableStateOf(listOf<Review>()) }
    val (favoriteSport, setFavoriteSport) = remember { mutableStateOf<Int?>(null) }
    val (selectedLevel, setSelectedLevel) = remember { mutableStateOf(LevelEnum.BEGINNER.name) }

    var reservations = reservationVm.allRes.observeAsState(initial = null)

    val courtsWithSlots = reservationVm.playingCourts.observeAsState(initial = null)

    Navigation(
        reservationVm,
        userVm,
        loadingVm,
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
        activity,
        reservations,
        courtsWithSlots
    )

}