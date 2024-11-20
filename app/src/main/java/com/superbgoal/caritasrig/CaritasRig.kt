package com.superbgoal.caritasrig

import android.app.Application
import android.os.StrictMode

class CaritasRig : Application() {
    override fun onCreate() {
        super.onCreate()

        // StrictMode untuk debugging
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll() // Deteksi semua jenis pelanggaran (I/O, disk read/write, network, dll.)
                .penaltyLog() // Log pelanggaran di Logcat
                // .penaltyDeath() // Pilihan opsional: Memaksa crash jika ada pelanggaran
                .build()
        )

        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectAll() // Deteksi masalah siklus hidup objek dan kebocoran
                .penaltyLog() // Log pelanggaran di Logcat
                .build()
        )
    }
}
