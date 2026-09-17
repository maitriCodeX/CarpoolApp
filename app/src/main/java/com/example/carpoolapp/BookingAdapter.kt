package com.example.carpoolapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookingAdapter(
    private val bookings: List<Booking>,
    private val onAccept: (Booking) -> Unit,
    private val onReject: (Booking) -> Unit
) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    class BookingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRoute: TextView = view.findViewById(R.id.tvBookingRoute)
        val tvPassenger: TextView = view.findViewById(R.id.tvBookingPassenger)
        val tvStatus: TextView = view.findViewById(R.id.tvBookingStatus)
        val actionRow: View = view.findViewById(R.id.actionRow)
        val btnAccept: Button = view.findViewById(R.id.btnAccept)
        val btnReject: Button = view.findViewById(R.id.btnReject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_booking, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]
        holder.tvRoute.text = "${booking.source} → ${booking.destination}"
        holder.tvPassenger.text = "Passenger: ${booking.passengerEmail}"
        holder.tvStatus.text = "Status: ${booking.status}"

        // Hide accept/reject buttons once a decision has been made
        holder.actionRow.visibility = if (booking.status == "pending") View.VISIBLE else View.GONE

        holder.btnAccept.setOnClickListener { onAccept(booking) }
        holder.btnReject.setOnClickListener { onReject(booking) }
    }

    override fun getItemCount(): Int = bookings.size
}