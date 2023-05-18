package it.polito.madgroup4.view.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.madgroup4.R
import it.polito.madgroup4.model.Profile
import it.polito.madgroup4.model.User
import it.polito.madgroup4.utility.formatDate
import it.polito.madgroup4.utility.uriToBitmap
import java.io.FileDescriptor
import java.io.IOException
import java.text.SimpleDateFormat

@Composable
fun Profile(setEditedUser : (User) -> Unit) {

    val context = LocalContext.current

    val sharedPref = context.getSharedPreferences("USER", Context.MODE_PRIVATE) ?: null
    var profile = Profile()
    if (sharedPref != null) {
        profile = Profile.getFromPreferences(sharedPref!!)
    }

    val user = remember { mutableStateOf(
        User(
            name = profile.name,
            surname = profile.surname,
            nickname = profile.nickname,
            email = profile.email,
            photo = profile.imageUri
        )) }


    setEditedUser(user.value)

    val contactItems = listOf(
        Pair(Icons.Default.Email, user.value.email!!),
//        Pair(Icons.Default.Transgender, user.gender)
    )

    Column(Modifier.fillMaxSize()) {

        Column(
            Modifier.fillMaxWidth()
        ) {

            if (user.value.photo != null) {
                uriToBitmap(Uri.parse(user.value.photo!!), context)?.let {
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
            /*Image(
                painter = painterResource(R.drawable.profile),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                    .then(Modifier.align(Alignment.CenterHorizontally))
            )*/

            Spacer(modifier = Modifier.height(25.dp))


            Text(
                text = user.value.name + " " + user.value.surname,
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                modifier = Modifier.fillMaxWidth()
            )


            Text(
                text = "@" + user.value.nickname,
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth()
            )

        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }

            items(contactItems.size) { index ->
                ContactItem(
                    icon = contactItems[index].first,
                    text = contactItems[index].second
                )
            }
        }
    }
}

@Composable
fun ContactItem(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()) {
        Icon(icon, contentDescription = null)

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = text,
            fontSize = 22.sp
        )

        Spacer(modifier = Modifier.height(50.dp))
    }
}
