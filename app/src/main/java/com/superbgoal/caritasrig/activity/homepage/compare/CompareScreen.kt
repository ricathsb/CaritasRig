package com.superbgoal.caritasrig.activity.homepage.compare

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

//@Composable
//fun RadarChart(
//    data: List<Float>,            // Normalized data points (0 to 1)
//    labels: List<String>,         // Labels for the axes
//    maxValue: Float = 1f,         // Maximum value for scaling
//    modifier: Modifier = Modifier,
//    primaryColor: Color = Color.Red,
//    secondaryColor: Color = Color.Blue
//) {
//    Canvas(modifier = modifier) {
//        val centerX = size.width / 2
//        val centerY = size.height / 2
//        val radius = min(centerX, centerY) * 0.8f
//        val numAxes = data.size
//        val angleStep = (2 * Math.PI / numAxes).toFloat()
//
//        // Draw grid and labels
//        for (i in 1..5) { // Create concentric rings
//            val scale = i / 5f
//            drawCircle(
//                color = Color.Gray.copy(alpha = 0.3f),
//                radius = radius * scale,
//                center = Offset(centerX, centerY)
//            )
//        }
//
//        // Draw axes
//        for (i in data.indices) {
//            val angle = i * angleStep
//            val endX = centerX + radius * cos(angle)
//            val endY = centerY + radius * sin(angle)
//            drawLine(
//                color = Color.Gray,
//                start = Offset(centerX, centerY),
//                end = Offset(endX, endY),
//                strokeWidth = 2f
//            )
//        }
//
//        // Draw data (polygon)
//        val path = Path()
//        for (i in data.indices) {
//            val angle = i * angleStep
//            val valueRadius = radius * (data[i] / maxValue)
//            val pointX = centerX + valueRadius * cos(angle)
//            val pointY = centerY + valueRadius * sin(angle)
//
//            if (i == 0) {
//                path.moveTo(pointX, pointY)
//            } else {
//                path.lineTo(pointX, pointY)
//            }
//        }
//        path.close()
//
//        drawPath(
//            path = path,
//            color = primaryColor.copy(alpha = 0.4f)
//        )
//        drawPath(
//            path = path,
//            color = primaryColor,
//            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
//        )
//    }
//}
//
//@Preview
//@Composable
//fun RadarChartPreview() {
//    val labels = listOf(
//        "Single-Core Score",
//        "Multi-Core Score",
//        "Price",
//        "Core Count",
//        "Boost Clock",
//        "TDP"
//    )
//
//    // Normalized data points (0 to 1)
//    val data7950X3D = listOf(1f, 1f, 1f, 1f, 1f, 0.7f)
//    val data7900X = listOf(0.98f, 0.88f, 0.7f, 0.75f, 0.98f, 1f)
//
//    MaterialTheme {
//        RadarChart(
//            data = data7950X3D,
//            labels = labels,
//            modifier = Modifier
//                .size(300.dp)
//                .padding(16.dp),
//            primaryColor = Color.Red
//        )
//    }
//}

