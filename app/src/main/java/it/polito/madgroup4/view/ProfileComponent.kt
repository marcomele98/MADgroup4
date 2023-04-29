package it.polito.madgroup4.view

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import it.polito.madgroup4.ShowProfileActivity

@Composable
fun Profile() {
  val context = LocalContext.current
  val intent = Intent(context, ShowProfileActivity::class.java)
  context.startActivity(intent)
}
