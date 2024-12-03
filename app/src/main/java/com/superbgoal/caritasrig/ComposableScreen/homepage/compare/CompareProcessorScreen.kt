package com.superbgoal.caritasrig.ComposableScreen.homepage.compare

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
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
    val processors: List<Processor> = remember {
        loadItemsFromResources(
            context = context,
            resourceId = R.raw.processor
        )
    }

    var searchText by remember { mutableStateOf("") }
    var selectedProcessors by remember { mutableStateOf<List<Processor>>(emptyList()) }

    val showSearchbar by remember(selectedProcessors) {
        mutableStateOf(selectedProcessors.size < 2)
    }

    val filteredProcessors = remember(searchText, processors) {
        if (searchText.isBlank()) processors
        else processors.filter { it.name.contains(searchText, ignoreCase = true) }
    }

    fun addProcessor(processor: Processor) {
        selectedProcessors = selectedProcessors + processor
    }

    fun removeProcessor(processor: Processor) {
        selectedProcessors = selectedProcessors - processor
    }

    // Background container
    Box(
        modifier = Modifier
            .fillMaxSize()

    ) {
        Image(
            painter = painterResource(id = R.drawable.component_bg),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
        )// Atur transparansi sesuai kebutuhan)
        {
            Column {
                if (showSearchbar) {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = { Text("Search Processor") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 13.dp, vertical = 15.dp)
                            .background(
                                color = colorResource(id = R.color.white),
                                shape = RoundedCornerShape(50.dp)
                            ),
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon",
                                tint = Color.DarkGray
                            )
                        },
                        shape = RoundedCornerShape(50.dp),
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    // Menampilkan prosesor yang dipilih
                    if (selectedProcessors.isNotEmpty()) {
                        items(selectedProcessors) { processor ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp, start = 8.dp, end = 8.dp, bottom = 8.dp)
                                    .border(
                                        width = 1.dp,
                                        color = Color.Gray,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = processor.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.White
                                    ,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { removeProcessor(processor) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove Processor",
                                        tint = Color.Red
                                    )
                                }
                            }
                        }
                    }

                    // Menampilkan RadarChart dan PerformanceBar jika dua prosesor dipilih
                    if (selectedProcessors.size == 2) {
                        item {
                            RadarChartProcessor(
                                processor1 = selectedProcessors[0],
                                processor2 = selectedProcessors[1]
                            )

                            PerformanceBar(
                                label = "Single-Core Score",
                                processor1Score = selectedProcessors[0].single_core_score,
                                processor2Score = selectedProcessors[1].single_core_score
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            PerformanceBar(
                                label = "Multi-Core Score",
                                processor1Score = selectedProcessors[0].multi_core_score,
                                processor2Score = selectedProcessors[1].multi_core_score
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    // Menampilkan hasil pencarian prosesor
                    if (filteredProcessors.isNotEmpty() && searchText.isNotBlank()) {
                        items(filteredProcessors) { processor ->
                            val backgroundColor = colorResource(id = R.color.brown1)

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(backgroundColor)
                                    .clickable {
                                        addProcessor(processor)
                                        searchText = "" // Menghapus teks pencarian setelah memilih
                                    }
                                    .padding(horizontal = 8.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = processor.name,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            Divider(
                                color = colorResource(id = R.color.brown),
                                thickness = 1.dp
                            )
                        }
                    }
                }
            }
        }

    }
}


@Composable
fun RadarChartProcessor(processor1: Processor, processor2: Processor) {
    val maxValues = listOf(16.0, 5.0, 6.0, 200.0, 2500.0, 30000.0)
    val scalarValue = 20.0

    fun clamp(value: Double): Double {
        return value.coerceIn(0.0, scalarValue)
    }

    val processor1Values = listOf(
        processor1.core_count.toDouble(),
        processor1.core_clock,
        processor1.boost_clock,
        processor1.tdp.toDouble(),
        processor1.single_core_score.toDouble(),
        processor1.multi_core_score.toDouble()
    ).mapIndexed { index, value ->
        clamp((value / maxValues[index]) * scalarValue)
    }

    val processor2Values = listOf(
        processor2.core_count.toDouble(),
        processor2.core_clock,
        processor2.boost_clock,
        processor2.tdp.toDouble(),
        processor2.single_core_score.toDouble(),
        processor2.multi_core_score.toDouble()
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
                "Core Count", "Core Clock", "Boost Clock",
                "TDP", "Single Core Score", "Multi Core Score"
            ),
            labelsStyle = TextStyle(
                color = Color.White
                ,
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
                color = Color.White
                ,
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
                name = processor1.name
            )
            LegendItem(
                color = Color(0xffFFDBDE),
                name = processor2.name
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Horizontal Bars for Single and Multi-Core Scores

    }
}

@Composable
fun PerformanceBar(label: String, processor1Score: Int, processor2Score: Int) {
    val maxScore = maxOf(processor1Score.coerceAtLeast(0), processor2Score.coerceAtLeast(0)).takeIf { it > 0 } ?: 1

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge, color = Color.White)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bar untuk Processor 1
            Box(
                modifier = Modifier
                    .weight(
                        (processor1Score.coerceAtLeast(0) / maxScore.toFloat()).coerceAtLeast(0.01f)
                    ) // Berat minimal 0.01 untuk memastikan nilai positif
                    .fillMaxHeight()
                    .background(Color(0xffc2ff86))
            )

            Spacer(modifier = Modifier.width(4.dp))

            // Bar untuk Processor 2
            Box(
                modifier = Modifier
                    .weight(
                        (processor2Score.coerceAtLeast(0) / maxScore.toFloat()).coerceAtLeast(0.01f)
                    ) // Berat minimal 0.01 untuk memastikan nilai positif
                    .fillMaxHeight()
                    .background(Color(0xffFFDBDE))
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Teks dengan Outline untuk Processor 1
            Text(
                text = "$processor1Score",
                style = MaterialTheme.typography.bodySmall.copy(
                    shadow = Shadow(
                        color = Color.Black
                        ,
                        blurRadius = 3f
                    )
                ),
                color = Color(0xFFBEFF7E)
            )

            // Teks dengan Outline untuk Processor 2
            Text(
                text = "$processor2Score",
                style = MaterialTheme.typography.bodySmall.copy(
                    shadow = Shadow(
                        color = Color.Black
                        ,
                        blurRadius = 3f
                    )
                ),
                color = Color(0xFFFF6D86)
            )
        }
    }
}