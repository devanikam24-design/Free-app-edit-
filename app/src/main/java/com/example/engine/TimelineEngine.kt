package com.example.engine

import com.example.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class TimelineEngine(
    initialProject: Project,
    private val onProjectChanged: (Project) -> Unit
) {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var playbackJob: Job? = null

    private val _project = MutableStateFlow(initialProject)
    val project: StateFlow<Project> = _project.asStateFlow()

    private val _currentTimeMs = MutableStateFlow(0L)
    val currentTimeMs: StateFlow<Long> = _currentTimeMs.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    private val _selectedClipId = MutableStateFlow<String?>(null)
    val selectedClipId: StateFlow<String?> = _selectedClipId.asStateFlow()

    private val _selectedTrackId = MutableStateFlow<String?>(null)
    val selectedTrackId: StateFlow<String?> = _selectedTrackId.asStateFlow()

    private val _isLooping = MutableStateFlow(true)
    val isLooping: StateFlow<Boolean> = _isLooping.asStateFlow()

    // Overlay Guide Controls
    private val _showSafeGuides = MutableStateFlow(false)
    val showSafeGuides: StateFlow<Boolean> = _showSafeGuides.asStateFlow()

    private val _showGrid = MutableStateFlow(false)
    val showGrid: StateFlow<Boolean> = _showGrid.asStateFlow()

    private val _isSplitComparison = MutableStateFlow(false)
    val isSplitComparison: StateFlow<Boolean> = _isSplitComparison.asStateFlow()

    private val _previewResolution = MutableStateFlow("Full") // Full, 1/2, 1/4
    val previewResolution: StateFlow<String> = _previewResolution.asStateFlow()

    init {
        // Auto-select first clip if available
        initialProject.tracks.firstOrNull { it.clips.isNotEmpty() }?.let { tr ->
            _selectedTrackId.value = tr.id
            _selectedClipId.value = tr.clips.firstOrNull()?.id
        }
    }

    fun setProject(p: Project) {
        _project.value = p
        if (_currentTimeMs.value > p.totalDurationMs) {
            _currentTimeMs.value = 0L
        }
    }

    fun play() {
        if (_isPlaying.value) return
        _isPlaying.value = true
        playbackJob = scope.launch {
            val frameIntervalMs = (1000L / _project.value.fps.fps.coerceAtLeast(24)).coerceIn(16L, 42L)
            var lastTime = System.currentTimeMillis()
            while (_isPlaying.value) {
                delay(frameIntervalMs)
                val now = System.currentTimeMillis()
                val delta = ((now - lastTime) * _playbackSpeed.value).toLong()
                lastTime = now

                val next = _currentTimeMs.value + delta
                val total = _project.value.totalDurationMs
                if (next >= total) {
                    if (_isLooping.value) {
                        _currentTimeMs.value = 0L
                    } else {
                        _currentTimeMs.value = total
                        _isPlaying.value = false
                        break
                    }
                } else {
                    _currentTimeMs.value = next
                }
            }
        }
    }

    fun pause() {
        _isPlaying.value = false
        playbackJob?.cancel()
        playbackJob = null
    }

    fun togglePlayPause() {
        if (_isPlaying.value) pause() else play()
    }

    fun stop() {
        pause()
        _currentTimeMs.value = 0L
    }

    fun seekTo(timeMs: Long) {
        val clamped = timeMs.coerceIn(0L, _project.value.totalDurationMs)
        _currentTimeMs.value = clamped
    }

    fun stepPrevFrame() {
        val frameMs = 1000L / _project.value.fps.fps
        seekTo((_currentTimeMs.value - frameMs).coerceAtLeast(0L))
    }

    fun stepNextFrame() {
        val frameMs = 1000L / _project.value.fps.fps
        seekTo((_currentTimeMs.value + frameMs).coerceAtMost(_project.value.totalDurationMs))
    }

    fun setPlaybackSpeed(speed: Float) {
        _playbackSpeed.value = speed
    }

    fun selectClip(trackId: String, clipId: String?) {
        _selectedTrackId.value = trackId
        _selectedClipId.value = clipId
    }

    fun toggleSafeGuides() {
        _showSafeGuides.value = !_showSafeGuides.value
    }

    fun toggleGrid() {
        _showGrid.value = !_showGrid.value
    }

    fun toggleSplitComparison() {
        _isSplitComparison.value = !_isSplitComparison.value
    }

    fun setPreviewResolution(res: String) {
        _previewResolution.value = res
    }

    // Editing Operations
    fun splitClipAtPlayhead() {
        val trackId = _selectedTrackId.value ?: return
        val clipId = _selectedClipId.value ?: return
        val playhead = _currentTimeMs.value

        val currentProj = _project.value
        val updatedTracks = currentProj.tracks.map { track ->
            if (track.id == trackId) {
                val newClips = mutableListOf<TimelineClip>()
                track.clips.forEach { clip ->
                    if (clip.id == clipId && playhead > clip.startTimeMs && playhead < (clip.startTimeMs + clip.durationMs)) {
                        val firstDuration = playhead - clip.startTimeMs
                        val secondDuration = clip.durationMs - firstDuration

                        val firstClip = clip.copy(
                            id = UUID.randomUUID().toString(),
                            name = "${clip.name} (Part 1)",
                            durationMs = firstDuration
                        )
                        val secondClip = clip.copy(
                            id = UUID.randomUUID().toString(),
                            name = "${clip.name} (Part 2)",
                            startTimeMs = playhead,
                            durationMs = secondDuration,
                            trimInMs = clip.trimInMs + firstDuration
                        )
                        newClips.add(firstClip)
                        newClips.add(secondClip)
                        _selectedClipId.value = secondClip.id
                    } else {
                        newClips.add(clip)
                    }
                }
                track.copy(clips = newClips)
            } else track
        }
        val updatedProj = currentProj.copy(tracks = updatedTracks)
        _project.value = updatedProj
        onProjectChanged(updatedProj)
    }

    fun trimClip(trackId: String, clipId: String, newStartMs: Long, newDurationMs: Long) {
        val currentProj = _project.value
        val updatedTracks = currentProj.tracks.map { track ->
            if (track.id == trackId) {
                val updatedClips = track.clips.map { clip ->
                    if (clip.id == clipId) {
                        clip.copy(
                            startTimeMs = newStartMs.coerceAtLeast(0L),
                            durationMs = newDurationMs.coerceAtLeast(500L)
                        )
                    } else clip
                }
                track.copy(clips = updatedClips)
            } else track
        }
        val updatedProj = currentProj.copy(tracks = updatedTracks)
        _project.value = updatedProj
        onProjectChanged(updatedProj)
    }

    fun deleteSelectedClip(rippleDelete: Boolean = false) {
        val trackId = _selectedTrackId.value ?: return
        val clipId = _selectedClipId.value ?: return

        val currentProj = _project.value
        var deletedDuration = 0L
        var deletedStart = 0L

        val updatedTracks = currentProj.tracks.map { track ->
            if (track.id == trackId) {
                val target = track.clips.find { it.id == clipId }
                if (target != null) {
                    deletedDuration = target.durationMs
                    deletedStart = target.startTimeMs
                }
                val remainingClips = track.clips.filter { it.id != clipId }
                if (rippleDelete && deletedDuration > 0) {
                    remainingClips.map { c ->
                        if (c.startTimeMs > deletedStart) {
                            c.copy(startTimeMs = (c.startTimeMs - deletedDuration).coerceAtLeast(0L))
                        } else c
                    }
                } else {
                    remainingClips
                }
                track.copy(clips = if (rippleDelete && deletedDuration > 0) {
                    remainingClips.map { c ->
                        if (c.startTimeMs > deletedStart) {
                            c.copy(startTimeMs = (c.startTimeMs - deletedDuration).coerceAtLeast(0L))
                        } else c
                    }
                } else remainingClips)
            } else track
        }
        _selectedClipId.value = null
        val updatedProj = currentProj.copy(tracks = updatedTracks)
        _project.value = updatedProj
        onProjectChanged(updatedProj)
    }

    fun duplicateSelectedClip() {
        val trackId = _selectedTrackId.value ?: return
        val clipId = _selectedClipId.value ?: return

        val currentProj = _project.value
        val updatedTracks = currentProj.tracks.map { track ->
            if (track.id == trackId) {
                val target = track.clips.find { it.id == clipId } ?: return@map track
                val copy = target.copy(
                    id = UUID.randomUUID().toString(),
                    name = "${target.name} (Copy)",
                    startTimeMs = target.startTimeMs + target.durationMs
                )
                track.copy(clips = track.clips + copy)
            } else track
        }
        val updatedProj = currentProj.copy(tracks = updatedTracks)
        _project.value = updatedProj
        onProjectChanged(updatedProj)
    }

    fun updateSelectedClip(transformFn: (TimelineClip) -> TimelineClip) {
        val trackId = _selectedTrackId.value ?: return
        val clipId = _selectedClipId.value ?: return

        val currentProj = _project.value
        val updatedTracks = currentProj.tracks.map { track ->
            if (track.id == trackId) {
                val updatedClips = track.clips.map { clip ->
                    if (clip.id == clipId) transformFn(clip) else clip
                }
                track.copy(clips = updatedClips)
            } else track
        }
        val updatedProj = currentProj.copy(tracks = updatedTracks)
        _project.value = updatedProj
        onProjectChanged(updatedProj)
    }

    fun addTrack(type: TrackType) {
        val currentProj = _project.value
        val count = currentProj.tracks.count { it.trackType == type } + 1
        val newTrack = TimelineTrack(
            name = "${type.label} $count",
            trackType = type
        )
        val updatedProj = currentProj.copy(tracks = currentProj.tracks + newTrack)
        _project.value = updatedProj
        onProjectChanged(updatedProj)
    }

    fun addClipToTrack(trackId: String, clip: TimelineClip) {
        val currentProj = _project.value
        val updatedTracks = currentProj.tracks.map { track ->
            if (track.id == trackId) {
                track.copy(clips = track.clips + clip)
            } else track
        }
        val updatedProj = currentProj.copy(tracks = updatedTracks)
        _project.value = updatedProj
        onProjectChanged(updatedProj)
    }

    fun formatTimecode(timeMs: Long): String {
        val totalSec = timeMs / 1000
        val min = totalSec / 60
        val sec = totalSec % 60
        val fps = _project.value.fps.fps.coerceAtLeast(24)
        val frames = ((timeMs % 1000) * fps / 1000).toInt()
        return String.format("%02d:%02d:%02d", min, sec, frames)
    }

    fun getSelectedClip(): TimelineClip? {
        val trackId = _selectedTrackId.value ?: return null
        val clipId = _selectedClipId.value ?: return null
        return _project.value.tracks.find { it.id == trackId }?.clips?.find { it.id == clipId }
    }

    fun cleanup() {
        playbackJob?.cancel()
    }
}
