package com.example.womansafety

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class AddGuardianActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_guardian)

        // Initialize Firebase Realtime Database
        database = FirebaseDatabase.getInstance().reference

        val guardianNameEditText = findViewById<EditText>(R.id.editTextGuardianName)
        val guardianPhoneEditText = findViewById<EditText>(R.id.editTextGuardianPhoneNumber)
        val guardianEmailEditText = findViewById<EditText>(R.id.editTextGuardianEmail)
        val saveButton = findViewById<Button>(R.id.buttonSaveGuardian)

        saveButton.setOnClickListener {
            val guardianName = guardianNameEditText.text.toString().trim()
            val guardianPhone = guardianPhoneEditText.text.toString().trim()
            val guardianEmail = guardianEmailEditText.text.toString().trim()

            if (guardianName.isNotEmpty() && guardianPhone.isNotEmpty() && guardianEmail.isNotEmpty()) {
                // Generate a unique ID for the guardian
                val guardianId = database.child("guardians").push().key

                // Create a map of guardian data
                val guardianData = hashMapOf(
                    "name" to guardianName,
                    "phone" to guardianPhone,
                    "email" to guardianEmail
                )

                // Save the guardian data to the database
                if (guardianId != null) {
                    database.child("guardians").child(guardianId).setValue(guardianData)
                    Toast.makeText(this, "Guardian added successfully!", Toast.LENGTH_SHORT).show()

                    // Finish the activity to return to the previous screen
                    finish()
                }
            } else {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
