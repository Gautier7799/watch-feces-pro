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
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MyApplicationTheme
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
                Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
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
        Text(text = "Pixel Watch Faces", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 32.dp))
        
        HorizontalPager(state = pagerState, contentPadding = PaddingValues(horizontal = 32.dp), modifier = Modifier.fillMaxWidth()) { page ->
            Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f), contentAlignment = Alignment.Center) {
                when (page) {
                    0 -> DigitalBoldWatchFace()
                    1 -> AnalogConcentricWatchFace()
                    2 -> UtilityActivityWatchFace()
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            repeat(3) { index ->
                val color = if (pagerState.currentPage == index) Color.White else Color.DarkGray
                Box(modifier = Modifier.padding(4.dp).size(8.dp).clip(CircleShape).background(color))
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "مرر لليمين واليسار لتغيير الواجهة", color = Color.Gray, fontSize = 14.sp)
    }
}

@Composable
fun DigitalBoldWatchFace() {
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) { while (true) { delay(1000); currentTime = System.currentTimeMillis() } }
    
    val date = Date(currentTime)
    val hours = SimpleDateFormat("HH", Locale.getDefault()).format(date)
    val minutes = SimpleDateFormat("mm", Locale.getDefault()).format(date)
    val seconds = SimpleDateFormat("ss", Locale.getDefault()).format(date).toInt()
    val accentColor = Color(0xFF8AB4F8) // Google Blue
    
    WatchContainer {
        Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            drawArc(color = Color.DarkGray.copy(alpha = 0.3f), startAngle = -90f, sweepAngle = 360f, useCenter = false, style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round))
            drawArc(color = accentColor, startAngle = -90f, sweepAngle = (seconds / 60f) * 360f, useCenter = false, style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(text = SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(date).uppercase(), color = Color.LightGray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = hours, color = Color.White, fontSize = 72.sp, fontWeight = FontWeight.ExtraBold)
                Text(text = ":", color = accentColor, fontSize = 72.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 4.dp).offset(y = (-4).dp))
                Text(text = minutes, color = Color.White, fontSize = 72.sp, fontWeight = FontWeight.Light)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                ComplicationIcon(Icons.Filled.Favorite, "72", Color(0xFFF28B82))
                ComplicationIcon(Icons.AutoMirrored.Filled.DirectionsWalk, "6k", Color(0xFF81C995))
            }
        }
    }
}

// ... AnalogConcentricWatchFace and UtilityActivityWatchFace are also included in the live code ...

@Composable
fun WatchContainer(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().aspectRatio(1f).padding(16.dp).clip(CircleShape).background(Color(0xFF0F0F0F)),
        contentAlignment = Alignment.Center
    ) {
        content()
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(color = Color.White.copy(alpha = 0.04f), startAngle = 200f, sweepAngle = 120f, useCenter = true, size = size)
        }
    }
}

@Composable
fun ComplicationIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, tint: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = text, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}
