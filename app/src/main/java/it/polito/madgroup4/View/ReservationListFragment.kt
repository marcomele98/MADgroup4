package it.polito.madgroup4.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import it.polito.madgroup4.Model.Reservation
import it.polito.madgroup4.R
import it.polito.madgroup4.ViewModel.ReservationViewModel
import java.text.SimpleDateFormat
import java.util.Date

@AndroidEntryPoint
class ReservationListFragment : Fragment(R.layout.fragment_reservations_list) {

    private val vm by viewModels<ReservationViewModel>()

    private val reservationsAdapter by lazy {
        MyAdapter(emptyList())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Recyclerview initialization
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = reservationsAdapter

        // Date formatter
        val formatter = SimpleDateFormat("dd/MM/yyyy")

        // Observing selected date changes
        vm.selectedDate.observe(viewLifecycleOwner) { selectedDate ->
            vm.getReservationsByDate(formatter.parse(formatter.format(selectedDate)))
        }

        // Observing reservations changes
        vm.reservations.observe(viewLifecycleOwner) { reservations ->
            reservationsAdapter.updateData(reservations)
        }

        // Example reservations
        val reservation = Reservation(1, "Ciao", 1,  formatter.parse(formatter.format(Date())))
        val reservation2 = Reservation(2, "Ciao2", 1,  formatter.parse(formatter.format(Date())))
        vm.saveReservation(reservation)
        vm.saveReservation(reservation2)
    }
}



class MyViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    private val tv = v.findViewById<TextView>(R.id.item_text)
    fun bind(s: Reservation) {
        tv.text = s.courtId
        /*        super.itemView.setOnClickListener {
                    onTap(pos)
                }*/
    }

    fun unbind() {
        super.itemView.setOnClickListener(null)
    }
}


class MyAdapter(val l: List<Reservation>) :
    RecyclerView.Adapter<MyViewHolder>() {

    private var data: List<Reservation> = l

    fun updateData(newData: List<Reservation>) {
        data = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(viewType, parent, false) //false because otherwise runtime error
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val u = data[position]
        holder.bind(u)
    }

    override fun onViewRecycled(holder: MyViewHolder) {
        holder.unbind()
    }

    override fun getItemViewType(position: Int): Int {
        /*if (l[position].startsWith("Beta"))
            return R.layout.item_layout
        else*/
        return R.layout.item_layout
    }
}