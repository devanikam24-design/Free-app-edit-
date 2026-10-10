package com.example.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    var progress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        // Smooth initial startup progress
        while (progress < 1.0f) {
            delay(25)
            progress += 0.02f
        }
        delay(300)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EgoBgPrimary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // Royal Logo Card
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(scale)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(EgoGoldDark, EgoGoldBright, EgoGoldDark)
                        )
                    )
                    .padding(3.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(EgoBgPrimary),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_ego_logo),
                    contentDescription = "EGO EDIT Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "EGO EDIT",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp,
                    color = EgoGoldBright
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "ULTIMATE PROFESSIONAL VIDEO EDITOR",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp,
                    color = EgoGoldLight
                )
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Progress loader
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .clip(RoundedCornerShape(4.dp))
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = EgoGoldBright,
                    trackColor = EgoBgElevated
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Initializing 650+ Feature Engine...",
                style = MaterialTheme.typography.labelSmall,
                color = EgoTextMuted,
                fontSize = 11.sp
            )
        }
    }
}
