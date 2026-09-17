package com.example.carpoolapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DriverManageRideAdapter(
    private val rides: List<Ride>,
    private val onItemClick: (Ride) -> Unit
) : RecyclerView.Adapter<DriverManageRideAdapter.ManageViewHolder>() {

    class ManageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRoute: TextView = view.findViewById(R.id.tvRoute)
        val tvDateTime: TextView = view.findViewById(R.id.tvDateTime)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_driver_manage_ride, parent, false)
        return ManageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ManageViewHolder, position: Int) {
        val ride = rides[position]
        holder.tvRoute.text = "${ride.source} → ${ride.destination}"
        holder.tvDateTime.text = "${ride.date}, ${ride.time} • ${ride.seats} seats available"
        holder.tvPrice.text = "₹${ride.price} per seat"

        holder.itemView.setOnClickListener { onItemClick(ride) }
    }

    override fun getItemCount(): Int = rides.size
}