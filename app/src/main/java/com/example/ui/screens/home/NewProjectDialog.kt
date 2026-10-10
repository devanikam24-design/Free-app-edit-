package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.*
import com.example.ui.components.GoldButton
import com.example.ui.components.GoldOutlinedButton
import com.example.ui.theme.*

@Composable
fun NewProjectDialog(
    onDismiss: () -> Unit,
    onCreateProject: (name: String, aspect: AspectRatioPreset, res: ResolutionPreset, fps: FpsPreset) -> Unit
) {
    var projectName by remember { mutableStateOf("New Cinematic Master") }
    var selectedAspect by remember { mutableStateOf(AspectRatioPreset.LANDSCAPE_16_9) }
    var selectedRes by remember { mutableStateOf(ResolutionPreset.FHD_1080P) }
    var selectedFps by remember { mutableStateOf(FpsPreset.FPS_30) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, EgoGold, RoundedCornerShape(16.dp)),
            color = EgoBgPanel
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "CREATE NEW MASTER PROJECT",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = EgoGoldBright,
                        letterSpacing = 1.sp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = projectName,
                    onValueChange = { projectName = it },
                    label = { Text("Project Title") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EgoGoldBright,
                        unfocusedBorderColor = EgoBorder,
                        focusedTextColor = EgoTextPrimary,
                        unfocusedTextColor = EgoTextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("new_project_name_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Aspect Ratio Selector
                Text("Aspect Ratio", style = MaterialTheme.typography.labelSmall, color = EgoTextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AspectRatioPreset.values().take(3).forEach { aspect ->
                        FilterChip(
                            selected = selectedAspect == aspect,
                            onClick = { selectedAspect = aspect },
                            label = { Text(aspect.label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EgoGold,
                                selectedLabelColor = EgoBgPrimary,
                                containerColor = EgoBgElevated,
                                labelColor = EgoTextPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AspectRatioPreset.values().drop(3).forEach { aspect ->
                        FilterChip(
                            selected = selectedAspect == aspect,
                            onClick = { selectedAspect = aspect },
                            label = { Text(aspect.label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EgoGold,
                                selectedLabelColor = EgoBgPrimary,
                                containerColor = EgoBgElevated,
                                labelColor = EgoTextPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Resolution
                Text("Resolution", style = MaterialTheme.typography.labelSmall, color = EgoTextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(ResolutionPreset.HD_720P, ResolutionPreset.FHD_1080P, ResolutionPreset.UHD_4K).forEach { res ->
                        FilterChip(
                            selected = selectedRes == res,
                            onClick = { selectedRes = res },
                            label = { Text(res.label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EgoGold,
                                selectedLabelColor = EgoBgPrimary,
                                containerColor = EgoBgElevated,
                                labelColor = EgoTextPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Frame Rate
                Text("Frame Rate", style = MaterialTheme.typography.labelSmall, color = EgoTextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FpsPreset.values().take(3).forEach { fps ->
                        FilterChip(
                            selected = selectedFps == fps,
                            onClick = { selectedFps = fps },
                            label = { Text(fps.label.take(6), fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EgoGold,
                                selectedLabelColor = EgoBgPrimary,
                                containerColor = EgoBgElevated,
                                labelColor = EgoTextPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    GoldOutlinedButton(
                        text = "Cancel",
                        onClick = onDismiss
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    GoldButton(
                        text = "Create Master",
                        onClick = {
                            onCreateProject(projectName, selectedAspect, selectedRes, selectedFps)
                        },
                        testTag = "create_project_confirm_button"
                    )
                }
            }
        }
    }
}
