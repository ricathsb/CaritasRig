package com.superbgoal.caritasrig.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Referensi ke Realtime Database
        val database = FirebaseDatabase.getInstance()
        val reference = database.reference.child("testConnection")

        // Coba membaca data dari Realtime Database
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Koneksi berhasil
                Log.d("FirebaseTest", "Connected to Database")
                Toast.makeText(this@TesActivity, "Connected to Database", Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(error: DatabaseError) {
                // Terjadi error atau tidak dapat mengakses database
                Log.e("FirebaseTest", "Failed to connect: ${error.message}")
                Toast.makeText(this@TesActivity, "Failed to connect: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
