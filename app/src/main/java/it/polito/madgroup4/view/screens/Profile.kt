package it.polito.madgroup4.view.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.madgroup4.R
import it.polito.madgroup4.model.User
import it.polito.madgroup4.utility.formatDate

@Composable
fun Profile1(
    user: User = User(
        name = "Marco",
        surname = "Mele",
        nickname = "marcomele98",
        email = "marcomele98@gmail.com",
        phone = "3334545451",
        gender = "Male",
        birthday = formatDate("01/01/1998"),
    )
) {

    print("Non vaaaaa")

    val contactItems = listOf(
        Pair(R.drawable.phone, user.phone),
        Pair(R.drawable.email, user.email),
        Pair(R.drawable.cake, user.birthday.toString()),
        Pair(R.drawable.gender, user.gender)
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(R.drawable.top_background),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )

            Image(
                painter = painterResource(R.drawable.profile),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(125.dp)
                    .align(Alignment.Center)
                    .padding(16.dp)
                    .clip(CircleShape)
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(2f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            item {
                Text(
                    text = user.name + " " + user.surname,
                    fontSize = 40.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Text(
                    text = "@" + user.nickname,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }

            items(contactItems.size) { index ->
                ContactItem(
                    iconResId = contactItems[index].first,
                    text = contactItems[index].second
                )
            }
        }
    }
}

@Composable
fun ContactItem(iconResId: Int, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(iconResId),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(5.dp))

        Text(
            text = text,
            fontSize = 20.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}