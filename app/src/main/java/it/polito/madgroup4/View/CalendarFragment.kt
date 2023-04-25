package it.polito.madgroup4.View

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.stacktips.view.CalendarListener
import com.stacktips.view.CustomCalendarView
import dagger.hilt.android.AndroidEntryPoint
import it.polito.madgroup4.R
import it.polito.madgroup4.ViewModel.ReservationViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class CalendarFragment : Fragment(R.layout.fragment_calendar) {

    val vm: ReservationViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var calendarView: CustomCalendarView = view.findViewById(R.id.calendar_view)

        val currentCalendar: Calendar = Calendar.getInstance(Locale.getDefault())

        calendarView.firstDayOfWeek = Calendar.MONDAY

        calendarView.setShowOverflowDate(false)

        calendarView.refreshCalendar(currentCalendar)

        calendarView.setCalendarListener(object : CalendarListener {
            override fun onDateSelected(date: Date?) {
                val df = SimpleDateFormat("dd-MM-yyyy")
                vm.setSelectedDate(date!!)
            }

            override fun onMonthChanged(date: Date?) {
                val df = SimpleDateFormat("MM-yyyy")
            }
        })

    }

}