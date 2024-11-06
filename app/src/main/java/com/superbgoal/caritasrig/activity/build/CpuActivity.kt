package com.superbgoal.caritasrig.activity.build

import android.content.Intent
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.reflect.TypeToken
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.activity.BuildActivity
import com.superbgoal.caritasrig.data.loadItemsFromResources
import com.superbgoal.caritasrig.data.model.Processor



class CpuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Define the type explicitly for Gson TypeToken
        val typeToken = object : TypeToken<List<Processor>>() {}.type
        val processors: List<Processor> = loadItemsFromResources(
            context = this,
            resourceId = R.raw.processor
        )


        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ProcessorList(processors)
                }
            }
        }
    }


    @Composable
fun ProcessorList(processors: List<Processor>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(processors) { processor ->
            ProcessorCard(processor)
        }
    }
}

@Composable
fun ProcessorCard(processor: Processor) {
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
                    text = processor.name,
                    style = MaterialTheme.typography.h6
                )
                Text(
                    text = "${processor.core_count} cores @ ${processor.core_clock} GHz Up To ${processor.boost_clock} GHz",
                    style = MaterialTheme.typography.body2
                )
            }
            Button(onClick = {
                val databaseUrl = "https://caritas-rig-default-rtdb.asia-southeast1.firebasedatabase.app"
                val database: DatabaseReference = FirebaseDatabase.getInstance(databaseUrl).reference
                val currentUser = FirebaseAuth.getInstance().currentUser
                database.child("users").child(currentUser?.uid.toString()).child("build").child("processor").setValue(processor.name)

                val intent = Intent().apply {
                    putExtra("processor", processor) // `selectedProcessor` adalah objek Processor yang dipilih
                }
                setResult(RESULT_OK, intent)
                finish()

            }) {
                Text(text = "Add")
            }

        }
    }
}

}
