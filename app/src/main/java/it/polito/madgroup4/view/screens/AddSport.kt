package it.polito.madgroup4.view.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.polito.madgroup4.model.Sport
import it.polito.madgroup4.view.components.SportCardSelector
import it.polito.madgroup4.viewmodel.LoadingStateViewModel
import it.polito.madgroup4.viewmodel.UserViewModel

@Composable
fun AddSport(
    userVm: UserViewModel,
    loadingVm: LoadingStateViewModel,
    navController: NavController,
    selectedSport: String,
    setFavoriteSport: (Int) -> Unit,
) {

    Column(
        Modifier.padding(horizontal = 16.dp)
    ) {
        Row() {
            SportCardSelector(sport = selectedSport, navController = navController, route = "Select New Sport")
        }

        /*Spacer(modifier = Modifier.height(40.dp))

        Row() {
            Text(
                text = "Select Your Level",
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontStyle = FontStyle.Italic
            )

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = { navController.navigate("Select Level") }) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    modifier = Modifier
                        .size(30.dp)
                        .alpha(0.6f),
                    contentDescription = null,
                )
            }
        }

        Text(
            text = "Your level",
            modifier = Modifier.alpha(0.5f),
            fontSize = 18.sp,
            fontStyle = FontStyle.Italic,
        )*/

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                val user = userVm.user.value!!
                user.sports = user.sports.plus(Sport(selectedSport, "BEGINNER"))
                userVm.saveUser(
                    user,
                    loadingVm,
                    "New favorite sport added successfully",
                    "Error while adding new favorite sport"
                )
                setFavoriteSport(user.sports.size - 1) // da fare il check se l'add dello sport è andata a buon fine
                navController.navigate("Select Level")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text(text = "Add Sport and select you level")
        }

    }

}