package it.polito.madgroup4.View

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun Home() { // si rompe se faccio indietro
  val context = LocalContext.current
  val intent = Intent(context, ReservationsActivity::class.java)
  context.startActivity(intent)
}