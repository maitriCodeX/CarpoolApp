package com.example.carpoolapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etNewPassword = findViewById<EditText>(R.id.etNewPassword)
        val btnSaveProfile = findViewById<Button>(R.id.btnSaveProfile)

        val uid = auth.currentUser?.uid ?: ""
        etEmail.setText(auth.currentUser?.email ?: "")

        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                etName.setText(doc.getString("name") ?: "")
            }

        btnSaveProfile.setOnClickListener {
            val name = etName.text.toString().trim()
            val newEmail = etEmail.text.toString().trim()
            val newPassword = etNewPassword.text.toString().trim()

            db.collection("users").document(uid).update("name", name)

            if (newEmail.isNotEmpty() && newEmail != auth.currentUser?.email) {
                auth.currentUser?.updateEmail(newEmail)
                    ?.addOnFailureListener { e ->
                        Toast.makeText(this, "Email update failed: ${e.message}. Try logging out and back in, then retry.", Toast.LENGTH_LONG).show()
                    }
            }

            if (newPassword.isNotEmpty()) {
                auth.currentUser?.updatePassword(newPassword)
                    ?.addOnFailureListener { e ->
                        Toast.makeText(this, "Password update failed: ${e.message}. Try logging out and back in, then retry.", Toast.LENGTH_LONG).show()
                    }
            }

            Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}