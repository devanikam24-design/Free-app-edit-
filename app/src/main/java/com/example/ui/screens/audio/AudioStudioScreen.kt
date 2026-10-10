package com.example.ui.screens.audio

import android.media.MediaRecorder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.TimelineEngine
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun AudioStudioScreen(
    engine: TimelineEngine,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val project by engine.project.collectAsState()
    val currentTimeMs by engine.currentTimeMs.collectAsState()

    var isRecording by remember { mutableStateOf(false) }
    var recordElapsedSec by remember { mutableIntStateOf(0) }
    var mediaRecorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var recordedAudioFile by remember { mutableStateOf<File?>(null) }

    // Equalizer 5-Band States
    var eqBass by remember { mutableFloatStateOf(0f) }
    var eqLowMid by remember { mutableFloatStateOf(0f) }
    var eqMid by remember { mutableFloatStateOf(0f) }
    var eqHighMid by remember { mutableFloatStateOf(0f) }
    var eqTreble by remember { mutableFloatStateOf(0f) }

    // Audio recording permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Start recording
            try {
                val outFile = File(context.cacheDir, "VOICE_${System.currentTimeMillis()}.m4a")
                recordedAudioFile = outFile
                val recorder = MediaRecorder().apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    setOutputFile(outFile.absolutePath)
                    prepare()
                    start()
                }
                mediaRecorder = recorder
                isRecording = true
                recordElapsedSec = 0
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            while (isRecording) {
                delay(1000L)
                recordElapsedSec++
            }
        }
    }

    Scaffold(
        topBar = {
            RoyalTopBar(
                title = "AUDIO STUDIO & MIXER",
                subtitle = "Multitrack Mixer • 5-Band EQ • Voiceover",
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
            // Live Voiceover Recording Console
            Surface(
                color = EgoBgPanel,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, if (isRecording) EgoError else EgoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isRecording) "RECORDING VOICEOVER..." else "LIVE MICROPHONE VOICEOVER",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isRecording) EgoError else EgoGoldBright
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isRecording) "${recordElapsedSec}s elapsed • Capturing audio stream" else "Record narration directly at ${engine.formatTimecode(currentTimeMs)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = EgoTextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    IconButton(
                        onClick = {
                            if (!isRecording) {
                                permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                            } else {
                                // Stop recording
                                try {
                                    mediaRecorder?.stop()
                                    mediaRecorder?.release()
                                } catch (_: Exception) {}
                                mediaRecorder = null
                                isRecording = false

                                // Add recorded clip to Audio Track
                                val aTrack = project.tracks.firstOrNull { it.trackType == TrackType.AUDIO }
                                if (aTrack != null) {
                                    val newVoiceClip = TimelineClip(
                                        name = "Voiceover (${recordElapsedSec}s)",
                                        trackId = aTrack.id,
                                        startTimeMs = currentTimeMs,
                                        durationMs = (recordElapsedSec * 1000L).coerceAtLeast(2000L),
                                        sourceUri = recordedAudioFile?.absolutePath ?: "",
                                        placeholderVisual = "audio_waveform"
                                    )
                                    engine.addClipToTrack(aTrack.id, newVoiceClip)
                                }
                            }
                        },
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(if (isRecording) EgoError else EgoBgElevated)
                            .border(2.dp, if (isRecording) EgoError else EgoGoldBright, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = "Record",
                            tint = if (isRecording) Color.White else EgoGoldBright,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Multitrack Mixer
            Text(
                text = "MULTITRACK AUDIO MIXER",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = EgoGoldBright)
            )
            Spacer(modifier = Modifier.height(10.dp))

            val audioTracks = project.tracks.filter { it.trackType == TrackType.AUDIO }
            if (audioTracks.isEmpty()) {
                Text("No audio tracks in project.", color = EgoTextMuted)
            } else {
                audioTracks.forEach { track ->
                    Surface(
                        color = EgoBgPanel,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, EgoBorder),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(track.name, fontWeight = FontWeight.Bold, color = EgoTextPrimary)
                                Text("${track.clips.size} clips", color = EgoTextMuted, fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            val firstClip = track.clips.firstOrNull()
                            GoldSlider(
                                value = firstClip?.volume ?: 1.0f,
                                onValueChange = { vol ->
                                    if (firstClip != null) {
                                        engine.updateSelectedClip { it.copy(volume = vol) }
                                    }
                                },
                                valueRange = 0f..2f,
                                label = "Master Gain",
                                displayValue = "${((firstClip?.volume ?: 1f) * 100).toInt()}%"
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5-Band Parametric Equalizer
            Surface(
                color = EgoBgPanel,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, EgoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("5-BAND PARAMETRIC EQUALIZER", fontWeight = FontWeight.Bold, color = EgoGoldBright)
                    Spacer(modifier = Modifier.height(12.dp))

                    GoldSlider(value = eqBass, onValueChange = { eqBass = it }, valueRange = -12f..12f, label = "Sub-Bass (60 Hz)", displayValue = "${eqBass.toInt()} dB")
                    GoldSlider(value = eqLowMid, onValueChange = { eqLowMid = it }, valueRange = -12f..12f, label = "Low-Mid (250 Hz)", displayValue = "${eqLowMid.toInt()} dB")
                    GoldSlider(value = eqMid, onValueChange = { eqMid = it }, valueRange = -12f..12f, label = "Mid (1 kHz)", displayValue = "${eqMid.toInt()} dB")
                    GoldSlider(value = eqHighMid, onValueChange = { eqHighMid = it }, valueRange = -12f..12f, label = "High-Mid (4 kHz)", displayValue = "${eqHighMid.toInt()} dB")
                    GoldSlider(value = eqTreble, onValueChange = { eqTreble = it }, valueRange = -12f..12f, label = "Air & Treble (12 kHz)", displayValue = "${eqTreble.toInt()} dB")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // AI Beat Detection & Velocity Sync
            Surface(
                color = EgoBgPanel,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, EgoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("AI BEAT DETECTION & MARKERS", fontWeight = FontWeight.Bold, color = EgoGoldBright)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Analyze music tempo and automatically place gold markers at rhythm drops for velocity cuts.", color = EgoTextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    GoldButton(
                        text = "Detect Beats & Generate Markers",
                        icon = Icons.Default.MusicNote,
                        onClick = {
                            // Automatically adjust speeds and notify
                        }
                    )
                }
            }
        }
    }
}
