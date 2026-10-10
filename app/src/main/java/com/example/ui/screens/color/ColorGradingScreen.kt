package com.example.ui.screens.color

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
fun ColorGradingScreen(
    engine: TimelineEngine,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val project by engine.project.collectAsState()
    val currentTimeMs by engine.currentTimeMs.collectAsState()
    val selectedClip = engine.getSelectedClip() ?: project.tracks.firstOrNull()?.clips?.firstOrNull()

    var activeGrade by remember(selectedClip) {
        mutableStateOf(selectedClip?.colorGrade ?: ColorGrade())
    }

    var selectedMode by remember { mutableIntStateOf(0) } // 0: Wheels, 1: Sliders, 2: LUTs

    fun applyColorGrade(newGrade: ColorGrade) {
        activeGrade = newGrade
        engine.updateSelectedClip { clip ->
            clip.copy(colorGrade = newGrade)
        }
    }

    Scaffold(
        topBar = {
            RoyalTopBar(
                title = "COLOR GRADING STUDIO",
                subtitle = "DaVinci 3-Way Wheels & 3D LUTs",
                onBack = onBack,
                actions = {
                    TextButton(onClick = { applyColorGrade(ColorGrade()) }) {
                        Text("RESET", color = EgoGoldBright, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
        ) {
            // Live Preview Monitor (Compact)
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
                    showGrid = false,
                    isSplitComparison = true, // Show Before / After split in Color Studio!
                    previewResolution = "Full",
                    modifier = Modifier.fillMaxSize()
                )
                // Split label
                Surface(
                    color = EgoBgPanel.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.align(Alignment.BottomStart).padding(6.dp)
                ) {
                    Text("RAW (Left) vs GRADED (Right)", color = EgoGoldBright, fontSize = 10.sp, modifier = Modifier.padding(4.dp))
                }
            }

            // Mode Selector (Color Wheels | Basic Sliders | 3D LUTs)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EgoBgPanel)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedMode == 0,
                    onClick = { selectedMode = 0 },
                    label = { Text("3-Way Wheels", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EgoGold,
                        selectedLabelColor = EgoBgPrimary,
                        containerColor = EgoBgElevated,
                        labelColor = EgoTextPrimary
                    )
                )
                FilterChip(
                    selected = selectedMode == 1,
                    onClick = { selectedMode = 1 },
                    label = { Text("Parametric Sliders", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EgoGold,
                        selectedLabelColor = EgoBgPrimary,
                        containerColor = EgoBgElevated,
                        labelColor = EgoTextPrimary
                    )
                )
                FilterChip(
                    selected = selectedMode == 2,
                    onClick = { selectedMode = 2 },
                    label = { Text("Cinematic LUTs", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EgoGold,
                        selectedLabelColor = EgoBgPrimary,
                        containerColor = EgoBgElevated,
                        labelColor = EgoTextPrimary
                    )
                )
            }

            Divider(color = EgoBorder)

            // Main Controls Body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                when (selectedMode) {
                    0 -> { // 3-Way Wheels (Lift, Gamma, Gain)
                        Text(
                            text = "3-WAY COLOR WHEELS (SHADOWS • MIDTONES • HIGHLIGHTS)",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = EgoGoldBright)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ColorWheel(
                                title = "Lift (Shadows)",
                                offsetR = activeGrade.liftR,
                                offsetB = activeGrade.liftB,
                                onColorChanged = { r, g, b ->
                                    applyColorGrade(activeGrade.copy(liftR = r, liftG = g, liftB = b))
                                },
                                modifier = Modifier.weight(1f)
                            )
                            ColorWheel(
                                title = "Gamma (Mids)",
                                offsetR = activeGrade.gammaR,
                                offsetB = activeGrade.gammaB,
                                onColorChanged = { r, g, b ->
                                    applyColorGrade(activeGrade.copy(gammaR = r, gammaG = g, gammaB = b))
                                },
                                modifier = Modifier.weight(1f)
                            )
                            ColorWheel(
                                title = "Gain (Highs)",
                                offsetR = activeGrade.gainR,
                                offsetB = activeGrade.gainB,
                                onColorChanged = { r, g, b ->
                                    applyColorGrade(activeGrade.copy(gainR = r, gainG = g, gainB = b))
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    1 -> { // Parametric Sliders
                        GoldSlider(
                            value = activeGrade.exposure,
                            onValueChange = { exp -> applyColorGrade(activeGrade.copy(exposure = exp)) },
                            valueRange = -2.0f..2.0f,
                            label = "Exposure (EV)",
                            displayValue = String.format("%.2f EV", activeGrade.exposure)
                        )
                        GoldSlider(
                            value = activeGrade.contrast,
                            onValueChange = { c -> applyColorGrade(activeGrade.copy(contrast = c)) },
                            valueRange = 0.5f..2.0f,
                            label = "Contrast",
                            displayValue = String.format("%.2f", activeGrade.contrast)
                        )
                        GoldSlider(
                            value = activeGrade.saturation,
                            onValueChange = { sat -> applyColorGrade(activeGrade.copy(saturation = sat)) },
                            valueRange = 0.0f..2.0f,
                            label = "Saturation",
                            displayValue = String.format("%.2f", activeGrade.saturation)
                        )
                        GoldSlider(
                            value = activeGrade.temperature,
                            onValueChange = { temp -> applyColorGrade(activeGrade.copy(temperature = temp)) },
                            valueRange = -1.0f..1.0f,
                            label = "White Balance (Cool ↔ Warm)",
                            displayValue = String.format("%.2f", activeGrade.temperature)
                        )
                        GoldSlider(
                            value = activeGrade.tint,
                            onValueChange = { tint -> applyColorGrade(activeGrade.copy(tint = tint)) },
                            valueRange = -1.0f..1.0f,
                            label = "Tint (Green ↔ Magenta)",
                            displayValue = String.format("%.2f", activeGrade.tint)
                        )
                    }

                    2 -> { // 3D LUT Presets
                        Text(
                            text = "SELECT 3D CINEMATIC LOOK-UP TABLE (LUT)",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = EgoGoldBright)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        val luts = listOf(
                            Triple("Royal Gold", "Warm obsidian & burnished 24k luster", 0xFFFFD978),
                            Triple("Golden Hour", "California sunset warmth & high contrast", 0xFFFF9900),
                            Triple("Teal & Orange", "Hollywood action blockbuster grade", 0xFF00CED1),
                            Triple("Cyberpunk", "Neon magenta & ultraviolet night", 0xFFFF007F),
                            Triple("Monochrome Noir", "High-contrast black & white classic", 0xFFCCCCCC),
                            Triple("None", "Default standard REC.709 profile", 0xFF666666)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            luts.forEach { (name, desc, colorHex) ->
                                val isSelected = activeGrade.lutPreset == name
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) EgoBgElevated else EgoBgPanel)
                                        .border(
                                            1.dp,
                                            if (isSelected) EgoGoldBright else EgoBorder,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { applyColorGrade(activeGrade.copy(lutPreset = name)) }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(colorHex))
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = name, fontWeight = FontWeight.Bold, color = if (isSelected) EgoGoldBright else EgoTextPrimary, fontSize = 13.sp)
                                        Text(text = desc, color = EgoTextMuted, fontSize = 11.sp)
                                    }
                                    if (isSelected) {
                                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EgoGoldBright, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
