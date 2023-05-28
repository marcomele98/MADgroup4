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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavBar(
    navController: NavHostController
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    NavigationBar(
        modifier = Modifier.height(75.dp),
    ) {

        NavigationBarItem(
            selected = navBackStackEntry?.destination?.route == "Playing Courts",
            icon = {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = "Playing Courts"
                )
            },
            label = { Text("Playing Courts") },
            onClick = {
                navController.navigate("Playing Courts")
            }
        )

        NavigationBarItem(
            selected = navBackStackEntry?.destination?.route == "Reservations",
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
            selected = navBackStackEntry?.destination?.route == "Profile",
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

