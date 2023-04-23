package it.polito.madgroup4.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import it.polito.madgroup4.Model.Reservation
import it.polito.madgroup4.R
import it.polito.madgroup4.ViewModel.MainViewModel

@AndroidEntryPoint
class ReservationsActivity : AppCompatActivity() {

  val viewModel by viewModels<MainViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_reservations)


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
