package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.ripple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

@Composable
fun RoyalTopBar(
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    onUndo: (() -> Unit)? = null,
    onRedo: (() -> Unit)? = null,
    onExport: (() -> Unit)? = null,
    onOpenSettings: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Surface(
        color = EgoBgSecondary,
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = EgoBorder, shape = RoundedCornerShape(0.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left section: Back or Logo + Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                if (onBack != null) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("top_bar_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = EgoGoldBright
                        )
                    }
                } else {
                    // Royal Monogram Logo
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.linearGradient(listOf(EgoGoldDark, EgoGoldBright))
                            )
                            .padding(1.5.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(EgoBgPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_ego_logo),
                            contentDescription = "EGO EDIT Logo",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }

                Column(modifier = Modifier.padding(end = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = EgoTextPrimary,
                                letterSpacing = 0.5.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = EgoGold,
                                fontWeight = FontWeight.Medium
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Right actions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (onUndo != null) {
                    IconButton(
                        onClick = onUndo,
                        modifier = Modifier.minimumInteractiveComponentSize().testTag("top_bar_undo_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Undo,
                            contentDescription = "Undo",
                            tint = EgoTextSecondary
                        )
                    }
                }
                if (onRedo != null) {
                    IconButton(
                        onClick = onRedo,
                        modifier = Modifier.minimumInteractiveComponentSize().testTag("top_bar_redo_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Redo,
                            contentDescription = "Redo",
                            tint = EgoTextSecondary
                        )
                    }
                }

                actions()

                if (onExport != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Button(
                        onClick = onExport,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EgoGold,
                            contentColor = EgoBgPrimary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("top_bar_export_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "EXPORT",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }

                if (onOpenSettings != null) {
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier.minimumInteractiveComponentSize().testTag("top_bar_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = EgoTextSecondary
                        )
                    }
                }
            }
        }
    }
}
