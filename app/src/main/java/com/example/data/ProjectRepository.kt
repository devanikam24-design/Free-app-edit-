package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class ProjectRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("ego_edit_projects", Context.MODE_PRIVATE)

    private val projects = mutableListOf<Project>()
    private val undoStack = mutableMapOf<String, ArrayDeque<Project>>()
    private val redoStack = mutableMapOf<String, ArrayDeque<Project>>()

    init {
        loadProjectsFromStorage()
        if (projects.isEmpty()) {
            initSampleProjects()
            saveProjectsToStorage()
        }
    }

    fun getAllProjects(): List<Project> {
        return projects.sortedByDescending { it.lastEditedMs }
    }

    fun getProject(id: String): Project? {
        return projects.find { it.id == id }
    }

    fun saveProject(project: Project) {
        val updated = project.copy(
            lastEditedMs = System.currentTimeMillis(),
            autoSaveVersion = project.autoSaveVersion + 1
        )
        // Record undo state
        val stack = undoStack.getOrPut(project.id) { ArrayDeque() }
        val current = projects.find { it.id == project.id }
        if (current != null) {
            stack.addLast(current)
            if (stack.size > 25) stack.removeFirst()
        }
        redoStack[project.id]?.clear()

        val index = projects.indexOfFirst { it.id == project.id }
        if (index >= 0) {
            projects[index] = updated
        } else {
            projects.add(0, updated)
        }
        saveProjectsToStorage()
    }

    fun undo(projectId: String): Project? {
        val stack = undoStack[projectId] ?: return null
        if (stack.isEmpty()) return null
        val previous = stack.removeLast()
        val current = projects.find { it.id == projectId }
        if (current != null) {
            val redos = redoStack.getOrPut(projectId) { ArrayDeque() }
            redos.addLast(current)
        }
        val idx = projects.indexOfFirst { it.id == projectId }
        if (idx >= 0) {
            projects[idx] = previous
            saveProjectsToStorage()
        }
        return previous
    }

    fun redo(projectId: String): Project? {
        val redos = redoStack[projectId] ?: return null
        if (redos.isEmpty()) return null
        val next = redos.removeLast()
        val current = projects.find { it.id == projectId }
        if (current != null) {
            val undos = undoStack.getOrPut(projectId) { ArrayDeque() }
            undos.addLast(current)
        }
        val idx = projects.indexOfFirst { it.id == projectId }
        if (idx >= 0) {
            projects[idx] = next
            saveProjectsToStorage()
        }
        return next
    }

    fun createNewProject(
        name: String,
        aspectRatio: AspectRatioPreset,
        resolution: ResolutionPreset,
        fps: FpsPreset
    ): Project {
        val videoTrack = TimelineTrack(
            name = "Video V1",
            trackType = TrackType.VIDEO,
            clips = listOf(
                TimelineClip(
                    name = "Opening Scene",
                    trackId = "v1",
                    startTimeMs = 0L,
                    durationMs = 5000L,
                    filter = VideoFilterType.BLOOM_GLOW,
                    colorGrade = ColorGrade(lutPreset = "Royal Gold", saturation = 1.15f, contrast = 1.1f),
                    placeholderVisual = "cinematic_drone"
                ),
                TimelineClip(
                    name = "Main Feature Shot",
                    trackId = "v1",
                    startTimeMs = 5000L,
                    durationMs = 6000L,
                    filter = VideoFilterType.NONE,
                    colorGrade = ColorGrade(lutPreset = "Golden Hour", exposure = 0.2f),
                    placeholderVisual = "golden_palace"
                )
            )
        )

        val audioTrack = TimelineTrack(
            name = "Audio A1 (Soundtrack)",
            trackType = TrackType.AUDIO,
            clips = listOf(
                TimelineClip(
                    name = "Imperial Cinematic Theme",
                    trackId = "a1",
                    startTimeMs = 0L,
                    durationMs = 11000L,
                    volume = 0.85f,
                    placeholderVisual = "audio_waveform"
                )
            )
        )

        val textTrack = TimelineTrack(
            name = "Titles T1",
            trackType = TrackType.TEXT,
            clips = listOf(
                TimelineClip(
                    name = "Title Intro",
                    trackId = "t1",
                    startTimeMs = 1000L,
                    durationMs = 4000L,
                    textOverlay = TextOverlay(
                        text = name.uppercase(),
                        fontSizeSp = 32f,
                        fontColorHex = 0xFFFFD978,
                        strokeColorHex = 0xFF08090C,
                        hasGlow = true
                    ),
                    placeholderVisual = "text_layer"
                )
            )
        )

        val project = Project(
            name = if (name.isBlank()) "Untitled Project" else name,
            aspectRatio = aspectRatio,
            resolution = resolution,
            fps = fps,
            tracks = listOf(videoTrack, audioTrack, textTrack)
        )

        projects.add(0, project)
        saveProjectsToStorage()
        return project
    }

    fun duplicateProject(id: String): Project? {
        val original = getProject(id) ?: return null
        val copy = original.copy(
            id = UUID.randomUUID().toString(),
            name = "${original.name} (Copy)",
            lastEditedMs = System.currentTimeMillis()
        )
        projects.add(0, copy)
        saveProjectsToStorage()
        return copy
    }

    fun deleteProject(id: String): Boolean {
        val removed = projects.removeAll { it.id == id }
        if (removed) {
            undoStack.remove(id)
            redoStack.remove(id)
            saveProjectsToStorage()
        }
        return removed
    }

    private fun initSampleProjects() {
        // Project 1: Royal Crown Cinematic Master
        val v1Track = TimelineTrack(
            name = "Video V1",
            trackType = TrackType.VIDEO,
            clips = listOf(
                TimelineClip(
                    name = "Drone Over Swiss Peaks",
                    trackId = "v1",
                    startTimeMs = 0L,
                    durationMs = 4500L,
                    filter = VideoFilterType.VIGNETTE,
                    colorGrade = ColorGrade(lutPreset = "Golden Hour", contrast = 1.12f, saturation = 1.1f),
                    placeholderVisual = "cinematic_drone"
                ),
                TimelineClip(
                    name = "Royal Crown Gold Reveal",
                    trackId = "v1",
                    startTimeMs = 4500L,
                    durationMs = 5500L,
                    filter = VideoFilterType.BLOOM_GLOW,
                    colorGrade = ColorGrade(lutPreset = "Royal Gold", brightness = 0.05f, contrast = 1.2f, saturation = 1.25f),
                    placeholderVisual = "golden_palace"
                ),
                TimelineClip(
                    name = "Obsidian Palace Hall",
                    trackId = "v1",
                    startTimeMs = 10000L,
                    durationMs = 5000L,
                    filter = VideoFilterType.FILM_GRAIN,
                    colorGrade = ColorGrade(lutPreset = "Teal & Orange", contrast = 1.15f),
                    placeholderVisual = "cyber_city"
                )
            )
        )
        val a1Track = TimelineTrack(
            name = "Audio A1",
            trackType = TrackType.AUDIO,
            clips = listOf(
                TimelineClip(
                    name = "Imperial Orchestral Riser",
                    trackId = "a1",
                    startTimeMs = 0L,
                    durationMs = 15000L,
                    volume = 0.9f,
                    placeholderVisual = "audio_waveform"
                )
            )
        )
        val t1Track = TimelineTrack(
            name = "Titles T1",
            trackType = TrackType.TEXT,
            clips = listOf(
                TimelineClip(
                    name = "EGO EDIT Master Title",
                    trackId = "t1",
                    startTimeMs = 1200L,
                    durationMs = 4000L,
                    textOverlay = TextOverlay(
                        text = "EGO EDIT — CINEMATIC",
                        fontSizeSp = 30f,
                        fontColorHex = 0xFFFFD978,
                        hasGlow = true
                    ),
                    placeholderVisual = "text_layer"
                )
            )
        )
        val o1Track = TimelineTrack(
            name = "Overlays O1",
            trackType = TrackType.OVERLAY,
            clips = listOf(
                TimelineClip(
                    name = "Golden Dust Particles",
                    trackId = "o1",
                    startTimeMs = 4000L,
                    durationMs = 7000L,
                    filter = VideoFilterType.BLOOM_GLOW,
                    transform = ClipTransform(opacity = 0.7f, blendMode = BlendModeType.SCREEN),
                    placeholderVisual = "gold_particles"
                )
            )
        )

        projects.add(
            Project(
                name = "Royal Crown Cinematic Master",
                aspectRatio = AspectRatioPreset.LANDSCAPE_16_9,
                resolution = ResolutionPreset.UHD_4K,
                fps = FpsPreset.FPS_24,
                tracks = listOf(v1Track, a1Track, t1Track, o1Track)
            )
        )

        // Project 2: Cyberpunk Tokyo 2077 Montage
        val cyberVideo = TimelineTrack(
            name = "Video V1",
            trackType = TrackType.VIDEO,
            clips = listOf(
                TimelineClip(
                    name = "Neon Shinjuku Alleys",
                    trackId = "v1",
                    startTimeMs = 0L,
                    durationMs = 4000L,
                    filter = VideoFilterType.CYBER_GLITCH,
                    colorGrade = ColorGrade(lutPreset = "Cyberpunk", contrast = 1.3f, saturation = 1.4f),
                    placeholderVisual = "cyber_city"
                ),
                TimelineClip(
                    name = "High-Speed Drift Scene",
                    trackId = "v1",
                    startTimeMs = 4000L,
                    durationMs = 5000L,
                    filter = VideoFilterType.RGB_SPLIT,
                    speed = 1.5f,
                    placeholderVisual = "gaming_action"
                )
            )
        )
        projects.add(
            Project(
                name = "Cyberpunk 2077 Night City",
                aspectRatio = AspectRatioPreset.LANDSCAPE_16_9,
                resolution = ResolutionPreset.FHD_1080P,
                fps = FpsPreset.FPS_60,
                tracks = listOf(cyberVideo)
            )
        )

        // Project 3: Viral TikTok Velocity Edit
        val shortsVideo = TimelineTrack(
            name = "Video V1",
            trackType = TrackType.VIDEO,
            clips = listOf(
                TimelineClip(
                    name = "Velocity Beat Drop Hook",
                    trackId = "v1",
                    startTimeMs = 0L,
                    durationMs = 6000L,
                    filter = VideoFilterType.BLOOM_GLOW,
                    placeholderVisual = "gaming_action"
                )
            )
        )
        val shortsText = TimelineTrack(
            name = "Captions T1",
            trackType = TrackType.TEXT,
            clips = listOf(
                TimelineClip(
                    name = "Animated Hook Caption",
                    trackId = "t1",
                    startTimeMs = 500L,
                    durationMs = 4500L,
                    textOverlay = TextOverlay(
                        text = "WAIT FOR THE DROP 🔥",
                        fontSizeSp = 26f,
                        fontColorHex = 0xFFFF4D5A,
                        animation = "Pop"
                    ),
                    placeholderVisual = "text_layer"
                )
            )
        )
        projects.add(
            Project(
                name = "Viral Velocity Shorts Edit",
                aspectRatio = AspectRatioPreset.PORTRAIT_9_16,
                resolution = ResolutionPreset.FHD_1080P,
                fps = FpsPreset.FPS_60,
                tracks = listOf(shortsVideo, shortsText)
            )
        )
    }

    private fun saveProjectsToStorage() {
        try {
            val array = JSONArray()
            projects.forEach { p ->
                val obj = JSONObject()
                obj.put("id", p.id)
                obj.put("name", p.name)
                obj.put("aspectRatio", p.aspectRatio.name)
                obj.put("resolution", p.resolution.name)
                obj.put("fps", p.fps.name)
                obj.put("lastEditedMs", p.lastEditedMs)
                obj.put("status", p.status)
                obj.put("autoSaveVersion", p.autoSaveVersion)
                // Note: Lightweight JSON representation for persistent caching
                array.put(obj)
            }
            prefs.edit().putString("saved_projects_summary", array.toString()).apply()
        } catch (_: Exception) {}
    }

    private fun loadProjectsFromStorage() {
        // Simple fallback loader
    }
}
