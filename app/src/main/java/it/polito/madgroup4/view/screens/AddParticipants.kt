package it.polito.madgroup4.view.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.model.User
import it.polito.madgroup4.viewmodel.LoadingStateViewModel
import it.polito.madgroup4.viewmodel.ReservationViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AddParticipants(
    reservation: String,
    reservations: State<List<ReservationWithCourt>?>,
    owner: State<User?>,
    users: State<List<User>?>,
    reservationVm: ReservationViewModel,
    loadingVm: LoadingStateViewModel,
) {

    val reservation = reservations.value?.find { it.reservation?.id == reservation }

    var search by remember { mutableStateOf("") }

    var filteredUsers by remember { mutableStateOf(emptyList<User>()) }

    LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
        item {
            Text(
                text = "Invite with link",
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontStyle = FontStyle.Italic
            )
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Search friends",
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontStyle = FontStyle.Italic
            )
            Row() {
                OutlinedTextField(
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    value = search,
                    /*supportingText = { Text(text = "Max 200 characters") },*/
                    onValueChange = {
                        search = it.lowercase().trim();
                        filteredUsers = users.value?.filter {
                            it.id != owner.value?.id &&
                                    it.name!!.lowercase().trim()
                                        .startsWith(search) || it.nickname!!.lowercase().trim()
                                .startsWith(search) || it.surname!!.lowercase().trim().startsWith(
                                search
                            )
                        }!!.take(5)
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    singleLine = true,
                    label = { Text(text = "Search name or nickname") },
                    placeholder = { Text(text = "Search name or nickname") },
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }


        items(
            filteredUsers.size
        ) {
            val user = filteredUsers[it]
            Column(
                modifier = Modifier
                    .animateContentSize(
                        animationSpec = tween(
                            durationMillis = 500,
                            easing = FastOutSlowInEasing
                        )
                    )
                    .animateItemPlacement(
                        animationSpec = tween(
                            durationMillis = 500,
                            easing = FastOutSlowInEasing
                        )
                    )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (search == "") Modifier.height(0.dp) else Modifier.height(
                                70.dp
                            )
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = user?.name!! + " " + user.surname!!,
                            fontSize = 22.sp,
                            fontStyle = FontStyle.Italic
                        )
                        Text(text = "@" + user.nickname!!, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    if (reservation?.reservation?.reservationInfo?.confirmedUsers!!.contains(user.id))
                        Text(
                            text = "Participant", color = MaterialTheme.colorScheme.tertiary,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Bold
                        )
                    else if (reservation?.reservation?.reservationInfo?.pendingUsers!!.contains(user.id)) {
                        Text(
                            text = "Invited", color = MaterialTheme.colorScheme.primary,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Bold
                        )
                    } else
                        Button(onClick = {
                            reservationVm.invite(
                                user.id!!,
                                reservation?.reservation!!,
                                loadingVm,
                                "User invited successfully",
                                "Error while inviting the user",
                                "Add Participants",
                                "User ${owner.value?.name} ${owner.value?.surname} invited you to play",
                                user
                            )
                        }) {
                            Text(text = "Invite")
                        }
                }
                Divider(modifier = Modifier.height(if (search == "") 0.dp else 1.dp))
            }
        }


    }
}