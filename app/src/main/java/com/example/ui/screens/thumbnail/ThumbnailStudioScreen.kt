package com.example.ui.screens.thumbnail

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun ThumbnailStudioScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var titleText by remember { mutableStateOf("UNREAL EDIT") }
    var badgeText by remember { mutableStateOf("4K 60FPS") }
    var selectedAspect by remember { mutableStateOf("16:9") } // 16:9 or 9:16
    var selectedTheme by remember { mutableStateOf("Royal Gold") }
    var isExported by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            RoyalTopBar(
                title = "THUMBNAIL STUDIO",
                subtitle = "High-CTR Graphic Cover Designer",
                onBack = onBack,
                actions = {
                    Button(
                        onClick = { isExported = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EgoGold, contentColor = EgoBgPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("thumbnail_export_button")
                    ) {
                        Text("SAVE PNG", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            )
        },
        containerColor = EgoBgPrimary,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (isExported) {
                Surface(
                    color = EgoSuccess.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, EgoSuccess),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Text(
                        "Saved High-Resolution Cover Thumbnail to Gallery!",
                        color = EgoSuccess,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Thumbnail Canvas Preview Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(if (selectedAspect == "16:9") 16f / 9f else 9f / 16f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        when (selectedTheme) {
                            "Royal Gold" -> Brush.verticalGradient(listOf(Color(0xFF1F1A0A), Color(0xFF0D0B05), Color(0xFF000000)))
                            "Cyber Neon" -> Brush.verticalGradient(listOf(Color(0xFF20052C), Color(0xFF090014), Color(0xFF020005)))
                            else -> Brush.verticalGradient(listOf(Color(0xFF151821), Color(0xFF0A0C10)))
                        }
                    )
                    .border(2.dp, EgoGoldBright, RoundedCornerShape(12.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Badge Sticker
                    if (badgeText.isNotBlank()) {
                        Surface(
                            color = EgoGoldBright,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Text(
                                text = badgeText.uppercase(),
                                color = EgoBgPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Bold 3D Header
                    Text(
                        text = titleText.uppercase(),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            color = if (selectedTheme == "Cyber Neon") Color(0xFF00FFFF) else EgoGoldBright
                        )
                    )
                    Text(
                        text = "BY EGO EDIT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 3.sp,
                            color = EgoTextMuted
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Editor Controls
            Surface(
                color = EgoBgPanel,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, EgoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("COVER TEXT & HOOK", fontWeight = FontWeight.Bold, color = EgoGoldBright, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = titleText,
                        onValueChange = { titleText = it },
                        label = { Text("Main Headline") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EgoGoldBright,
                            unfocusedBorderColor = EgoBorder,
                            focusedTextColor = EgoTextPrimary,
                            unfocusedTextColor = EgoTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = badgeText,
                        onValueChange = { badgeText = it },
                        label = { Text("Badge Text (e.g. 4K 60FPS, INSANE, 100%)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EgoGoldBright,
                            unfocusedBorderColor = EgoBorder,
                            focusedTextColor = EgoTextPrimary,
                            unfocusedTextColor = EgoTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Canvas Format", style = MaterialTheme.typography.labelSmall, color = EgoTextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("16:9", "9:16").forEach { asp ->
                            FilterChip(
                                selected = selectedAspect == asp,
                                onClick = { selectedAspect = asp },
                                label = { Text(if (asp == "16:9") "YouTube 16:9" else "Shorts 9:16", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EgoGold,
                                    selectedLabelColor = EgoBgPrimary,
                                    containerColor = EgoBgElevated,
                                    labelColor = EgoTextPrimary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Color Aesthetic", style = MaterialTheme.typography.labelSmall, color = EgoTextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Royal Gold", "Cyber Neon", "Minimal Dark").forEach { thm ->
                            FilterChip(
                                selected = selectedTheme == thm,
                                onClick = { selectedTheme = thm },
                                label = { Text(thm, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EgoGold,
                                    selectedLabelColor = EgoBgPrimary,
                                    containerColor = EgoBgElevated,
                                    labelColor = EgoTextPrimary
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
