package it.polito.madgroup4.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.boguszpawlowski.composecalendar.CalendarState
import io.github.boguszpawlowski.composecalendar.SelectableCalendar
import io.github.boguszpawlowski.composecalendar.day.DayState
import io.github.boguszpawlowski.composecalendar.header.MonthState
import io.github.boguszpawlowski.composecalendar.header.WeekState
import io.github.boguszpawlowski.composecalendar.selection.DynamicSelectionState
import it.polito.madgroup4.model.Reservation
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun Calendar(
    calendarState: CalendarState<DynamicSelectionState>,
    allReservations: List<Reservation>?,
) {
    SelectableCalendar(
        daysOfWeekHeader = {
            DaysOfWeekHeader(daysOfWeek = it)
        },
        monthHeader = {
            MonthHeader(monthState = it)
        },
        dayContent = { dayState ->
            MyDay(
                state = dayState,
                reservations = allReservations?.firstOrNull() {
                    it.date.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate() == dayState.date
                },
            )
        },
        calendarState = calendarState,
        showAdjacentMonths = false
    )
}

@Composable
fun MonthHeader(
    monthState: MonthState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            modifier = Modifier.testTag("Decrement"),
            onClick = { monthState.currentMonth = monthState.currentMonth.minusMonths(1) }
        ) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            modifier = Modifier.testTag("MonthLabel"),
            text = monthState.currentMonth.month
                .getDisplayName(TextStyle.FULL, Locale.getDefault())
                .lowercase()
                .replaceFirstChar { it.titlecase() }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = monthState.currentMonth.year.toString()
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            modifier = Modifier.testTag("Increment"),
            onClick = { monthState.currentMonth = monthState.currentMonth.plusMonths(1) }
        ) {
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "forward")
        }
    }
}

@Composable
fun DaysOfWeekHeader(
    daysOfWeek: List<DayOfWeek>,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        daysOfWeek.forEach { dayOfWeek ->
            androidx.compose.material.Text(
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                modifier = modifier
                    .weight(1f)
                    .wrapContentHeight()
            )
        }
    }
}

@Composable
fun MyDay(
    state: DayState<DynamicSelectionState>,
    reservations: Reservation?,
    modifier: Modifier = Modifier,
) {
    val date = state.date
    val selectionState = state.selectionState
    val isSelected =
        if (selectionState.selection.isEmpty() && date == LocalDate.now()) {
            true
        } else {
            selectionState.isDateSelected(date)
        }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .clickable {
                    selectionState.onDateSelected(date)
                },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected)
                            MaterialTheme.colorScheme.surfaceVariant
                        else
                            Color.Transparent
                    )
            ) {
                Text(
                    text = date.dayOfMonth.toString(),
                    modifier = Modifier.align(Alignment.Center),
                    color =
                    if (!state.isCurrentDay)
                        contentColorFor(
                            backgroundColor = if (isSelected)
                                MaterialTheme.colorScheme.surfaceVariant
                            else
                                MaterialTheme.colorScheme.surface
                        )
                    else
                        MaterialTheme.colorScheme.primary,
                )
            }
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(
                        if (reservations != null)
                            MaterialTheme.colorScheme.primary
                        else
                            Color.Transparent
                    )
            )
        }
    }
}



@Composable
fun WeekHeader(
    weekState: WeekState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            modifier = Modifier.testTag("Decrement"),
            onClick = { weekState.currentWeek = weekState.currentWeek.plusWeeks(-1) }
        ) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            modifier = Modifier.testTag("MonthLabel"),
            text = weekState.currentWeek.yearMonth.month
                .getDisplayName(TextStyle.FULL, Locale.getDefault())
                .lowercase()
                .replaceFirstChar { it.titlecase() }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = weekState.currentWeek.yearMonth.year.toString()
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            modifier = Modifier.testTag("Increment"),
            onClick = { weekState.currentWeek = weekState.currentWeek.plusWeeks(1) }
        ) {
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "forward")
        }
    }
}
