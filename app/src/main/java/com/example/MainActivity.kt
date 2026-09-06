package com.example

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.WatchFaceImageDial
import com.example.components.WatchFaceMinimalDigital
import com.example.components.WatchFaceEverydayAnalog
import com.example.components.WatchFaceChronograph
import com.example.components.WatchFaceKineticSphere
import com.example.model.WatchColorTheme
import com.example.model.WatchFaceType
import com.example.model.WatchComplicationData
import kotlinx.coroutines.delay
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalogMasterWatchFaceApp() {
    var currentTimeMs by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var selectedFaceType by remember { mutableStateOf(WatchFaceType.EVERYDAY_ANALOG) }
    var selectedTheme by remember { mutableStateOf(WatchColorTheme.CORAL_PEACH) }
    var isAmbientMode by remember { mutableStateOf(false) }
    var customImageUri by remember { mutableStateOf<Uri?>(null) }
    
    var showInstallDialog by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> 
            if (uri != null) {
                customImageUri = uri
                selectedFaceType = WatchFaceType.CUSTOM_PHOTO
            }
        }
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

    val formattedHours = String.format("%02d", hours)
    val formattedMinutes = String.format("%02d", minutes)
    val formattedSeconds = String.format("%02d", seconds)

    val complications = WatchComplicationData()

    val hourAngle = ((hours % 12 + minutes / 60f) * 30f)
    val minuteAngle = ((minutes + seconds / 60f) * 6f)
    val secondAngle = (seconds * 6f)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Constructeur",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color.White
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { }, modifier = Modifier.padding(start = 8.dp).background(Color(0xFF161B18), CircleShape).size(42.dp)) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { }, modifier = Modifier.padding(end = 8.dp).background(Color(0xFF161B18), CircleShape).size(42.dp)) {
                        Icon(Icons.Default.Save, contentDescription = "Save", tint = selectedTheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showInstallDialog = true },
                containerColor = selectedTheme.primary,
                contentColor = Color.Black,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Download, contentDescription = "Installer")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Installer",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        },
        containerColor = Color(0xFF0C100D)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Watch Face Preview Canvas
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
                    .border(2.dp, Color(0xFF1E2820), CircleShape)
                    .testTag("watch_face_canvas"),
                contentAlignment = Alignment.Center
            ) {
                when (selectedFaceType) {
                    WatchFaceType.MINIMAL_DIGITAL -> {
                        WatchFaceMinimalDigital(
                            formattedHours = formattedHours,
                            formattedMinutes = formattedMinutes,
                            formattedSeconds = formattedSeconds,
                            formattedDate = complications.dateMonthDay,
                            theme = selectedTheme,
                            complications = complications,
                            isAod = isAmbientMode,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    WatchFaceType.EVERYDAY_ANALOG -> {
                        WatchFaceEverydayAnalog(
                            hourAngle = hourAngle,
                            minuteAngle = minuteAngle,
                            secondAngle = secondAngle,
                            theme = selectedTheme,
                            complications = complications,
                            formattedDigitalTime = "$formattedHours:$formattedMinutes",
                            formattedDate = complications.dateMonthDay,
                            isAod = isAmbientMode,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    WatchFaceType.CLASSIC_CHRONOGRAPH -> {
                        WatchFaceChronograph(
                            hourAngle = hourAngle,
                            minuteAngle = minuteAngle,
                            secondAngle = secondAngle,
                            chronoSecondAngle = secondAngle * 1.5f,
                            chronoMinuteAngle = minuteAngle * 2f,
                            chronoHourAngle = hourAngle * 0.5f,
                            isStopwatchRunning = true,
                            theme = selectedTheme,
                            complications = complications,
                            isAod = isAmbientMode,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    WatchFaceType.KINETIC_SPHERE -> {
                        WatchFaceKineticSphere(
                            hourAngle = hourAngle,
                            minuteAngle = minuteAngle,
                            secondAngle = secondAngle,
                            theme = selectedTheme,
                            complications = complications,
                            isAod = isAmbientMode,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    else -> {
                        WatchFaceImageDial(
                            hourAngle = hourAngle,
                            minuteAngle = minuteAngle,
                            secondAngle = secondAngle,
                            theme = selectedTheme,
                            complications = complications,
                            imageUri = customImageUri,
                            isAod = isAmbientMode,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Actif / AOD / Refresh Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StateToggleButton(
                        text = "Actif",
                        icon = Icons.Default.WbSunny,
                        isSelected = !isAmbientMode,
                        selectedColor = selectedTheme.primary,
                        modifier = Modifier.weight(1f)
                    ) {
                        isAmbientMode = false
                    }
                    StateToggleButton(
                        text = "AOD",
                        icon = Icons.Default.NightsStay,
                        isSelected = isAmbientMode,
                        selectedColor = selectedTheme.primary,
                        modifier = Modifier.weight(1f)
                    ) {
                        isAmbientMode = true
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(
                    onClick = { 
                        isAmbientMode = false
                        selectedTheme = WatchColorTheme.CORAL_PEACH
                        selectedFaceType = WatchFaceType.EVERYDAY_ANALOG
                    },
                    modifier = Modifier.background(Color(0xFF161B18), CircleShape).size(48.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Card 2. Types d'aiguilles
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111713)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "2. Types d'aiguilles (أنواع العقارب)",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val types = listOf(
                            WatchFaceType.EVERYDAY_ANALOG to "Classic",
                            WatchFaceType.DIVER_SPORT to "Sport",
                            WatchFaceType.KINETIC_SPHERE to "Pixel_pill",
                            WatchFaceType.MINIMAL_DIGITAL to "Minimal"
                        )
                        types.forEach { (type, label) ->
                            StyleToggleButton(
                                text = label,
                                isSelected = selectedFaceType == type,
                                selectedColor = selectedTheme.primary
                            ) {
                                selectedFaceType = type
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card 1. Couleurs des aiguilles
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111713)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. Couleurs des aiguilles (ألوان العقارب)",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val themes = listOf(
                            WatchColorTheme.CORAL_PEACH to "Blanc & Corail",
                            WatchColorTheme.LEMONGRASS_MINT to "Craie & Menthe",
                            WatchColorTheme.BAY_BLUE to "Craie & Bleu",
                            WatchColorTheme.HAZEL_GOLD to "Or & Noir"
                        )
                        themes.forEach { (themeOption, label) ->
                            ThemeColorButton(
                                text = label,
                                isSelected = selectedTheme == themeOption,
                                theme = themeOption
                            ) {
                                selectedTheme = themeOption
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // Card 3. Formes Material You
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111713)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "3. Formes Material You (الأشكال الأصلية للمؤشرات)",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "يمكنك تغيير شكل المؤشرات المدمجة فوراً على الساعة بالشكل الهندسي المختار.",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(48.dp)) // Space for FAB
                }
            }
            
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun StyleToggleButton(
    text: String,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) selectedColor else Color(0xFF1A241D),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.Black else Color.White,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
    }
}

@Composable
fun ThemeColorButton(
    text: String,
    isSelected: Boolean,
    theme: WatchColorTheme,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) theme.primary.copy(alpha = 0.2f) else Color(0xFF1A241D),
        shape = RoundedCornerShape(24.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, theme.primary) else null,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(Color.White))
            Spacer(modifier = Modifier.width(4.dp))
            Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(theme.primary))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = if (isSelected) Color.White else Color.LightGray,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun StateToggleButton(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    selectedColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) selectedColor else Color(0xFF1A241D),
        shape = RoundedCornerShape(24.dp),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.Black else Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = if (isSelected) Color.Black else Color.White,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 14.sp
            )
        }
    }
}
