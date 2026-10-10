package com.example.ui.screens.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.GoldButton
import com.example.ui.components.RoyalTopBar
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    projects: List<Project>,
    onOpenProject: (Project) -> Unit,
    onCreateProject: (name: String, aspect: AspectRatioPreset, res: ResolutionPreset, fps: FpsPreset) -> Unit,
    onDuplicateProject: (String) -> Unit,
    onDeleteProject: (String) -> Unit,
    onNavigateToStudio: (String) -> Unit, // "color", "vfx", "audio", "ai", "subtitles", "thumbnail", "templates", "registry", "settings"
    modifier: Modifier = Modifier
) {
    var showNewProjectDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var projectToDelete by remember { mutableStateOf<Project?>(null) }

    val filteredProjects = remember(projects, searchQuery) {
        if (searchQuery.isBlank()) projects
        else projects.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            RoyalTopBar(
                title = "EGO EDIT",
                subtitle = "PROFESSIONAL STUDIO",
                onOpenSettings = { onNavigateToStudio("settings") },
                actions = {
                    IconButton(
                        onClick = { onNavigateToStudio("registry") },
                        modifier = Modifier.testTag("home_feature_registry_button")
                    ) {
                        Badge(
                            containerColor = EgoGold,
                            contentColor = EgoBgPrimary
                        ) {
                            Text("650+", fontWeight = FontWeight.Bold, fontSize = 9.sp)
                        }
                    }
                }
            )
        },
        containerColor = EgoBgPrimary,
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Hero Banner & Quick Actions
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(EgoBgElevated, EgoBgPanel)
                            )
                        )
                        .border(
                            1.dp,
                            Brush.linearGradient(listOf(EgoGoldDark, EgoGoldBright, EgoGoldDark)),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "ROYAL VIDEO ENGINE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = EgoGoldBright,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 2.sp
                                    )
                                )
                                Text(
                                    text = "Create Without Limits",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        color = EgoTextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Button(
                                onClick = { showNewProjectDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EgoGold,
                                    contentColor = EgoBgPrimary
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("home_new_project_hero_button")
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("NEW MASTER", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "650+ production editing features • 4K HDR mastering • DaVinci Color Wheels • Audio EQ • AI Reframe",
                            style = MaterialTheme.typography.bodySmall,
                            color = EgoTextSecondary
                        )
                    }
                }
            }

            // Quick Create Templates Carousel
            item {
                Text(
                    text = "QUICK CREATION PRESETS",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = EgoGold,
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    item {
                        QuickActionCard(
                            title = "AI Auto-Edit",
                            desc = "Smart highlight cut",
                            icon = Icons.Default.AutoAwesome,
                            color = EgoGoldBright,
                            onClick = { onNavigateToStudio("ai") }
                        )
                    }
                    item {
                        QuickActionCard(
                            title = "Gaming Montage",
                            desc = "60 FPS velocity beats",
                            icon = Icons.Default.SportsEsports,
                            color = Color(0xFF60A5FA),
                            onClick = { onNavigateToStudio("templates") }
                        )
                    }
                    item {
                        QuickActionCard(
                            title = "Anime Edit",
                            desc = "Impact speed lines & shake",
                            icon = Icons.Default.FlashOn,
                            color = Color(0xFFF472B6),
                            onClick = { onNavigateToStudio("templates") }
                        )
                    }
                    item {
                        QuickActionCard(
                            title = "Cinematic Film",
                            desc = "24 FPS 2.39:1 letterbox",
                            icon = Icons.Default.Movie,
                            color = EgoGold,
                            onClick = {
                                onCreateProject("Cinematic Master 24fps", AspectRatioPreset.CINEMA_21_9, ResolutionPreset.UHD_4K, FpsPreset.FPS_24)
                            }
                        )
                    }
                    item {
                        QuickActionCard(
                            title = "Shorts / Reels",
                            desc = "9:16 vertical hook",
                            icon = Icons.Default.Smartphone,
                            color = EgoSuccess,
                            onClick = {
                                onCreateProject("Viral Reel", AspectRatioPreset.PORTRAIT_9_16, ResolutionPreset.FHD_1080P, FpsPreset.FPS_60)
                            }
                        )
                    }
                    item {
                        QuickActionCard(
                            title = "Thumbnail Studio",
                            desc = "High-CTR 3D graphic cover",
                            icon = Icons.Default.Image,
                            color = EgoPurple,
                            onClick = { onNavigateToStudio("thumbnail") }
                        )
                    }
                }
            }

            // Studio Hub (Direct Access to 8 Specialized Studios)
            item {
                Text(
                    text = "STUDIO WORKSPACES",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = EgoGold,
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StudioLauncherCard(
                        title = "Color Studio",
                        subtitle = "DaVinci 3-Way",
                        icon = Icons.Default.Palette,
                        onClick = { onNavigateToStudio("color") },
                        modifier = Modifier.weight(1f)
                    )
                    StudioLauncherCard(
                        title = "VFX & Motion",
                        subtitle = "Keyframes & Green",
                        icon = Icons.Default.Flare,
                        onClick = { onNavigateToStudio("vfx") },
                        modifier = Modifier.weight(1f)
                    )
                    StudioLauncherCard(
                        title = "Audio Studio",
                        subtitle = "5-Band EQ & Mic",
                        icon = Icons.Default.GraphicEq,
                        onClick = { onNavigateToStudio("audio") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StudioLauncherCard(
                        title = "AI Suite",
                        subtitle = "Scripts & Reframe",
                        icon = Icons.Default.Psychology,
                        onClick = { onNavigateToStudio("ai") },
                        modifier = Modifier.weight(1f)
                    )
                    StudioLauncherCard(
                        title = "Subtitles",
                        subtitle = "Karaoke & SRT",
                        icon = Icons.Default.Subtitles,
                        onClick = { onNavigateToStudio("subtitles") },
                        modifier = Modifier.weight(1f)
                    )
                    StudioLauncherCard(
                        title = "650+ Registry",
                        subtitle = "All 26 Modules",
                        icon = Icons.Default.FactCheck,
                        onClick = { onNavigateToStudio("registry") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Recent Projects Header & Search
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RECENT MASTER PROJECTS (${filteredProjects.size})",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = EgoGold,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "Autosaved",
                        style = MaterialTheme.typography.labelSmall,
                        color = EgoSuccess
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search projects...", color = EgoTextMuted, fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = EgoTextSecondary)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EgoGold,
                        unfocusedBorderColor = EgoBorder,
                        focusedTextColor = EgoTextPrimary,
                        unfocusedTextColor = EgoTextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(50.dp)
                        .testTag("home_search_projects_input")
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Recent Projects List
            items(filteredProjects, key = { it.id }) { project ->
                ProjectItemCard(
                    project = project,
                    onOpen = { onOpenProject(project) },
                    onDuplicate = { onDuplicateProject(project.id) },
                    onDelete = { projectToDelete = project },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }
    }

    if (showNewProjectDialog) {
        NewProjectDialog(
            onDismiss = { showNewProjectDialog = false },
            onCreateProject = { name, aspect, res, fps ->
                showNewProjectDialog = false
                onCreateProject(name, aspect, res, fps)
            }
        )
    }

    // Delete Confirmation
    if (projectToDelete != null) {
        AlertDialog(
            onDismissRequest = { projectToDelete = null },
            title = { Text("Delete Master Project?", color = EgoTextPrimary) },
            text = { Text("This will permanently remove '${projectToDelete?.name}' and its autosaved snapshots.", color = EgoTextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        projectToDelete?.let { onDeleteProject(it.id) }
                        projectToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EgoError)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { projectToDelete = null }) {
                    Text("Cancel", color = EgoTextSecondary)
                }
            },
            containerColor = EgoBgPanel
        )
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    desc: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(135.dp)
            .height(110.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(EgoBgPanel)
            .border(1.dp, EgoBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, color = EgoTextPrimary, fontSize = 12.sp, maxLines = 1)
                Text(text = desc, color = EgoTextMuted, fontSize = 10.sp, maxLines = 1)
            }
        }
    }
}

@Composable
private fun StudioLauncherCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(72.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(EgoBgElevated)
            .border(1.dp, EgoBorder, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = null, tint = EgoGoldBright, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = title, fontWeight = FontWeight.Bold, color = EgoTextPrimary, fontSize = 11.sp, maxLines = 1)
            Text(text = subtitle, color = EgoTextMuted, fontSize = 9.sp, maxLines = 1)
        }
    }
}

@Composable
private fun ProjectItemCard(
    project: Project,
    onOpen: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateStr = remember(project.lastEditedMs) {
        SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault()).format(Date(project.lastEditedMs))
    }
    val durStr = remember(project.totalDurationMs) {
        val sec = project.totalDurationMs / 1000
        "${sec / 60}m ${sec % 60}s"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(EgoBgPanel)
            .border(1.dp, EgoBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onOpen)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Aspect Ratio badge box
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(EgoBgElevated)
                    .border(1.dp, EgoBorder, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.Movie, contentDescription = null, tint = EgoGold, modifier = Modifier.size(18.dp))
                    Text(
                        text = project.aspectRatio.label.take(4),
                        style = MaterialTheme.typography.labelSmall,
                        color = EgoTextMuted,
                        fontSize = 9.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = project.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = EgoTextPrimary),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${project.resolution.label} • ${project.fps.label.take(6)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = EgoGold
                    )
                    Text(
                        text = "• $durStr",
                        style = MaterialTheme.typography.labelSmall,
                        color = EgoTextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Edited $dateStr",
                    style = MaterialTheme.typography.labelSmall,
                    color = EgoTextMuted,
                    fontSize = 10.sp
                )
            }

            // Quick Actions: Duplicate & Delete
            Row {
                IconButton(onClick = onDuplicate, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = EgoTextSecondary, modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Delete", tint = EgoError, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
