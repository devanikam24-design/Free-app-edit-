package com.example.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import kotlin.math.roundToLong

@Composable
fun TimelineTrackView(
    project: Project,
    currentTimeMs: Long,
    selectedClipId: String?,
    selectedTrackId: String?,
    onSeekTo: (Long) -> Unit,
    onSelectClip: (trackId: String, clipId: String) -> Unit,
    onTrimClip: (trackId: String, clipId: String, newStart: Long, newDuration: Long) -> Unit,
    onToggleMute: (trackId: String) -> Unit,
    onToggleLock: (trackId: String) -> Unit,
    onToggleVisible: (trackId: String) -> Unit,
    onAddTrack: (TrackType) -> Unit,
    modifier: Modifier = Modifier
) {
    var zoomLevel by remember { mutableFloatStateOf(1.0f) } // 0.5x to 3.0x
    val dpPerSecond = 45.dp * zoomLevel
    val totalSeconds = (project.totalDurationMs / 1000f).coerceAtLeast(10f)
    val timelineWidthDp = dpPerSecond * totalSeconds + 200.dp

    val horizontalScrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(EgoBgSecondary)
            .border(1.dp, EgoBorder, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
    ) {
        // Timeline Header: Controls (Zoom, Magnet, Add Track, Timecode)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(EgoBgPanel)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = Icons.Default.Timeline, contentDescription = null, tint = EgoGold, modifier = Modifier.size(18.dp))
                Text(
                    text = "TIMELINE",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = EgoGoldBright)
                )

                // Track Adder Dropdown
                var showAddTrackMenu by remember { mutableStateOf(false) }
                Box {
                    IconButton(
                        onClick = { showAddTrackMenu = true },
                        modifier = Modifier.size(28.dp).testTag("timeline_add_track_button")
                    ) {
                        Icon(imageVector = Icons.Default.AddCircleOutline, contentDescription = "Add Track", tint = EgoTextSecondary)
                    }
                    DropdownMenu(
                        expanded = showAddTrackMenu,
                        onDismissRequest = { showAddTrackMenu = false },
                        modifier = Modifier.background(EgoBgElevated).border(1.dp, EgoBorder)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Video Track (V)", color = EgoTextPrimary) },
                            onClick = { onAddTrack(TrackType.VIDEO); showAddTrackMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Audio Track (A)", color = EgoTextPrimary) },
                            onClick = { onAddTrack(TrackType.AUDIO); showAddTrackMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Titles Track (T)", color = EgoTextPrimary) },
                            onClick = { onAddTrack(TrackType.TEXT); showAddTrackMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Overlay Track (O)", color = EgoTextPrimary) },
                            onClick = { onAddTrack(TrackType.OVERLAY); showAddTrackMenu = false }
                        )
                    }
                }
            }

            // Zoom slider & current time
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "Zoom", style = MaterialTheme.typography.labelSmall, color = EgoTextMuted)
                Slider(
                    value = zoomLevel,
                    onValueChange = { zoomLevel = it },
                    valueRange = 0.5f..2.5f,
                    colors = SliderDefaults.colors(thumbColor = EgoGold, activeTrackColor = EgoGoldMuted),
                    modifier = Modifier.width(90.dp).height(24.dp).testTag("timeline_zoom_slider")
                )
            }
        }

        Divider(color = EgoBorder, thickness = 1.dp)

        // Main Timeline Scroll Container
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // Track Headers Left Column (Fixed Width)
            Column(
                modifier = Modifier
                    .width(100.dp)
                    .background(EgoBgPanel)
            ) {
                // Empty corner for ruler
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp)
                        .background(EgoBgElevated)
                        .padding(horizontal = 6.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text("TRACKS", style = MaterialTheme.typography.labelSmall, color = EgoTextMuted, fontSize = 9.sp)
                }
                Divider(color = EgoBorder, thickness = 1.dp)

                // Track headers
                project.tracks.forEach { track ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .background(if (track.id == selectedTrackId) EgoBgElevated else EgoBgPanel)
                            .padding(horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = track.name,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = when (track.trackType) {
                                    TrackType.VIDEO -> Color(0xFF60A5FA)
                                    TrackType.AUDIO -> Color(0xFF34D399)
                                    TrackType.TEXT -> EgoGoldBright
                                    TrackType.OVERLAY -> Color(0xFFC084FC)
                                },
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Mute & Eye buttons
                        Row {
                            IconButton(
                                onClick = { onToggleVisible(track.id) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = if (track.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle Visibility",
                                    tint = if (track.isVisible) EgoTextSecondary else EgoError,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            IconButton(
                                onClick = { onToggleMute(track.id) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = if (track.isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                    contentDescription = "Toggle Mute",
                                    tint = if (track.isMuted) EgoError else EgoTextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                    Divider(color = EgoBorder, thickness = 1.dp)
                }
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(EgoBorder)
            )

            // Horizontally Scrollable Timeline Tracks + Playhead
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(horizontalScrollState)
            ) {
                Column(modifier = Modifier.width(timelineWidthDp)) {
                    // Time Ruler
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                            .background(EgoBgElevated)
                            .pointerInput(totalSeconds, dpPerSecond) {
                                detectTapGestures { offset ->
                                    val tappedSec = (offset.x / dpPerSecond.toPx()).coerceAtLeast(0f)
                                    onSeekTo((tappedSec * 1000f).roundToLong())
                                }
                            }
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val secWidthPx = dpPerSecond.toPx()
                            val totalSecs = (size.width / secWidthPx).toInt()
                            for (s in 0..totalSecs) {
                                val x = s * secWidthPx
                                val isMajor = s % 5 == 0
                                val lineH = if (isMajor) 14.dp.toPx() else 6.dp.toPx()
                                drawLine(
                                    color = if (isMajor) EgoGold else Color(0x55FFFFFF),
                                    start = Offset(x, size.height - lineH),
                                    end = Offset(x, size.height),
                                    strokeWidth = if (isMajor) 2f else 1f
                                )
                            }
                        }
                    }

                    // Track lanes
                    project.tracks.forEach { track ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp)
                                .background(EgoBgPrimary)
                                .pointerInput(track.id) {
                                    detectTapGestures { offset ->
                                        val clickedSec = (offset.x / dpPerSecond.toPx()).coerceAtLeast(0f)
                                        val clickedMs = (clickedSec * 1000f).roundToLong()
                                        val hitClip = track.clips.find { c ->
                                            clickedMs >= c.startTimeMs && clickedMs <= (c.startTimeMs + c.durationMs)
                                        }
                                        if (hitClip != null) {
                                            onSelectClip(track.id, hitClip.id)
                                        }
                                        onSeekTo(clickedMs)
                                    }
                                }
                        ) {
                            // Render Clips in this track
                            track.clips.forEach { clip ->
                                val clipStartSec = clip.startTimeMs / 1000f
                                val clipDurSec = clip.durationMs / 1000f
                                val clipOffsetDp = dpPerSecond * clipStartSec
                                val clipWidthDp = (dpPerSecond * clipDurSec).coerceAtLeast(36.dp)
                                val isSelected = clip.id == selectedClipId

                                Box(
                                    modifier = Modifier
                                        .offset(x = clipOffsetDp)
                                        .width(clipWidthDp)
                                        .fillMaxHeight()
                                        .padding(vertical = 4.dp, horizontal = 1.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            when (track.trackType) {
                                                TrackType.VIDEO -> Color(0xFF1E3A8A)
                                                TrackType.AUDIO -> Color(0xFF065F46)
                                                TrackType.TEXT -> Color(0xFF78350F)
                                                TrackType.OVERLAY -> Color(0xFF581C87)
                                            }
                                        )
                                        .border(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) EgoGoldBright else Color(0x33FFFFFF),
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .testTag("timeline_clip_${clip.id}")
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = clip.name,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            fontSize = 11.sp
                                        )
                                    }

                                    // If selected, show Left and Right Trim Drag Handles
                                    if (isSelected) {
                                        // Left Handle
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.CenterStart)
                                                .width(10.dp)
                                                .fillMaxHeight()
                                                .background(EgoGoldBright)
                                                .pointerInput(clip.id) {
                                                    detectDragGestures { change, dragAmount ->
                                                        change.consume()
                                                        val deltaSec = dragAmount.x / dpPerSecond.toPx()
                                                        val deltaMs = (deltaSec * 1000f).roundToLong()
                                                        val newStart = (clip.startTimeMs + deltaMs).coerceAtLeast(0L)
                                                        val newDur = (clip.durationMs - deltaMs).coerceAtLeast(500L)
                                                        onTrimClip(track.id, clip.id, newStart, newDur)
                                                    }
                                                }
                                        )
                                        // Right Handle
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.CenterEnd)
                                                .width(10.dp)
                                                .fillMaxHeight()
                                                .background(EgoGoldBright)
                                                .pointerInput(clip.id) {
                                                    detectDragGestures { change, dragAmount ->
                                                        change.consume()
                                                        val deltaSec = dragAmount.x / dpPerSecond.toPx()
                                                        val deltaMs = (deltaSec * 1000f).roundToLong()
                                                        val newDur = (clip.durationMs + deltaMs).coerceAtLeast(500L)
                                                        onTrimClip(track.id, clip.id, clip.startTimeMs, newDur)
                                                    }
                                                }
                                        )
                                    }
                                }
                            }
                        }
                        Divider(color = EgoBorder, thickness = 1.dp)
                    }
                }

                // Vertical Gold Playhead Needle (Overlay across all tracks)
                val playheadSec = currentTimeMs / 1000f
                val playheadOffsetDp = dpPerSecond * playheadSec

                Box(
                    modifier = Modifier
                        .offset(x = playheadOffsetDp - 8.dp)
                        .width(16.dp)
                        .fillMaxHeight()
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val deltaSec = dragAmount.x / dpPerSecond.toPx()
                                val newTime = (currentTimeMs + (deltaSec * 1000f).roundToLong())
                                    .coerceIn(0L, project.totalDurationMs)
                                onSeekTo(newTime)
                            }
                        }
                ) {
                    // Top diamond head
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .size(16.dp, 18.dp)
                            .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                            .background(EgoGoldBright)
                    )
                    // Thin gold needle line
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .width(2.dp)
                            .fillMaxHeight()
                            .background(EgoGoldBright)
                    )
                }
            }
        }
    }
}
