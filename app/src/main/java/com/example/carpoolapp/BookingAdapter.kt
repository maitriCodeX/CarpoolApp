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
    private val onReject: (Booking) -> Unit,
    private val onChat: (Booking) -> Unit
) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    class BookingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRoute: TextView = view.findViewById(R.id.tvBookingRoute)
        val tvPassenger: TextView = view.findViewById(R.id.tvBookingPassenger)
        val tvStatus: TextView = view.findViewById(R.id.tvBookingStatus)
        val layoutPendingActions: View = view.findViewById(R.id.layoutPendingActions)
        val btnAccept: Button = view.findViewById(R.id.btnAccept)
        val btnReject: Button = view.findViewById(R.id.btnReject)
        val btnChat: Button = view.findViewById(R.id.btnChat)
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
        holder.btnChat.setOnClickListener { onChat(booking) }

        // Drivers side visibility logic:
        // Accept and Reject are visible only when status is pending.
        // Chat option is visible in the form of an icon/button ONLY after driver accepts.
        if (booking.status == "pending") {
            holder.layoutPendingActions.visibility = View.VISIBLE
            holder.btnChat.visibility = View.GONE
        } else if (booking.status == "accepted") {
            holder.layoutPendingActions.visibility = View.GONE
            holder.btnChat.visibility = View.VISIBLE
        } else {
            // Rejected or other statuses
            holder.layoutPendingActions.visibility = View.GONE
            holder.btnChat.visibility = View.GONE
        }

        holder.btnAccept.setOnClickListener { onAccept(booking) }
        holder.btnReject.setOnClickListener { onReject(booking) }
    }

    override fun getItemCount(): Int = bookings.size
}