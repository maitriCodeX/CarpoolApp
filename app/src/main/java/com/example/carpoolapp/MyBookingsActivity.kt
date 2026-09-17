package com.example.carpoolapp

import android.content.Intent
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
        val bottomNavigation = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigation)
        rvMyBookings.layoutManager = LinearLayoutManager(this)

        bottomNavigation.selectedItemId = R.id.nav_history

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, RoleSelectionActivity::class.java))
                    true
                }
                R.id.nav_view_rides -> {
                    startActivity(Intent(this, RideListActivity::class.java))
                    true
                }
                R.id.nav_history -> true
                R.id.nav_profile -> {
                    startActivity(Intent(this, EditProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }

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