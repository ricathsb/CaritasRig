package com.superbgoal.caritasrig.activity.homepage.build

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.gson.reflect.TypeToken
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.data.loadItemsFromResources
import com.superbgoal.caritasrig.data.model.Motherboard

class MotherboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mengisi data dari file JSON untuk Motherboard
        val typeToken = object : TypeToken<List<Motherboard>>() {}.type
        val motherboards: List<Motherboard> = loadItemsFromResources(
            context = this,
            resourceId = R.raw.motherboard // Pastikan file JSON ini ada
        )

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MotherboardList(motherboards)
                }
            }
        }
    }

    @Composable
    fun MotherboardList(motherboards: List<Motherboard>) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(motherboards) { motherboard ->
                MotherboardCard(motherboard)
            }
        }
    }

    @Composable
    fun MotherboardCard(motherboard: Motherboard) {
        Card(
            elevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = motherboard.name,
                        style = MaterialTheme.typography.h6
                    )
                    Text(
                        text = "Socket: ${motherboard.socket} | Form Factor: ${motherboard.formFactor} | Max Memory: ${motherboard.maxMemory}GB | Slots: ${motherboard.memorySlots} | Color: ${motherboard.color}",
                        style = MaterialTheme.typography.body2
                    )
                }
                Button(onClick = { /* Tambahkan aksi untuk tombol "Add" */ }) {
                    Text(text = "Add")
                }
            }
        }
    }
}
