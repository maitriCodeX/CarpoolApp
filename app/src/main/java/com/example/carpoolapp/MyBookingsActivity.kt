package com.example.carpoolapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyBookingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_bookings)

        val rvMyBookings = findViewById<RecyclerView>(R.id.rvMyBookings)
        rvMyBookings.layoutManager = LinearLayoutManager(this)

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance().collection("bookings")
            .whereEqualTo("passengerId", uid).get()
            .addOnSuccessListener { result ->
                val bookingList = mutableListOf<Booking>()
                for (doc in result) {
                    val booking = doc.toObject(Booking::class.java)
                    booking.bookingId = doc.id
                    bookingList.add(booking)
                }
                rvMyBookings.adapter = MyBookingsAdapter(bookingList)
            }
    }
}