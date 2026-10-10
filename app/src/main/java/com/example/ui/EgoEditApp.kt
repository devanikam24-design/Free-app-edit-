package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.data.ProjectRepository
import com.example.engine.ExportRenderer
import com.example.engine.TimelineEngine
import com.example.model.*
import com.example.ui.screens.ai.AiWorkspaceScreen
import com.example.ui.screens.audio.AudioStudioScreen
import com.example.ui.screens.color.ColorGradingScreen
import com.example.ui.screens.editor.EditorScreen
import com.example.ui.screens.export.ExportManagerScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.registry.FeatureRegistryScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.splash.SplashScreen
import com.example.ui.screens.subtitles.SubtitleEditorScreen
import com.example.ui.screens.templates.TemplatesScreen
import com.example.ui.screens.thumbnail.ThumbnailStudioScreen
import com.example.ui.screens.vfx.VfxMotionScreen
import com.example.ui.theme.EgoBgPrimary
import kotlinx.coroutines.delay

enum class ScreenState {
    SPLASH,
    HOME,
    EDITOR,
    COLOR_STUDIO,
    VFX_STUDIO,
    AUDIO_STUDIO,
    AI_STUDIO,
    SUBTITLE_STUDIO,
    THUMBNAIL_STUDIO,
    EXPORT_MANAGER,
    TEMPLATES,
    FEATURE_REGISTRY,
    SETTINGS
}

