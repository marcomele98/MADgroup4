package it.polito.madgroup4.utility

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsBaseball
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.SportsHockey
import androidx.compose.material.icons.filled.SportsRugby
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material.icons.filled.SportsVolleyball
import androidx.compose.ui.graphics.vector.ImageVector
import com.google.firebase.Timestamp
import it.polito.madgroup4.R
import it.polito.madgroup4.model.Slot
import java.io.FileDescriptor
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale


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

fun formatDate(date: String): Date {
    val formatter = SimpleDateFormat("dd/MM/yyyy")
    return formatter.parse(date)
}

fun formatDate(date: LocalDate): Date {
    return formatDate(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()))
}

fun formatDateToTimestamp(date: LocalDate): Timestamp {
    return Timestamp(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()))
}

fun formatDateToTimestamp(date: Date): Timestamp {
    return Timestamp(Date.from(date.toInstant()))
}

fun formatTimestampToString(timestamp: Timestamp): String {
    val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
    val date = Date(milliseconds)
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return  dateFormat.format(date)
}

fun add0IfLengthIs1(n: Int): String {
    val time = n.toString()
    return if (time.length == 1) "0$time" else time
}

private fun calculateTimeAsNum(time: String): Int {
    val timeArray = time.split(":")
    return timeArray[0].toInt() * 60 + timeArray[1].toInt()
}

fun getWeekdaysStartingOn(localDate: LocalDate, firstDayOfWeek: DayOfWeek): List<LocalDate> {
    var currentDay: LocalDate = localDate
    val weekdays = mutableListOf<LocalDate>()
    while (currentDay.dayOfWeek != firstDayOfWeek) {
        currentDay = currentDay.minusDays(1)
    }
    repeat(7) {
        weekdays.add(currentDay)
        currentDay = currentDay.plusDays(1)
    }
    return weekdays
}

fun formatDate(date: Date): Date {
    val formatter = SimpleDateFormat("dd/MM/yyyy")
    return formatter.parse(formatter.format(date))
}


fun getAllSlots(
    listOfReservation: List<Int>,
    openingTime: String,
    closingTime: String
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

fun courtSelector(name: String): Int {
    return when (name) {
        "Campo Panetti" -> R.drawable.panetti
        "Campo Sicilia" -> R.drawable.sicilia
        "Palazzetto Grugliasco" -> R.drawable.grugliasco
        "Campo Albonico" -> R.drawable.albonico
        "Campo Braccini" -> R.drawable.braccini
        else -> R.drawable.panetti
    }
}

fun imageSelector(sport: String): ImageVector {
    return when (sport) {
        "Tennis" -> Icons.Default.SportsTennis
        "Football" -> Icons.Default.SportsSoccer
        "Basket" -> Icons.Default.SportsBasketball
        "Baseball" -> Icons.Default.SportsBaseball
        "Volleyball" -> Icons.Default.SportsVolleyball
        "Rugby" -> Icons.Default.SportsRugby
        "Hockey" -> Icons.Default.SportsHockey
        else -> Icons.Default.SportsTennis
    }
}

fun floatEquals(a: Float, b: Float): Boolean {
    return Math.abs(a - b) <= 0.00001
}

fun uriToBitmap(selectedFileUri: Uri, context: Context): Bitmap? {
    try {
        val parcelFileDescriptor = context.contentResolver.openFileDescriptor(selectedFileUri, "r")
        val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()
        return image
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}




@SuppressLint("Range")
fun rotateBitmap(input: Bitmap, context: Context, editImageUri: Uri): Bitmap? {
    val orientationColumn =
        arrayOf(MediaStore.Images.Media.ORIENTATION)
    val cur: Cursor? =
        context.contentResolver.query(editImageUri!!, orientationColumn, null, null, null)
    var orientation = -1
    if (cur != null && cur.moveToFirst()) {
        orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]))
    }
    Log.d("tryOrientation", orientation.toString() + "")
    val rotationMatrix = Matrix()
    rotationMatrix.setRotate(orientation.toFloat())
    return Bitmap.createBitmap(input, 0, 0, input.width, input.height, rotationMatrix, true)
}




fun isValidEmail(target: CharSequence): Boolean {
    return if (TextUtils.isEmpty(target)) {
        false
    } else {
        val control : Boolean = Patterns.EMAIL_ADDRESS.matcher(target).matches()
        if(control){
            val domain = target.toString().substring(target.toString().indexOf("@") + 1)
            return domain.endsWith("unito.it") || domain.endsWith("polito.it")
        } else {
            false
        }
    }
}