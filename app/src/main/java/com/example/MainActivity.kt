package com.example

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Color(0xFF98D7A5),
                    secondary = Color(0xFF2E694E),
                    surface = Color(0xFF131814),
                    background = Color(0xFF0C100D)
                )
            ) {
                Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF0C100D)) {
                    AnalogMasterWatchFaceApp()
                }
            }
        }
    }
}

// -------------------------------------------------------------
// النماذج والبيانات الأساسية (Enums & Layouts)
// -------------------------------------------------------------
enum class WatchFaceLayout(val title: String) {
    TRACK_CONCENTRIC("Pixel Track (صورة 1)"),
    TRACK_CHRONO("Pixel Chrono (صورة 2)"),
    ANALOG_PRO("Material You Pro")
}

enum class WidgetType(val title: String, val defaultText: String) {
    BATTERY("Batterie", "85%"),
    STEPS("Pas", "8,532"),
    HEART_RATE("Fréquence", "72 bpm"),
    WEATHER("Météo", "24°C"),
    CALENDAR("Événement", "14:00")
}

enum class HandStyle(val label: String) {
    CLASSIC("Classique (عادية)"),
    SPORT("Sport (رياضية)"),
    PIXEL_PILL("Pixel Pill (بكسل)"),
    MINIMAL("Minimal (بسيطة)")
}

enum class DialIndexStyle(val title: String) {
    NONE("Sans (بدون علامات)"),
    CLASSIC("Classique (خطوط بيضاء)"),
    RED_DOTS("Points rouges (نقاط حمراء)"),
    NUMBERS("Chiffres 3, 6, 9, 12"),
    MINIMAL_4("Minimal 4 (أخضر زمردي)")
}

// -------------------------------------------------------------
// خوارزميات التنسيق اللوني التلقائي مع الخلفية (Color Harmonization)
// -------------------------------------------------------------
fun getHarmoniousColorsForBackground(bgColor: Color): Pair<Color, Color> {
    return when (bgColor) {
        Color(0xFF8D503C) -> Pair(Color(0xFFFFDBCF), Color(0xFFFF8A65)) // Terracotta
        Color(0xFF786D5F) -> Pair(Color(0xFFF2EBE1), Color(0xFFFFCC80)) // Taupe
        Color(0xFF5D737E) -> Pair(Color(0xFFD6EAF8), Color(0xFF80D8FF)) // Bleu Ardoise
        Color(0xFF7E7A4A) -> Pair(Color(0xFFF9FBE7), Color(0xFFC6FF00)) // Olive
        Color(0xFF98D7A5) -> Pair(Color(0xFF0B331A), Color(0xFF2E7D32)) // Menthe
        Color(0xFF282524), Color(0xFF0F1410), Color(0xFF141C16) -> Pair(Color(0xFFEDE0D4), Color(0xFFFF7043)) // Dark
        else -> Pair(Color.White, Color(0xFFFF5722))
    }
}

fun extractHarmoniousColorsFromUri(context: Context, uri: Uri?): Pair<Color, Color> {
    if (uri == null) return Pair(Color.White, Color(0xFFFF5722))
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        if (bitmap != null) {
            val pixel = bitmap.getPixel(bitmap.width / 2, bitmap.height / 2)
            val r = android.graphics.Color.red(pixel) / 255f
            val g = android.graphics.Color.green(pixel) / 255f
            val b = android.graphics.Color.blue(pixel) / 255f
            val brightness = 0.299f * r + 0.587f * g + 0.114f * b
            if (brightness > 0.5f) {
                Pair(Color(0xFF1B1B1B), Color(0xFFD84315))
            } else {
                Pair(Color(0xFFF5F5F5), Color(0xFFFF7043))
            }
        } else {
            Pair(Color.White, Color(0xFFFF5722))
        }
    } catch (e: Exception) {
        Pair(Color.White, Color(0xFFFF5722))
    }
}
