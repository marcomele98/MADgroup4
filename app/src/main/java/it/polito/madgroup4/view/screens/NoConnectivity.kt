package it.polito.madgroup4.view.screens

/*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun NoConnectivity() {
    Text(
        text = "No connectivity"
    )
}
*/

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NoConnectivity() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center),
        ) {
                Text(
                    text = "No internet connection, please check your connection to use the app",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.primary
                )
            Spacer(modifier = Modifier.height(20.dp))

            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}


