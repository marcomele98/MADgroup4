package it.polito.madgroup4.utility

import it.polito.madgroup4.model.Reservation
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.viewmodel.ReservationViewModel
import java.util.Date

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


fun calculateAvailableSlot(
    vm: ReservationViewModel,
    reservation: ReservationWithCourt
): List<String> {

    var listOfReservation = listOf<Int>()
    vm.getSlotsByCourtIdAndDate(reservation.reservation!!.courtId, reservation.reservation!!.date)
    listOfReservation = vm.slots.value!!

    var start = calculateTimeAsNum(reservation.playingCourt!!.openingTime)
    var end = calculateTimeAsNum(reservation.playingCourt!!.closingTime)

    var numSlots = (end - start) / 60

    println(numSlots)

    var array = List<Int>(numSlots) { index -> index }

    array = array.filter { !listOfReservation.contains(it) }

    array.forEach { println(it) }

    return array.map {
        calculateStartEndTime(
            reservation.playingCourt!!.openingTime,
            it
        )
    }
}
