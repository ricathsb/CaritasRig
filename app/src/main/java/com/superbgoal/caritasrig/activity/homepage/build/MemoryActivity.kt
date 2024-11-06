package com.superbgoal.caritasrig.activity.homepage.build

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.superbgoal.caritasrig.functions.auth.Maintenance

class MemoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Maintenance()
        }
    }
}


