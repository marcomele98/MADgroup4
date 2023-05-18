package it.polito.madgroup4.view.screens

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.madgroup4.R
import it.polito.madgroup4.model.User
import it.polito.madgroup4.utility.formatDate
import java.text.SimpleDateFormat

@Composable
fun Profile(
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

    val formatter = SimpleDateFormat("dd/MM/yyyy")


    val contactItems = listOf(
        Pair(Icons.Default.Email, user.email),
//        Pair(Icons.Default.Transgender, user.gender)
    )

    Column(Modifier.fillMaxSize()) {

        Column(
            Modifier.fillMaxWidth()
        ) {


            Image(
                painter = painterResource(R.drawable.profile),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                    .then(Modifier.align(Alignment.CenterHorizontally))
            )

            Spacer(modifier = Modifier.height(25.dp))


            Text(
                text = user.name + " " + user.surname,
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                modifier = Modifier.fillMaxWidth()
            )


            Text(
                text = "@" + user.nickname,
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
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null)

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = text,
            fontSize = 22.sp
        )

        Spacer(modifier = Modifier.height(50.dp))
    }
}