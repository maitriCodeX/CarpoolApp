package com.example.carpoolapp

import android.graphics.Color
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DashboardActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var isDriverMode = false
    private var currentTab = 0

    // Top navbar
    private lateinit var btnRoleSwitch: Button

    // Bottom Navigation Elements
    private lateinit var btnTab0: LinearLayout
    private lateinit var btnTab1: LinearLayout
    private lateinit var btnTab2: LinearLayout
    private lateinit var btnTab3: LinearLayout

    private lateinit var ivTab0: ImageView
    private lateinit var ivTab1: ImageView
    private lateinit var ivTab2: ImageView
    private lateinit var ivTab3: ImageView

    private lateinit var tvTab0: TextView
    private lateinit var tvTab1: TextView
    private lateinit var tvTab2: TextView
    private lateinit var tvTab3: TextView

    // Tab Containers
    private lateinit var layoutPassengerHome: LinearLayout
    private lateinit var layoutPassengerStatus: LinearLayout
    private lateinit var layoutPassengerHistory: LinearLayout
    private lateinit var layoutDriverOffer: View
    private lateinit var layoutDriverRequests: LinearLayout
    private lateinit var layoutDriverHistory: LinearLayout
    private lateinit var layoutProfile: View

    // Lists & Adapters data
    private lateinit var rvPassengerRides: RecyclerView
    private lateinit var rvPassengerStatus: RecyclerView
    private lateinit var rvPassengerHistory: RecyclerView
    private lateinit var rvDriverRequests: RecyclerView
    private lateinit var rvDriverHistory: RecyclerView
    private lateinit var etSearchRide: EditText

    private lateinit var rvDriverHomeOffers: RecyclerView
    private lateinit var btnCreateRideIntent: Button

    private var allRidesList = mutableListOf<Ride>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        initViews()
        setupBottomBarClicks()
        setupRoleSwitch()
        setupProfileLogic()
        setupOfferRideLogic()
        setupPassengerSearch()

        // Initialize display
        selectTab(0)
    }

    private fun initViews() {
        btnRoleSwitch = findViewById(R.id.btnRoleSwitch)

        btnTab0 = findViewById(R.id.btnTab0)
        btnTab1 = findViewById(R.id.btnTab1)
        btnTab2 = findViewById(R.id.btnTab2)
        btnTab3 = findViewById(R.id.btnTab3)

        ivTab0 = findViewById(R.id.ivTab0)
        ivTab1 = findViewById(R.id.ivTab1)
        ivTab2 = findViewById(R.id.ivTab2)
        ivTab3 = findViewById(R.id.ivTab3)

        tvTab0 = findViewById(R.id.tvTab0)
        tvTab1 = findViewById(R.id.tvTab1)
        tvTab2 = findViewById(R.id.tvTab2)
        tvTab3 = findViewById(R.id.tvTab3)

        layoutPassengerHome = findViewById(R.id.layoutPassengerHome)
        layoutPassengerStatus = findViewById(R.id.layoutPassengerStatus)
        layoutPassengerHistory = findViewById(R.id.layoutPassengerHistory)
        layoutDriverOffer = findViewById(R.id.layoutDriverOffer)
        layoutDriverRequests = findViewById(R.id.layoutDriverRequests)
        layoutDriverHistory = findViewById(R.id.layoutDriverHistory)
        layoutProfile = findViewById(R.id.layoutProfile)

        rvPassengerRides = findViewById(R.id.rvPassengerRides)
        rvPassengerStatus = findViewById(R.id.rvPassengerStatus)
        rvPassengerHistory = findViewById(R.id.rvPassengerHistory)
        rvDriverRequests = findViewById(R.id.rvDriverRequests)
        rvDriverHistory = findViewById(R.id.rvDriverHistory)
        rvDriverHomeOffers = findViewById(R.id.rvDriverHomeOffers)
        btnCreateRideIntent = findViewById(R.id.btnCreateRideIntent)
        etSearchRide = findViewById(R.id.etSearchRide)

        rvPassengerRides.layoutManager = LinearLayoutManager(this)
        rvPassengerStatus.layoutManager = LinearLayoutManager(this)
        rvPassengerHistory.layoutManager = LinearLayoutManager(this)
        rvDriverRequests.layoutManager = LinearLayoutManager(this)
        rvDriverHistory.layoutManager = LinearLayoutManager(this)
        rvDriverHomeOffers.layoutManager = LinearLayoutManager(this)
    }

    private fun setupBottomBarClicks() {
        btnTab0.setOnClickListener { selectTab(0) }
        btnTab1.setOnClickListener { selectTab(1) }
        btnTab2.setOnClickListener { selectTab(2) }
        btnTab3.setOnClickListener { selectTab(3) }
    }

    private fun setupRoleSwitch() {
        btnRoleSwitch.setOnClickListener {
            isDriverMode = !isDriverMode
            if (isDriverMode) {
                btnRoleSwitch.text = "Switch to Passenger"
                Toast.makeText(this, "Driver Mode Activated", Toast.LENGTH_SHORT).show()
            } else {
                btnRoleSwitch.text = "Switch to Driver"
                Toast.makeText(this, "Passenger Mode Activated", Toast.LENGTH_SHORT).show()
            }
            selectTab(currentTab)
        }
    }

    private fun selectTab(index: Int) {
        currentTab = index
        updateTabUI(index)
        updateTabLabels()
        hideAllContainers()

        val uid = auth.currentUser?.uid ?: return

        if (!isDriverMode) {
            // Passenger Mode pages
            when (index) {
                0 -> {
                    layoutPassengerHome.visibility = View.VISIBLE
                    loadPassengerAvailableRides()
                }
                1 -> {
                    layoutPassengerStatus.visibility = View.VISIBLE
                    loadPassengerBookings(rvPassengerStatus)
                }
                2 -> {
                    layoutPassengerHistory.visibility = View.VISIBLE
                    loadPassengerBookings(rvPassengerHistory)
                }
                3 -> {
                    layoutProfile.visibility = View.VISIBLE
                    loadProfileData()
                }
            }
        } else {
            // Driver Mode pages
            when (index) {
                0 -> {
                    layoutDriverOffer.visibility = View.VISIBLE
                    loadDriverHomeOffers()
                }
                1 -> {
                    layoutDriverRequests.visibility = View.VISIBLE
                    loadDriverRequestsList()
                }
                2 -> {
                    layoutDriverHistory.visibility = View.VISIBLE
                    loadDriverOfferedHistory()
                }
                3 -> {
                    layoutProfile.visibility = View.VISIBLE
                    loadProfileData()
                }
            }
        }
    }

    private fun updateTabUI(selectedIndex: Int) {
        val tabs = arrayOf(btnTab0, btnTab1, btnTab2, btnTab3)
        val icons = arrayOf(ivTab0, ivTab1, ivTab2, ivTab3)
        val texts = arrayOf(tvTab0, tvTab1, tvTab2, tvTab3)

        for (i in 0..3) {
            if (i == selectedIndex) {
                tabs[i].setBackgroundResource(R.drawable.bg_active_pill)
                icons[i].imageTintList = ColorStateList.valueOf(Color.BLACK)
                texts[i].visibility = View.GONE
            } else {
                tabs[i].setBackgroundResource(android.R.color.transparent)
                icons[i].imageTintList = ColorStateList.valueOf(Color.WHITE)
                texts[i].visibility = View.GONE
            }
        }
    }

    private fun updateTabLabels() {
        if (!isDriverMode) {
            tvTab0.text = "Home"
            tvTab1.text = "Status"
            tvTab2.text = "History"
            tvTab3.text = "Profile"
        } else {
            tvTab0.text = "Offer"
            tvTab1.text = "Requests"
            tvTab2.text = "History"
            tvTab3.text = "Profile"
        }
    }

    private fun hideAllContainers() {
        layoutPassengerHome.visibility = View.GONE
        layoutPassengerStatus.visibility = View.GONE
        layoutPassengerHistory.visibility = View.GONE
        layoutDriverOffer.visibility = View.GONE
        layoutDriverRequests.visibility = View.GONE
        layoutDriverHistory.visibility = View.GONE
        layoutProfile.visibility = View.GONE
    }

    // ================= PASSENGER ACTIONS LAYER =================
    private fun loadPassengerAvailableRides() {
        db.collection("rides").get()
            .addOnSuccessListener { result ->
                allRidesList.clear()
                for (doc in result) {
                    val ride = doc.toObject(Ride::class.java)
                    ride.rideId = doc.id
                    allRidesList.add(ride)
                }
                // Sort by date/time descending to show new rides on top
                allRidesList.sortByDescending { it.date + it.time }
                displayRides(allRidesList)
            }
    }

    private fun displayRides(list: List<Ride>) {
        val adapter = RideAdapter(list) { selectedRide ->
            val intent = Intent(this, RideDetailsActivity::class.java)
            intent.putExtra("rideId", selectedRide.rideId)
            intent.putExtra("source", selectedRide.source)
            intent.putExtra("destination", selectedRide.destination)
            intent.putExtra("date", selectedRide.date)
            intent.putExtra("time", selectedRide.time)
            intent.putExtra("price", selectedRide.price)
            intent.putExtra("driverId", selectedRide.driverId)
            intent.putExtra("driverEmail", selectedRide.driverEmail)
            startActivity(intent)
        }
        rvPassengerRides.adapter = adapter
    }

    private fun setupPassengerSearch() {
        etSearchRide.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim().lowercase()
                val filtered = allRidesList.filter {
                    it.destination.lowercase().contains(query) || it.source.lowercase().contains(query)
                }
                displayRides(filtered)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadPassengerBookings(recyclerView: RecyclerView) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("bookings")
            .whereEqualTo("passengerId", uid).get()
            .addOnSuccessListener { result ->
                val bookingList = mutableListOf<Booking>()
                for (doc in result) {
                    val booking = doc.toObject(Booking::class.java)
                    booking.bookingId = doc.id
                    bookingList.add(booking)
                }
                // Sort by date/time descending to show new bookings on top
                bookingList.sortByDescending { it.date + it.time }

                recyclerView.adapter = MyBookingsAdapter(bookingList) { booking ->
                    val intent = Intent(this, PassengerBookingDetailsActivity::class.java)
                    intent.putExtra("bookingId", booking.bookingId)
                    intent.putExtra("source", booking.source)
                    intent.putExtra("destination", booking.destination)
                    intent.putExtra("date", booking.date)
                    intent.putExtra("time", booking.time)
                    intent.putExtra("price", booking.price)
                    intent.putExtra("status", booking.status)
                    intent.putExtra("driverId", booking.driverId)
                    intent.putExtra("passengerEmail", booking.passengerEmail)
                    startActivity(intent)
                }
            }
    }

    private fun setupOfferRideLogic() {
        btnCreateRideIntent.setOnClickListener {
            startActivity(Intent(this, OfferRideActivity::class.java))
        }
    }

    private fun loadDriverHomeOffers() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("rides").whereEqualTo("driverId", uid).get()
            .addOnSuccessListener { result ->
                val rideList = mutableListOf<Ride>()
                for (doc in result) {
                    val ride = doc.toObject(Ride::class.java)
                    ride.rideId = doc.id
                    rideList.add(ride)
                }
                val adapter = DriverManageRideAdapter(rideList) { ride ->
                    val intent = Intent(this, DriverEditRideActivity::class.java)
                    intent.putExtra("rideId", ride.rideId)
                    intent.putExtra("source", ride.source)
                    intent.putExtra("destination", ride.destination)
                    intent.putExtra("date", ride.date)
                    intent.putExtra("time", ride.time)
                    intent.putExtra("seats", ride.seats)
                    intent.putExtra("price", ride.price)
                    startActivity(intent)
                }
                rvDriverHomeOffers.adapter = adapter
            }
    }

    private fun loadDriverRequestsList() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("bookings").whereEqualTo("driverId", uid).get()
            .addOnSuccessListener { result ->
                val bookingList = mutableListOf<Booking>()
                for (doc in result) {
                    val booking = doc.toObject(Booking::class.java)
                    booking.bookingId = doc.id
                    bookingList.add(booking)
                }

                val adapter = BookingAdapter(
                    bookingList,
                    onAccept = { booking -> updateBookingStatus(booking, "accepted") },
                    onReject = { booking -> updateBookingStatus(booking, "rejected") },
                    onChat = { booking ->
                        val intent = Intent(this, ChatActivity::class.java)
                        intent.putExtra("bookingId", booking.bookingId)
                        startActivity(intent)
                    }
                )
                rvDriverRequests.adapter = adapter
            }
    }

    private fun updateBookingStatus(booking: Booking, newStatus: String) {
        db.collection("bookings").document(booking.bookingId)
            .update("status", newStatus)
            .addOnSuccessListener {
                Toast.makeText(this, "Booking $newStatus", Toast.LENGTH_SHORT).show()
                loadDriverRequestsList()
            }
    }

    private fun loadDriverOfferedHistory() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("rides").whereEqualTo("driverId", uid).get()
            .addOnSuccessListener { result ->
                val rideList = mutableListOf<Ride>()
                for (doc in result) {
                    val ride = doc.toObject(Ride::class.java)
                    ride.rideId = doc.id
                    rideList.add(ride)
                }
                rvDriverHistory.adapter = RideAdapter(rideList) {}
            }
    }

    // ================= PROFILE MANAGEMENT LAYER =================
    private lateinit var etProfileName: EditText
    private lateinit var etProfileEmail: EditText
    private lateinit var etProfilePassword: EditText
    private lateinit var btnProfileSave: Button
    private lateinit var btnProfileLogout: Button

    private fun setupProfileLogic() {
        etProfileName = findViewById(R.id.etProfileName)
        etProfileEmail = findViewById(R.id.etProfileEmail)
        etProfilePassword = findViewById(R.id.etProfilePassword)
        btnProfileSave = findViewById(R.id.btnProfileSave)
        btnProfileLogout = findViewById(R.id.btnProfileLogout)
        val btnChangeProfilePic = findViewById<View>(R.id.btnChangeProfilePic)
        val ivProfilePic = findViewById<ImageView>(R.id.ivProfilePic)

        btnChangeProfilePic.setOnClickListener {
            // Placeholder for profile picture selection
            Toast.makeText(this, "Profile picture selection coming soon!", Toast.LENGTH_SHORT).show()
        }

        btnProfileSave.setOnClickListener {
            val name = etProfileName.text.toString().trim()
            val newEmail = etProfileEmail.text.toString().trim()
            val newPassword = etProfilePassword.text.toString().trim()
            val uid = auth.currentUser?.uid ?: return@setOnClickListener

            db.collection("users").document(uid).update("name", name)

            if (newEmail.isNotEmpty() && newEmail != auth.currentUser?.email) {
                auth.currentUser?.updateEmail(newEmail)
                    ?.addOnFailureListener { e ->
                        Toast.makeText(this, "Email update failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }

            if (newPassword.isNotEmpty()) {
                auth.currentUser?.updatePassword(newPassword)
                    ?.addOnFailureListener { e ->
                        Toast.makeText(this, "Password update failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }

            Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show()
        }

        btnProfileLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun loadProfileData() {
        val uid = auth.currentUser?.uid ?: return
        etProfileEmail.setText(auth.currentUser?.email ?: "")
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    etProfileName.setText(doc.getString("name") ?: "")
                }
            }
    }
}