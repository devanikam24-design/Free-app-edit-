package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun QuickToolbar(
    onSplit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: (ripple: Boolean) -> Unit,
    onAddMedia: () -> Unit,
    onAddText: () -> Unit,
    onAddAudio: () -> Unit,
    onOpenColor: () -> Unit,
    onOpenVfx: () -> Unit,
    onOpenAudio: () -> Unit,
    hasSelection: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        color = EgoBgPanel,
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, EgoBorder, RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Split Tool
            ToolButton(
                icon = Icons.Default.ContentCut,
                label = "Split",
                onClick = onSplit,
                enabled = hasSelection,
                tint = EgoGoldBright,
                testTag = "toolbar_split_button"
            )

            // Duplicate Tool
            ToolButton(
                icon = Icons.Default.ContentCopy,
                label = "Duplicate",
                onClick = onDuplicate,
                enabled = hasSelection,
                tint = EgoTextPrimary,
                testTag = "toolbar_duplicate_button"
            )

            // Delete Tool
            ToolButton(
                icon = Icons.Default.DeleteOutline,
                label = "Delete",
                onClick = { onDelete(false) },
                enabled = hasSelection,
                tint = EgoError,
                testTag = "toolbar_delete_button"
            )

            // Ripple Delete Tool
            ToolButton(
                icon = Icons.Default.DeleteSweep,
                label = "Ripple Del",
                onClick = { onDelete(true) },
                enabled = hasSelection,
                tint = EgoWarning,
                testTag = "toolbar_ripple_delete_button"
            )

            Divider(
                color = EgoBorder,
                modifier = Modifier
                    .height(28.dp)
                    .width(1.dp)
                    .padding(horizontal = 4.dp)
            )

            // Add Media
            ToolButton(
                icon = Icons.Default.VideoLibrary,
                label = "+ Media",
                onClick = onAddMedia,
                tint = EgoInfo,
                testTag = "toolbar_add_media_button"
            )

            // Add Text
            ToolButton(
                icon = Icons.Default.TextFields,
                label = "+ Text",
                onClick = onAddText,
                tint = EgoGoldBright,
                testTag = "toolbar_add_text_button"
            )

            // Add Audio
            ToolButton(
                icon = Icons.Default.MusicNote,
                label = "+ Audio",
                onClick = onAddAudio,
                tint = EgoSuccess,
                testTag = "toolbar_add_audio_button"
            )

            Divider(
                color = EgoBorder,
                modifier = Modifier
                    .height(28.dp)
                    .width(1.dp)
                    .padding(horizontal = 4.dp)
            )

            // Open Studios
            ToolButton(
                icon = Icons.Default.Palette,
                label = "Color Grading",
                onClick = onOpenColor,
                tint = EgoGold,
                testTag = "toolbar_color_grade_button"
            )

            ToolButton(
                icon = Icons.Default.AutoAwesome,
                label = "VFX Studio",
                onClick = onOpenVfx,
                tint = EgoPurple,
                testTag = "toolbar_vfx_button"
            )

            ToolButton(
                icon = Icons.Default.GraphicEq,
                label = "Audio Studio",
                onClick = onOpenAudio,
                tint = EgoSuccess,
                testTag = "toolbar_audio_studio_button"
            )
        }
    }
}

@Composable
private fun ToolButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    tint: Color = EgoTextPrimary,
    testTag: String
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = EgoBgElevated,
            contentColor = tint,
            disabledContainerColor = EgoBgSecondary,
            disabledContentColor = EgoTextMuted
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
        modifier = Modifier
            .height(36.dp)
            .testTag(testTag)
    ) {
        Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, fontSize = 11.sp, maxLines = 1)
    }
}