@Composable
fun EgoEditApp() {
    val context = LocalContext.current
    val repository = remember { ProjectRepository(context) }
    val exportRenderer = remember { ExportRenderer(context) }

    var currentScreen by remember { mutableStateOf(ScreenState.SPLASH) }
    var activeProject by remember { mutableStateOf<Project?>(null) }
    var engine by remember { mutableStateOf<TimelineEngine?>(null) }

    var projectsList by remember { mutableStateOf(repository.getAllProjects()) }

    fun refreshProjects() {
        projectsList = repository.getAllProjects()
    }

    fun openProjectInEditor(project: Project) {
        activeProject = project
        engine?.cleanup()
        engine = TimelineEngine(project) { updated ->
            activeProject = updated
            repository.saveProject(updated)
            refreshProjects()
        }
        currentScreen = ScreenState.EDITOR
    }

    // Periodic Autosave every 60s
    LaunchedEffect(engine) {
        val eng = engine ?: return@LaunchedEffect
        while (true) {
            delay(60000L)
            val currentProj = eng.project.value
            repository.saveProject(currentProj)
            refreshProjects()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EgoBgPrimary)
    ) {
        when (currentScreen) {
            ScreenState.SPLASH -> {
                SplashScreen(
                    onSplashFinished = {
                        currentScreen = ScreenState.HOME
                    }
                )
            }

            ScreenState.HOME -> {
                HomeScreen(
                    projects = projectsList,
                    onOpenProject = { p -> openProjectInEditor(p) },
                    onCreateProject = { name, aspect, res, fps ->
                        val created = repository.createNewProject(name, aspect, res, fps)
                        refreshProjects()
                        openProjectInEditor(created)
                    },
                    onDuplicateProject = { id ->
                        repository.duplicateProject(id)
                        refreshProjects()
                    },
                    onDeleteProject = { id ->
                        repository.deleteProject(id)
                        refreshProjects()
                    },
                    onNavigateToStudio = { studio ->
                        // Ensure an active project exists before entering studios
                        if (activeProject == null && projectsList.isNotEmpty()) {
                            openProjectInEditor(projectsList.first())
                        }
                        when (studio) {
                            "color" -> currentScreen = ScreenState.COLOR_STUDIO
                            "vfx" -> currentScreen = ScreenState.VFX_STUDIO
                            "audio" -> currentScreen = ScreenState.AUDIO_STUDIO
                            "ai" -> currentScreen = ScreenState.AI_STUDIO
                            "subtitles" -> currentScreen = ScreenState.SUBTITLE_STUDIO
                            "thumbnail" -> currentScreen = ScreenState.THUMBNAIL_STUDIO
                            "templates" -> currentScreen = ScreenState.TEMPLATES
                            "registry" -> currentScreen = ScreenState.FEATURE_REGISTRY
                            "settings" -> currentScreen = ScreenState.SETTINGS
                        }
                    }
                )
            }

            ScreenState.EDITOR -> {
                val currentEngine = engine
                if (currentEngine != null) {
                    BackHandler {
                        currentEngine.pause()
                        currentScreen = ScreenState.HOME
                    }
                    EditorScreen(
                        engine = currentEngine,
                        onBackToHome = {
                            currentEngine.pause()
                            currentScreen = ScreenState.HOME
                        },
                        onOpenExport = { currentScreen = ScreenState.EXPORT_MANAGER },
                        onOpenStudio = { studio ->
                            when (studio) {
                                "color" -> currentScreen = ScreenState.COLOR_STUDIO
                                "vfx" -> currentScreen = ScreenState.VFX_STUDIO
                                "audio" -> currentScreen = ScreenState.AUDIO_STUDIO
                                "ai" -> currentScreen = ScreenState.AI_STUDIO
                                "subtitles" -> currentScreen = ScreenState.SUBTITLE_STUDIO
                                "templates" -> currentScreen = ScreenState.TEMPLATES
                            }
                        },
                        onUndo = {
                            val activeId = activeProject?.id ?: return@EditorScreen
                            val undone = repository.undo(activeId)
                            if (undone != null) {
                                activeProject = undone
                                currentEngine.setProject(undone)
                            }
                        },
                        onRedo = {
                            val activeId = activeProject?.id ?: return@EditorScreen
                            val redone = repository.redo(activeId)
                            if (redone != null) {
                                activeProject = redone
                                currentEngine.setProject(redone)
                            }
                        }
                    )
                } else {
                    currentScreen = ScreenState.HOME
                }
            }

            ScreenState.COLOR_STUDIO -> {
                val currentEngine = engine ?: return@Box
                BackHandler { currentScreen = ScreenState.EDITOR }
                ColorGradingScreen(
                    engine = currentEngine,
                    onBack = { currentScreen = ScreenState.EDITOR }
                )
            }

            ScreenState.VFX_STUDIO -> {
                val currentEngine = engine ?: return@Box
                BackHandler { currentScreen = ScreenState.EDITOR }
                VfxMotionScreen(
                    engine = currentEngine,
                    onBack = { currentScreen = ScreenState.EDITOR }
                )
            }

            ScreenState.AUDIO_STUDIO -> {
                val currentEngine = engine ?: return@Box
                BackHandler { currentScreen = ScreenState.EDITOR }
                AudioStudioScreen(
                    engine = currentEngine,
                    onBack = { currentScreen = ScreenState.EDITOR }
                )
            }

            ScreenState.AI_STUDIO -> {
                val currentEngine = engine ?: return@Box
                BackHandler { currentScreen = ScreenState.EDITOR }
                AiWorkspaceScreen(
                    engine = currentEngine,
                    onBack = { currentScreen = ScreenState.EDITOR }
                )
            }

            ScreenState.SUBTITLE_STUDIO -> {
                val currentEngine = engine ?: return@Box
                BackHandler { currentScreen = ScreenState.EDITOR }
                SubtitleEditorScreen(
                    engine = currentEngine,
                    onBack = { currentScreen = ScreenState.EDITOR }
                )
            }

            ScreenState.THUMBNAIL_STUDIO -> {
                BackHandler { currentScreen = ScreenState.HOME }
                ThumbnailStudioScreen(
                    onBack = { currentScreen = ScreenState.HOME }
                )
            }

            ScreenState.EXPORT_MANAGER -> {
                val currentProject = activeProject ?: return@Box
                BackHandler { currentScreen = ScreenState.EDITOR }
                ExportManagerScreen(
                    project = currentProject,
                    exportRenderer = exportRenderer,
                    onBack = { currentScreen = ScreenState.EDITOR }
                )
            }

            ScreenState.TEMPLATES -> {
                val currentEngine = engine ?: return@Box
                BackHandler { currentScreen = ScreenState.EDITOR }
                TemplatesScreen(
                    engine = currentEngine,
                    onBack = { currentScreen = ScreenState.EDITOR }
                )
            }

            ScreenState.FEATURE_REGISTRY -> {
                BackHandler { currentScreen = ScreenState.HOME }
                FeatureRegistryScreen(
                    onBack = { currentScreen = ScreenState.HOME }
                )
            }

            ScreenState.SETTINGS -> {
                BackHandler { currentScreen = ScreenState.HOME }
                SettingsScreen(
                    onBack = { currentScreen = ScreenState.HOME }
                )
            }
        }
    }
}
