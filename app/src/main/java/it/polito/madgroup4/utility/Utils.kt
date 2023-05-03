package it.polito.madgroup4.utility

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.ui.graphics.vector.ImageVector
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.viewmodel.ReservationViewModel
import kotlinx.datetime.DatePeriod
import java.time.DayOfWeek
import java.time.LocalDate

fun calculateStartEndTime(startTime: String, slotId: Int): String {
    val startTime = calculateTimeAsNum(startTime)

    val slotStart = startTime + slotId * 60
    val slotEnd = startTime + (slotId + 1) * 60

    val slotStartHour = slotStart / 60
    val slotStartMinute = slotStart % 60

    val slotEndHour = slotEnd / 60
    val slotEndMinute = slotEnd % 60

    val slotStartTime = "$slotStartHour:$slotStartMinute"
    val slotEndTime = "$slotEndHour:$slotEndMinute"

    return "$slotStartTime - $slotEndTime"
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

