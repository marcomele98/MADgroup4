package it.polito.madgroup4.view.components

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import it.polito.madgroup4.view.ShowProfileActivity

@Composable
fun Profile() {
  val context = LocalContext.current
  val intent = Intent(context, ShowProfileActivity::class.java)
  context.startActivity(intent)
}
