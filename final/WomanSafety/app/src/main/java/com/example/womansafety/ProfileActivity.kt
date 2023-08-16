package com.example.womansafety

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.example.womansafety.databinding.ActivityProfileBinding // Import your view binding class
import com.google.firebase.auth.UserProfileChangeRequest
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


import com.example.womansafety.Adapter.GuardianListAdapter
import com.example.womansafety.model.Guardian
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityProfileBinding // Declare your view binding variable
    private lateinit var adapter: GuardianListAdapter
    private lateinit var recyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater) // Initialize the binding
        val view = binding.root
        setContentView(view)




        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()


        // Load user information
        loadUserInfo()

        binding.btnSaveName.setOnClickListener {
            saveName()
        }

        binding.btnAddGuardian.setOnClickListener {
            startActivity(Intent(this, AddGuardianActivity::class.java))
        }

        binding.btnSendHelp.setOnClickListener {
            startActivity(Intent(this, SendHelpActivity::class.java))
        }
        recyclerView = findViewById(R.id.recyclerViewGuardians)
        adapter = GuardianListAdapter()

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter


        fetchGuardiansFromFirebase()
    }

    private fun loadUserInfo() {
        // Get current user
        val currentUser = auth.currentUser

        // Update UI with user information
        if (currentUser != null) {
            val userEmail = currentUser.email
            val userName = currentUser.displayName

            // Set email in TextView
            binding.txtEmail.text = userEmail

            // Set user name in EditText
            binding.edtName.setText(userName)
        }
    }

    private fun saveName() {
        val newName = binding.edtName.text.toString()
        val user = auth.currentUser

        if (user != null) {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build()

            user.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Update successful
                        Toast.makeText(this, "Name updated successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        // Update failed
                        Toast.makeText(this, "Failed to update name", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
    private fun fetchGuardiansFromFirebase() {
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid

        if (userId != null) {
            val guardiansReference = FirebaseDatabase.getInstance().reference
                .child("users")
                .child(userId)
                .child("guardians")

            guardiansReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val guardians = mutableListOf<Guardian>()

                    for (guardianSnapshot in snapshot.children) {
                        val guardian = guardianSnapshot.getValue(Guardian::class.java)
                        guardian?.let {
                            guardians.add(it)
                        }
                    }

                    updateGuardiansUI(guardians)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                    Toast.makeText(
                        this@ProfileActivity,
                        "Error fetching guardians: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

    private fun updateGuardiansUI(guardians: List<Guardian>) {
        adapter.updateData(guardians)
    }
}















