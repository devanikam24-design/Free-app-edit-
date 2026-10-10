package com.example.engine

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import kotlin.math.*

@Composable
fun VideoPreviewCanvas(
    project: Project,
    currentTimeMs: Long,
    showSafeGuides: Boolean,
    showGrid: Boolean,
    isSplitComparison: Boolean,
    previewResolution: String,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Maintain exact aspect ratio
        Box(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(project.aspectRatio.ratio, matchHeightConstraintsFirst = false)
                .background(EgoBgPrimary)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Draw each visible track from bottom to top
                project.tracks.filter { it.isVisible }.forEach { track ->
                    // Find active clip at currentTimeMs
                    val activeClip = track.clips.find { clip ->
                        currentTimeMs >= clip.startTimeMs && currentTimeMs <= (clip.startTimeMs + clip.durationMs)
                    }

                    if (activeClip != null) {
                        val clipElapsed = currentTimeMs - activeClip.startTimeMs
                        val clipDuration = activeClip.durationMs

                        // Calculate transition opacity/progress
                        var transitionAlpha = 1.0f
                        if (activeClip.transitionIn != TransitionType.NONE && clipElapsed < activeClip.transitionIn.durationMs) {
                            transitionAlpha = (clipElapsed.toFloat() / activeClip.transitionIn.durationMs).coerceIn(0f, 1f)
                        } else if (activeClip.transitionOut != TransitionType.NONE && (clipDuration - clipElapsed) < activeClip.transitionOut.durationMs) {
                            val rem = clipDuration - clipElapsed
                            transitionAlpha = (rem.toFloat() / activeClip.transitionOut.durationMs).coerceIn(0f, 1f)
                        }

                        val overallAlpha = (activeClip.transform.opacity * transitionAlpha).coerceIn(0f, 1f)

                        // Render scene content with transform
                        withTransform({
                            val transX = activeClip.transform.posX * (canvasWidth / 2f)
                            val transY = activeClip.transform.posY * (canvasHeight / 2f)
                            translate(transX, transY)
                            scale(activeClip.transform.scaleX, activeClip.transform.scaleY, pivot = center)
                            rotate(activeClip.transform.rotation, pivot = center)
                        }) {
                            if (isSplitComparison) {
                                // Draw Left: Raw / Neutral
                                clipRect(left = 0f, top = 0f, right = canvasWidth / 2f, bottom = canvasHeight) {
                                    drawVisualContent(
                                        visualType = activeClip.placeholderVisual,
                                        elapsedMs = clipElapsed,
                                        colorGrade = ColorGrade(), // Neutral raw
                                        filter = VideoFilterType.NONE,
                                        textOverlay = activeClip.textOverlay,
                                        alpha = overallAlpha,
                                        textMeasurer = textMeasurer
                                    )
                                }
                                // Draw Right: Graded
                                clipRect(left = canvasWidth / 2f, top = 0f, right = canvasWidth, bottom = canvasHeight) {
                                    drawVisualContent(
                                        visualType = activeClip.placeholderVisual,
                                        elapsedMs = clipElapsed,
                                        colorGrade = activeClip.colorGrade,
                                        filter = activeClip.filter,
                                        textOverlay = activeClip.textOverlay,
                                        alpha = overallAlpha,
                                        textMeasurer = textMeasurer
                                    )
                                }
                                // Divider Line
                                drawLine(
                                    color = EgoGoldBright,
                                    start = Offset(canvasWidth / 2f, 0f),
                                    end = Offset(canvasWidth / 2f, canvasHeight),
                                    strokeWidth = 2.dp.toPx()
                                )
                            } else {
                                drawVisualContent(
                                    visualType = activeClip.placeholderVisual,
                                    elapsedMs = clipElapsed,
                                    colorGrade = activeClip.colorGrade,
                                    filter = activeClip.filter,
                                    textOverlay = activeClip.textOverlay,
                                    alpha = overallAlpha,
                                    textMeasurer = textMeasurer
                                )
                            }
                        }
                    }
                }

                // Draw Safe Guides if enabled
                if (showSafeGuides) {
                    drawSafeGuides(canvasWidth, canvasHeight)
                }

                // Draw Rule-of-Thirds Grid if enabled
                if (showGrid) {
                    drawGridOverlay(canvasWidth, canvasHeight)
                }
            }
        }
    }
}

