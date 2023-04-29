package it.polito.madgroup4.View

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import it.polito.madgroup4.View.ui.theme.MADgroup4Theme

@AndroidEntryPoint
class ReservationActivityCompose : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      MADgroup4Theme {
        // A surface container using the 'background' color from the theme
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          MainScreen()
        }
      }
    }
  }
}

@Composable
fun MainScreen() {
  Navbar()
}

/*@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
  MADgroup4Theme {
    MainScreen()
  }
}*/