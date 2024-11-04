package com.superbgoal.caritasrig.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class CheckActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        // Logika untuk menentukan ke mana user akan diarahkan
        if (auth.currentUser != null) {
            val user = auth.currentUser
            if (user != null) {
                if (user.isEmailVerified) {
                    // Cek jika user sudah ada di database dan arahkan ke HomeActivity
                    startActivity(Intent(this, HomeActivity::class.java))
                } else {
                    Toast.makeText(this, "Please verify your email before proceeding.", Toast.LENGTH_SHORT).show()
                    // Arahkan ke LoginActivity atau aktivitas lainnya
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            }
        } else {
            // Jika user belum login, arahkan ke LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish() // Tutup CheckActivity setelah mengarahkan
    }
}
