package com.example.ui.screens.registry

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.*
import com.example.ui.components.GoldButton
import com.example.ui.components.RoyalTopBar
import com.example.ui.theme.*

@Composable
fun FeatureRegistryScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedModuleId by remember { mutableStateOf<Int?>(null) } // null = All
    var selectedStatus by remember { mutableStateOf<FeatureStatus?>(null) }
    var detailFeature by remember { mutableStateOf<FeatureItem?>(null) }

    val allFeatures = remember { FeatureRegistryRepository.allFeatures }

    val filteredFeatures = remember(searchQuery, selectedModuleId, selectedStatus) {
        allFeatures.filter { f ->
            val matchModule = selectedModuleId == null || f.moduleId == selectedModuleId
            val matchStatus = selectedStatus == null || f.status == selectedStatus
            val matchSearch = searchQuery.isBlank() ||
                    f.name.contains(searchQuery, ignoreCase = true) ||
                    f.description.contains(searchQuery, ignoreCase = true) ||
                    f.moduleName.contains(searchQuery, ignoreCase = true)
            matchModule && matchStatus && matchSearch
        }
    }

    val implementedCount = remember { allFeatures.count { it.status == FeatureStatus.IMPLEMENTED } }
    val partialCount = remember { allFeatures.count { it.status == FeatureStatus.PARTIALLY_IMPLEMENTED } }
    val plannedCount = remember { allFeatures.count { it.status == FeatureStatus.PLANNED } }

    Scaffold(
        topBar = {
            RoyalTopBar(
                title = "FEATURE REGISTRY (650+)",
                subtitle = "26-Module Architecture & Roadmap",
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
            // Summary KPI Stats Strip
            Surface(
                color = EgoBgPanel,
                border = BorderStroke(1.dp, EgoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("TOTAL: 650+ FEATURES", color = EgoGoldBright, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatusMiniPill("Active: $implementedCount", EgoSuccess)
                        StatusMiniPill("Partial: $partialCount", EgoGoldBright)
                        StatusMiniPill("Planned: $plannedCount", EgoInfo)
                    }
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search 650+ features across 26 modules...", color = EgoTextMuted, fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = EgoTextSecondary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EgoGold,
                    unfocusedBorderColor = EgoBorder,
                    focusedTextColor = EgoTextPrimary,
                    unfocusedTextColor = EgoTextPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .height(48.dp)
                    .testTag("feature_registry_search_input")
            )

            // Status Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedStatus == null,
                    onClick = { selectedStatus = null },
                    label = { Text("All Statuses", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EgoGold,
                        selectedLabelColor = EgoBgPrimary,
                        containerColor = EgoBgElevated,
                        labelColor = EgoTextPrimary
                    )
                )
                FeatureStatus.values().forEach { st ->
                    FilterChip(
                        selected = selectedStatus == st,
                        onClick = { selectedStatus = st },
                        label = { Text(st.label, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(st.colorHex),
                            selectedLabelColor = EgoBgPrimary,
                            containerColor = EgoBgElevated,
                            labelColor = EgoTextPrimary
                        )
                    )
                }
            }

            // 26 Modules Carousel
            LazyRow(
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedModuleId == null,
                        onClick = { selectedModuleId = null },
                        label = { Text("All 26 Modules", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EgoGold,
                            selectedLabelColor = EgoBgPrimary,
                            containerColor = EgoBgElevated,
                            labelColor = EgoTextPrimary
                        )
                    )
                }
                items(FeatureRegistryRepository.modules) { (modId, modName) ->
                    FilterChip(
                        selected = selectedModuleId == modId,
                        onClick = { selectedModuleId = modId },
                        label = { Text("M$modId: $modName", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EgoGold,
                            selectedLabelColor = EgoBgPrimary,
                            containerColor = EgoBgElevated,
                            labelColor = EgoTextPrimary
                        )
                    )
                }
            }

            Divider(color = EgoBorder)

            // Feature List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredFeatures, key = { it.id }) { feat ->
                    Surface(
                        color = EgoBgPanel,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, EgoBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { detailFeature = feat }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = feat.name,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = EgoTextPrimary),
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = Color(feat.status.colorHex).copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp),
                                    border = BorderStroke(1.dp, Color(feat.status.colorHex))
                                ) {
                                    Text(
                                        text = feat.status.label,
                                        color = Color(feat.status.colorHex),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Module ${feat.moduleId}: ${feat.moduleName}",
                                color = EgoGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = feat.description,
                                color = EgoTextSecondary,
                                fontSize = 11.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }

    // Feature Detail Modal
    if (detailFeature != null) {
        val f = detailFeature!!
        Dialog(onDismissRequest = { detailFeature = null }) {
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
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = f.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = EgoGoldBright),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { detailFeature = null }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Close, null, tint = EgoTextSecondary)
                        }
                    }

                    Text("Module ${f.moduleId}: ${f.moduleName}", color = EgoGold, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        color = Color(f.status.colorHex).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, Color(f.status.colorHex))
                    ) {
                        Text(
                            text = "STATUS: ${f.status.label.uppercase()}",
                            color = Color(f.status.colorHex),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(f.description, color = EgoTextPrimary, fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(16.dp))
                    DetailRow("User Interface", f.uiComponent)
                    DetailRow("Core Processing Logic", f.coreLogic)
                    DetailRow("Dependencies", f.dependencies)
                    DetailRow("Supported Platforms", f.supportedPlatforms)
                    DetailRow("Performance Requirement", f.performanceReq)
                    DetailRow("Testing Criteria", f.testingCriteria)

                    Spacer(modifier = Modifier.height(20.dp))
                    GoldButton(
                        text = "Close Specification",
                        onClick = { detailFeature = null },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusMiniPill(label: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, color)
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun DetailRow(title: String, desc: String) {
    Column(modifier = Modifier.padding(bottom = 10.dp)) {
        Text(title.uppercase(), color = EgoGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(desc, color = EgoTextSecondary, fontSize = 12.sp)
    }
}
