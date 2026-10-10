package com.example.ui.components

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
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun InspectorPanel(
    selectedClip: TimelineClip?,
    onUpdateClip: ((TimelineClip) -> TimelineClip) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Transform", "Color", "VFX", "Audio/Speed", "Text")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(EgoBgPanel)
            .border(1.dp, EgoBorder, RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "INSPECTOR: ${selectedClip?.name ?: "No Clip Selected"}",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = EgoGoldBright),
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Tabs Row
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = EgoBgSecondary,
            contentColor = EgoGoldBright,
            edgePadding = 0.dp,
            divider = {},
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, EgoBorder, RoundedCornerShape(8.dp))
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = tab,
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) EgoGoldBright else EgoTextSecondary
                        )
                    },
                    modifier = Modifier.testTag("inspector_tab_$index")
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (selectedClip == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tap any clip on the timeline to edit properties",
                    style = MaterialTheme.typography.bodySmall,
                    color = EgoTextMuted
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                when (selectedTab) {
                    0 -> { // Transform
                        GoldSlider(
                            value = selectedClip.transform.scaleX,
                            onValueChange = { s ->
                                onUpdateClip { it.copy(transform = it.transform.copy(scaleX = s, scaleY = s)) }
                            },
                            valueRange = 0.2f..3.0f,
                            label = "Scale",
                            displayValue = "${String.format("%.2f", selectedClip.transform.scaleX)}x"
                        )
                        GoldSlider(
                            value = selectedClip.transform.rotation,
                            onValueChange = { r ->
                                onUpdateClip { it.copy(transform = it.transform.copy(rotation = r)) }
                            },
                            valueRange = -180f..180f,
                            label = "Rotation",
                            displayValue = "${selectedClip.transform.rotation.toInt()}°"
                        )
                        GoldSlider(
                            value = selectedClip.transform.opacity,
                            onValueChange = { o ->
                                onUpdateClip { it.copy(transform = it.transform.copy(opacity = o)) }
                            },
                            valueRange = 0f..1f,
                            label = "Opacity",
                            displayValue = "${(selectedClip.transform.opacity * 100).toInt()}%"
                        )
                        // Blend mode selection
                        Text("Blend Mode", style = MaterialTheme.typography.bodySmall, color = EgoTextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            BlendModeType.values().forEach { mode ->
                                FilterChip(
                                    selected = selectedClip.transform.blendMode == mode,
                                    onClick = {
                                        onUpdateClip { it.copy(transform = it.transform.copy(blendMode = mode)) }
                                    },
                                    label = { Text(mode.label, fontSize = 11.sp) },
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

                    1 -> { // Color
                        GoldSlider(
                            value = selectedClip.colorGrade.exposure,
                            onValueChange = { exp ->
                                onUpdateClip { it.copy(colorGrade = it.colorGrade.copy(exposure = exp)) }
                            },
                            valueRange = -1.5f..1.5f,
                            label = "Exposure",
                            displayValue = String.format("%.2f EV", selectedClip.colorGrade.exposure)
                        )
                        GoldSlider(
                            value = selectedClip.colorGrade.contrast,
                            onValueChange = { c ->
                                onUpdateClip { it.copy(colorGrade = it.colorGrade.copy(contrast = c)) }
                            },
                            valueRange = 0.5f..2.0f,
                            label = "Contrast",
                            displayValue = String.format("%.2f", selectedClip.colorGrade.contrast)
                        )
                        GoldSlider(
                            value = selectedClip.colorGrade.saturation,
                            onValueChange = { sat ->
                                onUpdateClip { it.copy(colorGrade = it.colorGrade.copy(saturation = sat)) }
                            },
                            valueRange = 0f..2.0f,
                            label = "Saturation",
                            displayValue = String.format("%.2f", selectedClip.colorGrade.saturation)
                        )
                        // Cinematic LUT presets
                        Text("3D LUT Preset", style = MaterialTheme.typography.bodySmall, color = EgoTextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        val luts = listOf("None", "Royal Gold", "Golden Hour", "Teal & Orange", "Cyberpunk", "Monochrome Noir")
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            luts.forEach { lut ->
                                FilterChip(
                                    selected = selectedClip.colorGrade.lutPreset == lut,
                                    onClick = {
                                        onUpdateClip { it.copy(colorGrade = it.colorGrade.copy(lutPreset = lut)) }
                                    },
                                    label = { Text(lut, fontSize = 11.sp) },
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

                    2 -> { // VFX Filters
                        Text("Applied Video Filter", style = MaterialTheme.typography.bodySmall, color = EgoTextSecondary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            VideoFilterType.values().forEach { filter ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (selectedClip.filter == filter) EgoBgElevated else Color.Transparent)
                                        .border(
                                            1.dp,
                                            if (selectedClip.filter == filter) EgoGoldBright else EgoBorder,
                                            RoundedCornerShape(6.dp)
                                        )
                                        .clickable {
                                            onUpdateClip { it.copy(filter = filter) }
                                        }
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = filter.label,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (selectedClip.filter == filter) EgoGoldBright else EgoTextPrimary
                                    )
                                    Text(
                                        text = filter.category,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = EgoTextMuted
                                    )
                                }
                            }
                        }
                    }

                    3 -> { // Audio & Speed
                        GoldSlider(
                            value = selectedClip.volume,
                            onValueChange = { vol ->
                                onUpdateClip { it.copy(volume = vol) }
                            },
                            valueRange = 0f..2.0f,
                            label = "Clip Volume",
                            displayValue = "${(selectedClip.volume * 100).toInt()}%"
                        )
                        GoldSlider(
                            value = selectedClip.speed,
                            onValueChange = { spd ->
                                onUpdateClip { it.copy(speed = spd) }
                            },
                            valueRange = 0.25f..4.0f,
                            label = "Playback Speed",
                            displayValue = "${String.format("%.2f", selectedClip.speed)}x"
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Reverse Video", style = MaterialTheme.typography.bodySmall, color = EgoTextPrimary)
                            Switch(
                                checked = selectedClip.isReversed,
                                onCheckedChange = { rev ->
                                    onUpdateClip { it.copy(isReversed = rev) }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = EgoGoldBright,
                                    checkedTrackColor = EgoGoldDark
                                )
                            )
                        }
                    }

                    4 -> { // Text Overlay
                        var currentText by remember(selectedClip.textOverlay?.text) {
                            mutableStateOf(selectedClip.textOverlay?.text ?: "")
                        }
                        OutlinedTextField(
                            value = currentText,
                            onValueChange = {
                                currentText = it
                                onUpdateClip { clip ->
                                    val currentOverlay = clip.textOverlay ?: TextOverlay()
                                    clip.copy(textOverlay = currentOverlay.copy(text = it))
                                }
                            },
                            label = { Text("Overlay Text") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EgoGoldBright,
                                unfocusedBorderColor = EgoBorder,
                                focusedTextColor = EgoTextPrimary,
                                unfocusedTextColor = EgoTextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("inspector_text_input")
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        val currentOverlay = selectedClip.textOverlay ?: TextOverlay()
                        GoldSlider(
                            value = currentOverlay.fontSizeSp,
                            onValueChange = { sz ->
                                onUpdateClip { clip ->
                                    val ov = clip.textOverlay ?: TextOverlay()
                                    clip.copy(textOverlay = ov.copy(fontSizeSp = sz))
                                }
                            },
                            valueRange = 14f..64f,
                            label = "Font Size",
                            displayValue = "${currentOverlay.fontSizeSp.toInt()} sp"
                        )
                    }
                }
            }
        }
    }
}