private fun DrawScope.drawVisualContent(
    visualType: String,
    elapsedMs: Long,
    colorGrade: ColorGrade,
    filter: VideoFilterType,
    textOverlay: TextOverlay?,
    alpha: Float,
    textMeasurer: TextMeasurer
) {
    val t = elapsedMs / 1000f

    // Base color tones modified by LUT & color grading
    val (baseSky, baseGround, accentColor) = computeGradedPalette(colorGrade)

    when (visualType) {
        "cinematic_drone" -> {
            // Majestic mountain landscape with sunset sky and rays
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(baseSky, accentColor.copy(alpha = 0.5f), baseGround)
                ),
                alpha = alpha
            )
            // Mountain peaks
            val p1 = Path().apply {
                moveTo(0f, size.height * 0.7f)
                lineTo(size.width * 0.3f, size.height * 0.35f + sin(t) * 10f)
                lineTo(size.width * 0.55f, size.height * 0.55f)
                lineTo(size.width * 0.85f, size.height * 0.3f)
                lineTo(size.width, size.height * 0.65f)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(p1, color = baseGround.copy(alpha = 0.85f * alpha))

            // Golden Sun with flare
            val sunPos = Offset(size.width * 0.85f, size.height * 0.25f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(EgoGoldBright.copy(alpha = 0.9f * alpha), Color.Transparent),
                    center = sunPos,
                    radius = size.width * 0.25f
                ),
                center = sunPos,
                radius = size.width * 0.25f
            )
        }

        "golden_palace" -> {
            // Luxury Obsidian and 24K Gold Palace Facade
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0D0F14), Color(0xFF1B1E26), Color(0xFF08090C))
                ),
                alpha = alpha
            )
            // Architectural Gold Pillars
            val pillarCount = 5
            val spacing = size.width / (pillarCount + 1)
            for (i in 1..pillarCount) {
                val px = i * spacing
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(EgoGoldDark, EgoGoldBright, EgoGoldDark)
                    ),
                    topLeft = Offset(px - 14f, size.height * 0.2f),
                    size = Size(28f, size.height * 0.8f),
                    alpha = alpha * 0.9f
                )
            }
            // Royal Geometric Emblem in center
            val emblemRadius = size.height * 0.18f
            drawCircle(
                color = EgoGold.copy(alpha = 0.3f * alpha),
                center = center,
                radius = emblemRadius,
                style = Stroke(width = 3.dp.toPx())
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(EgoGoldBright.copy(alpha = 0.6f * alpha), Color.Transparent)
                ),
                center = center,
                radius = emblemRadius * 0.8f
            )
        }

        "cyber_city" -> {
            // Futuristic Tokyo Cyberpunk with Neon Grid
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF050510), Color(0xFF150A2A), Color(0xFF020208))
                ),
                alpha = alpha
            )
            // Skyscrapers silhouettes
            val buildings = listOf(0.1f to 0.5f, 0.28f to 0.35f, 0.45f to 0.6f, 0.62f to 0.3f, 0.82f to 0.45f)
            buildings.forEach { (xPct, hPct) ->
                drawRect(
                    color = Color(0xFF0D0B18).copy(alpha = alpha),
                    topLeft = Offset(size.width * xPct - 40f, size.height * (1f - hPct)),
                    size = Size(80f, size.height * hPct)
                )
            }
            // Neon horizontal highway grid
            for (i in 1..8) {
                val gy = size.height * 0.7f + i * (size.height * 0.035f)
                drawLine(
                    color = if (i % 2 == 0) Color(0xFF38BDF8).copy(alpha = 0.4f * alpha) else Color(0xFFFF007F).copy(alpha = 0.4f * alpha),
                    start = Offset(0f, gy),
                    end = Offset(size.width, gy),
                    strokeWidth = (i * 0.8f).coerceAtLeast(1f)
                )
            }
        }

        "gaming_action" -> {
            // High octane gaming montage visual with HUD
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF1A1A24), Color(0xFF0A0A0E))
                ),
                alpha = alpha
            )
            // Dynamic HUD crosshair
            drawCircle(
                color = EgoGoldBright.copy(alpha = 0.7f * alpha),
                center = center,
                radius = 35.dp.toPx(),
                style = Stroke(width = 2.dp.toPx())
            )
            drawLine(
                color = EgoGoldBright.copy(alpha = 0.8f * alpha),
                start = Offset(center.x - 50.dp.toPx(), center.y),
                end = Offset(center.x + 50.dp.toPx(), center.y),
                strokeWidth = 1.5.dp.toPx()
            )
            drawLine(
                color = EgoGoldBright.copy(alpha = 0.8f * alpha),
                start = Offset(center.x, center.y - 50.dp.toPx()),
                end = Offset(center.x, center.y + 50.dp.toPx()),
                strokeWidth = 1.5.dp.toPx()
            )
            // Animated impact speed lines
            val lineCount = 12
            for (i in 0 until lineCount) {
                val angle = (i * (360f / lineCount) + t * 40f) * (PI.toFloat() / 180f)
                val r1 = size.width * 0.35f
                val r2 = size.width * 0.48f
                drawLine(
                    color = Color.White.copy(alpha = 0.25f * alpha),
                    start = Offset(center.x + cos(angle) * r1, center.y + sin(angle) * r1),
                    end = Offset(center.x + cos(angle) * r2, center.y + sin(angle) * r2),
                    strokeWidth = 2.dp.toPx()
                )
            }
        }

        "gold_particles" -> {
            // Floating 24k gold embers and spark overlay
            val particleCount = 28
            for (i in 0 until particleCount) {
                val seed = i * 137.5f
                val px = (sin(seed + t * 0.8f) * 0.5f + 0.5f) * size.width
                val py = ((seed * 11.3f + t * 60f) % size.height)
                val rad = (sin(seed * 0.3f) * 2f + 3.5f).dp.toPx()
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(EgoGoldLight.copy(alpha = 0.85f * alpha), Color.Transparent)
                    ),
                    center = Offset(px, py),
                    radius = rad * 1.8f
                )
            }
        }

        "audio_waveform" -> {
            // High fidelity oscilloscope wave
            drawRect(color = Color(0xFF0B1410).copy(alpha = alpha))
            val wavePoints = 40
            for (i in 0 until wavePoints) {
                val wx = (i.toFloat() / wavePoints) * size.width
                val amp = (sin(i * 0.4f + t * 5f) * 0.5f + 0.5f) * (size.height * 0.4f)
                drawLine(
                    color = EgoSuccess.copy(alpha = 0.75f * alpha),
                    start = Offset(wx, center.y - amp / 2f),
                    end = Offset(wx, center.y + amp / 2f),
                    strokeWidth = 3.dp.toPx()
                )
            }
        }

        else -> {
            drawRect(color = Color(0xFF14171F).copy(alpha = alpha))
        }
    }

    // Apply Video Filter Overlays
    when (filter) {
        VideoFilterType.VIGNETTE -> {
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color.Transparent, Color(0xDD000000)),
                    center = center,
                    radius = size.width * 0.65f
                ),
                alpha = alpha
            )
        }
        VideoFilterType.BLOOM_GLOW -> {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(EgoGoldBright.copy(alpha = 0.3f * alpha), Color.Transparent),
                    center = center,
                    radius = size.width * 0.55f
                ),
                center = center,
                radius = size.width * 0.55f
            )
        }
        VideoFilterType.FILM_GRAIN -> {
            // Simulated grain points
            for (i in 0..120) {
                val gx = (sin(i * 91.1f + t * 123f) * 0.5f + 0.5f) * size.width
                val gy = (cos(i * 47.3f + t * 87f) * 0.5f + 0.5f) * size.height
                drawCircle(
                    color = Color.White.copy(alpha = 0.12f * alpha),
                    center = Offset(gx, gy),
                    radius = 1.dp.toPx()
                )
            }
        }
        VideoFilterType.VHS_RETRO -> {
            // Scanlines
            for (y in 0 until size.height.toInt() step 8) {
                drawLine(
                    color = Color.Black.copy(alpha = 0.28f * alpha),
                    start = Offset(0f, y.toFloat()),
                    end = Offset(size.width, y.toFloat()),
                    strokeWidth = 1.dp.toPx()
                )
            }
            // VHS OSD Text
            val vhsResult = textMeasurer.measure(
                AnnotatedString("PLAY ► SP  00:0${(elapsedMs / 1000)}:24"),
                style = TextStyle(color = Color(0xFF00FF7F), fontSize = 14.sp)
            )
            drawText(vhsResult, topLeft = Offset(24.dp.toPx(), 24.dp.toPx()))
        }
        VideoFilterType.CYBER_GLITCH -> {
            // Horizontal displacement slice
            val sliceY = ((t * 240f) % size.height)
            drawRect(
                color = Color(0xFFFF0055).copy(alpha = 0.35f * alpha),
                topLeft = Offset(0f, sliceY),
                size = Size(size.width, 18.dp.toPx())
            )
            drawRect(
                color = Color(0xFF00E5FF).copy(alpha = 0.35f * alpha),
                topLeft = Offset(8.dp.toPx(), (sliceY + 24.dp.toPx()) % size.height),
                size = Size(size.width, 12.dp.toPx())
            )
        }
        VideoFilterType.NEON_EDGE -> {
            drawRect(
                color = Color(0xFF00FFFF).copy(alpha = 0.5f * alpha),
                topLeft = Offset(4f, 4f),
                size = Size(size.width - 8f, size.height - 8f),
                style = Stroke(width = 3.dp.toPx())
            )
        }
        VideoFilterType.OLD_FILM -> {
            drawRect(
                color = Color(0x358B5A2B), // Sepia overlay
                alpha = alpha
            )
        }
        else -> {}
    }

    // Render Text Overlay if present
    if (textOverlay != null && textOverlay.text.isNotBlank()) {
        val fullText = textOverlay.text
        val displayText = if (textOverlay.animation == "Typewriter") {
            val charsToShow = ((elapsedMs / 120L).toInt()).coerceIn(0, fullText.length)
            fullText.take(charsToShow)
        } else {
            fullText
        }

        if (displayText.isNotEmpty()) {
            val measured = textMeasurer.measure(
                AnnotatedString(displayText),
                style = TextStyle(
                    color = Color(textOverlay.fontColorHex),
                    fontSize = textOverlay.fontSizeSp.sp,
                    fontWeight = FontWeight.Bold,
                    shadow = if (textOverlay.hasGlow) Shadow(
                        color = EgoGoldBright,
                        offset = Offset(0f, 2f),
                        blurRadius = 12f
                    ) else null
                )
            )
            val tx = center.x - (measured.size.width / 2f)
            val ty = center.y - (measured.size.height / 2f)
            drawText(measured, topLeft = Offset(tx, ty))
        }
    }
}

