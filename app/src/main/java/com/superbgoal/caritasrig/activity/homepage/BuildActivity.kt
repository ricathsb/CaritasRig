package com.superbgoal.caritasrig.activity.homepage

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.activity.homepage.build.CasingActivity
import com.superbgoal.caritasrig.activity.homepage.build.CpuActivity
import com.superbgoal.caritasrig.activity.homepage.build.InternalHardDriveActivity
import com.superbgoal.caritasrig.activity.homepage.build.MemoryActivity
import com.superbgoal.caritasrig.activity.homepage.build.MotherboardActivity
import com.superbgoal.caritasrig.activity.homepage.build.PowerSupplyActivity
import com.superbgoal.caritasrig.activity.homepage.build.VideoCardActivity
import com.superbgoal.caritasrig.functions.auth.ProcessorInfo
import com.superbgoal.caritasrig.functions.auth.VideoCardInfo
import com.superbgoal.caritasrig.data.model.Processor
import com.superbgoal.caritasrig.data.model.VideoCard

class BuildActivity : ComponentActivity() {
    private var processor: Processor? = null
    private var videoCard: VideoCard? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BuildScreen(processor, videoCard, onAddComponentClick = { component ->
                when (component) {
                    "CPU" -> startActivityForResult(Intent(this, CpuActivity::class.java), REQUEST_CODE_CPU)
                    "GPU" -> startActivityForResult(Intent(this, VideoCardActivity::class.java), REQUEST_CODE_GPU)
                    "Case" -> startActivity(Intent(this, CasingActivity::class.java))
                    "Motherboard" -> startActivity(Intent(this, MotherboardActivity::class.java))
                    "RAM" -> startActivity(Intent(this, MemoryActivity::class.java))
                    "Storage" -> startActivity(Intent(this, InternalHardDriveActivity::class.java))
                    "Power Supply" -> startActivity(Intent(this, PowerSupplyActivity::class.java))
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            data?.let {
                when (requestCode) {
                    REQUEST_CODE_CPU -> processor = it.getParcelableExtra("processor")
                    REQUEST_CODE_GPU -> videoCard = it.getParcelableExtra("videoCard")
                }
            }
            setContent {
                BuildScreen(processor, videoCard, onAddComponentClick = { component ->
                    when (component) {
                        "CPU" -> startActivityForResult(Intent(this, CpuActivity::class.java), REQUEST_CODE_CPU)
                        "GPU" -> startActivityForResult(Intent(this, VideoCardActivity::class.java), REQUEST_CODE_GPU)
                        "Case" -> startActivity(Intent(this, CasingActivity::class.java))
                        "Motherboard" -> startActivity(Intent(this, MotherboardActivity::class.java))
                        "RAM" -> startActivity(Intent(this, MemoryActivity::class.java))
                        "Storage" -> startActivity(Intent(this, InternalHardDriveActivity::class.java))
                        "Power Supply" -> startActivity(Intent(this, PowerSupplyActivity::class.java))
                    }
                })
            }
        }
    }

    companion object {
        const val REQUEST_CODE_CPU = 1
        const val REQUEST_CODE_GPU = 2
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildScreen(
    processor: Processor?,
    videoCard: VideoCard?,
    onAddComponentClick: (String) -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_build),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {},
                    modifier = Modifier.height(145.dp),
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                val intent = Intent(context, HomeActivity::class.java)
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .padding(start = 30.dp, top = 60.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_home),
                                contentDescription = "Home",
                                modifier = Modifier.size(80.dp),
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                // Add save function here
                            },
                            modifier = Modifier
                                .padding(end = 30.dp, top = 60.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_save),
                                contentDescription = "Save",
                                modifier = Modifier.size(80.dp),
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp, 0.dp, 16.dp, 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { ComponentCard(title = "CPU", processor = processor, onAddClick = { onAddComponentClick("CPU") }) }
                item { ComponentCard(title = "Case", onAddClick = { onAddComponentClick("Case") }) }
                item { ComponentCard(title = "GPU", videoCard = videoCard, onAddClick = { onAddComponentClick("GPU") }) }
                item { ComponentCard(title = "Motherboard", onAddClick = { onAddComponentClick("Motherboard") }) }
                item { ComponentCard(title = "RAM", onAddClick = { onAddComponentClick("RAM") }) }
                item { ComponentCard(title = "Storage", onAddClick = { onAddComponentClick("Storage") }) }
                item { ComponentCard(title = "Power Supply", onAddClick = { onAddComponentClick("Power Supply") }) }
            }
        }
    }
}


@Composable
fun ComponentCard(
    title: String,
    processor: Processor? = null,
    videoCard: VideoCard? = null,
    onAddClick: () -> Unit
) {
    val context = LocalContext.current // Define context here

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp),
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(10.dp)
                )
            }

            if (title == "CPU" && processor != null) {
                ProcessorInfo(processor)
            }

            if (title == "GPU" && videoCard != null) {
                VideoCardInfo(videoCard)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(10.dp))  // Sudut melengkung 10 dp
                    .background(colorResource(id = R.color.brown)),  // Warna cokelat dari colors.xml
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { onAddClick() }, // Panggil onAddClick ketika tombol ditekan
                    modifier = Modifier
                        .padding(8.dp)
                        .background(Color.Transparent),  // Warna latar belakang transparan
                    elevation = ButtonDefaults.buttonElevation(0.dp) // Menghapus efek bayangan tombol
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.add_btn),  // Gambar dari res/drawable/add_btn
                        contentDescription = "Add Icon",
                        tint = Color.Unspecified,  // Mempertahankan warna asli ikon
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))  // Jarak antara ikon dan teks
                    Text(text = "Add Component")
                }
            }
        }
    }
}
