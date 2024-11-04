package com.superbgoal.caritasrig.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.superbgoal.caritasrig.activity.build.CasingActivity
import com.superbgoal.caritasrig.activity.build.CpuActivity
import com.superbgoal.caritasrig.activity.build.InternalHardDriveActivity
import com.superbgoal.caritasrig.activity.build.MemoryActivity
import com.superbgoal.caritasrig.activity.build.MotherboardActivity
import com.superbgoal.caritasrig.activity.build.PowerSupplyActivity
import com.superbgoal.caritasrig.activity.build.VideoCardActivity

class BuildActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BuildScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Build Your Setup",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                modifier = Modifier.background(Color.White),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors()
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { ComponentCard(title = "CPU", activity = CpuActivity::class.java) }
            item { ComponentCard(title = "Case", activity = CasingActivity::class.java) }
            item { ComponentCard(title = "GPU", activity = VideoCardActivity::class.java) }
            item { ComponentCard(title = "Motherboard", activity = MotherboardActivity::class.java) }
            item { ComponentCard(title = "RAM", activity = MemoryActivity::class.java) }
            item { ComponentCard(title = "Storage", activity = InternalHardDriveActivity::class.java) }
            item { ComponentCard(title = "Power Supply", activity = PowerSupplyActivity::class.java) }
        }
    }
}

@Composable
fun ComponentCard(title: String, activity: Class<out ComponentActivity>) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )

            Button(
                onClick = {
                    val intent = Intent(context, activity)
                    context.startActivity(intent)
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(text = "Add Component")
            }
        }
    }
}
