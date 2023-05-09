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
import it.polito.madgroup4.utility.CourtWithSlots
import it.polito.madgroup4.view.ui.theme.MADgroup4Theme
import it.polito.madgroup4.viewmodel.ReservationViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date

@AndroidEntryPoint
class ProfileCompose : ComponentActivity() {

  //val vm by viewModels<ReservationViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      MADgroup4Theme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.surface
        ) {
          MainScreen(/*vm*/)
        }
      }
    }
  }
}

@Composable
fun MainScreen(/*vm: ReservationViewModel*/) {

}