package it.polito.madgroup4.View

import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import it.polito.madgroup4.Model.Reservation
import it.polito.madgroup4.R
import it.polito.madgroup4.ViewModel.ReservationViewModel
import java.text.SimpleDateFormat
import java.util.Date

@AndroidEntryPoint
class ReservationsActivity : AppCompatActivity() {

  val vm by viewModels<ReservationViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_reservations)
    val formatter = SimpleDateFormat(
      "dd/MM/yyyy"
    )

/*
    val reservation = Reservation(1, "Ciao", 1, formatter.parse(formatter.format(Date())))
    val reservation2 = Reservation(2, "Ciao2", 1, formatter.parse(formatter.format(Date())))

    val b = findViewById<Button>(R.id.button)
    b.setOnClickListener {
      vm.saveReservation(reservation)
      vm.saveReservation(reservation2)
    }

    val get = findViewById<Button>(R.id.button2)
    get.setOnClickListener {
      vm.getReservationsByDate(formatter.parse(formatter.format(Date()))).observe(this) { reservations ->
        println(reservations)
      }
    }*/


    /*var reservationsAdapter = ReservationsAdapter()

    // Setup RecyclerView
    val reservationsRecyclerView = findViewById<RecyclerView>(R.id.reservationsRecyclerView)
    reservationsRecyclerView.layoutManager = LinearLayoutManager(this)
    reservationsRecyclerView.adapter = reservationsAdapter

    // Fetch reservations for current user and update adapter
    viewModel.getReservations().observe(this) { reservations ->
      // caricare le reservations
      reservationsAdapter.updateReservations(reservations)
    } */
  }

  override fun onDestroy() {
    super.onDestroy()
    vm.reservations.removeObservers(this)
  }

  // Inner class representing a RecyclerView Adapter for reservations
  /*private inner class ReservationsAdapter : RecyclerView.Adapter<ReservationViewHolder>() {
    private var reservations: List<Reservation> = emptyList()

    fun updateReservations(newReservations: List<Reservation>) {
      reservations = newReservations
      notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reservation, parent, false)
      return ReservationViewHolder(view)
    }

    override fun getItemCount() = reservations.size

    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
      val reservation = reservations[position]
      // TODO
    }
  }*/

  // Inner class representing a RecyclerView ViewHolder for a reservation item
  /*private inner class ReservationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val courtNameTextView: TextView = itemView.findViewById(R.id.courtNameTextView)
    val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
    val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
    val cancelButton: Button = itemView.findViewById(R.id.cancelButton)
  }*/
}