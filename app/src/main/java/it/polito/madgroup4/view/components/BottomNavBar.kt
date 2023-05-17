package it.polito.madgroup4.view.components

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun BottomNavBar(
    navController: NavHostController
){

    NavigationBar(
        modifier = Modifier.height(75.dp),
    ) {

        NavigationBarItem(
            selected = navController.currentBackStackEntry?.destination?.route == "Playing Courts",
            icon = {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = "Courts"
                )
            },
            label = { Text("Playing Courts") },
            onClick = {
                navController.navigate("Playing Courts")
            }
        )

        NavigationBarItem(
            selected = navController.currentBackStackEntry?.destination?.route == "Reservations",
            icon = {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = "Reservations"
                )
            },
            label = { Text("Reservations") },
            onClick = {
                navController.navigate("Reservations")
            }
        )

        NavigationBarItem(
            selected = navController.currentBackStackEntry?.destination?.route == "Profile",
            icon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Profile"
                )
            },
            label = { Text("Profile") },
            onClick = { navController.navigate("Profile") }
        )

    }

}