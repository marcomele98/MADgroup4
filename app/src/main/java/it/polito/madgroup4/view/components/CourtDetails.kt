package it.polito.madgroup4.view.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.madgroup4.model.PlayingCourt
import it.polito.madgroup4.utility.imageSelector

@Composable
fun CourtDetails(
    playingCourt: PlayingCourt,
    onClick: () -> Unit,
    //onClick2: (Int) -> Unit,
) {

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = playingCourt.name!!,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageSelector(playingCourt.sport!!),
                contentDescription = "Court",
                modifier = Modifier
                    .size(35.dp)
            )
        }
        Spacer(modifier = Modifier.height(30.dp))

        Row() {
            Icon(Icons.Default.LocationOn, contentDescription = "Location")
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = playingCourt.address + ", " + playingCourt.city + " (" + playingCourt.province + ")",
                fontSize = 22.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row() {
            Icon(Icons.Default.Euro, contentDescription = "Price")
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = playingCourt.price.toString(),
                fontSize = 22.sp
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(text = "See all reviews for this court")
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}