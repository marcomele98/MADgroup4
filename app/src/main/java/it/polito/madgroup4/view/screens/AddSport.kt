package it.polito.madgroup4.view.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.polito.madgroup4.view.components.SportCardSelector
import it.polito.madgroup4.viewmodel.LoadingStateViewModel
import it.polito.madgroup4.viewmodel.UserViewModel

@Composable
fun AddSport(
    userVm: UserViewModel,
    loadingVm: LoadingStateViewModel,
    navController: NavController,
    selectedSport: String
) {

    Column(
        Modifier.padding(horizontal = 16.dp)
    ) {
        Row() {
            SportCardSelector(sport = selectedSport, navController = navController)
        }

    }

}