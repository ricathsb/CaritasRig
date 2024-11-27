package com.superbgoal.caritasrig.activity.homepage.compare

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aay.compose.radarChart.RadarChart
import com.aay.compose.radarChart.model.NetLinesStyle
import com.aay.compose.radarChart.model.Polygon
import com.aay.compose.radarChart.model.PolygonStyle


@Composable
fun ProcessorComparisonScreen() {
    val processorList = remember { mutableStateListOf<Processor2>() }

    val isDialogVisible = remember { mutableStateOf(false) }

    val availableProcessors = listOf(
        Processor2(
            name = "AMD Ryzen 9 7950X3D",
            price = 564.0,
            core_count = 16,
            core_clock = 4.2,
            boost_clock = 5.7,
            tdp = 120,
            graphics = "Radeon",
            smt = true,
            single_core_score = 2100,
            multi_core_score = 30000
        ),
        Processor2(
            name = "AMD Ryzen 9 7900X",
            price = 396.58,
            core_count = 12,
            core_clock = 4.7,
            boost_clock = 5.6,
            tdp = 170,
            graphics = "Radeon",
            smt = true,
            single_core_score = 2050,
            multi_core_score = 26500
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Processor Comparison",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = { isDialogVisible.value = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Processor")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Selected Processors:",
            style = MaterialTheme.typography.bodyMedium
        )
        processorList.forEach { processor ->
            Text(text = "- ${processor.name}", style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (processorList.size == 2) {
            RadarChartProsesor(processorList[0], processorList[1])
        } else {
            Text(
                text = "Please select 2 processors to compare.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }

    if (isDialogVisible.value) {
        AlertDialog(
            onDismissRequest = { isDialogVisible.value = false },
            title = {
                Text("Select a Processor")
            },
            text = {
                Column {
                    availableProcessors.forEach { processor ->
                        Button(
                            onClick = {
                                if (processorList.size < 2 && !processorList.contains(processor)) {
                                    processorList.add(processor)
                                }
                                isDialogVisible.value = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(processor.name)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { isDialogVisible.value = false }) {
                    Text("Close")
                }
            }
        )
    }
}


@Composable
fun RadarChartProsesor(processor1: Processor2, processor2: Processor2) {
    val maxValues = listOf(16.0, 5.0, 6.0, 200.0, 2500.0, 30000.0)
    val scalarValue = 20.0

    val processor1Values = listOf(
        processor1.core_count.toDouble(),
        processor1.core_clock,
        processor1.boost_clock,
        processor1.tdp.toDouble(),
        processor1.single_core_score.toDouble(),
        processor1.multi_core_score.toDouble()
    ).mapIndexed { index, value ->
        (value / maxValues[index]) * scalarValue
    }

    val processor2Values = listOf(
        processor2.core_count.toDouble(),
        processor2.core_clock,
        processor2.boost_clock,
        processor2.tdp.toDouble(),
        processor2.single_core_score.toDouble(),
        processor2.multi_core_score.toDouble()
    ).mapIndexed { index, value ->
        (value / maxValues[index]) * scalarValue
    }

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

data class Processor2(
    val name: String,
    val price: Double,
    val core_count: Int,
    val core_clock: Double,
    val boost_clock: Double,
    val tdp: Int,
    val graphics: String,
    val smt: Boolean,
    val single_core_score: Int,
    val multi_core_score: Int
)


