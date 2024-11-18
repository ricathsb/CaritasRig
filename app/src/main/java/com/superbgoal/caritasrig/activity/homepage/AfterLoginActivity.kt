package com.superbgoal.caritasrig.activity.homepage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.superbgoal.caritasrig.activity.homepage.navbar.NavbarHost

class AfterLoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mengatur tampilan menggunakan Jetpack Compose
        setContent {
            NavbarHost() // Menampilkan NavbarHost setelah login
        }
    }
}