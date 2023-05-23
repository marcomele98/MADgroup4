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
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import it.polito.madgroup4.model.LevelEnum
import it.polito.madgroup4.model.PlayingCourt
import it.polito.madgroup4.model.Reservation
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.model.Review
import it.polito.madgroup4.utility.CourtWithSlots
import it.polito.madgroup4.view.ui.theme.MADgroup4Theme
import it.polito.madgroup4.viewmodel.ReservationViewModel
import it.polito.madgroup4.viewmodel.ReviewViewModel
import it.polito.madgroup4.viewmodel.UserViewModel
import it.polito.madgroup4.viewmodel.LoadingStateViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date


@AndroidEntryPoint
class ReservationActivityCompose : ComponentActivity() {

    val reservationVm by viewModels<ReservationViewModel>()

    val userVm by viewModels<UserViewModel>()

    val reviewVm by viewModels<ReviewViewModel>()

    val loadingVm by viewModels<LoadingStateViewModel>()

    private val db = FirebaseFirestore.getInstance()

    /*val user1 = db
        .collection("users")
        .document("48JnBn7vpjvj0minb62P")
        .get()
        .addOnSuccessListener { res ->
            val users =
                res.toObject(User::class.java)
            //use it as needed
        }
        .addOnFailureListener {
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
        }
*/

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
    /*    val u1 =
            User(1, "Mario", "Rossi", "mariorossi98", "mario@gmail.com" )
        val u2 =
            User(2, "Luca", "Bianchi", "lucabianchi97", "bianchi@gmail.com", )
        val u3 =
            User(3, "Giuseppe", "Verdi", "giuseppeverdi96", "verdi@gmail.com")*/

    val formatter = SimpleDateFormat("dd/MM/yyyy")
    val reservation =
        Reservation(1, 1, "francesco@gmail.com", 1, formatter.parse(formatter.format(Date())))
    val reservation2 =
        Reservation(2, 2, "francesco@gmail.com", 2, formatter.parse(formatter.format(Date())))
    val reservation3 =
        Reservation(3, 1, "marco@gmail.com", 2, formatter.parse(formatter.format(Date())))
    val reservation4 = Reservation(4, 1, "francesco@gmail.com", 3, formatter.parse("11/05/2023"))
    val reservation5 = Reservation(5, 2, "marco@gmail.com", 10, formatter.parse("21/05/2023"))

    /*  val reservation6 = Reservation(6, 1, 2, formatter.parse(formatter.format(Date())))
        val reservation7 = Reservation(7, 1, 0, formatter.parse(formatter.format(Date())))
        val reservation8 = Reservation(8, 1, 5, formatter.parse(formatter.format(Date())))
        val reservation9 = Reservation(9, 1, 6, formatter.parse(formatter.format(Date())))
        val reservation10 = Reservation(10, 1, 7, formatter.parse(formatter.format(Date())))
        val reservation11 = Reservation(11, 1, 8, formatter.parse(formatter.format(Date())))
        val reservation12 = Reservation(12, 1, 9, formatter.parse(formatter.format(Date())))
        val reservation13 = Reservation(13, 1, 10, formatter.parse(formatter.format(Date())))
        val reservation14 = Reservation(14, 1, 11, formatter.parse(formatter.format(Date())))
        val reservation15 = Reservation(15, 1, 3, formatter.parse(formatter.format(Date())))
        val reservation16 = Reservation(16, 1, 4, formatter.parse(formatter.format(Date())))*/

    override fun onCreate(savedInstanceState: Bundle?) {
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

    val context = LocalContext.current

    /*
        val sharedPref = context.getSharedPreferences("USER", Context.MODE_PRIVATE) ?: null
        var profile = Profile()
        if (sharedPref != null) {
            profile = Profile.getFromPreferences(sharedPref!!)
        }
    */

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

    /*    userVm.getUser(userId)*/

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