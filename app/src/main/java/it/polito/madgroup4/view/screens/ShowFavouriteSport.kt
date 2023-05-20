package it.polito.madgroup4.view.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.madgroup4.model.Sport
import it.polito.madgroup4.view.components.AchievementCard

@Composable
fun ShowFavouriteSport(
    sport: Sport,
    navController: NavController
) {

    //val sortedAchievements = sport.achievements?.sortedByDescending { formatDate(it.date!!) }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)

    ) {
        item {
            /*
            Row() {
                Icon(
                    imageSelector(sport.name!!), contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = sport.name, fontSize = 22.sp
                )
                Spacer(
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            */
            Row() {
                Text(
                    text = "Your Level",
                    fontSize = 23.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    modifier = Modifier
                        .size(30.dp)
                        .alpha(0.6f)
                        .clickable {},
                    //tint = MaterialTheme.colorScheme.secondary,
                    contentDescription = null
                )
            }
            Text(
                text = sport.level!!,
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
        }

        items(sport?.achievements?.size ?: 0) { index ->
            AchievementCard(achievement = sport?.achievements!![index])
        }
    }


}