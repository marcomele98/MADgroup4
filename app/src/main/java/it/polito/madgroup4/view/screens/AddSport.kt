package it.polito.madgroup4.view.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.madgroup4.model.Sport
import it.polito.madgroup4.view.components.LevelCardSelector
import it.polito.madgroup4.view.components.SportCardSelector
import it.polito.madgroup4.viewmodel.LoadingStateViewModel
import it.polito.madgroup4.viewmodel.UserViewModel


@Composable
fun AddSport(
    userVm: UserViewModel,
    loadingVm: LoadingStateViewModel,
    navController: NavController,
    selectedSport: String,
    selectedLevel: String,
    setTopBarAction: (() -> Unit) -> Unit
) {


    LaunchedEffect(selectedSport, selectedLevel) {
        setTopBarAction {
            val user = userVm.user.value!!
            user.sports = user.sports.plus(Sport(selectedSport, selectedLevel))
            user.sports = user.sports.sortedBy { it.name }
            userVm.saveUser(
                user,
                loadingVm,
                "New favorite sport added successfully",
                "Error while adding new favorite sport",
                null,
                "Profile"
            )
        }
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = "Select a sport",
            fontSize = 23.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        )

        Spacer(modifier = Modifier.height(10.dp))

        SportCardSelector(
            sport = selectedSport,
            onClick = { navController.navigate("Select Your Sport") }
        )

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = "Select a Level",
            fontSize = 23.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        )

        Spacer(modifier = Modifier.height(10.dp))

        LevelCardSelector(
            level = selectedLevel,
            onClick = { navController.navigate("Select Your Level") })
    }

}


