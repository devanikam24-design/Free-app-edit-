package com.example.ui.screens.editor

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.engine.VideoPreviewCanvas
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun EditorScreen(
    engine: TimelineEngine,
    onBackToHome: () -> Unit,
    onOpenExport: () -> Unit,
    onOpenStudio: (String) -> Unit, // "color", "vfx", "audio", "subtitles", "ai", "templates"
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val project by engine.project.collectAsState()
    val currentTimeMs by engine.currentTimeMs.collectAsState()
    val isPlaying by engine.isPlaying.collectAsState()
    val selectedClipId by engine.selectedClipId.collectAsState()
    val selectedTrackId by engine.selectedTrackId.collectAsState()
    val showSafeGuides by engine.showSafeGuides.collectAsState()
    val showGrid by engine.showGrid.collectAsState()
    val isSplitComparison by engine.isSplitComparison.collectAsState()
    val previewResolution by engine.previewResolution.collectAsState()

    var activePanel by remember { mutableStateOf<String?>("inspector") } // "inspector", "media", or null

    // System Media Picker for importing user images/videos
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val vTrack = project.tracks.firstOrNull { it.trackType == TrackType.VIDEO }
            if (vTrack != null) {
                val newClip = TimelineClip(
                    name = "Imported Media",
                    trackId = vTrack.id,
                    startTimeMs = currentTimeMs,
                    durationMs = 5000L,
                    sourceUri = uri.toString(),
                    placeholderVisual = "cinematic_drone"
                )
                engine.addClipToTrack(vTrack.id, newClip)
            }
        }
    }

    Scaffold(
        topBar = {
            RoyalTopBar(
                title = project.name,
                subtitle = "${project.aspectRatio.label} • ${project.resolution.label}",
                onBack = onBackToHome,
                onUndo = onUndo,
                onRedo = onRedo,
                onExport = onOpenExport,
                actions = {
                    // Studio switchers quick menu
                    var showStudioMenu by remember { mutableStateOf(false) }
                    Box {
                        IconButton(
                            onClick = { showStudioMenu = true },
                            modifier = Modifier.testTag("editor_studio_menu_button")
                        ) {
                            Icon(imageVector = Icons.Default.Apps, contentDescription = "Studios", tint = EgoGoldBright)
                        }
                        DropdownMenu(
                            expanded = showStudioMenu,
                            onDismissRequest = { showStudioMenu = false },
                            modifier = Modifier.background(EgoBgElevated).border(1.dp, EgoBorder)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Color Grading Studio", color = EgoTextPrimary) },
                                onClick = { onOpenStudio("color"); showStudioMenu = false },
                                leadingIcon = { Icon(Icons.Default.Palette, null, tint = EgoGold) }
                            )
                            DropdownMenuItem(
                                text = { Text("VFX & Motion Graphics", color = EgoTextPrimary) },
                                onClick = { onOpenStudio("vfx"); showStudioMenu = false },
                                leadingIcon = { Icon(Icons.Default.Flare, null, tint = EgoPurple) }
                            )
                            DropdownMenuItem(
                                text = { Text("Audio Studio & Mixer", color = EgoTextPrimary) },
                                onClick = { onOpenStudio("audio"); showStudioMenu = false },
                                leadingIcon = { Icon(Icons.Default.GraphicEq, null, tint = EgoSuccess) }
                            )
                            DropdownMenuItem(
                                text = { Text("AI Editing Suite", color = EgoTextPrimary) },
                                onClick = { onOpenStudio("ai"); showStudioMenu = false },
                                leadingIcon = { Icon(Icons.Default.Psychology, null, tint = EgoInfo) }
                            )
                            DropdownMenuItem(
                                text = { Text("Subtitle Editor", color = EgoTextPrimary) },
                                onClick = { onOpenStudio("subtitles"); showStudioMenu = false },
                                leadingIcon = { Icon(Icons.Default.Subtitles, null, tint = EgoGoldBright) }
                            )
                            DropdownMenuItem(
                                text = { Text("Templates Marketplace", color = EgoTextPrimary) },
                                onClick = { onOpenStudio("templates"); showStudioMenu = false },
                                leadingIcon = { Icon(Icons.Default.DashboardCustomize, null, tint = Color(0xFFF472B6)) }
                            )
                        }
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
            // TOP SECTION: Video Preview Monitor (50% - 55% of height)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.1f)
                    .background(Color.Black)
                    .padding(8.dp)
            ) {
                // Real-time canvas renderer
                VideoPreviewCanvas(
                    project = project,
                    currentTimeMs = currentTimeMs,
                    showSafeGuides = showSafeGuides,
                    showGrid = showGrid,
                    isSplitComparison = isSplitComparison,
                    previewResolution = previewResolution,
                    modifier = Modifier.fillMaxSize()
                )

                // Top Floating Monitor HUD (Timecode & Tools)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Timecode Chip
                    Surface(
                        color = EgoBgPanel.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, EgoBorder)
                    ) {
                        Text(
                            text = "${engine.formatTimecode(currentTimeMs)} / ${engine.formatTimecode(project.totalDurationMs)}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = EgoGoldBright,
                                letterSpacing = 1.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Monitor Display Toggles (Guides, Grid, Split A/B, Resolution)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = { engine.toggleSafeGuides() },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CropFree,
                                contentDescription = "Safe Guides",
                                tint = if (showSafeGuides) EgoGoldBright else EgoTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        IconButton(
                            onClick = { engine.toggleGrid() },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.GridOn,
                                contentDescription = "Grid",
                                tint = if (showGrid) EgoGoldBright else EgoTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        IconButton(
                            onClick = { engine.toggleSplitComparison() },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Compare,
                                contentDescription = "Before/After Split",
                                tint = if (isSplitComparison) EgoGoldBright else EgoTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Bottom Transport Controls (Floating bar)
                Surface(
                    color = EgoBgPanel.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, EgoBorder),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Previous Frame
                        IconButton(
                            onClick = { engine.stepPrevFrame() },
                            modifier = Modifier.size(32.dp).testTag("transport_prev_frame")
                        ) {
                            Icon(imageVector = Icons.Default.SkipPrevious, contentDescription = "Prev Frame", tint = EgoTextPrimary)
                        }

                        // Play / Pause
                        IconButton(
                            onClick = { engine.togglePlayPause() },
                            modifier = Modifier.size(38.dp).testTag("transport_play_pause")
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause",
                                tint = EgoGoldBright,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Next Frame
                        IconButton(
                            onClick = { engine.stepNextFrame() },
                            modifier = Modifier.size(32.dp).testTag("transport_next_frame")
                        ) {
                            Icon(imageVector = Icons.Default.SkipNext, contentDescription = "Next Frame", tint = EgoTextPrimary)
                        }

                        // Stop
                        IconButton(
                            onClick = { engine.stop() },
                            modifier = Modifier.size(32.dp).testTag("transport_stop")
                        ) {
                            Icon(imageVector = Icons.Default.Stop, contentDescription = "Stop", tint = EgoError)
                        }
                    }
                }
            }

            // MIDDLE SECTION: Collapsible Inspector / Media Drawer (height-constrained)
            if (activePanel == "inspector") {
                val selectedClip = engine.getSelectedClip()
                InspectorPanel(
                    selectedClip = selectedClip,
                    onUpdateClip = { transform -> engine.updateSelectedClip(transform) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 180.dp)
                )
            } else if (activePanel == "media") {
                MediaDrawer(
                    onImportSystemMedia = {
                        photoPickerLauncher.launch(
                            androidx.activity.result.PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageAndVideo
                            )
                        )
                    },
                    onAddSampleClip = { visual, name ->
                        val vTrack = project.tracks.firstOrNull { it.trackType == TrackType.VIDEO }
                        if (vTrack != null) {
                            val newClip = TimelineClip(
                                name = name,
                                trackId = vTrack.id,
                                startTimeMs = currentTimeMs,
                                durationMs = 5000L,
                                placeholderVisual = visual
                            )
                            engine.addClipToTrack(vTrack.id, newClip)
                        }
                    },
                    onClose = { activePanel = "inspector" }
                )
            }

            // Quick Tool Bar
            QuickToolbar(
                onSplit = { engine.splitClipAtPlayhead() },
                onDuplicate = { engine.duplicateSelectedClip() },
                onDelete = { ripple -> engine.deleteSelectedClip(ripple) },
                onAddMedia = { activePanel = "media" },
                onAddText = {
                    val tTrack = project.tracks.firstOrNull { it.trackType == TrackType.TEXT }
                    if (tTrack != null) {
                        val textClip = TimelineClip(
                            name = "Title Text",
                            trackId = tTrack.id,
                            startTimeMs = currentTimeMs,
                            durationMs = 4000L,
                            textOverlay = TextOverlay(text = "NEW TITLE", fontSizeSp = 28f),
                            placeholderVisual = "text_layer"
                        )
                        engine.addClipToTrack(tTrack.id, textClip)
                    } else {
                        engine.addTrack(TrackType.TEXT)
                    }
                },
                onAddAudio = {
                    val aTrack = project.tracks.firstOrNull { it.trackType == TrackType.AUDIO }
                    if (aTrack != null) {
                        val audioClip = TimelineClip(
                            name = "Audio Beat",
                            trackId = aTrack.id,
                            startTimeMs = currentTimeMs,
                            durationMs = 8000L,
                            volume = 0.9f,
                            placeholderVisual = "audio_waveform"
                        )
                        engine.addClipToTrack(aTrack.id, audioClip)
                    } else {
                        engine.addTrack(TrackType.AUDIO)
                    }
                },
                onOpenColor = { onOpenStudio("color") },
                onOpenVfx = { onOpenStudio("vfx") },
                onOpenAudio = { onOpenStudio("audio") },
                hasSelection = selectedClipId != null
            )

            // BOTTOM SECTION: Multi-Track Timeline
            TimelineTrackView(
                project = project,
                currentTimeMs = currentTimeMs,
                selectedClipId = selectedClipId,
                selectedTrackId = selectedTrackId,
                onSeekTo = { ms -> engine.seekTo(ms) },
                onSelectClip = { tId, cId ->
                    engine.selectClip(tId, cId)
                    activePanel = "inspector"
                },
                onTrimClip = { tId, cId, start, dur -> engine.trimClip(tId, cId, start, dur) },
                onToggleMute = { /* handled in engine if needed */ },
                onToggleLock = { /* handled in engine */ },
                onToggleVisible = { /* handled in engine */ },
                onAddTrack = { type -> engine.addTrack(type) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.95f)
            )
        }
    }
}

@Composable
private fun MediaDrawer(
    onImportSystemMedia: () -> Unit,
    onAddSampleClip: (visual: String, name: String) -> Unit,
    onClose: () -> Unit
) {
    Surface(
        color = EgoBgPanel,
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .border(1.dp, EgoBorder)
            .padding(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MEDIA BINS & ASSETS",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = EgoGoldBright)
                )
                IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = EgoTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Import from gallery button
                Box(
                    modifier = Modifier
                        .width(110.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .background(EgoBgElevated)
                        .border(1.dp, EgoGold, RoundedCornerShape(8.dp))
                        .clickable(onClick = onImportSystemMedia)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, tint = EgoGoldBright)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Pick Media", fontWeight = FontWeight.Bold, color = EgoTextPrimary, fontSize = 11.sp)
                        Text("Camera/Gallery", color = EgoTextMuted, fontSize = 9.sp)
                    }
                }

                // Sample Cinematic Footage Bins
                val samples = listOf(
                    Triple("cinematic_drone", "Drone Aerial", "4K Sunset Sky"),
                    Triple("golden_palace", "Gold Palace", "Imperial Hall"),
                    Triple("cyber_city", "Cyber City", "Tokyo Neon 60fps"),
                    Triple("gaming_action", "Gaming Clip", "Velocity Montage"),
                    Triple("gold_particles", "Gold Embers", "VFX Overlay")
                )
                samples.forEach { (visual, label, desc) ->
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(8.dp))
                            .background(EgoBgElevated)
                            .border(1.dp, EgoBorder, RoundedCornerShape(8.dp))
                            .clickable { onAddSampleClip(visual, label) }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Default.MovieCreation, contentDescription = null, tint = EgoInfo)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(label, fontWeight = FontWeight.Bold, color = EgoTextPrimary, fontSize = 11.sp, maxLines = 1)
                            Text(desc, color = EgoTextMuted, fontSize = 9.sp, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}
