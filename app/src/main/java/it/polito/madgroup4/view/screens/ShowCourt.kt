package it.polito.madgroup4.view.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.polito.madgroup4.model.PlayingCourt
import it.polito.madgroup4.view.components.CourtDetails


@ExperimentalMaterial3Api
@Composable
fun ShowCourt(
    playingCourt: PlayingCourt,
    navController: NavController
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        CourtDetails(
            playingCourt,
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = { navController.navigate("Rate This Playing Court") },
            modifier = Modifier
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(text = "Rate This Playing Court")
        }
    }
}