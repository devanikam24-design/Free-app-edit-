package com.example.ui.screens.vfx

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.TimelineEngine
import com.example.engine.VideoPreviewCanvas
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun VfxMotionScreen(
    engine: TimelineEngine,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val project by engine.project.collectAsState()
    val currentTimeMs by engine.currentTimeMs.collectAsState()
    val selectedClip = engine.getSelectedClip() ?: project.tracks.firstOrNull()?.clips?.firstOrNull()

    var activeVfxTab by remember { mutableIntStateOf(0) } // 0: Chroma Key, 1: Keyframing, 2: Particles, 3: Camera Shake

    Scaffold(
        topBar = {
            RoyalTopBar(
                title = "VFX & MOTION GRAPHICS",
                subtitle = "Green Screen, Keyframes & Particles",
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
        ) {
            // Live Preview Monitor
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Black)
                    .padding(8.dp)
            ) {
                VideoPreviewCanvas(
                    project = project,
                    currentTimeMs = currentTimeMs,
                    showSafeGuides = false,
                    showGrid = true,
                    isSplitComparison = false,
                    previewResolution = "Full",
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Tabs
            ScrollableTabRow(
                selectedTabIndex = activeVfxTab,
                containerColor = EgoBgPanel,
                contentColor = EgoGoldBright,
                edgePadding = 12.dp,
                divider = { Divider(color = EgoBorder) }
            ) {
                listOf("Chroma Key", "Transform Keyframes", "Particle Emitter", "Camera Shake").forEachIndexed { idx, title ->
                    Tab(
                        selected = activeVfxTab == idx,
                        onClick = { activeVfxTab = idx },
                        text = {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (activeVfxTab == idx) FontWeight.Bold else FontWeight.Normal,
                                color = if (activeVfxTab == idx) EgoGoldBright else EgoTextSecondary
                            )
                        }
                    )
                }
            }

            // Controls Body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                if (selectedClip == null) {
                    Text("Select a clip on the timeline to configure VFX properties.", color = EgoTextMuted)
                } else {
                    when (activeVfxTab) {
                        0 -> { // Chroma Key (Green Screen)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Chroma Key Isolation", fontWeight = FontWeight.Bold, color = EgoTextPrimary)
                                    Text("Remove green/blue backdrop from video", color = EgoTextMuted, fontSize = 11.sp)
                                }
                                Switch(
                                    checked = selectedClip.chromaKeyActive,
                                    onCheckedChange = { active ->
                                        engine.updateSelectedClip { it.copy(chromaKeyActive = active) }
                                    },
                                    colors = SwitchDefaults.colors(checkedThumbColor = EgoGoldBright, checkedTrackColor = EgoGoldDark)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text("Key Backdrop Color", style = MaterialTheme.typography.bodySmall, color = EgoTextSecondary)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val colors = listOf("Green" to 0xFF00FF00, "Blue" to 0xFF0055FF, "Magenta" to 0xFFFF00FF)
                                colors.forEach { (name, hex) ->
                                    val isSelected = selectedClip.chromaKeyColorHex == hex
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { engine.updateSelectedClip { it.copy(chromaKeyColorHex = hex) } },
                                        label = { Text(name, fontSize = 11.sp) },
                                        leadingIcon = {
                                            Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(2.dp)).background(Color(hex)))
                                        },
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

                            GoldSlider(
                                value = selectedClip.chromaTolerance,
                                onValueChange = { tol ->
                                    engine.updateSelectedClip { it.copy(chromaTolerance = tol) }
                                },
                                valueRange = 0.1f..0.8f,
                                label = "Color Tolerance Threshold",
                                displayValue = "${(selectedClip.chromaTolerance * 100).toInt()}%"
                            )
                        }

                        1 -> { // Transform Keyframes
                            Text("ANIMATION KEYFRAMES (${selectedClip.keyframes.size} KEYFRAMES)", fontWeight = FontWeight.Bold, color = EgoGoldBright)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Animate scale, position, and rotation over time", color = EgoTextMuted, fontSize = 11.sp)

                            Spacer(modifier = Modifier.height(14.dp))

                            GoldButton(
                                text = "+ Add Keyframe at ${engine.formatTimecode(currentTimeMs)}",
                                icon = Icons.Default.Add,
                                onClick = {
                                    val newKf = Keyframe(
                                        timeMs = currentTimeMs,
                                        scale = selectedClip.transform.scaleX,
                                        rotation = selectedClip.transform.rotation,
                                        opacity = selectedClip.transform.opacity
                                    )
                                    engine.updateSelectedClip { it.copy(keyframes = it.keyframes + newKf) }
                                }
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            selectedClip.keyframes.forEachIndexed { i, kf ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(EgoBgElevated)
                                        .border(1.dp, EgoBorder, RoundedCornerShape(8.dp))
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Keyframe #${i + 1} @ ${engine.formatTimecode(kf.timeMs)}", color = EgoTextPrimary, fontSize = 12.sp)
                                    IconButton(
                                        onClick = {
                                            engine.updateSelectedClip { clip ->
                                                clip.copy(keyframes = clip.keyframes.filterIndexed { idx, _ -> idx != i })
                                            }
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Close, null, tint = EgoError, modifier = Modifier.size(16.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }

                        2 -> { // Particle Emitter
                            Text("PROCEDURAL PARTICLE GENERATOR", fontWeight = FontWeight.Bold, color = EgoGoldBright)
                            Spacer(modifier = Modifier.height(8.dp))
                            val particles = listOf(
                                Triple("gold_particles", "Royal Gold Dust Embers", "Floating 24K sparks and light rays"),
                                Triple("cinematic_drone", "None / Scenery", "Disable particle layer"),
                                Triple("cyber_city", "Cyber Neon Rain", "Vertical illuminated precipitation")
                            )
                            particles.forEach { (id, name, desc) ->
                                val isSelected = selectedClip.placeholderVisual == id
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) EgoBgElevated else EgoBgPanel)
                                        .border(1.dp, if (isSelected) EgoGoldBright else EgoBorder, RoundedCornerShape(8.dp))
                                        .clickable { engine.updateSelectedClip { it.copy(placeholderVisual = id) } }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(name, fontWeight = FontWeight.Bold, color = if (isSelected) EgoGoldBright else EgoTextPrimary, fontSize = 12.sp)
                                        Text(desc, color = EgoTextMuted, fontSize = 10.sp)
                                    }
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, null, tint = EgoGoldBright)
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }

                        3 -> { // Camera Shake
                            Text("CINEMATIC CAMERA SHAKE GENERATOR", fontWeight = FontWeight.Bold, color = EgoGoldBright)
                            Spacer(modifier = Modifier.height(10.dp))
                            val shakes = listOf("Subtle Handheld", "Action Sprint", "Explosion Rumble", "None")
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                shakes.forEach { s ->
                                    GoldOutlinedButton(
                                        text = s,
                                        onClick = {
                                            engine.updateSelectedClip {
                                                it.copy(filter = if (s == "None") VideoFilterType.NONE else VideoFilterType.CYBER_GLITCH)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
