package it.polito.madgroup4.view.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.boguszpawlowski.composecalendar.SelectableWeekCalendar
import io.github.boguszpawlowski.composecalendar.rememberSelectableWeekCalendarState
import io.github.boguszpawlowski.composecalendar.week.Week
import it.polito.madgroup4.model.Achievement
import it.polito.madgroup4.model.User
import it.polito.madgroup4.utility.formatDateToTimestamp
import it.polito.madgroup4.utility.getWeekdaysStartingOn
import it.polito.madgroup4.view.components.DaysOfWeekHeader
import it.polito.madgroup4.view.components.MyDay
import it.polito.madgroup4.view.components.WeekHeader
import it.polito.madgroup4.viewmodel.LoadingStateViewModel
import it.polito.madgroup4.viewmodel.UserViewModel
import java.time.DayOfWeek
import java.time.LocalDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAchievement(
    userVm: UserViewModel,
    sport: Int,
    userState: State<User?>,
    loadingVm: LoadingStateViewModel,
    setTopBarAction: (() -> Unit) -> Unit,
) {

    var description by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now()) }

    val calendarState = rememberSelectableWeekCalendarState(
        initialSelection = listOf(date),
        initialWeek = Week(getWeekdaysStartingOn(date, DayOfWeek.MONDAY)),
        firstDayOfWeek = DayOfWeek.MONDAY,
    )

    LaunchedEffect(calendarState.selectionState.selection) {
        if (date.isAfter(LocalDate.now()))
            date = LocalDate.now()

        if (calendarState.selectionState.selection.isEmpty())
            calendarState.selectionState.selection = listOf(LocalDate.now())

        if (calendarState.selectionState.selection[0] != date)
            date = calendarState.selectionState.selection[0]
    }

    LaunchedEffect(description, title, date) {
        setTopBarAction {
            val achievement = Achievement(
                title = title,
                description = description,
                date = formatDateToTimestamp(date)
            )
            val user = userVm.user.value!!
            user.sports = user.sports.map { it ->
                if (it.name == userState.value!!.sports[sport].name) {
                    it.achievements = it.achievements + achievement
                    it.achievements = it.achievements.sortedByDescending { it.date }
                    it
                } else {
                    it
                }
            }
            userVm.saveUser(
                user,
                loadingVm,
                "Achievement created successfully",
                "Error while creating achievement",
                null,
                "Your Sport"
            )
        }
    }

    Column(
        Modifier.padding(horizontal = 16.dp)
    ) {

        Spacer(modifier = Modifier.size(30.dp))
        OutlinedTextField(
            value = title,
            //supportingText = { Text(text = "Max 50 characters") },
            onValueChange = { title = it },
            modifier = Modifier
                .fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Sentences
            ),
            singleLine = false,
            maxLines = 2,
            label = { Text(text = "Title") },
            placeholder = { Text(text = "Add a title") },
        )


        Spacer(modifier = Modifier.size(20.dp))
        SelectableWeekCalendar(
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
                    isActive = !dayState.date.isAfter(LocalDate.now()),
                    state = dayState
                )
            },
        )


        Spacer(modifier = Modifier.size(20.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Sentences
            ),
            singleLine = false,
            maxLines = 5,
            label = { Text(text = "Description") },
            placeholder = { Text(text = "Add description") },
        )
    }
}