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
import it.polito.madgroup4.view.ui.theme.MADgroup4Theme
import it.polito.madgroup4.model.PlayingCourt
import it.polito.madgroup4.model.Reservation
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.viewmodel.ReservationViewModel
import java.text.SimpleDateFormat
import java.util.Date


@AndroidEntryPoint
class ReservationActivityCompose : ComponentActivity() {

    val vm by viewModels<ReservationViewModel>()


    val playingCourt = PlayingCourt(1, "Campo 1", 10.0, "8:30", "20:30", "Tennis", "Via Filippo Turati, 7", "Torino", "TO", "3333333333", "campo1@gmail.com")
    val playingCourt2 = PlayingCourt(2, "Campo 2", 10.0, "8:30", "20:30", "Football", "Corso Francia", "Torino", "TO", "3333333334", "campo2@gmail.com")
    val playingCourt3 = PlayingCourt(3, "Campo 3", 10.0, "8:30", "20:30", "Tennis", "Via Marconi", "Torino", "TO", "3333333335", "campo3@gmail.com")


    val formatter = SimpleDateFormat(
        "dd/MM/yyyy"
    )
    val reservation = Reservation(1, 1, 1, formatter.parse(formatter.format(Date())))
    val reservation2 = Reservation(2, 2, 2, formatter.parse(formatter.format(Date())))
    val reservation3 = Reservation(3, 1, 2, formatter.parse(formatter.format(Date())))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.savePlayingCourt(playingCourt)
        vm.savePlayingCourt(playingCourt2)
        vm.savePlayingCourt(playingCourt3)
        vm.saveReservation(reservation)
        vm.saveReservation(reservation2)
        vm.saveReservation(reservation3)
        setContent {
            MADgroup4Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(vm)
                }
            }
        }
    }
}

@Composable
fun MainScreen(vm: ReservationViewModel) {

    val (reservation, setReservation) = remember {
        mutableStateOf(ReservationWithCourt(null, null))
    }
    Navbar(vm, reservation, setReservation)
}



/*@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
  MADgroup4Theme {
    MainScreen()
  }
}*/