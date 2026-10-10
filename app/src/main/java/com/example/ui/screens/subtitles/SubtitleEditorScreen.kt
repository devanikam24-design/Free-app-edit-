package com.example.ui.screens.subtitles

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
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import java.util.UUID

data class SubtitleEntry(
    val id: String = UUID.randomUUID().toString(),
    val startMs: Long,
    val endMs: Long,
    val text: String
)

@Composable
fun SubtitleEditorScreen(
    engine: TimelineEngine,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val project by engine.project.collectAsState()
    val currentTimeMs by engine.currentTimeMs.collectAsState()

    var subtitles by remember {
        mutableStateOf(
            listOf(
                SubtitleEntry(startMs = 0L, endMs = 4500L, text = "Welcome to the ultimate editing experience."),
                SubtitleEntry(startMs = 4500L, endMs = 9500L, text = "Crafted with royal gold precision and power."),
                SubtitleEntry(startMs = 9500L, endMs = 15000L, text = "EGO EDIT — Master your vision.")
            )
        )
    }

    var newText by remember { mutableStateOf("") }
    var subtitleFontSize by remember { mutableFloatStateOf(24f) }
    var isKaraokeEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            RoyalTopBar(
                title = "SUBTITLE & CAPTION STUDIO",
                subtitle = "Animated Captions • Karaoke • SRT Export",
                onBack = onBack,
                actions = {
                    TextButton(onClick = {
                        // Export SRT formatted text
                    }) {
                        Text("EXPORT SRT", color = EgoGoldBright, fontWeight = FontWeight.Bold, fontSize = 11.sp)
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
                .padding(16.dp)
        ) {
            // Live Subtitle Preview Box
            Surface(
                color = Color.Black,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, EgoBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                val activeSub = subtitles.find { currentTimeMs in it.startMs..it.endMs }
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (activeSub != null) {
                        Surface(
                            color = Color(0xCC08090C),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, EgoGold)
                        ) {
                            Text(
                                text = activeSub.text,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isKaraokeEnabled) EgoGoldBright else Color.White
                                ),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    } else {
                        Text("No active subtitle at ${engine.formatTimecode(currentTimeMs)}", color = EgoTextMuted, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Add Subtitle Card
            Surface(
                color = EgoBgPanel,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, EgoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("ADD CAPTION AT CURRENT PLAYHEAD (${engine.formatTimecode(currentTimeMs)})", fontWeight = FontWeight.Bold, color = EgoGoldBright, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newText,
                            onValueChange = { newText = it },
                            placeholder = { Text("Enter caption text...") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EgoGoldBright,
                                unfocusedBorderColor = EgoBorder,
                                focusedTextColor = EgoTextPrimary,
                                unfocusedTextColor = EgoTextPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        GoldButton(
                            text = "Add",
                            onClick = {
                                if (newText.isNotBlank()) {
                                    val entry = SubtitleEntry(
                                        startMs = currentTimeMs,
                                        endMs = currentTimeMs + 3500L,
                                        text = newText
                                    )
                                    subtitles = (subtitles + entry).sortedBy { it.startMs }
                                    newText = ""
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Subtitle List
            Text("CAPTIONS TIMELINE (${subtitles.size} ENTRIES)", fontWeight = FontWeight.Bold, color = EgoGold, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                subtitles.forEach { sub ->
                    val isCurrent = currentTimeMs in sub.startMs..sub.endMs
                    Surface(
                        color = if (isCurrent) EgoBgElevated else EgoBgPanel,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, if (isCurrent) EgoGoldBright else EgoBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { engine.seekTo(sub.startMs) }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${engine.formatTimecode(sub.startMs)} ➔ ${engine.formatTimecode(sub.endMs)}",
                                    color = EgoGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = sub.text, color = EgoTextPrimary, fontSize = 13.sp)
                            }
                            IconButton(
                                onClick = { subtitles = subtitles.filter { it.id != sub.id } },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.DeleteOutline, null, tint = EgoError, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
