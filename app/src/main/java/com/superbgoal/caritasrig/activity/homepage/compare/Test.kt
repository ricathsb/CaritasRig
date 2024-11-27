package com.superbgoal.caritasrig.activity.homepage.compare

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.superbgoal.caritasrig.data.model.component.Processor
import com.superbgoal.caritasrig.functions.loadItemsFromResources


@Composable
fun ProcessorComparisonScreen() {
    val context = LocalContext.current
    // Load processor data
    val processors: List<Processor> = remember {
        loadItemsFromResources(
            context = context,
            resourceId = R.raw.processor
        )
    }

    // State to hold selected processors
    var selectedProcessors by remember { mutableStateOf<List<Processor>>(emptyList()) }

    // State to manage dialog visibility for selecting processor
    var showDialog by remember { mutableStateOf(false) }
    var selectedProcessorIndex by remember { mutableStateOf(-1) } // -1 indicates no selection

    // Add processor to selected list
    fun addProcessor(processor: Processor) {
        selectedProcessors = selectedProcessors + processor
    }

    // Remove processor from selected list
    fun removeProcessor(processor: Processor) {
        selectedProcessors = selectedProcessors - processor
    }

    // Display UI for comparison
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Compare Processors",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Display button to add processor
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Add Processor")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display selected processors list
        if (selectedProcessors.isNotEmpty()) {
            Text(
                text = "Selected Processors:",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )

            LazyColumn {
                items(selectedProcessors) { processor ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                // Logic for handling click to compare processors (optional)
                            }
                    ) {
                        Text(
                            text = processor.name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Black,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { removeProcessor(processor) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove Processor",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Show Radar Chart comparison if 2 processors are selected
        if (selectedProcessors.size == 2) {
            RadarChartProsesor(
                processor1 = selectedProcessors[0],
                processor2 = selectedProcessors[1]
            )
        }
    }

    // Show dialog for processor selection
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Select Processor") },
            text = {
                LazyColumn {
                    items(processors) { processor ->
                        Text(
                            text = processor.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable {
                                    addProcessor(processor)
                                    showDialog = false
                                }
                                .padding(8.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun RadarChartProsesor(processor1: Processor, processor2: Processor) {
    val maxValues = listOf(16.0, 5.0, 6.0, 200.0, 2500.0, 30000.0)
    val scalarValue = 20.0

    // Function to ensure values are within the range [0, scalarValue]
    fun clamp(value: Double): Double {
        return value.coerceIn(0.0, scalarValue)
    }

    // Calculate values for processor1
    val processor1Values = listOf(
        processor1.core_count.toDouble(),
        processor1.core_clock,
        processor1.boost_clock,
        processor1.tdp.toDouble(),
        processor1.single_core_score.toDouble(),
        processor1.multi_core_score.toDouble()
    ).mapIndexed { index, value ->
        clamp((value / maxValues[index]) * scalarValue)  // Ensure the value is within range
    }

    // Calculate values for processor2
    val processor2Values = listOf(
        processor2.core_count.toDouble(),
        processor2.core_clock,
        processor2.boost_clock,
        processor2.tdp.toDouble(),
        processor2.single_core_score.toDouble(),
        processor2.multi_core_score.toDouble()
    ).mapIndexed { index, value ->
        clamp((value / maxValues[index]) * scalarValue)  // Ensure the value is within range
    }

    // Draw Radar Chart with clamped values
    RadarChart(
        modifier = Modifier.fillMaxSize(),
        radarLabels = listOf(
            "Core Count", "Core Clock", "Boost Clock",
            "TDP", "Single Core Score", "Multi Core Score"
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
                values = processor1Values,
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
                values = processor2Values,
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
}





