package com.superbgoal.caritasrig.activity.homepage.component

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.reflect.TypeToken
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.activity.homepage.build.BuildActivity
import com.superbgoal.caritasrig.data.loadItemsFromResources
import com.superbgoal.caritasrig.data.model.component.PowerSupply
import com.superbgoal.caritasrig.data.model.buildmanager.BuildManager
import com.superbgoal.caritasrig.functions.auth.ComponentCard
import com.superbgoal.caritasrig.functions.auth.saveComponent

class PowerSupplyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val buildTitle = BuildManager.getBuildTitle()


        // Load power supplies from JSON resource
        val typeToken = object : TypeToken<List<PowerSupply>>() {}.type
        val powerSupplies: List<PowerSupply> = loadItemsFromResources(
            context = this,
            resourceId = R.raw.powersupply // Ensure this JSON file exists
        )

        setContent {
            MaterialTheme {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Background image
                    Image(
                        painter = painterResource(id = R.drawable.component_bg),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Main content with TopAppBar and PowerSupplyList
                    Column {
                        TopAppBar(
                            backgroundColor = Color.Transparent,
                            contentColor = Color.White,
                            elevation = 0.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            title = {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp, bottom = 10.dp)
                                ) {
                                    Text(
                                        text = "Part Pick",
                                        style = MaterialTheme.typography.h4,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "Power Supply",
                                        style = MaterialTheme.typography.subtitle1,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        val intent = Intent(this@PowerSupplyActivity, BuildActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    },
                                    modifier = Modifier.padding(start = 20.dp, top = 10.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_back),
                                        contentDescription = "Back"
                                    )
                                }
                            },
                            actions = {
                                IconButton(
                                    onClick = {
                                        // Filter action (not implemented)
                                    },
                                    modifier = Modifier.padding(end = 20.dp, top = 10.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_filter),
                                        contentDescription = "Filter"
                                    )
                                }
                            }
                        )

                        // PowerSupplyList content
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = Color.Transparent
                        ) {
                            PowerSupplyList(powerSupplies)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun PowerSupplyList(powerSupplies: List<PowerSupply>) {
        val context = LocalContext.current

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(powerSupplies) { powerSupply ->
                // Track loading state for each power supply
                val isLoading = remember { mutableStateOf(false) }

                // Menggunakan ComponentCard untuk setiap power supply
                ComponentCard(
                    title = powerSupply.name,
                    details = "Type: ${powerSupply.type} | Efficiency: ${powerSupply.efficiency} | Wattage: ${powerSupply.wattage}W | Modularity: ${powerSupply.modular} | Color: ${powerSupply.color}",
                    context = context,
                    component = powerSupply,
                    isLoading = isLoading.value, // Pass loading state to card
                    onAddClick = {
                        // Mulai proses loading ketika tombol Add ditekan
                        isLoading.value = true
                        Log.d("PowerSupplyActivity", "Selected Power Supply: ${powerSupply.name}")

                        // Mendapatkan userId dan buildTitle
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        val userId = currentUser?.uid.toString()
                        val buildTitle = BuildManager.getBuildTitle()

                        // Simpan power supply jika buildTitle tersedia
                        buildTitle?.let { title ->
                            saveComponent(
                                userId = userId,
                                buildTitle = title,
                                componentType = "powersupply", // Tipe komponen
                                componentData = powerSupply, // Data power supply
                                onSuccess = {
                                    // Berhenti loading ketika sukses
                                    isLoading.value = false
                                    Log.d("PowerSupplyActivity", "Power Supply ${powerSupply.name} saved successfully under build title: $title")

                                    // Navigasi ke BuildActivity setelah berhasil
                                    val intent = Intent(context, BuildActivity::class.java).apply {
                                        putExtra("component_title", powerSupply.name)
                                        putExtra("component_data", powerSupply) // Component sent as Parcelable
                                    }
                                    context.startActivity(intent)
                                },
                                onFailure = { errorMessage ->
                                    // Berhenti loading ketika gagal
                                    isLoading.value = false
                                    Log.e("PowerSupplyActivity", "Failed to store Power Supply under build title: $errorMessage")
                                },
                                onLoading = { isLoading.value = it } // Update loading state
                            )
                        } ?: run {
                            // Berhenti loading jika buildTitle null
                            isLoading.value = false
                            Log.e("PowerSupplyActivity", "Build title is null; unable to store Power Supply.")
                        }
                    }
                )
            }
        }
    }

}
