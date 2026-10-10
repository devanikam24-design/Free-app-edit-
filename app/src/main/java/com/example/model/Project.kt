package com.example.model

import java.util.UUID

enum class AspectRatioPreset(val label: String, val ratio: Float, val width: Int, val height: Int) {
    LANDSCAPE_16_9("16:9 Cinema", 16f / 9f, 1920, 1080),
    PORTRAIT_9_16("9:16 Shorts/TikTok", 9f / 16f, 1080, 1920),
    SQUARE_1_1("1:1 Square", 1f, 1080, 1080),
    PORTRAIT_4_5("4:5 Instagram", 4f / 5f, 1080, 1350),
    CINEMA_21_9("21:9 Anamorphic", 21f / 9f, 2560, 1080)
}

enum class ResolutionPreset(val label: String, val width: Int, val height: Int) {
    HD_720P("720p HD", 1280, 720),
    FHD_1080P("1080p Full HD", 1920, 1080),
    QHD_1440P("1440p 2K QHD", 2560, 1440),
    UHD_4K("4K Ultra HD", 3840, 2160),
    UHD_8K("8K Cinema Master", 7680, 4320)
}

enum class FpsPreset(val label: String, val fps: Int) {
    FPS_24("24 fps (Cinematic Film)", 24),
    FPS_30("30 fps (Standard Video)", 30),
    FPS_60("60 fps (Smooth Gaming/Action)", 60),
    FPS_120("120 fps (Super Slow-Mo)", 120)
}

enum class TrackType(val label: String, val colorHex: Long) {
    VIDEO("Video V1-V3", 0xFF1E3A8A),
    AUDIO("Audio A1-A2", 0xFF065F46),
    TEXT("Text & Titles", 0xFF854D0E),
    OVERLAY("VFX & Overlays", 0xFF581C87)
}

enum class BlendModeType(val label: String) {
    NORMAL("Normal"),
    SCREEN("Screen (Brighten)"),
    MULTIPLY("Multiply (Darken)"),
    OVERLAY("Overlay (Contrast)"),
    ADD("Add / Linear Dodge"),
    DIFFERENCE("Difference (Invert)")
}

enum class VideoFilterType(val label: String, val category: String) {
    NONE("Original (No FX)", "Basic"),
    GAUSSIAN_BLUR("Gaussian Blur", "Blur"),
    BLOOM_GLOW("Royal Bloom & Glow", "Lighting"),
    FILM_GRAIN("35mm Film Grain", "Stylize"),
    VHS_RETRO("VHS 1980s Retro", "Stylize"),
    CYBER_GLITCH("Cyber Glitch & Split", "Distortion"),
    RGB_SPLIT("Chromatic Aberration", "Distortion"),
    VIGNETTE("Cinematic Vignette", "Lighting"),
    NEON_EDGE("Neon Cyber Edges", "Stylize"),
    OLD_FILM("Vintage Sepia Film", "Stylize"),
    SHARPEN("Unsharp Mask / Detail", "Enhance"),
    KALEIDOSCOPE("Kaleidoscope Prism", "Distortion"),
    CARTOON("Toon Posterize", "Artistic"),
    MOSAIC("Pixel Mosaic", "Stylize")
}

enum class TransitionType(val label: String, val durationMs: Long = 600L) {
    NONE("None"),
    CROSS_DISSOLVE("Cross Dissolve", 800L),
    DIP_TO_BLACK("Dip to Black", 600L),
    DIP_TO_WHITE("Dip to White", 500L),
    ZOOM_IN("Zoom In Warp", 500L),
    WHIP_PAN("Whip Pan Left", 450L),
    GLITCH_TRANSITION("Glitch Impact", 400L),
    LIGHT_LEAK("Golden Light Leak", 700L),
    SLIDE_UP("Slide Up Reveal", 500L)
}

