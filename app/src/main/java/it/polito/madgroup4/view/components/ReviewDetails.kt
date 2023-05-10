package it.polito.madgroup4.view.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarConfig
import com.gowtham.ratingbar.RatingBarStyle
import com.gowtham.ratingbar.StepSize
import it.polito.madgroup4.model.Review

@Composable
fun ReviewDetails(
  review: Review,
) {


  Column {

    Row() {
      Icon(Icons.Default.ManageAccounts, contentDescription = "Service")
      Spacer(modifier = Modifier.width(10.dp))
      RatingBar(value = review.serviceRating,
        onValueChange = {},
        config = RatingBarConfig().style(RatingBarStyle.Normal)
          .activeColor(MaterialTheme.colorScheme.primary)
          .inactiveColor(MaterialTheme.colorScheme.surfaceVariant).stepSize(StepSize.HALF)
          .numStars(5).size(35.dp).padding(6.dp),
        onRatingChanged = {})
    }

    Spacer(modifier = Modifier.height(20.dp))

    Row() {
      Icon(Icons.Default.Apartment, contentDescription = "Structure")
      Spacer(modifier = Modifier.width(10.dp))
      RatingBar(value = review.structureRating,
        onValueChange = {},
        config = RatingBarConfig().style(RatingBarStyle.Normal)
          .activeColor(MaterialTheme.colorScheme.primary)
          .inactiveColor(MaterialTheme.colorScheme.surfaceVariant).stepSize(StepSize.HALF)
          .numStars(5).size(35.dp).padding(6.dp),
        onRatingChanged = {})
    }

    Spacer(modifier = Modifier.height(20.dp))

    Row() {
      Icon(Icons.Default.CleaningServices, contentDescription = "Cleaning")
      Spacer(modifier = Modifier.width(10.dp))
      RatingBar(value = review.cleaningRating,
        onValueChange = {},
        config = RatingBarConfig().style(RatingBarStyle.Normal)
          .activeColor(MaterialTheme.colorScheme.primary)
          .inactiveColor(MaterialTheme.colorScheme.surfaceVariant).stepSize(StepSize.HALF)
          .numStars(5).size(35.dp).padding(6.dp),
        onRatingChanged = {})
    }

  }
}