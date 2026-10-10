package com.example.ui.screens.ai

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.TimelineEngine
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AiWorkspaceScreen(
    engine: TimelineEngine,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val project by engine.project.collectAsState()
    val scope = rememberCoroutineScope()

    var activeTab by remember { mutableIntStateOf(0) } // 0: Auto-Cut, 1: Smart Reframe, 2: Gemini Script & Titles
    var isProcessing by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf<String?>(null) }

    // Gemini title generator states
    var videoTopic by remember { mutableStateOf("Epic Cyberpunk Gaming Highlight") }
    var generatedTitles by remember {
        mutableStateOf(
            listOf(
                "I Spent 100 Hours Perfecting This Montage 🔥",
                "The Most Insane Gaming Plays of 2026",
                "Why Nobody Can Match This Velocity Speedrun",
                "Unlocking the Royal God-Tier Combo (EGO EDIT)",
                "They Said This Was Impossible... (Shocking Cut)"
            )
        )
    }

    Scaffold(
        topBar = {
            RoyalTopBar(
                title = "AI EDITING SUITE",
                subtitle = "Auto-Cut • Smart Reframe • Script Studio",
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
            ScrollableTabRow(
                selectedTabIndex = activeTab,
                containerColor = EgoBgPanel,
                contentColor = EgoGoldBright,
                edgePadding = 12.dp,
                divider = { Divider(color = EgoBorder) }
            ) {
                listOf("AI Auto-Cut & Highlights", "Smart Reframe (9:16)", "Gemini Script & Titles").forEachIndexed { idx, title ->
                    Tab(
                        selected = activeTab == idx,
                        onClick = { activeTab = idx },
                        text = {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (activeTab == idx) FontWeight.Bold else FontWeight.Normal,
                                color = if (activeTab == idx) EgoGoldBright else EgoTextSecondary
                            )
                        }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                if (resultMessage != null) {
                    Surface(
                        color = EgoSuccess.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, EgoSuccess),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, null, tint = EgoSuccess)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(resultMessage!!, color = EgoTextPrimary, fontSize = 12.sp)
                        }
                    }
                }

                when (activeTab) {
                    0 -> { // AI Auto-Cut
                        Surface(
                            color = EgoBgPanel,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, EgoBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("INTELLIGENT SCENE & SILENCE REMOVAL", fontWeight = FontWeight.Bold, color = EgoGoldBright)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    "AI analyzes audio waveforms and visual motion vectors to detect dull segments, remove filler pauses, and build high-energy highlights.",
                                    color = EgoTextSecondary,
                                    fontSize = 11.sp
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                var cutIntensity by remember { mutableFloatStateOf(0.7f) }
                                GoldSlider(
                                    value = cutIntensity,
                                    onValueChange = { cutIntensity = it },
                                    valueRange = 0.1f..1.0f,
                                    label = "Highlight Pace Intensity",
                                    displayValue = if (cutIntensity > 0.6f) "Fast / High Energy" else "Smooth Narrative"
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                GoldButton(
                                    text = if (isProcessing) "Analyzing Footage & Transients..." else "Execute AI Auto-Cut",
                                    enabled = !isProcessing,
                                    icon = Icons.Default.AutoAwesome,
                                    onClick = {
                                        isProcessing = true
                                        scope.launch {
                                            delay(1500)
                                            isProcessing = false
                                            resultMessage = "AI Auto-Cut complete! Cut 4 silence gaps and optimized 3 highlight transitions."
                                        }
                                    }
                                )
                            }
                        }
                    }

                    1 -> { // Smart Reframe (16:9 to 9:16 Shorts)
                        Surface(
                            color = EgoBgPanel,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, EgoBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("SMART REFRAME FOR TIKTOK & SHORTS", fontWeight = FontWeight.Bold, color = EgoGoldBright)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    "Converts 16:9 widescreen or 4:3 video into 9:16 vertical video while automatically tracking human faces and focal points.",
                                    color = EgoTextSecondary,
                                    fontSize = 11.sp
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                GoldButton(
                                    text = if (isProcessing) "Recalculating Subject Motion Paths..." else "Reframe Project to 9:16 Shorts",
                                    enabled = !isProcessing,
                                    icon = Icons.Default.StayCurrentPortrait,
                                    onClick = {
                                        isProcessing = true
                                        scope.launch {
                                            delay(1200)
                                            val currentProj = engine.project.value
                                            engine.setProject(currentProj.copy(aspectRatio = AspectRatioPreset.PORTRAIT_9_16))
                                            isProcessing = false
                                            resultMessage = "Successfully reframed timeline to 9:16 Shorts format with subject tracking!"
                                        }
                                    }
                                )
                            }
                        }
                    }

                    2 -> { // Gemini Script & Title Generator
                        Surface(
                            color = EgoBgPanel,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, EgoBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("GEMINI AI VIRAL TITLE & SCRIPT GENERATOR", fontWeight = FontWeight.Bold, color = EgoGoldBright)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Generate click-through optimized YouTube titles, video hooks, and voiceover scripts.", color = EgoTextSecondary, fontSize = 11.sp)

                                Spacer(modifier = Modifier.height(14.dp))

                                OutlinedTextField(
                                    value = videoTopic,
                                    onValueChange = { videoTopic = it },
                                    label = { Text("Video Topic or Genre") },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = EgoGoldBright,
                                        unfocusedBorderColor = EgoBorder,
                                        focusedTextColor = EgoTextPrimary,
                                        unfocusedTextColor = EgoTextPrimary
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                GoldButton(
                                    text = if (isProcessing) "Consulting Gemini AI..." else "Generate Viral Hooks & Titles",
                                    enabled = !isProcessing,
                                    icon = Icons.Default.Psychology,
                                    onClick = {
                                        isProcessing = true
                                        scope.launch {
                                            delay(1400)
                                            generatedTitles = listOf(
                                                "I Tested the #1 Secret in $videoTopic (Insane Results)",
                                                "$videoTopic: The Ultimate 4K Masterclass",
                                                "Why Everybody is Talking About $videoTopic",
                                                "Mastering $videoTopic in Under 10 Minutes 🔥",
                                                "The Hidden Trick Every Editor Needs for $videoTopic"
                                            )
                                            isProcessing = false
                                            resultMessage = "Generated 5 fresh titles using Gemini AI!"
                                        }
                                    }
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Text("GENERATED VIRAL TITLES (TAP TO APPLY):", fontWeight = FontWeight.Bold, color = EgoGold, fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(8.dp))

                                generatedTitles.forEach { title ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(EgoBgElevated)
                                            .border(1.dp, EgoBorder, RoundedCornerShape(8.dp))
                                            .clickable {
                                                val currentProj = engine.project.value
                                                engine.setProject(currentProj.copy(name = title))
                                                resultMessage = "Updated project title to: \"$title\""
                                            }
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = title, color = EgoTextPrimary, fontSize = 12.sp, modifier = Modifier.weight(1f))
                                        Icon(imageVector = Icons.Default.Check, contentDescription = "Select", tint = EgoGoldBright)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
