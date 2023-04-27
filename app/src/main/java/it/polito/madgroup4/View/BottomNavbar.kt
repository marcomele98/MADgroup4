package it.polito.madgroup4.View

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navbar() {
  val navController = rememberNavController()
  Scaffold (
    bottomBar = {
      BottomAppBar() {
        TextButton(
          onClick = { navController.navigate("Profile") },
          modifier = Modifier.weight(1f)
        ) {
          Text(text = "Profile", color = MaterialTheme.colorScheme.primary)
        }
        TextButton(
          onClick = { navController.navigate("Home") },
          modifier = Modifier.weight(1f)
        ) {
          Text(text = "Home", color = MaterialTheme.colorScheme.primary)
        }
      }
    }
  ) {
    Box(Modifier.padding(it)) {
      NavHost(navController = navController, startDestination = "Profile") {
        composable("Profile") { Profile() }
        composable("Home") { Home() }
      }
    }
  }
}