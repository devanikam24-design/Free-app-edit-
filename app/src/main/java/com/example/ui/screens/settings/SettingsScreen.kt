package com.example.ui.screens.settings

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var hardwareAccel by remember { mutableStateOf(true) }
    var proxyEditing by remember { mutableStateOf(true) }
    var autosaveInterval by remember { mutableStateOf("2 Minutes") }
    var colorSpace by remember { mutableStateOf("REC.709 Cinema") }
    var cacheClearedMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            RoyalTopBar(
                title = "SETTINGS & PREFERENCES",
                subtitle = "Hardware, Color Science & Recovery",
                onBack = onBack
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
            if (cacheClearedMessage != null) {
                Surface(
                    color = EgoSuccess.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, EgoSuccess),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Text(
                        text = cacheClearedMessage!!,
                        color = EgoSuccess,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Performance & Acceleration
            Surface(
                color = EgoBgPanel,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, EgoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("PERFORMANCE & GPU ACCELERATION", fontWeight = FontWeight.Bold, color = EgoGoldBright, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Hardware Encoding & Render Pipeline", color = EgoTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text("Leverage Qualcomm/Mali GPU hardware acceleration", color = EgoTextMuted, fontSize = 11.sp)
                        }
                        Switch(
                            checked = hardwareAccel,
                            onCheckedChange = { hardwareAccel = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = EgoGoldBright, checkedTrackColor = EgoGoldDark)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Automatic Proxy Generation (720p)", color = EgoTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text("Generates lightweight proxies for lag-free 4K/8K timeline scrubbing", color = EgoTextMuted, fontSize = 11.sp)
                        }
                        Switch(
                            checked = proxyEditing,
                            onCheckedChange = { proxyEditing = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = EgoGoldBright, checkedTrackColor = EgoGoldDark)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Color Science & Autosave
            Surface(
                color = EgoBgPanel,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, EgoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("COLOR SCIENCE & AUTOSAVE VAULT", fontWeight = FontWeight.Bold, color = EgoGoldBright, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Autosave Frequency", color = EgoTextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("1 Minute", "2 Minutes", "5 Minutes").forEach { ivl ->
                            FilterChip(
                                selected = autosaveInterval == ivl,
                                onClick = { autosaveInterval = ivl },
                                label = { Text(ivl, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EgoGold,
                                    selectedLabelColor = EgoBgPrimary,
                                    containerColor = EgoBgElevated,
                                    labelColor = EgoTextPrimary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Timeline Color Managed Working Space", color = EgoTextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("REC.709 Cinema", "DCI-P3 Wide", "HDR HLG").forEach { cs ->
                            FilterChip(
                                selected = colorSpace == cs,
                                onClick = { colorSpace = cs },
                                label = { Text(cs, fontSize = 11.sp) },
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

            Spacer(modifier = Modifier.height(16.dp))

            // Storage & Maintenance
            Surface(
                color = EgoBgPanel,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, EgoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("CACHE & STORAGE RECOVERY", fontWeight = FontWeight.Bold, color = EgoGoldBright, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Temporary render cache: 124.6 MB", color = EgoTextSecondary, fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(12.dp))
                    GoldOutlinedButton(
                        text = "Purge Render Cache & Temporary Bitmaps",
                        icon = Icons.Default.CleaningServices,
                        onClick = {
                            cacheClearedMessage = "Cleared 124.6 MB of temporary render cache!"
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // App Identity & Build info
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("EGO EDIT MASTER", fontWeight = FontWeight.Bold, color = EgoGold, letterSpacing = 2.sp, fontSize = 12.sp)
                Text("Version 1.0.0 • 650+ Capabilities Engine", color = EgoTextMuted, fontSize = 11.sp)
                Text("Crafted for professional video creators", color = EgoTextMuted, fontSize = 10.sp)
            }
        }
    }
}
