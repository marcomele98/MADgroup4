package it.polito.madgroup4.view.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.madgroup4.model.Reservation
import it.polito.madgroup4.utility.Slot

@Composable
fun SlotSelector(
    slots: List<Slot>,
    onClick: (Int) -> Unit,
    selectedSlot: Int? = null,
    reservation: Reservation? = null
) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(128.dp),

            // content padding
            contentPadding = PaddingValues(
                start = 12.dp,
                top = 16.dp,
                end = 12.dp,
                bottom = 16.dp
            ),
            content = {
                items(slots.size) { index ->
                    ElevatedCard(
                        modifier = Modifier
                            .padding(4.dp)
                            .clickable(
                                enabled = !(slots[index].isBooked && index != reservation?.slotNumber),
                            ) {
                                onClick(index)
                            }
                            .fillMaxWidth()
                            .alpha(if (slots[index].isBooked && index != reservation?.slotNumber) 0.5f else 1f),
                            colors = if(index == selectedSlot) {
                                CardDefaults.cardColors()
                            }else{
                                CardDefaults.outlinedCardColors()
                            },
                        //TODO: se sono nell'edit faccio vedere lo slot prenotato di colore surfaceVariant

                    ) {
                        Text(
                            text = slots[index].time,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }


            }
        )
    }

}