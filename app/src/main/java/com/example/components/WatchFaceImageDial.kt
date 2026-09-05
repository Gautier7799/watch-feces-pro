package com.example.components

import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.example.model.WatchColorTheme
import com.example.model.WatchComplicationData
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WatchFaceImageDial(
    hourAngle: Float,
    minuteAngle: Float,
    secondAngle: Float,
    imageResId: Int? = null,
    imageUri: Uri? = null,
    theme: WatchColorTheme,
    complications: WatchComplicationData,
    isAod: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        if (imageUri != null) {
            AsyncImage(
                model = imageUri,
                contentDescription = "Watch Face Custom Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        } else if (imageResId != null) {
            androidx.compose.foundation.Image(
                painter = painterResource(id = imageResId),
                contentDescription = "Watch Face Dial Asset",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        }

        if (isAod) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.65f))
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.45f)
                            )
                        )
                    )
            )
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.width / 2f

            for (i in 0 until 12) {
                val angleDeg = i * 30f
                val angleRad = Math.toRadians(angleDeg.toDouble())
                val isMajor = i % 3 == 0
                val innerR = if (isMajor) radius * 0.84f else radius * 0.88f
                val outerR = radius * 0.94f

                val start = Offset(
                    center.x + (innerR * sin(angleRad)).toFloat(),
                    center.y - (innerR * cos(angleRad)).toFloat()
                )
                val end = Offset(
                    center.x + (outerR * sin(angleRad)).toFloat(),
                    center.y - (outerR * cos(angleRad)).toFloat()
                )

                drawLine(
                    color = if (isMajor) theme.primary.copy(alpha = if (isAod) 0.6f else 0.9f)
                    else Color.White.copy(alpha = if (isAod) 0.3f else 0.5f),
                    start = start,
                    end = end,
                    strokeWidth = if (isMajor) 4.5f else 2.5f,
                    cap = StrokeCap.Round
                )
            }

            val handStroke = 2.5f
            val handCorner = 8f

            // عقرب الساعات
            rotate(hourAngle, pivot = center) {
                val hourWidth = size.width * 0.055f
                val hourLength = size.width * 0.28f
                val hourPath = Path().apply {
                    addRoundRect(
                        RoundRect(
                            left = center.x - hourWidth / 2f,
                            top = center.y - hourLength,
                            right = center.x + hourWidth / 2f,
                            bottom = center.y + hourWidth * 0.8f,
                            cornerRadius = CornerRadius(handCorner, handCorner)
                        )
                    )
                }

                drawPath(
                    path = hourPath,
                    color = if (isAod) Color.Black.copy(alpha = 0.7f) else Color.White,
                    style = Fill
                )
                drawPath(
                    path = hourPath,
                    color = if (isAod) theme.primary else Color.White,
                    style = Stroke(
                        width = if (isAod) 3.5f else handStroke,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }

            // عقرب الدقائق
            rotate(minuteAngle, pivot = center) {
                val minWidth = size.width * 0.045f
                val minLength = size.width * 0.40f
                val minPath = Path().apply {
                    addRoundRect(
                        RoundRect(
                            left = center.x - minWidth / 2f,
                            top = center.y - minLength,
                            right = center.x + minWidth / 2f,
                            bottom = center.y + minWidth * 0.8f,
                            cornerRadius = CornerRadius(handCorner, handCorner)
                        )
                    )
                }

                drawPath(
                    path = minPath,
                    color = if (isAod) Color.Black.copy(alpha = 0.7f) else Color.White,
                    style = Fill
                )
                drawPath(
                    path = minPath,
                    color = if (isAod) theme.primary else Color.White,
                    style = Stroke(
                        width = if (isAod) 3.5f else handStroke,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }

            // المسمار المركزي
            drawCircle(color = Color.Black, radius = size.width * 0.032f, center = center)
            drawCircle(
                color = if (isAod) theme.primary else Color.White,
                radius = size.width * 0.024f,
                center = center,
                style = Stroke(width = 2.5f)
            )

            // عقرب الثواني (في الوضع النشط فقط)
            if (!isAod) {
                rotate(secondAngle, pivot = center) {
                    val secNeedleLength = size.width * 0.43f
                    val secTailLength = size.width * 0.10f
                    val secColor = theme.accent

                    drawLine(
                        color = secColor,
                        start = Offset(center.x, center.y + secTailLength),
                        end = Offset(center.x, center.y - secNeedleLength),
                        strokeWidth = 3f,
                        cap = StrokeCap.Round
                    )
                    drawCircle(
                        color = secColor,
                        radius = size.width * 0.028f,
                        center = center,
                        style = Stroke(width = 3f)
                    )
                    drawCircle(color = Color.Black, radius = size.width * 0.012f, center = center, style = Fill)
                }
            }
        }
    }
}
