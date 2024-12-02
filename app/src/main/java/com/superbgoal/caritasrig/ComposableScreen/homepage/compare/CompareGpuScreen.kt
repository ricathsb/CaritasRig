package com.superbgoal.caritasrig.ComposableScreen.homepage.compare

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aay.compose.radarChart.RadarChart
import com.aay.compose.radarChart.model.NetLinesStyle
import com.aay.compose.radarChart.model.Polygon
import com.aay.compose.radarChart.model.PolygonStyle
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.data.model.component.GpuBenchmark
import com.superbgoal.caritasrig.functions.loadItemsFromResources

@Composable
fun GPUComparisonScreen() {
    val context = LocalContext.current
    val gpus: List<GpuBenchmark> = remember {
        loadItemsFromResources(
            context = context,
            resourceId = R.raw.gpu_benchmark
        )
    }

    var searchText by remember { mutableStateOf("") }
    var selectedGPUs by remember { mutableStateOf<List<GpuBenchmark>>(emptyList()) }

    // Show search bar based on the number of selected GPUs
    val showSearchbar by remember(selectedGPUs) {
        mutableStateOf(selectedGPUs.size < 2)
    }

    // Filtered list based on search text
    val filteredGPUs = remember(searchText, gpus) {
        if (searchText.isBlank()) gpus
        else gpus.filter { it.gpuName.contains(searchText, ignoreCase = true) }
    }

    fun addGPU(gpu: GpuBenchmark) {
        selectedGPUs = selectedGPUs + gpu
    }

    fun removeGPU(gpu: GpuBenchmark) {
        selectedGPUs = selectedGPUs - gpu
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (showSearchbar) {
            item {
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Search GPU") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon"
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (selectedGPUs.isNotEmpty()) {
            items(selectedGPUs) { gpu ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = gpu.gpuName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { removeGPU(gpu) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove GPU",
                            tint = Color.Red
                        )
                    }
                }
            }
        }

        if (selectedGPUs.size == 2) {
            item {
                RadarChartGpu(
                    gpu1 = selectedGPUs[0],
                    gpu2 = selectedGPUs[1]
                )

                GpuPerformanceBar(
                    label = "G2D mark ",
                    gpu1Score = selectedGPUs[0].G2Dmark,
                    gpu2Score = selectedGPUs[1].G2Dmark
                )

                Spacer(modifier = Modifier.height(8.dp))

                GpuPerformanceBar(
                    label = "G3D mark ",
                    gpu1Score = selectedGPUs[0].G3Dmark,
                    gpu2Score = selectedGPUs[1].G3Dmark
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        if (filteredGPUs.isNotEmpty() && searchText.isNotBlank()) {
            items(filteredGPUs) { gpu ->
                Text(
                    text = gpu.gpuName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            addGPU(gpu)
                            searchText = "" // Clear search after selection
                        },
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun RadarChartGpu(gpu1: GpuBenchmark, gpu2: GpuBenchmark) {
    // Maksimal nilai untuk setiap parameter GPU
    val maxValues = listOf(20000.0, 1000.0, 20.0, 400.0, 50.0) // G3DMark, G2DMark, GPU Value, TDP, Power Performance
    val scalarValue = 20.0

    fun clamp(value: Double): Double {
        return value.coerceIn(0.0, scalarValue)
    }

    // Normalisasi nilai GPU 1
    val gpu1Values = listOf(
        gpu1.G3Dmark.toDouble(),
        gpu1.G2Dmark.toDouble(),
        gpu1.gpuValue,
        gpu1.TDP,
        gpu1.powerPerformance
    ).mapIndexed { index, value ->
        clamp((value / maxValues[index]) * scalarValue)
    }

    // Normalisasi nilai GPU 2
    val gpu2Values = listOf(
        gpu2.G3Dmark.toDouble(),
        gpu2.G2Dmark.toDouble(),
        gpu2.gpuValue,
        gpu2.TDP,
        gpu2.powerPerformance
    ).mapIndexed { index, value ->
        clamp((value / maxValues[index]) * scalarValue)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Radar Chart
        RadarChart(
            modifier = Modifier
                .height(500.dp)
                .fillMaxWidth(),
            radarLabels = listOf(
                "G3DMark", "G2DMark", "Value", "TDP", "Power Performance"
            ),
            labelsStyle = TextStyle(
                color = Color.Black,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp
            ),
            netLinesStyle = NetLinesStyle(
                netLineColor = Color(0x90D3D3D3),
                netLinesStrokeWidth = 2f,
                netLinesStrokeCap = StrokeCap.Round
            ),
            scalarSteps = 5,
            scalarValue = scalarValue,
            scalarValuesStyle = TextStyle(
                color = Color.Black,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp
            ),
            polygons = listOf(
                Polygon(
                    values = gpu1Values,
                    unit = "",
                    style = PolygonStyle(
                        fillColor = Color(0xffc2ff86),
                        fillColorAlpha = 0.5f,
                        borderColor = Color(0xffe6ffd6),
                        borderColorAlpha = 0.5f,
                        borderStrokeWidth = 2f,
                        borderStrokeCap = StrokeCap.Butt,
                    )
                ),
                Polygon(
                    values = gpu2Values,
                    unit = "",
                    style = PolygonStyle(
                        fillColor = Color(0xffFFDBDE),
                        fillColorAlpha = 0.5f,
                        borderColor = Color(0xffFF8B99),
                        borderColorAlpha = 0.5f,
                        borderStrokeWidth = 2f,
                        borderStrokeCap = StrokeCap.Butt
                    )
                )
            )
        )

        // Legend
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem(
                color = Color(0xffc2ff86),
                name = gpu1.gpuName
            )
            LegendItem(
                color = Color(0xffFFDBDE),
                name = gpu2.gpuName
            )
        }
    }
}

@Composable
fun GpuPerformanceBar(
    label: String,
    gpu1Score: Int,
    gpu2Score: Int
) {
    // Tentukan skor maksimal antara kedua GPU untuk perbandingan
    val maxScore = maxOf(gpu1Score.coerceAtLeast(0), gpu2Score.coerceAtLeast(0)).takeIf { it > 0 } ?: 1

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bar untuk GPU 1
            Box(
                modifier = Modifier
                    .weight((gpu1Score.coerceAtLeast(0) / maxScore.toFloat()).coerceAtLeast(0.01f)) // Pastikan bar tidak terlalu kecil
                    .fillMaxHeight()
                    .background(Color(0xFFc2ff86)) // Warna untuk GPU 1
            )

            Spacer(modifier = Modifier.width(4.dp))

            // Bar untuk GPU 2
            Box(
                modifier = Modifier
                    .weight((gpu2Score.coerceAtLeast(0) / maxScore.toFloat()).coerceAtLeast(0.01f)) // Pastikan bar tidak terlalu kecil
                    .fillMaxHeight()
                    .background(Color(0xFFFFDBDE)) // Warna untuk GPU 2
            )
        }

        // Menampilkan nilai score untuk kedua GPU
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$gpu1Score",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFc2ff86) // Warna untuk GPU 1
            )
            Text(
                text = "$gpu2Score",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFFFDBDE) // Warna untuk GPU 2
            )
        }
    }
}

