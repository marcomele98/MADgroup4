package it.polito.madgroup4.view.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavController
import it.polito.madgroup4.model.CourtWithSlots
import it.polito.madgroup4.model.ReservationInfo
import it.polito.madgroup4.model.Stuff
import it.polito.madgroup4.view.components.LevelCardSelector

@Composable
fun AdditionalInfo(
    playingCourt: String? = null,
    courtsWithSlots: State<List<CourtWithSlots>?>,
    navController: NavController,
    stuff: List<Stuff>,
    setStuff: (List<Stuff>) -> Unit,
    reservationInfo: ReservationInfo,
    setReservationInfo: (ReservationInfo) -> Unit,
    selectedLevel: String,
) {

    val courtWithSlots = courtsWithSlots.value?.find { it.playingCourt?.name == playingCourt }
    setStuff(courtWithSlots?.playingCourt?.stuff as MutableList<Stuff>)

    LaunchedEffect(Unit) {
        setReservationInfo(
            reservationInfo.copy(
                totalAvailable = 0,
                totalNumber = courtWithSlots?.playingCourt?.maxNumber!!,
            )
        )
    }

    val (checked, setChecked) = remember { mutableStateOf(reservationInfo.public!!) }

    Column() {
        LazyColumn(Modifier.weight(1f)) {

            item {
                Text(
                    text = "Details",
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 16.dp, end = 3.dp)
                ) {
                    Text(text = "Public match", fontSize = 22.sp)
                    Spacer(modifier = Modifier.weight(1f))
                    Checkbox(checked = checked, onCheckedChange = {
                        setReservationInfo(reservationInfo.copy(public = !checked))
                        setChecked(!checked)
                    })
                }
            }

            item {
                AnimatedVisibility(
                    visible = checked,
                    enter = fadeIn() + slideInVertically(
                        initialOffsetY = { -40 },
                        animationSpec = tween(
                            durationMillis = 500,
                            easing = FastOutSlowInEasing
                        )
                    ),
                    exit = fadeOut() + slideOutVertically(
                        targetOffsetY = { -40 },
                        animationSpec = tween(
                            durationMillis = 500,
                            easing = FastOutSlowInEasing
                        )
                    )
                ) {
                    Column(Modifier.padding(horizontal = 16.dp)) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "Outsider users\nallowed",
                                fontSize = 22.sp,
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            NumberButton(
                                initialQuantity = reservationInfo?.totalAvailable ?: 0,
                                onNumberChange = {
                                    setReservationInfo(reservationInfo.copy(totalAvailable = it))
                                },
                                max = courtWithSlots?.playingCourt?.maxNumber!! - 1
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))


                        Text(
                            text = "Suggested Level",
                            fontSize = 22.sp,
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        LevelCardSelector(
                            level = selectedLevel,
                            onClick = {
                                navController.navigate("Select Your Level")
                            }
                        )

                    }
                }
            }


            item { Spacer(modifier = Modifier.height(20.dp)) }

            item {
                Text(
                    text = "Rent equipment",
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }

            items(stuff.size) {
                val name = stuff?.get(it)?.name
                val itemPrice = stuff?.get(it)?.price
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(text = "${name!!}  ${itemPrice!!}€", fontSize = 22.sp)
                    Spacer(modifier = Modifier.weight(1f))
                    NumberButton(
                        initialQuantity = stuff?.get(it)?.quantity!!,
                        onNumberChange = { n ->
                            val newStuff = stuff.toMutableList()
                            newStuff[it].quantity = n
                            setStuff(newStuff)
                        },
                        stuff[it].maxQuantity!!
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

        }

        Spacer(modifier = Modifier.height(8.dp))


        Button(
            onClick = { navController.navigate("Confirm Reservation") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            Text(text = "Next")
        }

        Spacer(modifier = Modifier.height(16.dp))


    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberButton(initialQuantity: Int, onNumberChange: (Int) -> Unit, max: Int) {
    var number by remember { mutableStateOf(initialQuantity) }



    LaunchedEffect(number) {
        onNumberChange(number)
    }


    OutlinedTextField(
        leadingIcon = {
            IconButton(onClick = { if (number != 0) number-- }, Modifier.size(25.dp)) {
                Icon(Icons.Filled.Remove, contentDescription = "Decrement")
            }
        },
        trailingIcon = {
            IconButton(onClick = { if (number < max) number++ }, Modifier.size(25.dp)) {
                Icon(Icons.Filled.Add, contentDescription = "Increment")
            }
        },
        value = number.toString(),
        onValueChange = {
            if (it == "") number = 0
            else if (it.isDigitsOnly()) {
                if (it.toInt() > max) number = max
                else number = it.toInt()
            }
        },
        textStyle = TextStyle(textAlign = TextAlign.Center),
        modifier = Modifier
            .height(50.dp)
            .width(130.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),

        )
}