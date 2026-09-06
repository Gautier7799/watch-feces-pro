package com.example

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.WatchFaceImageDial
import com.example.components.WatchFaceMinimalDigital
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

@Composable
fun AnalogMasterWatchFaceApp() {
    var currentTimeMs by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var selectedFaceType by remember { mutableStateOf(WatchFaceType.MINIMAL_DIGITAL) }
    var selectedTheme by remember { mutableStateOf(WatchColorTheme.LEMONGRASS_MINT) }
    var isAmbientMode by remember { mutableStateOf(false) }
    var customImageUri by remember { mutableStateOf<Uri?>(null) }
    
    var showInstallDialog by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> customImageUri = uri }
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Menu */ }, modifier = Modifier.background(Color(0xFF1A241D), CircleShape)) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
            }
            Text(
                text = "Constructeur",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black
            )
            IconButton(onClick = { /* Save */ }, modifier = Modifier.background(Color(0xFF1A241D), CircleShape)) {
                Icon(Icons.Default.Save, contentDescription = "Save", tint = selectedTheme.primary)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Watch Face Preview Canvas
        Box(
            modifier = Modifier
                .size(280.dp)
                .clip(CircleShape)
                .background(Color.Black)
                .border(6.dp, Color(0xFF1E2820), CircleShape)
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
                        isAod = isAmbientMode
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
                        isAod = isAmbientMode
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Controls Section
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(32.dp),
            color = Color(0xFF111713)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A241D), contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Photo active", fontSize = 12.sp)
                    }
                    if (customImageUri != null) {
                        IconButton(onClick = { customImageUri = null }, modifier = Modifier.background(Color(0xFF2D1E1E), CircleShape)) {
                            Icon(Icons.Default.Close, contentDescription = "Remove photo", tint = Color(0xFFE57373))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
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
                    Spacer(modifier = Modifier.width(8.dp))
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

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "أنواع العقارب",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
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

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "ألوان السمات",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WatchColorTheme.values().forEach { themeOption ->
                        val isChosen = selectedTheme == themeOption
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(themeOption.primary)
                                .border(
                                    width = if (isChosen) 3.dp else 1.dp,
                                    color = if (isChosen) Color.White else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedTheme = themeOption }
                                .testTag("color_theme_${themeOption.name}"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isChosen) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { showInstallDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = selectedTheme.primary,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("installer_button")
                ) {
                    Icon(imageVector = Icons.Default.InstallMobile, contentDescription = "Install")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Installer",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
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
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        )
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
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 14.sp
            )
        }
    }
}
