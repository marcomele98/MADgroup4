package it.polito.madgroup4.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import it.polito.madgroup4.model.PlayingCourt
import it.polito.madgroup4.model.Reservation
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.model.Review
import it.polito.madgroup4.model.User
import it.polito.madgroup4.utility.CourtWithSlots
import it.polito.madgroup4.utility.formatDate
import it.polito.madgroup4.view.ui.theme.MADgroup4Theme
import it.polito.madgroup4.viewmodel.ReservationViewModel
import it.polito.madgroup4.viewmodel.ReviewViewModel
import it.polito.madgroup4.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date


@AndroidEntryPoint
class ReservationActivityCompose : ComponentActivity() {

    val vm by viewModels<ReservationViewModel>()

    val userVm by viewModels<UserViewModel>()

    val reviewVm by viewModels<ReviewViewModel>()


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
    val u1 =
        User(1, "Mario", "Rossi", "mario@gmail.com", "3333333333", "M", formatDate("03/04/1998"))
    val u2 =
        User(2, "Luca", "Bianchi", "bianchi@gmail.com", "3333333334", "M", formatDate("03/04/1997"))
    val u3 =
        User(3, "Giuseppe", "Verdi", "verdi@gmail.com", "3333333335", "M", formatDate("03/04/1996"))


    val formatter = SimpleDateFormat("dd/MM/yyyy")
    val reservation = Reservation(1, 1, 1, 1, formatter.parse(formatter.format(Date())))
    val reservation2 = Reservation(2, 2, 1, 2, formatter.parse(formatter.format(Date())))
    val reservation3 = Reservation(3, 1, 2, 2, formatter.parse(formatter.format(Date())))
    val reservation4 = Reservation(4, 1, 1, 3, formatter.parse("11/05/2023"))
    val reservation5 = Reservation(5, 2, 2, 3, formatter.parse("14/05/2023"))/*    val reservation6 = Reservation(6, 1, 2, formatter.parse(formatter.format(Date())))
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
        vm.savePlayingCourt(playingCourt)
        vm.savePlayingCourt(playingCourt2)
        vm.savePlayingCourt(playingCourt3)
        userVm.saveUser(u1)
        userVm.saveUser(u2)
        userVm.saveUser(u3)
        vm.saveReservation(reservation)
        vm.saveReservation(reservation2)
        vm.saveReservation(reservation3)
        vm.saveReservation(reservation4)
        vm.saveReservation(reservation5)/*        vm.saveReservation(reservation6)
                vm.saveReservation(reservation7)
                vm.saveReservation(reservation8)
                vm.saveReservation(reservation9)
                vm.saveReservation(reservation10)
                vm.saveReservation(reservation11)
                vm.saveReservation(reservation12)
                vm.saveReservation(reservation13)
                vm.saveReservation(reservation14)
                vm.saveReservation(reservation15)
                vm.saveReservation(reservation16)*/

        setContent {
            MADgroup4Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface
                ) {
                    MainScreen(vm, userVm, reviewVm)
                }
            }
        }
    }
}

@Composable
fun MainScreen(vm: ReservationViewModel, userVm: UserViewModel, reviewVm: ReviewViewModel) {

    val (reservation, setReservation) = remember {
        mutableStateOf(ReservationWithCourt(null, null))
    }

    val sports = listOf("Tennis", "Football")
    val (selectedSport, setSelectedSport) = remember { mutableStateOf(sports[0]) }
    val (creationDate, setCreationDate) = remember { mutableStateOf(LocalDate.now()) }
    val (selectedCourt, setSelectedCourt) = remember { mutableStateOf(CourtWithSlots(null, null)) }
    val (selectedSlot, setSelectedSlot) = remember {
        mutableStateOf(-1)
    }
    val (showedCourt, setShowedCourt) = remember { mutableStateOf(PlayingCourt()) }

    val (showedReview, setShowedReview) = remember { mutableStateOf(Review(1,1,1, "Test", date=Date())) }
    val (reviews, setReviews) = remember { mutableStateOf(listOf<Review>()) }


    //TODO: prendo l'id dalle preferences
    val userId: Long = 1

    userVm.getUser(userId)

    vm.getAllReservations(userId)

    Navigation(
        vm,
        reservation,
        setReservation,
        sports,
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
        userVm,
        userId,
        reviewVm,
        reviews,
        setReviews,
        showedReview,
        setShowedReview
    )

}


/*@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
  MADgroup4Theme {
    MainScreen()
  }
}*/