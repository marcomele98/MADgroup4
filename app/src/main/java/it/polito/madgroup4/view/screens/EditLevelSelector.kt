package it.polito.madgroup4.view.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.madgroup4.model.LevelEnum
import it.polito.madgroup4.model.User
import it.polito.madgroup4.viewmodel.LoadingStateViewModel
import it.polito.madgroup4.viewmodel.UserViewModel


@Composable
fun EditLevelSelector(
    sport: Int,
    setTopBarAction: (() -> Unit) -> Unit,
    userVm: UserViewModel,
    user: State<User?>,
    loadingVm: LoadingStateViewModel,
) {


    val levels = LevelEnum.values().map { it.name }

    val (selectedLev, setSelectedLev) = remember { mutableStateOf(user.value!!.sports[sport].level) }


    setTopBarAction {
        val newSports = user.value!!.sports.toMutableList()
        newSports[sport].level = selectedLev
        userVm.saveUser(
            user.value!!.copy(sports = newSports),
            loadingVm,
            "Level changed successfully",
            "Error while changing level",
            null,
            "Your Sport"
        )
    }


    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
    )

    {
        items(levels.size) { index ->
            ElevatedCard(
                modifier = Modifier
                    .padding(bottom = 10.dp),
                colors = if (levels[index] == selectedLev) {
                    CardDefaults.cardColors()
                } else {
                    CardDefaults.outlinedCardColors()
                },
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable {
                                setSelectedLev(levels[index])
                            }
                    ) {
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = levels[index],
                            fontSize = 30.sp
                        )
                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                        )
                    }
                }
            }
        }
    }
}