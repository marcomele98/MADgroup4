package it.polito.madgroup4.view.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.madgroup4.R

@Composable
fun FirstLogin(
    navController: NavController
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 70.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "logo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(200.dp)
                .then(Modifier.align(Alignment.CenterHorizontally))
        )
        Spacer(modifier = Modifier.height(80.dp))
        Text(
            text = "Welcome to CUS Torino App!\n\n\n" +
                    "This is your first login, so you have to complete your profile.\n\n",
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 25.sp,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                navController.navigate("Complete Your Profile")
            },
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Text(text = "Continue")
        }
    }

}