data class ColorGrade(
    val brightness: Float = 0f,      // -1.0 to 1.0
    val contrast: Float = 1.0f,     // 0.5 to 2.0
    val exposure: Float = 0f,       // -2.0 to 2.0
    val temperature: Float = 0f,    // -1.0 (Cool) to 1.0 (Warm)
    val tint: Float = 0f,           // -1.0 (Green) to 1.0 (Magenta)
    val saturation: Float = 1.0f,   // 0.0 to 2.0
    val vibrance: Float = 0f,       // -1.0 to 1.0
    val highlights: Float = 0f,     // -1.0 to 1.0
    val shadows: Float = 0f,        // -1.0 to 1.0
    val whites: Float = 0f,
    val blacks: Float = 0f,
    // 3-Way Color Wheels
    val liftR: Float = 0f, val liftG: Float = 0f, val liftB: Float = 0f,       // Shadows
    val gammaR: Float = 0f, val gammaG: Float = 0f, val gammaB: Float = 0f,    // Midtones
    val gainR: Float = 0f, val gainG: Float = 0f, val gainB: Float = 0f,       // Highlights
    val lutPreset: String = "None"  // "Golden Hour", "Teal & Orange", "Monochrome Noir", "Royal Gold", "Kodak 2383", "Cyberpunk"
)

data class ClipTransform(
    val posX: Float = 0f,
    val posY: Float = 0f,
    val scaleX: Float = 1f,
    val scaleY: Float = 1f,
    val rotation: Float = 0f,
    val opacity: Float = 1f,
    val blendMode: BlendModeType = BlendModeType.NORMAL
)

data class Keyframe(
    val timeMs: Long,
    val posX: Float = 0f,
    val posY: Float = 0f,
    val scale: Float = 1f,
    val rotation: Float = 0f,
    val opacity: Float = 1f
)

data class TextOverlay(
    val text: String = "EGO EDIT",
    val fontSizeSp: Float = 28f,
    val fontColorHex: Long = 0xFFD4AF37,
    val strokeColorHex: Long = 0xFF08090C,
    val strokeWidth: Float = 2f,
    val hasGlow: Boolean = true,
    val isBold: Boolean = true,
    val animation: String = "Typewriter" // None, Fade, Pop, Typewriter, Karaoke
)

data class TimelineClip(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val trackId: String,
    val startTimeMs: Long,
    val durationMs: Long,
    val trimInMs: Long = 0L,
    val trimOutMs: Long = 0L,
    val sourceUri: String = "",
    val colorGrade: ColorGrade = ColorGrade(),
    val transform: ClipTransform = ClipTransform(),
    val filter: VideoFilterType = VideoFilterType.NONE,
    val transitionIn: TransitionType = TransitionType.NONE,
    val transitionOut: TransitionType = TransitionType.NONE,
    val volume: Float = 1.0f,
    val speed: Float = 1.0f,
    val keyframes: List<Keyframe> = emptyList(),
    val textOverlay: TextOverlay? = null,
    val isReversed: Boolean = false,
    val chromaKeyActive: Boolean = false,
    val chromaKeyColorHex: Long = 0xFF00FF00,
    val chromaTolerance: Float = 0.35f,
    val placeholderVisual: String = "cinematic_drone" // sample visual generator identifier
)

data class TimelineTrack(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val trackType: TrackType,
    val isMuted: Boolean = false,
    val isLocked: Boolean = false,
    val isSolo: Boolean = false,
    val isVisible: Boolean = true,
    val clips: List<TimelineClip> = emptyList()
)

data class Project(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Untitled Masterpiece",
    val aspectRatio: AspectRatioPreset = AspectRatioPreset.LANDSCAPE_16_9,
    val resolution: ResolutionPreset = ResolutionPreset.FHD_1080P,
    val fps: FpsPreset = FpsPreset.FPS_30,
    val lastEditedMs: Long = System.currentTimeMillis(),
    val tracks: List<TimelineTrack> = emptyList(),
    val status: String = "Draft",
    val thumbnailUri: String = "",
    val autoSaveVersion: Int = 1
) {
    val totalDurationMs: Long
        get() {
            var maxEnd = 0L
            tracks.forEach { track ->
                track.clips.forEach { clip ->
                    val end = clip.startTimeMs + clip.durationMs
                    if (end > maxEnd) maxEnd = end
                }
            }
            return if (maxEnd > 0) maxEnd else 15000L // Default 15s if empty
        }
}
