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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.github.boguszpawlowski.composecalendar.SelectableWeekCalendar
import io.github.boguszpawlowski.composecalendar.rememberSelectableWeekCalendarState
import io.github.boguszpawlowski.composecalendar.week.Week
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.utility.formatDate
import it.polito.madgroup4.utility.getWeekdaysStartingOn
import it.polito.madgroup4.view.components.DaysOfWeekHeader
import it.polito.madgroup4.view.components.MyDay
import it.polito.madgroup4.view.components.ReservationList
import it.polito.madgroup4.view.components.SportCardSelector
import it.polito.madgroup4.view.components.WeekHeader
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun Explore(
    reservations: List<ReservationWithCourt>?,
    setReservation: (String) -> Unit,
    navController: NavController,
    selectedSport: String,
) {

    val (selectedDate, setSelectedDate) = remember { mutableStateOf(LocalDate.now()) }

    val (checked, setChecked) = remember { mutableStateOf(false) }

    val calendarState = rememberSelectableWeekCalendarState(
        initialSelection = listOf(selectedDate),
        initialWeek = Week(getWeekdaysStartingOn(selectedDate, DayOfWeek.MONDAY)),
        firstDayOfWeek = DayOfWeek.MONDAY,
    )

    LaunchedEffect(calendarState.selectionState.selection) {
        if (calendarState.selectionState.selection.isEmpty()) calendarState.selectionState.selection =
            listOf(LocalDate.now())

        if (calendarState.selectionState.selection[0] != selectedDate) setSelectedDate(calendarState.selectionState.selection[0])
    }


    Column(modifier = Modifier.fillMaxWidth()) {

        Row(
            Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 10.dp)) {
            SportCardSelector(
                sport = selectedSport,
                onClick = { navController.navigate("Select Sport") })
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 16.dp, end = 30.dp)
        ) {
            Text(
                text = "Filter by date",
                fontSize = 20.sp,
                fontWeight = Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Checkbox(checked = checked, onCheckedChange = {
                setChecked(!checked)
            })
        }


        AnimatedVisibility(
            visible = checked, enter = fadeIn() + slideInVertically(
                initialOffsetY = { -40 }, animationSpec = tween(
                    durationMillis = 500, easing = FastOutSlowInEasing
                )
            ), exit = fadeOut() + slideOutVertically(
                targetOffsetY = { -40 }, animationSpec = tween(
                    durationMillis = 500, easing = FastOutSlowInEasing
                )
            )
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            SelectableWeekCalendar(
                Modifier.padding(horizontal = 16.dp),
                calendarState = calendarState,
                weekHeader = { WeekHeader(weekState = it) },
                daysOfWeekHeader = {
                    DaysOfWeekHeader(
                        daysOfWeek = listOf(
                            DayOfWeek.MONDAY,
                            DayOfWeek.TUESDAY,
                            DayOfWeek.WEDNESDAY,
                            DayOfWeek.THURSDAY,
                            DayOfWeek.FRIDAY,
                            DayOfWeek.SATURDAY,
                            DayOfWeek.SUNDAY
                        )
                    )
                },
                dayContent = { dayState ->
                    MyDay(
                        isActive = !dayState.date.isBefore(LocalDate.now()),
                        state = dayState,
                        reservations = reservations?.filter { it.reservation?.sport == selectedSport }
                            ?.firstOrNull() {
                                it.reservation?.date?.toDate()?.toInstant()
                                    ?.atZone(ZoneId.systemDefault())?.toLocalDate() == dayState.date
                            }?.reservation,
                        showReservations = true,
                    )
                },
            )

        }




        Spacer(modifier = Modifier.height(16.dp))

        ReservationList(
            modifier = Modifier.padding(horizontal = 16.dp),
            reservations = reservations?.filter {
                (!checked || it.reservation?.date?.toDate()?.let { it1 ->
                    formatDate(it1)
                } == formatDate(
                    selectedDate
                )) && it.playingCourt?.sport == selectedSport
            },
            setReservation = setReservation,
            navController = navController,
            nextRoute = "Public Match Details",
            text = "No public matches found",
        )
    }
}

