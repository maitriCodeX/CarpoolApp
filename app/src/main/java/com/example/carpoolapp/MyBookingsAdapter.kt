package com.example.carpoolapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyBookingsAdapter(private val bookings: List<Booking>) :
    RecyclerView.Adapter<MyBookingsAdapter.MyBookingViewHolder>() {

    class MyBookingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRoute: TextView = view.findViewById(R.id.tvMyBookingRoute)
        val tvDateTime: TextView = view.findViewById(R.id.tvMyBookingDateTime)
        val tvStatus: TextView = view.findViewById(R.id.tvMyBookingStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyBookingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_my_booking, parent, false)
        return MyBookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyBookingViewHolder, position: Int) {
        val booking = bookings[position]
        holder.tvRoute.text = "${booking.source} → ${booking.destination}"
        holder.tvDateTime.text = "${booking.date}, ${booking.time} • ₹${booking.price}"
        holder.tvStatus.text = "Status: ${booking.status}"
        holder.tvStatus.setTextColor(
            when (booking.status) {
                "accepted" -> Color.parseColor("#2E7D32")
                "rejected" -> Color.parseColor("#C62828")
                else -> Color.parseColor("#F9A825")
            }
        )
    }

    override fun getItemCount(): Int = bookings.size
}