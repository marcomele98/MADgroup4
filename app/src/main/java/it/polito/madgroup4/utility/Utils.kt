package it.polito.madgroup4.utility

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.ui.graphics.vector.ImageVector
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.viewmodel.ReservationViewModel
import kotlinx.datetime.DatePeriod
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Date

fun calculateStartEndTime(startTime: String, slotId: Int): String {
    val startTime = calculateTimeAsNum(startTime)

    val slotStart = startTime + slotId * 60
    val slotEnd = startTime + (slotId + 1) * 60

    val slotStartHour = add0IfLengthIs1(slotStart / 60)
    val slotStartMinute = add0IfLengthIs1(slotStart % 60)

    val slotEndHour = add0IfLengthIs1(slotEnd / 60)
    val slotEndMinute = add0IfLengthIs1(slotEnd % 60)

    val slotStartTime = "$slotStartHour:$slotStartMinute"
    val slotEndTime = "$slotEndHour:$slotEndMinute"

    return "$slotStartTime - $slotEndTime"
}

fun formatDate(date: LocalDate): Date{
    return formatDate(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()))
}

fun add0IfLengthIs1(n: Int): String {
    val time = n.toString()
    return if(time.length == 1) "0$time" else time
}

private fun calculateTimeAsNum(time: String): Int {
    val timeArray = time.split(":")
    return timeArray[0].toInt() * 60 + timeArray[1].toInt()
}

fun getWeekdaysStartingOnSunday(localDate: LocalDate, firstDayOfWeek: DayOfWeek): List<LocalDate> {
    var currentDay: LocalDate = localDate
    val weekdays = mutableListOf<LocalDate>()
    while(currentDay.dayOfWeek != firstDayOfWeek) {
        currentDay = currentDay.minusDays(1)
    }
    repeat(7) {
        weekdays.add(currentDay)
        currentDay= currentDay.plusDays(1)
    }
    return weekdays
}

fun formatDate(date: Date): Date{
    val formatter = SimpleDateFormat("dd/MM/yyyy")
    return formatter.parse(formatter.format(date))
}

fun calculateAvailableSlot(
    vm: ReservationViewModel,
    reservation: ReservationWithCourt
): List<Slot> {

    /*
        vm.getSlotsByCourtIdAndDate(reservation.reservation!!.courtId, reservation.reservation!!.date)
    */

    var listOfReservation = vm.slots.value!!

    return getAllSlots(listOfReservation, reservation.playingCourt!!.openingTime, reservation.playingCourt!!.closingTime)
}

fun getAllSlots(
    listOfReservation: List<Int>,
    openingTime : String,
    closingTime : String
): MutableList<Slot> {
    var listOfSlots = mutableListOf<Slot>()

    var start = calculateTimeAsNum(openingTime)
    var end = calculateTimeAsNum(closingTime)

    var numSlots = (end - start) / 60

    var array = List<Int>(numSlots) { index -> index }

    for (i in array) {
        listOfSlots.add(
            Slot(
                i, listOfReservation.contains(i), calculateStartEndTime(
                    openingTime,
                    i
                )
            )
        )
    }

    return listOfSlots
}

fun imageSelector(sport: String): ImageVector {
    return when (sport) {
        "Tennis" -> Icons.Default.SportsTennis
        "Football" -> Icons.Default.SportsSoccer
        else -> Icons.Default.SportsTennis
    }
}

