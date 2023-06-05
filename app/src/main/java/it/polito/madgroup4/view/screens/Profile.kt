package it.polito.madgroup4.view.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.madgroup4.R
import it.polito.madgroup4.model.LevelEnum
import it.polito.madgroup4.model.User
import it.polito.madgroup4.view.components.SportCard
import it.polito.madgroup4.viewmodel.UserViewModel

@Composable
fun Profile(
    user: State<User?>,
    setFavoriteSport: (Int) -> Unit,
    navController: NavController,
    userVm: UserViewModel,
    setSelectedLevel: (String) -> Unit,
    setSelectedSport: (String) -> Unit,
    remainingSports: List<String>,
    setRemainingSports: (List<String>) -> Unit,
    sports: List<String>,
) {


    LaunchedEffect(Unit) {
        setRemainingSports(sports.minus((user.value?.sports?.map { it.name!! }
            ?: emptyList()).toSet()))
    }

    val userPic = userVm.userPhoto.observeAsState()

    if (user.value != null) {

        Column(
            Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {

            Column(
                Modifier.fillMaxWidth()
            ) {
                if (userPic.value != null) {
                    Image(
                        bitmap = userPic.value!!.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(1.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                            .then(Modifier.align(Alignment.CenterHorizontally))
                    )
                } else if (user.value?.photo == true) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .align(Alignment.CenterHorizontally)
                            .border(1.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(1.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                            .then(Modifier.align(Alignment.CenterHorizontally))
                    )
                }

                Spacer(modifier = Modifier.height(25.dp))

                Text(
                    text = user.value?.name + " " + user.value?.surname,
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp,
                    modifier = Modifier.fillMaxWidth()
                )


                Text(
                    text = "@" + user.value?.nickname,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    modifier = Modifier.fillMaxWidth()
                )

            }


            Spacer(modifier = Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Your Sports",
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontStyle = FontStyle.Italic
                )
                Spacer(modifier = Modifier.weight(1f))
                if (remainingSports.isNotEmpty()) {
                    IconButton(onClick = {
                        navController.navigate("Add Sport")
                        setSelectedLevel(LevelEnum.BEGINNER.name)
                        setSelectedSport(remainingSports[0])
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            modifier = Modifier
                                .size(30.dp)
                                .alpha(0.6f),
                            //tint = MaterialTheme.colorScheme.secondary,
                            contentDescription = null
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))


            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                if (user.value?.sports?.size == 0) {
                    Text(
                        text = "You have not added any sport yet",
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 18.sp,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {


                    items(user.value?.sports?.size!!) { index ->
                        SportCard(sport = user.value?.sports?.get(index)!!, onClick = {
                            setFavoriteSport(index)
                            setSelectedLevel(user.value?.sports?.get(index)?.level!!)
                            navController.navigate("Your Sport")
                        })
                    }
                }
            }
        }
    } else {
        LoadingScreen()
    }
}

