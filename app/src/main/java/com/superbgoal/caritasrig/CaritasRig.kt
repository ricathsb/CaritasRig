package com.superbgoal.caritasrig

import android.app.Application
import android.util.Log
import com.superbgoal.caritasrig.api.Kurs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CaritasRig : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d("CaritasRig", "Application created")
    }
}

