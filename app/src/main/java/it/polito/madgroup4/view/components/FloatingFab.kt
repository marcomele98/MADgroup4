package it.polito.madgroup4.view.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun FloatingFab(navController: NavHostController) {
    FloatingActionButton(
        onClick = {
            navController.navigate("CreateReservation")
        }
    ) {
        Icon(Icons.Filled.Add, contentDescription = "Aggiungi")
    }
}