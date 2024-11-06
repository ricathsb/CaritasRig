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
import com.superbgoal.caritasrig.data.model.PowerSupply

class PowerSupplyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mengisi data dari file JSON untuk PowerSupply
        val typeToken = object : TypeToken<List<PowerSupply>>() {}.type
        val powerSupplies: List<PowerSupply> = loadItemsFromResources(
            context = this,
            resourceId = R.raw.powersupply // Pastikan file JSON ini ada
        )

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PowerSupplyList(powerSupplies)
                }
            }
        }
    }

    @Composable
    fun PowerSupplyList(powerSupplies: List<PowerSupply>) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(powerSupplies) { powerSupply ->
                PowerSupplyCard(powerSupply)
            }
        }
    }

    @Composable
    fun PowerSupplyCard(powerSupply: PowerSupply) {
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
                        text = powerSupply.name,
                        style = MaterialTheme.typography.h6
                    )
                    Text(
                        text = "Type: ${powerSupply.type} | Efficiency: ${powerSupply.efficiency} | Wattage: ${powerSupply.wattage}W | Modularity: ${powerSupply.modular} | Color: ${powerSupply.color}",
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
