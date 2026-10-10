package com.example.ui.screens.templates

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.engine.TimelineEngine
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

data class TemplateItem(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val durationStr: String,
    val aspectRatio: AspectRatioPreset,
    val resolution: ResolutionPreset,
    val fps: FpsPreset,
    val visualPlaceholder: String
)

@Composable
fun TemplatesScreen(
    engine: TimelineEngine,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var appliedMessage by remember { mutableStateOf<String?>(null) }

    val templates = listOf(
        TemplateItem(
            id = "t_royal",
            title = "Royal Crown Gold Intro",
            category = "Cinematic Branding",
            description = "High-contrast 24K gold geometric frame reveal with orchestral riser and bloom glow.",
            durationStr = "12s",
            aspectRatio = AspectRatioPreset.LANDSCAPE_16_9,
            resolution = ResolutionPreset.UHD_4K,
            fps = FpsPreset.FPS_24,
            visualPlaceholder = "golden_palace"
        ),
        TemplateItem(
            id = "t_gaming",
            title = "Gaming Montage Velocity",
            category = "Esports & Gaming",
            description = "60 FPS high-velocity cuts, impact kill flashes, cyber glitch displacement, and bass drops.",
            durationStr = "18s",
            aspectRatio = AspectRatioPreset.LANDSCAPE_16_9,
            resolution = ResolutionPreset.FHD_1080P,
            fps = FpsPreset.FPS_60,
            visualPlaceholder = "gaming_action"
        ),
        TemplateItem(
            id = "t_anime",
            title = "Anime Impact Velocity",
            category = "Anime & Edits",
            description = "Dynamic speed lines, chromatic shockwaves, freeze-frame beats, and neon outlines.",
            durationStr = "15s",
            aspectRatio = AspectRatioPreset.LANDSCAPE_16_9,
            resolution = ResolutionPreset.FHD_1080P,
            fps = FpsPreset.FPS_60,
            visualPlaceholder = "cyber_city"
        ),
        TemplateItem(
            id = "t_shorts",
            title = "Viral TikTok / Shorts Hook",
            category = "Social Media",
            description = "9:16 vertical layout with punchy kinetic captions, emoji badges, and sound effects.",
            durationStr = "10s",
            aspectRatio = AspectRatioPreset.PORTRAIT_9_16,
            resolution = ResolutionPreset.FHD_1080P,
            fps = FpsPreset.FPS_60,
            visualPlaceholder = "text_layer"
        ),
        TemplateItem(
            id = "t_drone",
            title = "Cinematic Travel Film",
            category = "Cinematic",
            description = "24 FPS 2.39:1 letterbox, 35mm grain, California golden hour LUT, and gentle slow motion.",
            durationStr = "20s",
            aspectRatio = AspectRatioPreset.CINEMA_21_9,
            resolution = ResolutionPreset.UHD_4K,
            fps = FpsPreset.FPS_24,
            visualPlaceholder = "cinematic_drone"
        )
    )

    Scaffold(
        topBar = {
            RoyalTopBar(
                title = "TEMPLATES & PRESETS",
                subtitle = "1-Click Production Timelines",
                onBack = onBack
            )
        },
        containerColor = EgoBgPrimary,
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (appliedMessage != null) {
                item {
                    Surface(
                        color = EgoSuccess.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, EgoSuccess),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                    ) {
                        Text(
                            text = appliedMessage!!,
                            color = EgoSuccess,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            items(templates, key = { it.id }) { tmpl ->
                Surface(
                    color = EgoBgPanel,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, EgoBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = tmpl.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = EgoGoldBright)
                            )
                            Surface(
                                color = EgoBgElevated,
                                shape = RoundedCornerShape(4.dp),
                                border = BorderStroke(1.dp, EgoBorder)
                            ) {
                                Text(
                                    text = tmpl.durationStr,
                                    color = EgoTextSecondary,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = tmpl.category, color = EgoGold, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = tmpl.description, color = EgoTextSecondary, fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${tmpl.aspectRatio.label.take(4)} • ${tmpl.resolution.label} • ${tmpl.fps.label.take(6)}",
                                color = EgoTextMuted,
                                fontSize = 11.sp
                            )
                            GoldButton(
                                text = "Apply to Timeline",
                                icon = Icons.Default.DownloadDone,
                                onClick = {
                                    // Generate tracks and update project
                                    val currentProj = engine.project.value
                                    val vTrack = TimelineTrack(
                                        name = "Video V1",
                                        trackType = TrackType.VIDEO,
                                        clips = listOf(
                                            TimelineClip(
                                                name = "${tmpl.title} Shot",
                                                trackId = "v1",
                                                startTimeMs = 0L,
                                                durationMs = 8000L,
                                                placeholderVisual = tmpl.visualPlaceholder,
                                                filter = if (tmpl.id == "t_gaming") VideoFilterType.CYBER_GLITCH else VideoFilterType.BLOOM_GLOW
                                            )
                                        )
                                    )
                                    val newProj = currentProj.copy(
                                        name = tmpl.title,
                                        aspectRatio = tmpl.aspectRatio,
                                        resolution = tmpl.resolution,
                                        fps = tmpl.fps,
                                        tracks = listOf(vTrack)
                                    )
                                    engine.setProject(newProj)
                                    appliedMessage = "Applied '${tmpl.title}' to the current timeline!"
                                },
                                testTag = "template_apply_${tmpl.id}"
                            )
                        }
                    }
                }
            }
        }
    }
}
