package com.example

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    background = Color(0xFF101412),
                    surface = Color(0xFF1A221D),
                    primary = Color(0xFF00E676)
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF101412)
                ) {
                    WatchFaceConstructorScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchFaceConstructorScreen() {
    var currentTimeMs by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var watchStyle by remember { mutableStateOf(WatchStyle.ANALOG) }
    var isEditing by remember { mutableStateOf(false) }

    var primaryColor by remember { mutableStateOf(Color(0xFF00E676)) }
    var accentColor by remember { mutableStateOf(Color(0xFFF44336)) }
    var showSeconds by remember { mutableStateOf(true) }
    var showBattery by remember { mutableStateOf(true) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )

    LaunchedEffect(Unit) {
        while (true) {
            currentTimeMs = System.currentTimeMillis()
            delay(1000)
        }
    }

    val calendar = Calendar.getInstance().apply { timeInMillis = currentTimeMs }
    val hours = calendar.get(Calendar.HOUR_OF_DAY)
    val minutes = calendar.get(Calendar.MINUTE)
    val seconds = calendar.get(Calendar.SECOND)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Watch Face Builder", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { isEditing = !isEditing }) {
                        Icon(
                            imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = "Edit Settings",
                            tint = primaryColor
                        )
                    }
                }
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            // Watch Face Preview container
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
                    .border(12.dp, Color(0xFF2A2A2A), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Background Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                when (watchStyle) {
                    WatchStyle.ANALOG -> {
                        AnalogWatchFace(
                            hours = hours,
                            minutes = minutes,
                            seconds = seconds,
                            primaryColor = primaryColor,
                            accentColor = accentColor,
                            showSeconds = showSeconds
                        )
                    }
                    WatchStyle.DIGITAL -> {
                        DigitalWatchFace(
                            hours = hours,
                            minutes = minutes,
                            seconds = seconds,
                            primaryColor = primaryColor,
                            accentColor = accentColor,
                            showSeconds = showSeconds
                        )
                    }
                }

                if (showBattery) {
                    BatteryIndicator(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 24.dp),
                        color = primaryColor
                    )
                }
            }

            AnimatedVisibility(
                visible = isEditing,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        "Style Options",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StyleToggleButton(
                            label = "Analog",
                            isSelected = watchStyle == WatchStyle.ANALOG,
                            color = primaryColor,
                            onClick = { watchStyle = WatchStyle.ANALOG }
                        )
                        StyleToggleButton(
                            label = "Digital",
                            isSelected = watchStyle == WatchStyle.DIGITAL,
                            color = primaryColor,
                            onClick = { watchStyle = WatchStyle.DIGITAL }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Background Image",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Button(
                        onClick = { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A2A2A)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (selectedImageUri == null) "Select Image" else "Change Image")
                    }

                    if (selectedImageUri != null) {
                        TextButton(
                            onClick = { selectedImageUri = null },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Remove Image", color = Color(0xFFE57373))
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Theme Colors",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        val colorOptions = listOf(Color(0xFF00E676), Color(0xFF29B6F6), Color(0xFFAB47BC), Color(0xFFFFA726), Color.White)
                        colorOptions.forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (primaryColor == color) 3.dp else 0.dp,
                                        color = if (primaryColor == color) Color.White else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { primaryColor = color }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Features",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StateToggleButton(
                            icon = Icons.Default.Timer,
                            label = "Seconds",
                            isActive = showSeconds,
                            color = primaryColor,
                            onClick = { showSeconds = !showSeconds }
                        )
                        StateToggleButton(
                            icon = Icons.Default.BatteryFull,
                            label = "Battery",
                            isActive = showBattery,
                            color = primaryColor,
                            onClick = { showBattery = !showBattery }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnalogWatchFace(
    hours: Int,
    minutes: Int,
    seconds: Int,
    primaryColor: Color,
    accentColor: Color,
    showSeconds: Boolean
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.width / 2

        for (i in 0..11) {
            val angle = i * 30 * (Math.PI / 180)
            val lineLength = if (i % 3 == 0) 15.dp.toPx() else 8.dp.toPx()
            val strokeWidth = if (i % 3 == 0) 4f else 2f
            
            val startX = center.x + (radius - lineLength - 10.dp.toPx()) * cos(angle).toFloat()
            val startY = center.y + (radius - lineLength - 10.dp.toPx()) * sin(angle).toFloat()
            val endX = center.x + (radius - 10.dp.toPx()) * cos(angle).toFloat()
            val endY = center.y + (radius - 10.dp.toPx()) * sin(angle).toFloat()
            
            drawLine(
                color = Color.White.copy(alpha = 0.8f),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }

        val hAngle = ((hours % 12 + minutes / 60f) * 30 - 90) * (Math.PI / 180)
        drawLine(
            color = Color.White,
            start = center,
            end = Offset(
                x = center.x + (radius * 0.5f) * cos(hAngle).toFloat(),
                y = center.y + (radius * 0.5f) * sin(hAngle).toFloat()
            ),
            strokeWidth = 10f,
            cap = StrokeCap.Round
        )

        val mAngle = ((minutes + seconds / 60f) * 6 - 90) * (Math.PI / 180)
        drawLine(
            color = primaryColor,
            start = center,
            end = Offset(
                x = center.x + (radius * 0.75f) * cos(mAngle).toFloat(),
                y = center.y + (radius * 0.75f) * sin(mAngle).toFloat()
            ),
            strokeWidth = 6f,
            cap = StrokeCap.Round
        )

        if (showSeconds) {
            val sAngle = (seconds * 6 - 90) * (Math.PI / 180)
            drawLine(
                color = accentColor,
                start = Offset(
                    x = center.x - (radius * 0.15f) * cos(sAngle).toFloat(),
                    y = center.y - (radius * 0.15f) * sin(sAngle).toFloat()
                ),
                end = Offset(
                    x = center.x + (radius * 0.85f) * cos(sAngle).toFloat(),
                    y = center.y + (radius * 0.85f) * sin(sAngle).toFloat()
                ),
                strokeWidth = 3f,
                cap = StrokeCap.Round
            )
            drawCircle(
                color = accentColor,
                radius = 4.dp.toPx(),
                center = center
            )
        } else {
            drawCircle(
                color = primaryColor,
                radius = 6.dp.toPx(),
                center = center
            )
        }
    }
}

@Composable
fun DigitalWatchFace(
    hours: Int,
    minutes: Int,
    seconds: Int,
    primaryColor: Color,
    accentColor: Color,
    showSeconds: Boolean
) {
    val timeString = String.format("%02d:%02d", hours, minutes)
    val secString = String.format("%02d", seconds)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = timeString,
                color = Color.White,
                fontSize = 48.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            if (showSeconds) {
                Text(
                    text = secString,
                    color = accentColor,
                    fontSize = 24.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .height(4.dp)
                .width(60.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(primaryColor)
        )
    }
}

@Composable
fun BatteryIndicator(modifier: Modifier = Modifier, color: Color) {
    Row(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.BatteryFull,
            contentDescription = "Battery",
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            "84%",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun StyleToggleButton(
    label: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) color.copy(alpha = 0.2f) else Color(0xFF2A2A2A))
            .border(
                width = 2.dp,
                color = if (isSelected) color else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) color else Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun StateToggleButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (isActive) color else Color(0xFF2A2A2A)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) Color.Black else Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            color = if (isActive) color else Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

enum class WatchStyle {
    ANALOG, DIGITAL
}
