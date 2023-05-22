package it.polito.madgroup4.view.screens

import android.net.Uri
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import it.polito.madgroup4.R
import it.polito.madgroup4.model.LevelEnum
import it.polito.madgroup4.model.User
import it.polito.madgroup4.utility.uriToBitmap
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
) {

    val context = LocalContext.current

    val contactItems = listOf(
        Pair(Icons.Default.Email, user.value?.email!!),
//        Pair(Icons.Default.Transgender, user.gender)
    )

    Column(Modifier.padding(horizontal = 16.dp).fillMaxSize()) {

        Column(
            Modifier.fillMaxWidth()
        ) {

            if (user.value?.photo != null && user.value?.photo != "") {
                uriToBitmap(Uri.parse(user.value?.photo!!), context)?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(1.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                            .then(Modifier.align(Alignment.CenterHorizontally))
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

        ContactItem(
            icon = contactItems[0].first,
            text = contactItems[0].second
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row {
            Text(
                text = "Your Sports",
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontStyle = FontStyle.Italic
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Outlined.Add,
                modifier = Modifier
                    .size(30.dp)
                    .alpha(0.6f)
                    .clickable {
                        navController.navigate("Add Sport")
                        setSelectedLevel(LevelEnum.BEGINNER.name)
                        setSelectedSport(remainingSports[0])
                    },
                //tint = MaterialTheme.colorScheme.secondary,
                contentDescription = null
            )
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

}

@Composable
fun ContactItem(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(icon, contentDescription = null)

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = text,
            fontSize = 22.sp
        )
    }
}
