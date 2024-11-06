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
import com.superbgoal.caritasrig.data.model.Casing
import com.superbgoal.caritasrig.data.model.Processor



class CasingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Define the type explicitly for Gson TypeToken
        val typeToken = object : TypeToken<List<Processor>>() {}.type
        val casing: List<Casing> = loadItemsFromResources(
            context = this,
            resourceId = R.raw.casing
        )


        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CasingList(casing)
                }
            }
        }
    }


    @Composable
    fun CasingList(casing: List<Casing>) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(casing) { casing ->
                CasingCard(casing)
            }
        }
    }

    @Composable
    fun CasingCard(casing: Casing) {
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
                        text = casing.name,
                        style = MaterialTheme.typography.h6
                    )
                    Text(
                        text = "${casing.type} | ${casing.color} | PSU: ${casing.psu ?: "Not included"} | Volume: ${casing.externalVolume} L | 3.5\" Bays: ${casing.internal35Bays}",
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
