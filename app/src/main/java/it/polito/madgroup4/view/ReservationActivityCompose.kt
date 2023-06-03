package it.polito.madgroup4.view

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import it.polito.madgroup4.R
import it.polito.madgroup4.model.Court
import it.polito.madgroup4.model.LevelEnum
import it.polito.madgroup4.model.ReservationInfo
import it.polito.madgroup4.model.Review
import it.polito.madgroup4.model.Stuff
import it.polito.madgroup4.view.ui.theme.MADgroup4Theme
import it.polito.madgroup4.viewmodel.LoadingStateViewModel
import it.polito.madgroup4.viewmodel.ReservationViewModel
import it.polito.madgroup4.viewmodel.SplashViewModel
import it.polito.madgroup4.viewmodel.UserViewModel
import java.time.LocalDate


class ReservationActivityCompose : ComponentActivity() {

    val reservationVm by viewModels<ReservationViewModel>()

    val loadingVm by viewModels<LoadingStateViewModel>()

    private val splashViewModel by viewModels<SplashViewModel>()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Notifications permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(
                this,
                "FCM can't post notifications without POST_NOTIFICATIONS permission",
                Toast.LENGTH_LONG,
            ).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { splashViewModel.isLoading.value }

        super.onCreate(savedInstanceState)
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val userVm = UserViewModel(reservationVm)

        val connectivity = isNetworkAvailable(this)

        setContent {
            MADgroup4Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface
                ) {
                    MainScreen(
                        reservationVm, userVm, loadingVm, connectivity, this
                    )
                }
            }

        }



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelId = getString(R.string.default_notification_channel_id)
            val channelName = getString(R.string.default_notification_channel_name)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_LOW,
                ),
            )
        }

        askNotificationPermission()

        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener { pendingDynamicLinkData ->
                Log.i("Test dynamic link", "We have a Dynamic Link")
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                }
                if (deepLink != null) {
                    Log.i(
                        "Test dynamic link", "We have a Dynamic Link"
                    )
                    val reservationId = deepLink!!.getQueryParameter("reservationId")
                    // poi dobbiamo gestirlo
                }
            }
            .addOnFailureListener { e ->
                Log.i("ERROR", "ERROR")
            }

    }

    private fun askNotificationPermission() {
        // This is only necessary for API Level > 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
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

    val user = userVm.user.observeAsState()

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

    val reservations = reservationVm.allRes.observeAsState(initial = null)

    val sharedReservations = reservationVm.sharedReservations.observeAsState(initial = null)

    val courtsWithSlots = reservationVm.playingCourts.observeAsState(initial = null)

    val (stuff, setStuff) = remember {
        mutableStateOf(
            listOf<Stuff>()
        )
    }

    val (reservationInfo, setReservationInfo) = remember {
        mutableStateOf(
            ReservationInfo()
        )
    }

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
        courtsWithSlots,
        stuff,
        setStuff,
        reservationInfo,
        setReservationInfo,
        sharedReservations
    )

}