private fun computeGradedPalette(cg: ColorGrade): Triple<Color, Color, Color> {
    // Base colors adjusted by LUT & Grading
    return when (cg.lutPreset) {
        "Royal Gold" -> Triple(Color(0xFF141008), Color(0xFF0A0702), EgoGoldBright)
        "Golden Hour" -> Triple(Color(0xFF381E0F), Color(0xFF120803), Color(0xFFFFB049))
        "Teal & Orange" -> Triple(Color(0xFF07212B), Color(0xFF030C10), Color(0xFFFF7A00))
        "Cyberpunk" -> Triple(Color(0xFF1F092E), Color(0xFF07020E), Color(0xFF00FFFF))
        "Monochrome Noir" -> Triple(Color(0xFF222222), Color(0xFF080808), Color(0xFF888888))
        else -> Triple(Color(0xFF181C26), Color(0xFF090B0F), EgoGold)
    }
}

private fun DrawScope.drawSafeGuides(w: Float, h: Float) {
    // Action Safe: 90%
    val asW = w * 0.9f
    val asH = h * 0.9f
    val asLeft = (w - asW) / 2f
    val asTop = (h - asH) / 2f
    drawRect(
        color = Color(0x66FFD978),
        topLeft = Offset(asLeft, asTop),
        size = Size(asW, asH),
        style = Stroke(width = 1.dp.toPx())
    )

    // Title Safe: 80%
    val tsW = w * 0.8f
    val tsH = h * 0.8f
    val tsLeft = (w - tsW) / 2f
    val tsTop = (h - tsH) / 2f
    drawRect(
        color = Color(0xAA38BDF8),
        topLeft = Offset(tsLeft, tsTop),
        size = Size(tsW, tsH),
        style = Stroke(width = 1.dp.toPx())
    )
}

private fun DrawScope.drawGridOverlay(w: Float, h: Float) {
    val col1 = w / 3f
    val col2 = w * 2f / 3f
    val row1 = h / 3f
    val row2 = h * 2f / 3f

    val gridColor = Color(0x44FFFFFF)
    drawLine(gridColor, Offset(col1, 0f), Offset(col1, h), strokeWidth = 1.dp.toPx())
    drawLine(gridColor, Offset(col2, 0f), Offset(col2, h), strokeWidth = 1.dp.toPx())
    drawLine(gridColor, Offset(0f, row1), Offset(w, row1), strokeWidth = 1.dp.toPx())
    drawLine(gridColor, Offset(0f, row2), Offset(w, row2), strokeWidth = 1.dp.toPx())
}
