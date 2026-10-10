package com.example.ui.screens.export

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.ExportOptions
import com.example.engine.ExportRenderer
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun ExportManagerScreen(
    project: Project,
    exportRenderer: ExportRenderer,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val renderProgress by exportRenderer.progress.collectAsState()

    var selectedFormat by remember { mutableStateOf("MP4") }
    var selectedCodec by remember { mutableStateOf("H.264 / AVC") }
    var selectedRes by remember { mutableStateOf(project.resolution) }
    var selectedFps by remember { mutableStateOf(project.fps) }
    var bitrateMbps by remember { mutableIntStateOf(35) }

    Scaffold(
        topBar = {
            RoyalTopBar(
                title = "EXPORT & RENDER MANAGER",
                subtitle = "Master 4K Rendering • Hardware Accelerated",
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
            // Render Status Card if active or complete
            if (renderProgress.isRendering || renderProgress.isComplete) {
                Surface(
                    color = EgoBgPanel,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, if (renderProgress.isComplete) EgoSuccess else EgoGold),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (renderProgress.isComplete) "MASTER RENDER COMPLETE!" else "ENCODING TIMELINE FRAMES...",
                                fontWeight = FontWeight.Bold,
                                color = if (renderProgress.isComplete) EgoSuccess else EgoGoldBright,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "${renderProgress.progressPercent.toInt()}%",
                                fontWeight = FontWeight.Black,
                                color = EgoGoldBright
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { renderProgress.progressPercent / 100f },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                            color = if (renderProgress.isComplete) EgoSuccess else EgoGoldBright,
                            trackColor = EgoBgElevated
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Frame ${renderProgress.currentFrame} / ${renderProgress.totalFrames}",
                                color = EgoTextSecondary,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "Speed: ${String.format("%.1f", renderProgress.currentFps)} FPS • ~${renderProgress.estimatedRemainingSec}s left",
                                color = EgoTextMuted,
                                fontSize = 11.sp
                            )
                        }

                        if (renderProgress.isComplete && renderProgress.outputFile != null) {
                            Spacer(modifier = Modifier.height(14.dp))
                            GoldButton(
                                text = "Share Video File (${String.format("%.1f", renderProgress.estimatedSizeMb)} MB)",
                                icon = Icons.Default.Share,
                                onClick = { exportRenderer.shareExportedFile(renderProgress.outputFile!!) },
                                testTag = "export_share_button"
                            )
                        }
                    }
                }
            }

            // Presets Cards
            Text("PLATFORM EXPORT PRESETS", fontWeight = FontWeight.Bold, color = EgoGold, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PlatformPresetCard(
                    title = "YouTube 4K",
                    res = "3840x2160",
                    fps = "60 FPS",
                    onClick = {
                        selectedRes = ResolutionPreset.UHD_4K
                        selectedFps = FpsPreset.FPS_60
                        bitrateMbps = 50
                    },
                    modifier = Modifier.weight(1f)
                )
                PlatformPresetCard(
                    title = "TikTok 9:16",
                    res = "1080x1920",
                    fps = "60 FPS",
                    onClick = {
                        selectedRes = ResolutionPreset.FHD_1080P
                        selectedFps = FpsPreset.FPS_60
                        bitrateMbps = 25
                    },
                    modifier = Modifier.weight(1f)
                )
                PlatformPresetCard(
                    title = "Cinema 24fps",
                    res = "4K Master",
                    fps = "24 FPS",
                    onClick = {
                        selectedRes = ResolutionPreset.UHD_4K
                        selectedFps = FpsPreset.FPS_24
                        bitrateMbps = 65
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Custom Encoder Settings
            Surface(
                color = EgoBgPanel,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, EgoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("MASTER ENCODER CONFIGURATION", fontWeight = FontWeight.Bold, color = EgoGoldBright, fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Resolution", style = MaterialTheme.typography.labelSmall, color = EgoTextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(ResolutionPreset.HD_720P, ResolutionPreset.FHD_1080P, ResolutionPreset.UHD_4K, ResolutionPreset.UHD_8K).forEach { res ->
                            FilterChip(
                                selected = selectedRes == res,
                                onClick = { selectedRes = res },
                                label = { Text(res.label, fontSize = 11.sp) },
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

                    Text("Frame Rate", style = MaterialTheme.typography.labelSmall, color = EgoTextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FpsPreset.values().forEach { fps ->
                            FilterChip(
                                selected = selectedFps == fps,
                                onClick = { selectedFps = fps },
                                label = { Text(fps.label.take(6), fontSize = 11.sp) },
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

                    Text("Video Codec", style = MaterialTheme.typography.labelSmall, color = EgoTextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("H.264 / AVC", "H.265 / HEVC", "ProRes HQ").forEach { cdc ->
                            FilterChip(
                                selected = selectedCodec == cdc,
                                onClick = { selectedCodec = cdc },
                                label = { Text(cdc, fontSize = 11.sp) },
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
                        value = bitrateMbps.toFloat(),
                        onValueChange = { bitrateMbps = it.toInt() },
                        valueRange = 8f..100f,
                        label = "Encoding Bitrate (VBR)",
                        displayValue = "$bitrateMbps Mbps"
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    GoldButton(
                        text = "START MASTER RENDER",
                        icon = Icons.Default.PlayCircle,
                        enabled = !renderProgress.isRendering,
                        onClick = {
                            val opts = ExportOptions(
                                format = selectedFormat,
                                codec = selectedCodec,
                                resolution = selectedRes,
                                fps = selectedFps,
                                bitrateMbps = bitrateMbps
                            )
                            exportRenderer.startRender(project, opts)
                        },
                        testTag = "export_start_render_button"
                    )
                }
            }
        }
    }
}

@Composable
private fun PlatformPresetCard(
    title: String,
    res: String,
    fps: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = EgoBgElevated,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, EgoBorder),
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(2.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = EgoGoldBright, fontSize = 11.sp, maxLines = 1)
            Spacer(modifier = Modifier.height(4.dp))
            Text(res, color = EgoTextPrimary, fontSize = 10.sp)
            Text(fps, color = EgoTextMuted, fontSize = 9.sp)
        }
    }
}
