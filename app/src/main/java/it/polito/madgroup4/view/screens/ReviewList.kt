package it.polito.madgroup4.view.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.polito.madgroup4.model.Review
import it.polito.madgroup4.view.components.ReviewCard

@Composable
fun ReviewList(
    reviews: List<Review>
) {

    Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp)
          .clip(RoundedCornerShape(12.dp))
    ) {
        LazyColumn(Modifier.fillMaxSize()) {
            items(reviews.size) { index ->
                ReviewCard(
                    reviews[index]
                )
            }
        }
    }
}