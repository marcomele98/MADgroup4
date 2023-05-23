package it.polito.madgroup4.view.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import it.polito.madgroup4.model.Sport
import it.polito.madgroup4.model.User
import it.polito.madgroup4.view.components.AchievementCard
import it.polito.madgroup4.viewmodel.LoadingStateViewModel
import it.polito.madgroup4.viewmodel.UserViewModel

@Composable
fun ShowFavouriteSport(
    sport: Int,
    user: State<User?>,
    userVm: UserViewModel,
    navController: NavController,
    loadingVm: LoadingStateViewModel,
    setSelectedLevel: (String) -> Unit
) {

    val openDialog = remember { mutableStateOf(false) }
    //val openDialogCard = remember { mutableStateOf(false) }
    var deletedAchievement by remember { mutableStateOf<Int?>(null) }

    if (openDialog.value) {
        AlertDialog(onDismissRequest = {
            openDialog.value = false
        }, confirmButton = {
            TextButton(onClick = {
                val user = userVm.user.value!!
                user.sports = user.sports.minus(user.sports[sport])
                userVm.saveUser(
                    user,
                    loadingVm,
                    "Sport deleted successfully",
                    "Error while deleting the sport"
                )
                navController.navigate("Profile")
                openDialog.value = false
            }) {
                Text("Remove")
            }
        }, dismissButton = {
            TextButton(onClick = {
                openDialog.value = false
            }) {
                Text("Cancel")
            }
        }, title = {
            Text("Remove Sport")
        }, text = {
            Text(
                text = "Are you sure you want to remove this sport from you favourite list?\nIf you proceed you will lose all the achievements related to this sport.",
            )
        }, properties = DialogProperties(
            dismissOnBackPress = true, dismissOnClickOutside = true
        )
        )
    }

    if (/*openDialogCard.value*/ deletedAchievement != null) {
        AlertDialog(onDismissRequest = {
            deletedAchievement = null
            //openDialogCard.value = false
        }, confirmButton = {
            TextButton(onClick = {
                val user = userVm.user.value!!
                user.sports[sport].achievements =
                    user.sports[sport].achievements.minus(user.sports[sport].achievements[deletedAchievement!!])
                userVm.saveUser(
                    user,
                    loadingVm,
                    "Achievement deleted successfully",
                    "Error while deleting the achievement"
                )
                //openDialogCard.value = false
                deletedAchievement = null
            }) {
                Text("Remove")
            }
        }, dismissButton = {
            TextButton(onClick = {
                //openDialogCard.value = false
                deletedAchievement = null
            }) {
                Text("Cancel")
            }
        }, title = {
            Text("Remove Achievement")
        }, text = {
            Text(
                text = "Are you sure you want to remove this achievement?",
            )
        }, properties = DialogProperties(
            dismissOnBackPress = true, dismissOnClickOutside = true
        )
        )
    }

    if(user.value?.sports!!.filter { it.name == user.value?.sports?.getOrNull(sport)?.name }.isNotEmpty()) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {

            Row() {
                Text(
                    text = "Your Level",
                    fontSize = 23.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontStyle = FontStyle.Italic
                )
                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = {
                    setSelectedLevel(user.value?.sports?.get(sport)?.level!!)
                    navController.navigate("Edit Your Level")
                }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        modifier = Modifier
                            .size(30.dp)
                            .alpha(0.6f),
                        //tint = MaterialTheme.colorScheme.secondary,
                        contentDescription = null,
                    )
                }
            }
            Text(
                text = user.value?.sports?.get(sport)?.level!!,
                fontSize = 18.sp,
                fontStyle = FontStyle.Italic,
                //color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row {
                Text(
                    text = "Your Achivements",
                    fontSize = 23.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Outlined.Add,
                    modifier = Modifier
                        .size(30.dp)
                        .alpha(0.6f)
                        .clickable {
                            navController.navigate("Create Achievement")
                        },
                    //tint = MaterialTheme.colorScheme.secondary,
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.height(10.dp))


            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                if (user.value?.sports?.getOrNull(sport)?.achievements?.size == 0) {
                    Text(
                        text = "No achievements yet",
                        modifier = Modifier
                            .align(Alignment.Center),
                        fontSize = 18.sp,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()

                ) {
                    items(user.value?.sports?.getOrNull(sport)?.achievements?.size ?: 0) { index ->
                        AchievementCard(
                            achievement = user.value?.sports?.get(sport)?.achievements!![index],
                            onDelete = {
                                //openDialogCard.value = true
                                deletedAchievement = index
                                /*userVm.removeAchievement(
                                    user.value?.sports?.get(sport)?.name!!,
                                    user.value?.sports?.get(sport)?.achievements!![index].title!!,
                                    loadingVm
                                )*/
                            }
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(8.dp))

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    openDialog.value = !openDialog.value
                }
            ) {
                Text(text = "Delete Sport")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

    }
}