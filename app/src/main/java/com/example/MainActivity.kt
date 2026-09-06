package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    WatchFaceShowcase()
                }
            }
        }
    }
}

@Composable
fun WatchFaceShowcase() {
    val pagerState = rememberPagerState(pageCount = { 3 })
    
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Pixel Watch Faces",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 32.dp),
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                when (page) {
                    0 -> DigitalBoldWatchFace()
                    1 -> AnalogConcentricWatchFace()
                    2 -> UtilityActivityWatchFace()
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            repeat(3) { index ->
                val color = if (pagerState.currentPage == index) Color.White else Color.DarkGray
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "مرر لليمين واليسار لتغيير الواجهة",
            color = Color.Gray,
            fontSize = 14.sp
        )
    }
}

// 1. Digital Bold (Pixel Style)
@Composable
fun DigitalBoldWatchFace() {
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTime = System.currentTimeMillis()
        }
    }
    
    val date = Date(currentTime)
    val hourFormat = SimpleDateFormat("HH", Locale.getDefault())
    val minuteFormat = SimpleDateFormat("mm", Locale.getDefault())
    val secondFormat = SimpleDateFormat("ss", Locale.getDefault())
    val dayFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
    
    val hours = hourFormat.format(date)
    val minutes = minuteFormat.format(date)
    val seconds = secondFormat.format(date).toInt()
    
    val accentColor = Color(0xFF8AB4F8) // Google Blue accent
    
    WatchContainer {
        // Outer seconds arc
        Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            drawArc(
                color = Color.DarkGray.copy(alpha = 0.3f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = accentColor,
                startAngle = -90f,
                sweepAngle = (seconds / 60f) * 360f,
                useCenter = false,
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = dayFormat.format(date).uppercase(),
                color = Color.LightGray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = hours,
                    color = Color.White,
                    fontSize = 72.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = ":",
                    color = accentColor,
                    fontSize = 72.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 4.dp).offset(y = (-4).dp)
                )
                Text(
                    text = minutes,
                    color = Color.White,
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Light
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                ComplicationIcon(Icons.Filled.Favorite, "72", Color(0xFFF28B82))
                ComplicationIcon(Icons.AutoMirrored.Filled.DirectionsWalk, "6k", Color(0xFF81C995))
            }
        }
    }
}

// 2. Analog Concentric (Pixel Watch Signature)
@Composable
fun AnalogConcentricWatchFace() {
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(50) // Faster update for smooth sweeping second hand
            currentTime = System.currentTimeMillis()
        }
    }
    
    val date = Date(currentTime)
    val h = SimpleDateFormat("H", Locale.getDefault()).format(date).toInt()
    val m = SimpleDateFormat("m", Locale.getDefault()).format(date).toInt()
    val s = SimpleDateFormat("s", Locale.getDefault()).format(date).toInt()
    val ms = SimpleDateFormat("S", Locale.getDefault()).format(date).toInt()
    
    val accentColor = Color(0xFFFDE293) // Google Yellow accent
    
    WatchContainer {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width / 2
            
            // Draw tick marks
            for (i in 0 until 60) {
                val angle = i * 6f
                val tickLength = if (i % 5 == 0) 16.dp.toPx() else 8.dp.toPx()
                val tickColor = if (i % 5 == 0) Color.White else Color.Gray
                
                rotate(angle) {
                    drawLine(
                        color = tickColor,
                        start = Offset(center.x, 12.dp.toPx()),
                        end = Offset(center.x, 12.dp.toPx() + tickLength),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }
            
            // Hour hand
            val hourAngle = (h % 12 + m / 60f) * 30f
            rotate(hourAngle) {
                drawLine(
                    color = Color.White,
                    start = center,
                    end = Offset(center.x, center.y - radius * 0.5f),
                    strokeWidth = 8.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
            
            // Minute hand
            val minuteAngle = (m + s / 60f) * 6f
            rotate(minuteAngle) {
                drawLine(
                    color = Color.LightGray,
                    start = center,
                    end = Offset(center.x, center.y - radius * 0.75f),
                    strokeWidth = 6.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
            
            // Second hand (Sweeping)
            val secondAngle = s * 6f + (ms / 1000f) * 6f
            rotate(secondAngle) {
                drawLine(
                    color = accentColor,
                    start = Offset(center.x, center.y + 24.dp.toPx()),
                    end = Offset(center.x, center.y - radius * 0.85f),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
            
            // Center dot
            drawCircle(
                color = accentColor,
                radius = 6.dp.toPx(),
                center = center
            )
            drawCircle(
                color = Color.Black,
                radius = 2.dp.toPx(),
                center = center
            )
        }
        
        // Date complication at 3 o'clock
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = SimpleDateFormat("d", Locale.getDefault()).format(date),
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(end = 24.dp)
                    .clip(CircleShape)
                    .background(accentColor)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
    }
}

// 3. Utility / Activity Watch Face
@Composable
fun UtilityActivityWatchFace() {
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTime = System.currentTimeMillis()
        }
    }
    val date = Date(currentTime)
    val timeStr = SimpleDateFormat("h:mm", Locale.getDefault()).format(date)
    val secStr = SimpleDateFormat("ss", Locale.getDefault()).format(date)
    
    WatchContainer {
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top: Weather
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.WbSunny, "Weather", tint = Color(0xFFFDB813), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("24°", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 16.sp)
            }
            
            // Center: Time
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = timeStr,
                    color = Color.White,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.alignByBaseline()
                )
                Text(
                    text = secStr,
                    color = Color(0xFF34A853), // Google Green
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.alignByBaseline().padding(start = 4.dp)
                )
            }
            
            // Bottom: Arcs
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                ActivityArc(Color(0xFFE91E63), 0.7f, Icons.Filled.Favorite)
                ActivityArc(Color(0xFF03A9F4), 0.5f, Icons.AutoMirrored.Filled.DirectionsWalk)
                ActivityArc(Color(0xFFFFC107), 0.9f, Icons.Filled.BatteryFull)
            }
        }
    }
}

@Composable
fun ActivityArc(color: Color, progress: Float, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(
        modifier = Modifier.size(56.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = color.copy(alpha = 0.2f),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = color,
                startAngle = 135f,
                sweepAngle = 270f * progress,
                useCenter = false,
                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
    }
}

@Composable
fun WatchContainer(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(16.dp)
            .clip(CircleShape)
            .background(Color(0xFF0F0F0F)), // Very dark gray, almost black
        contentAlignment = Alignment.Center
    ) {
        content()
        // Subtle glare effect to simulate glass
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasSize = size
            drawArc(
                color = Color.White.copy(alpha = 0.04f),
                startAngle = 200f,
                sweepAngle = 120f,
                useCenter = true,
                size = canvasSize
            )
        }
    }
}

@Composable
fun ComplicationIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, tint: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
