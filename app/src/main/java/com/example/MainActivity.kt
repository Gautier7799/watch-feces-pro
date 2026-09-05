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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.InstallMobile
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.WatchFaceImageDial
import com.example.components.WatchFaceMinimalDigital
import com.example.model.WatchColorTheme
import com.example.model.WatchComplicationData
import com.example.model.WatchFaceType
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
    var selectedFaceType by remember { mutableStateOf(WatchFaceType.CUSTOM_PHOTO) }
    var isAodMode by remember { mutableStateOf(false) }
    var selectedTheme by remember { mutableStateOf(WatchColorTheme.CORAL_PEACH) }
    var customImageUri by remember { mutableStateOf<Uri?>(null) }
    var showHardwareBezel by remember { mutableStateOf(true) }
    var showInstallDialog by remember { mutableStateOf(false) }
    var isInstalling by remember { mutableStateOf(false) }
    var installSuccess by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            customImageUri = uri
            selectedFaceType = WatchFaceType.CUSTOM_PHOTO
        }
    }

    var hourAngle by remember { mutableStateOf(0f) }
    var minuteAngle by remember { mutableStateOf(0f) }
    var secondAngle by remember { mutableStateOf(0f) }
    var formattedHours by remember { mutableStateOf("12") }
    var formattedMinutes by remember { mutableStateOf("15") }
    var formattedSeconds by remember { mutableStateOf("33") }
    var formattedDate by remember { mutableStateOf("08-24") }

    val complications = remember {
        WatchComplicationData(
            batteryLevel = 84,
            heartRate = 75,
            steps = 4280,
            temperatureFahrenheit = 61,
            dayOfWeek = "MON",
            dateMonthDay = "08-24",
            weatherIconMoon = true
        )
    }

    LaunchedEffect(Unit) {
        val dateFormat = SimpleDateFormat("MM-dd", Locale.US)
        val hourFormat = SimpleDateFormat("hh", Locale.US)
        val minFormat = SimpleDateFormat("mm", Locale.US)
        val secFormat = SimpleDateFormat("ss", Locale.US)

        while (true) {
            val cal = Calendar.getInstance()
            val now = cal.time
            val hour = cal.get(Calendar.HOUR)
            val minute = cal.get(Calendar.MINUTE)
            val second = cal.get(Calendar.SECOND)
            val millis = cal.get(Calendar.MILLISECOND)

            secondAngle = (second + millis / 1000f) * 6f
            minuteAngle = (minute + second / 60f) * 6f
            hourAngle = (hour + minute / 60f) * 30f

            formattedDate = dateFormat.format(now)
            formattedHours = hourFormat.format(now)
            formattedMinutes = minFormat.format(now)
            formattedSeconds = secFormat.format(now)

            delay(200)
        }
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Pixel Watch Pro",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { }, modifier = Modifier.testTag("menu_button")) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showHardwareBezel = !showHardwareBezel },
                        modifier = Modifier.testTag("toggle_bezel_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Watch,
                            contentDescription = "Toggle Bezel",
                            tint = if (showHardwareBezel) selectedTheme.accent else Color.Gray
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF101412),
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF101412)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.testTag("import_image_top_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Importer image",
                        tint = selectedTheme.accent
                    )
                }

                Row(
                    modifier = Modifier
                        .background(Color(0xFF1A221D), RoundedCornerShape(24.dp))
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StyleToggleButton(
                        text = "رقمي Pixel",
                        isSelected = selectedFaceType == WatchFaceType.MINIMAL_DIGITAL,
                        selectedColor = selectedTheme.accent,
                        onClick = { selectedFaceType = WatchFaceType.MINIMAL_DIGITAL }
                    )
                    StyleToggleButton(
                        text = "صورة وعقارب",
                        isSelected = selectedFaceType == WatchFaceType.CUSTOM_PHOTO,
                        selectedColor = selectedTheme.accent,
                        onClick = { selectedFaceType = WatchFaceType.CUSTOM_PHOTO }
                    )
                }

                IconButton(
                    onClick = { showInstallDialog = true },
                    modifier = Modifier.testTag("save_button")
                ) {
                    Icon(Icons.Default.Save, contentDescription = "Enregistrer", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // إطار ساعة Pixel Watch المدمج
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(290.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (showHardwareBezel) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val r = size.width / 2f
                            val center = Offset(r, r)
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0xFF2C3530), Color(0xFF0D120F), Color.Black),
                                    center = center,
                                    radius = r
                                ),
                                radius = r
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize(if (showHardwareBezel) 0.88f else 1f)
                            .clip(CircleShape)
                    ) {
                        when (selectedFaceType) {
                            WatchFaceType.MINIMAL_DIGITAL -> {
                                WatchFaceMinimalDigital(
                                    formattedHours = formattedHours,
                                    formattedMinutes = formattedMinutes,
                                    formattedSeconds = formattedSeconds,
                                    formattedDate = formattedDate,
                                    theme = selectedTheme,
                                    complications = complications,
                                    isAod = isAodMode,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            else -> {
                                WatchFaceImageDial(
                                    hourAngle = hourAngle,
                                    minuteAngle = minuteAngle,
                                    secondAngle = secondAngle,
                                    imageUri = customImageUri,
                                    theme = selectedTheme,
                                    complications = complications,
                                    isAod = isAodMode,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFF1A221D), RoundedCornerShape(24.dp))
                        .padding(4.dp)
                ) {
                    StateToggleButton(
                        text = "نشط (Active)",
                        icon = Icons.Default.Visibility,
                        isSelected = !isAodMode,
                        selectedColor = selectedTheme.accent,
                        modifier = Modifier.weight(1f),
                        onClick = { isAodMode = false }
                    )
                    StateToggleButton(
                        text = "وضع السكون (AOD)",
                        icon = Icons.Default.Schedule,
                        isSelected = isAodMode,
                        selectedColor = selectedTheme.accent,
                        modifier = Modifier.weight(1f),
                        onClick = { isAodMode = true }
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                IconButton(
                    onClick = {
                        isAodMode = false
                        selectedTheme = WatchColorTheme.CORAL_PEACH
                    },
                    modifier = Modifier
                        .background(Color(0xFF1A221D), CircleShape)
                        .testTag("refresh_button")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Réinitialiser", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            AnimatedVisibility(
                visible = selectedFaceType == WatchFaceType.CUSTOM_PHOTO,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Button(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = selectedTheme.primary,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .testTag("pick_photo_button")
                ) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (customImageUri != null) "تغيير صورة واجهة الساعة" else "اختيار صورة من معرض هاتفك",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A261D)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "ألوان السمات (Theme Colors)",
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

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { showInstallDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = selectedTheme.accent,
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
                            text = "تثبيت الواجهة على الساعة",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showInstallDialog) {
        AlertDialog(
            onDismissRequest = { showInstallDialog = false },
            icon = {
                Icon(
                    imageVector = if (installSuccess) Icons.Default.CheckCircle else Icons.Default.Watch,
                    contentDescription = null,
                    tint = selectedTheme.accent,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = if (installSuccess) "الواجهة جاهزة بنجاح!" else "تثبيت الواجهة",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "النوع: ${selectedFaceType.titleAr}", color = Color.White, fontWeight = FontWeight.SemiBold)
                    Text(text = "البروتوكول: Wear OS WFF 5.1", color = Color.LightGray, fontSize = 13.sp)
                    Text(text = "السمة: ${selectedTheme.titleAr}", color = selectedTheme.accent, fontSize = 13.sp)
                    if (isInstalling) {
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = selectedTheme.accent)
                        Text(text = "جاري تجميع حزمة الواجهة وإرسالها للساعة...", fontSize = 12.sp, color = Color.Gray)
                    } else if (installSuccess) {
                        Text(
                            text = "تم نقل وضبط الواجهة بنجاح على ساعة Pixel Watch!",
                            color = Color(0xFF81C784),
                            fontSize = 13.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (!isInstalling && !installSuccess) {
                            isInstalling = true
                        } else {
                            showInstallDialog = false
                            isInstalling = false
                            installSuccess = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = selectedTheme.accent, contentColor = Color.Black)
                ) {
                    Text(if (isInstalling) "جاري النقل..." else if (installSuccess) "تم الإغلاق" else "بدء التثبيت")
                }
            },
            dismissButton = {
                TextButton(onClick = { showInstallDialog = false }) {
                    Text("إلغاء", color = Color.Gray)
                }
            },
            containerColor = Color(0xFF1E2820)
        )

        if (isInstalling) {
            LaunchedEffect(Unit) {
                delay(1200)
                isInstalling = false
                installSuccess = true
            }
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
        color = if (isSelected) selectedColor else Color.Transparent,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp)
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.Black else Color.White,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
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
        color = if (isSelected) selectedColor else Color.Transparent,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.Black else Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                color = if (isSelected) Color.Black else Color.White,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 13.sp
            )
        }
    }
}
