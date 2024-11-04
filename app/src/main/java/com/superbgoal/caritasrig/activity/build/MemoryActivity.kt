package com.superbgoal.caritasrig.activity.build

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.superbgoal.caritasrig.auth.Maintenance

class MemoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Maintenance()
        }
    }
}


