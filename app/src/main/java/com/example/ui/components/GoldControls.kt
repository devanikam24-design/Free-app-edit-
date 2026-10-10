package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.*

@Composable
fun GoldButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    testTag: String = "gold_button"
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = EgoGold,
            contentColor = EgoBgPrimary,
            disabledContainerColor = EgoBgElevated,
            disabledContentColor = EgoTextMuted
        ),
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
        modifier = modifier
            .minimumInteractiveComponentSize()
            .testTag(testTag)
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun GoldOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    testTag: String = "gold_outlined_button"
) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = EgoGoldBright
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = Brush.linearGradient(listOf(EgoGoldDark, EgoGoldBright))
        ),
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        modifier = modifier
            .minimumInteractiveComponentSize()
            .testTag(testTag)
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text = text, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}

@Composable
fun GoldSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    label: String,
    displayValue: String,
    modifier: Modifier = Modifier,
    testTag: String = "gold_slider"
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = EgoTextSecondary)
            Text(text = displayValue, style = MaterialTheme.typography.labelSmall, color = EgoGoldBright, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = EgoGoldBright,
                activeTrackColor = EgoGold,
                inactiveTrackColor = EgoBorder
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}

@Composable
fun ColorWheel(
    title: String,
    offsetR: Float,
    offsetB: Float,
    onColorChanged: (r: Float, g: Float, b: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var thumbPos by remember(offsetR, offsetB) {
        mutableStateOf(Offset(offsetR, offsetB))
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(EgoBgElevated)
            .border(1.dp, EgoBorder, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = EgoGoldLight)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(EgoBgPrimary)
                .border(1.dp, EgoBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val radius = size.width / 2f
                            val newX = (thumbPos.x + dragAmount.x / radius).coerceIn(-0.85f, 0.85f)
                            val newY = (thumbPos.y + dragAmount.y / radius).coerceIn(-0.85f, 0.85f)
                            thumbPos = Offset(newX, newY)
                            // Map thumb to R, G, B offsets
                            val red = (newX * 0.5f).coerceIn(-0.5f, 0.5f)
                            val blue = (newY * 0.5f).coerceIn(-0.5f, 0.5f)
                            val green = (-(red + blue) / 2f).coerceIn(-0.5f, 0.5f)
                            onColorChanged(red, green, blue)
                        }
                    }
            ) {
                val radius = size.width / 2f
                val centerOffset = Offset(radius, radius)

                // Draw Hue spectrum circle ring
                val sweepColors = listOf(
                    Color.Red, Color.Yellow, Color.Green, Color.Cyan, Color.Blue, Color.Magenta, Color.Red
                )
                drawCircle(
                    brush = Brush.sweepGradient(sweepColors, center = centerOffset),
                    radius = radius * 0.9f,
                    center = centerOffset
                )
                // Desaturate center towards dark gray
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xDD15171E), Color.Transparent),
                        center = centerOffset,
                        radius = radius * 0.9f
                    ),
                    radius = radius * 0.9f,
                    center = centerOffset
                )

                // Draw crosshair axes
                drawLine(Color(0x55FFFFFF), Offset(radius, 0f), Offset(radius, size.height), strokeWidth = 1f)
                drawLine(Color(0x55FFFFFF), Offset(0f, radius), Offset(size.width, radius), strokeWidth = 1f)

                // Draw Thumb puck
                val puckPx = centerOffset.x + (thumbPos.x * radius * 0.85f)
                val puckPy = centerOffset.y + (thumbPos.y * radius * 0.85f)
                drawCircle(
                    color = EgoGoldBright,
                    radius = 7.dp.toPx(),
                    center = Offset(puckPx, puckPy)
                )
                drawCircle(
                    color = Color.Black,
                    radius = 4.dp.toPx(),
                    center = Offset(puckPx, puckPy)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "R: ${String.format("%.2f", offsetR)} | B: ${String.format("%.2f", offsetB)}",
            style = MaterialTheme.typography.labelSmall,
            color = EgoTextMuted
        )
    }
}
