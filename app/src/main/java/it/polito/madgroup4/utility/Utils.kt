package it.polito.madgroup4.utility

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import it.polito.madgroup4.model.ReservationWithCourt
import it.polito.madgroup4.viewmodel.ReservationViewModel
import java.io.ByteArrayOutputStream
import java.io.FileDescriptor
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Base64


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

fun getWeekdaysStartingOnSunday(localDate: LocalDate, firstDayOfWeek: DayOfWeek): List<LocalDate> {
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

fun calculateAvailableSlot(
    vm: ReservationViewModel,
    reservation: ReservationWithCourt
): List<Slot> {

    /*
        vm.getSlotsByCourtIdAndDate(reservation.reservation!!.courtId, reservation.reservation!!.date)
    */

    var listOfReservation = vm.slots.value!!

    return getAllSlots(
        listOfReservation,
        reservation.playingCourt!!.openingTime!!,
        reservation.playingCourt!!.closingTime!!
    )
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

fun imageSelector(sport: String): ImageVector {
    return when (sport) {
        "Tennis" -> Icons.Default.SportsTennis
        "Football" -> Icons.Default.SportsSoccer
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


fun saveProPicInternally(image: Bitmap, context: Context): Uri? {
    // Check for permission
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // Request permission if not granted
        println("Permission not granted")
        return null
    } else {
        val filename = "img_${SystemClock.uptimeMillis()}" + ".jpeg"
        val outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE)
        val byteArrayOutputStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        outputStream.write(byteArray)
        outputStream.close()
        return Uri.fromFile(context.getFileStreamPath(filename))

    }
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

fun drawableToBitmap(drawable: Drawable): Bitmap? {
    if (drawable is BitmapDrawable) {
        // Se il Drawable è già una BitmapDrawable, restituisci direttamente la Bitmap
        return drawable.bitmap
    }

    // Ottieni le dimensioni del Drawable
    val width = drawable.intrinsicWidth
    val height = drawable.intrinsicHeight

    // Crea una Bitmap vuota con le dimensioni del Drawable
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    // Crea un oggetto Canvas per disegnare sulla Bitmap
    val canvas = Canvas(bitmap)

    // Imposta il limite del disegno sulla dimensione del Drawable
    drawable.setBounds(0, 0, canvas.width, canvas.height)

    // Disegna il Drawable sulla Bitmap utilizzando il Canvas
    drawable.draw(canvas)

    // Restituisci la Bitmap creata
    return bitmap
}


fun stringToBitmap(encodedString: String): Bitmap? {
    try {
        val decodedBytes = Base64.decode(encodedString, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

fun bitmapToString(bitmap: Bitmap): String {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    val byteArray = outputStream.toByteArray()
    val encodedString = Base64.encodeToString(byteArray, Base64.DEFAULT)
    return encodedString
}