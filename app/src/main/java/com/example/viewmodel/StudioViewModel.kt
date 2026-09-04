package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.VoiceAgentSpeaker
import com.example.audio.SpeechRecognizerManager
import com.example.audio.SpeechState
import com.example.audio.VoiceCommandProcessor
import com.example.audio.VoiceCommandResult
import com.example.audio.ActionType
import com.example.data.api.GeminiClient
import com.example.data.api.YouTubeScriptService
import com.example.data.local.ProjectRepository
import com.example.data.local.SamlytDatabase
import com.example.data.local.ScriptEntity
import com.example.data.local.StoryboardEntity
import com.example.data.model.CastMember
import com.example.data.model.DepartmentType
import com.example.data.model.GenerationType
import com.example.data.model.LocationItem
import com.example.data.model.ParsedBeat
import com.example.data.model.ProductionBible
import com.example.data.model.ProductionProposal
import com.example.data.model.ProductionStatus
import com.example.data.model.ProjectItem
import com.example.data.model.SavedVaultThumbnail
import com.example.data.model.ProjectType
import com.example.data.model.ResearchResult
import com.example.data.model.ScriptScene
import com.example.data.model.StoryboardShot
import com.example.data.model.StudioScreen
import com.example.data.model.ThumbnailConcept
import com.example.data.model.UserSettings
import com.example.data.model.VoiceThought
import com.example.data.model.YouTubeFormulaBlueprint
import com.example.util.ScriptExporter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Stack
import java.util.UUID

class StudioViewModel(application: Application) : AndroidViewModel(application) {
  private val database = SamlytDatabase.getDatabase(application)
  private val repository = ProjectRepository(
    projectDao = database.projectDao(),
    scriptDao = database.scriptDao(),
    storyboardDao = database.storyboardDao(),
    settingsDao = database.settingsDao(),
    context = application
  )

  // Voice Agent Speaker with Female Voice Profile (Text-to-Speech)
  val voiceAgentSpeaker = VoiceAgentSpeaker(application)
  val isVoiceAgentSpeaking: StateFlow<Boolean> = voiceAgentSpeaker.isSpeaking
  val voiceAgentName: StateFlow<String> = voiceAgentSpeaker.activeVoiceName
  val isVoiceMuted: StateFlow<Boolean> = voiceAgentSpeaker.isMuted
  val voiceSpeechRate: StateFlow<Float> = voiceAgentSpeaker.speechRate
  val voicePitch: StateFlow<Float> = voiceAgentSpeaker.pitch

  // Speech-to-Text Manager using Android SpeechRecognizer
  val speechRecognizerManager = SpeechRecognizerManager(application) { spokenText ->
    handleRecognizedVoiceCommand(spokenText)
  }
  val speechState: StateFlow<SpeechState> = speechRecognizerManager.speechState
  val isVoiceListening: StateFlow<Boolean> = speechRecognizerManager.isListening
  val partialVoiceText: StateFlow<String> = speechRecognizerManager.partialText
  val voiceRmsLevel: StateFlow<Float> = speechRecognizerManager.rmsLevel
  val voiceErrorMessage: StateFlow<String?> = speechRecognizerManager.errorMessage

  // Screen Navigation Stack
  private val _currentScreen = MutableStateFlow(StudioScreen.HOME)
  val currentScreen: StateFlow<StudioScreen> = _currentScreen.asStateFlow()
  private val backStack = Stack<StudioScreen>()

  // Settings
  private val _settings = MutableStateFlow(repository.loadSettings())
  val settings: StateFlow<UserSettings> = _settings.asStateFlow()

  // Active Project State
  private val _activeProject = MutableStateFlow<ProjectItem?>(null)
  val activeProject: StateFlow<ProjectItem?> = _activeProject.asStateFlow()
  val activeProjectScripts = MutableStateFlow<List<ScriptEntity>>(emptyList())
  val activeProjectStoryboards = MutableStateFlow<List<StoryboardEntity>>(emptyList())

  val isApiConnected = MutableStateFlow(true)
  val apiConnectionStatus = MutableStateFlow("Connected (Gemini 3.5 Flash / Retrofit Active)")

  val projects: StateFlow<List<ProjectItem>> = repository.allProjects
    .let { flow ->
      val state = MutableStateFlow<List<ProjectItem>>(emptyList())
      viewModelScope.launch {
        flow.collect { list ->
          state.value = list
          if (_activeProject.value == null && list.isNotEmpty()) {
            selectActiveProject(list.first())
          }
        }
      }
      state.asStateFlow()
    }

  // Auto-Save Status
  private val _lastAutoSaved = MutableStateFlow(System.currentTimeMillis())
  val lastAutoSaved: StateFlow<Long> = _lastAutoSaved.asStateFlow()

  // Research Engine State
  val researchUrl = MutableStateFlow("https://youtube.com/watch?v=cinematic_lens_psychology")
  val researchAngle = MutableStateFlow("Hook & Retention Breakdown")
  val isAnalyzingResearch = MutableStateFlow(false)
  val researchResult = MutableStateFlow<ResearchResult?>(null)

  // Script Studio State
  val scriptPremise = MutableStateFlow("Why 90% of cinematic lenses lie to your brain, starting with Hitchcock's Vertigo shot...")
  val scriptTone = MutableStateFlow("Cinematic")
  val scriptDuration = MutableStateFlow("Standard 8 - 12 min")
  val scriptArchitecture = MutableStateFlow("3-Act Essay")
  val isDraftingScript = MutableStateFlow(false)
  val scriptScenes = MutableStateFlow<List<ScriptScene>>(emptyList())
  val isAssistingScript = MutableStateFlow(false)
  val scriptAssistantFeedback = MutableStateFlow<String?>(null)

  // YouTube Formula Reverse Engineering State
  val activeFormulaBlueprint = MutableStateFlow<YouTubeFormulaBlueprint?>(null)
  val isReverseEngineeringFormula = MutableStateFlow(false)

  // Visuals Storyboard State
  val storyboardPremise = MutableStateFlow("35mm anamorphic prime, dramatic neon split lighting, dark atmosphere with volumetric haze")
  val storyboardAspect = MutableStateFlow("16:9")
  val storyboardOptics = MutableStateFlow("Anamorphic Prime")
  val isGeneratingStoryboard = MutableStateFlow(false)
  val storyboardShots = MutableStateFlow<List<StoryboardShot>>(emptyList())

  // Thumbnail Studio State
  val thumbnailPremise = MutableStateFlow("Shocked expression under neon split lighting, giant mysterious monolith")
  val thumbnailTitle = MutableStateFlow("Why Minimalist Tech Won")
  val thumbnailArchetype = MutableStateFlow("Curiosity Gap")
  val thumbnailAspect = MutableStateFlow("16:9")
  val isGeneratingThumbnails = MutableStateFlow(false)
  val thumbnailConcepts = MutableStateFlow<List<ThumbnailConcept>>(emptyList())
  val thumbnailToastMessage = MutableStateFlow<String?>(null)

  // Voice Director State
  val voiceDirectorMode = MutableStateFlow("Director Mode")
  val voiceThoughtStream = MutableStateFlow<List<VoiceThought>>(
    listOf(
      VoiceThought(
        id = "1",
        speaker = "Director (You)",
        timeAgo = "Just now",
        text = "Let's build a fast-paced 12-minute video essay opening on why early cinema color timing feels more visceral than modern HDR..."
      ),
      VoiceThought(
        id = "2",
        speaker = "SAMLYT AI",
        timeAgo = "Just now",
        text = "Got it. Setting narrative tension profile: High Retention. Hook target: 0:00 - 0:45. Act I should launch with a 35mm Technicolor bath versus sterile Rec.2020 clip juxtaposition.",
        isSynthesizing = true,
        actionPills = listOf("Add B-Roll Cue", "14s Retention Spike")
      )
    )
  )

  // Vault State
  val isVaultUnlocked = MutableStateFlow(true)
  val vaultPin = MutableStateFlow("1234")
  val vaultThumbnails = MutableStateFlow<List<SavedVaultThumbnail>>(emptyList())

  // Active Production Bible State
  val productionBible = MutableStateFlow(ProductionBible())

  // Cast & Locations Library (Continuity Memory)
  val castMembers = MutableStateFlow(
    listOf(
      CastMember(
        id = "cast-1",
        name = "Investigator Alex",
        continuityTokens = "Charcoal trench coat, silver signet ring on right index, soft silhouette backlight",
        voiceProfile = "Deep baritone, 132 WPM measured cadence",
        appearances = 4
      ),
      CastMember(
        id = "cast-2",
        name = "Dr. Aris Thorne",
        continuityTokens = "Vintage tortoiseshell spectacles, tweed waistcoat, silver pocketwatch chain",
        voiceProfile = "Raspy academic, breathless intellectual pacing",
        appearances = 2
      ),
      CastMember(
        id = "cast-3",
        name = "The Cipher Subject",
        continuityTokens = "Faceless silhouette, hooded tactical windbreaker, neon reflection across dark visor",
        voiceProfile = "Synthesized vocoder whisper, pitch-shifted -3 semitones",
        appearances = 3
      )
    )
  )

  val locationItems = MutableStateFlow(
    listOf(
      LocationItem(
        id = "loc-1",
        name = "The Subterranean Vault",
        environmentStyle = "Brutalist monolithic concrete walls, exposed ceiling conduits, fluorescent flicker",
        continuityNotes = "Volumetric haze density 0.45, green phosphor CRT glow, damp floor reflections",
        shotHistoryCount = 5
      ),
      LocationItem(
        id = "loc-2",
        name = "Archive 7-B",
        environmentStyle = "Endless microfiche metal cabinets, high arched ceilings, wooden research desks",
        continuityNotes = "Amber task lamp positioned frame-right, floating dust motes in tungsten ray",
        shotHistoryCount = 3
      ),
      LocationItem(
        id = "loc-3",
        name = "Neon Rain Alley",
        environmentStyle = "Rain-slicked asphalt, dense electrical wires overhead, industrial steam vents",
        continuityNotes = "Anamorphic cyan horizontal streak flare from storefront signage",
        shotHistoryCount = 4
      )
    )
  )

  // Specialized Department Agents Real Statuses
  val departmentStatuses = MutableStateFlow(
    mapOf(
      DepartmentType.SCRIPT to ProductionStatus.APPROVED,
      DepartmentType.BREAKDOWN to ProductionStatus.WORKING,
      DepartmentType.DIRECTOR to ProductionStatus.APPROVED,
      DepartmentType.CINEMATOGRAPHY to ProductionStatus.WORKING,
      DepartmentType.PERFORMANCE to ProductionStatus.QUEUED,
      DepartmentType.LOCATION to ProductionStatus.APPROVED,
      DepartmentType.VFX to ProductionStatus.REVIEW,
      DepartmentType.SOUND to ProductionStatus.QUEUED,
      DepartmentType.EDITOR to ProductionStatus.QUEUED,
      DepartmentType.DISTRIBUTION to ProductionStatus.WORKING
    )
  )

  // Operating Pattern: ANALYZE -> PROPOSE -> PREVIEW -> USER APPROVAL -> APPLY -> VERSION
  val productionProposals = MutableStateFlow(
    listOf(
      ProductionProposal(
        id = "prop-1",
        department = DepartmentType.CINEMATOGRAPHY,
        title = "Switch Shot 2 Lens to 40mm Kowa Anamorphic",
        analysis = "Current 50mm spherical lens clashes with the active Production Bible's anamorphic requirement and fails to capture the amber task lamp continuity in Archive 7-B.",
        proposedChange = "Replace spherical lens with 40mm Kowa Anamorphic Prime (T2.2) and add 5600K rim backlight.",
        previewContent = "PROMPT PREVIEW: 40mm Kowa Anamorphic close-up, Archive 7-B, Dr. Aris Thorne examining microfiche, cyan edge light, warm tungsten desk spill, anamorphic horizontal streak flare.",
        versionTag = "V2.4.1-CINE",
        status = ProductionStatus.REVIEW
      ),
      ProductionProposal(
        id = "prop-2",
        department = DepartmentType.VFX,
        title = "Veo Generative Motion on Vault Orbital Pan",
        analysis = "Document Beat 3 contains explicit 'Animation: fast orbital camera roll' directive. Flagged for Veo video generation.",
        proposedChange = "Generate 4-second 24fps motion plate using Veo prompt with locked geometry constraints.",
        previewContent = "VEO MOTION PREVIEW: Veo 24fps 4s camera orbiting 180 degrees around central Brutalist console in subterranean vault, CRT scanlines shifting across metal enclosure.",
        versionTag = "V2.4.2-VFX",
        status = ProductionStatus.REVIEW
      )
    )
  )

  // Document-Imported Production Beat State
  val rawDocumentText = MutableStateFlow("")
  val parsedBeats = MutableStateFlow<List<ParsedBeat>>(emptyList())
  val isReviewCheckpointVisible = MutableStateFlow(false)

  // API connection test state
  val apiTestMessage = MutableStateFlow<String?>(null)
  val isTestingApi = MutableStateFlow(false)

  init {
    val key = getEffectiveGeminiKey()
    if (key.isBlank()) {
      isApiConnected.value = false
      apiConnectionStatus.value = "No Key (Configure in Settings)"
    } else {
      isApiConnected.value = true
      apiConnectionStatus.value = "Connected (Gemini 3.5 Flash / Retrofit Active)"
    }
    startAutoSaveWorker()
    loadVaultThumbnails()
  }

  fun navigateTo(screen: StudioScreen) {
    if (_currentScreen.value != screen) {
      backStack.push(_currentScreen.value)
      _currentScreen.value = screen
    }
  }

  fun navigateBack(): Boolean {
    if (!backStack.isEmpty()) {
      _currentScreen.value = backStack.pop()
      return true
    }
    if (_currentScreen.value != StudioScreen.HOME) {
      _currentScreen.value = StudioScreen.HOME
      return true
    }
    return false
  }

  private fun startAutoSaveWorker() {
    viewModelScope.launch {
      while (true) {
        delay(30_000) // 30 seconds auto-save requirement
        autoSaveActiveWork()
        _lastAutoSaved.value = System.currentTimeMillis()
      }
    }
  }

  private suspend fun autoSaveActiveWork() {
    // If we have an active script, auto-save to projects
    if (scriptScenes.value.isNotEmpty()) {
      val project = ProjectItem(
        id = "script_autosave_current",
        title = if (scriptPremise.value.isNotBlank()) scriptPremise.value.take(35) + "..." else "Cinematic Script Draft",
        type = ProjectType.SCRIPT,
        description = "${scriptScenes.value.size} Scenes * ${scriptDuration.value} * ${scriptTone.value}",
        timestamp = System.currentTimeMillis(),
        targetDuration = scriptDuration.value,
        tone = scriptTone.value,
        content = scriptScenes.value.joinToString("\n\n") { "${it.timestamp} [${it.actTitle}]\n${it.narration}\nB-Roll: ${it.bRollCue}" }
      )
      repository.saveProject(project)
    }

    if (storyboardShots.value.isNotEmpty()) {
      val project = ProjectItem(
        id = "storyboard_autosave_current",
        title = "Storyboard: ${storyboardOptics.value} (${storyboardAspect.value})",
        type = ProjectType.STORYBOARD,
        description = "${storyboardShots.value.size} Shots * ${storyboardOptics.value}",
        timestamp = System.currentTimeMillis(),
        content = storyboardShots.value.joinToString("\n") { "Shot ${it.shotNumber}: ${it.focalLength} - ${it.movement}" }
      )
      repository.saveProject(project)
    }
  }

  fun analyzeYouTube() {
    viewModelScope.launch {
      isAnalyzingResearch.value = true
      val res = GeminiClient.analyzeYouTube(
        url = researchUrl.value,
        angle = researchAngle.value,
        userKey = getEffectiveGeminiKey()
      )
      researchResult.value = res
      isAnalyzingResearch.value = false

      // Save as project
      repository.saveProject(
        ProjectItem(
          id = UUID.randomUUID().toString(),
          title = res.videoTitle,
          type = ProjectType.RESEARCH,
          description = "Channel Analysis: ${res.channelTitle} * Score: ${res.hookScore}/100",
          content = res.retentionCurveNotes
        )
      )
    }
  }

  fun reverseEngineerYouTubeChannel(queryOrUrl: String) {
    if (queryOrUrl.isBlank()) return
    viewModelScope.launch {
      isReverseEngineeringFormula.value = true
      val key = getEffectiveGeminiKey()
      val blueprint = GeminiClient.reverseEngineerYouTubeFormula(queryOrUrl, key)
      activeFormulaBlueprint.value = blueprint
      isReverseEngineeringFormula.value = false

      // Also create a research entity project for persistence
      val proj = ProjectItem(
        id = UUID.randomUUID().toString(),
        title = "Formula: ${blueprint.creatorStyle}",
        type = ProjectType.RESEARCH,
        description = "${blueprint.channelOrUrl} * ${blueprint.pacingCutRate}",
        content = buildString {
          appendLine("CREATOR STYLE: ${blueprint.creatorStyle}")
          appendLine("HOOK FORMULA: ${blueprint.hookFormula}")
          appendLine("PACING: ${blueprint.pacingCutRate}")
          appendLine("NARRATIVE FRAMEWORK: ${blueprint.narrativeFramework}")
          appendLine("VISUAL AESTHETIC: ${blueprint.visualAesthetic}")
          appendLine("SOUND DESIGN: ${blueprint.soundDesignArchitecture}")
          appendLine("WORKFLOW CHECKLIST:\n${blueprint.workflowSteps.mapIndexed { i, s -> "${i + 1}. $s" }.joinToString("\n")}")
        },
        channelTarget = "YouTube",
        status = "Formula Deconstructed"
      )
      repository.saveProject(proj)
    }
  }

  fun applyFormulaToActiveProject(blueprint: YouTubeFormulaBlueprint) {
    scriptTone.value = blueprint.creatorStyle.take(30)
    scriptArchitecture.value = blueprint.sampleScriptArchitecture
    storyboardOptics.value = if (blueprint.visualAesthetic.contains("35mm", ignoreCase = true)) "35mm Prime Cinema" else "24mm Anamorphic"
    
    // Update active project if one exists
    _activeProject.value?.let { curr ->
      val updated = curr.copy(
        tone = blueprint.creatorStyle.take(30),
        description = "Deconstructed Formula: ${blueprint.creatorStyle} * Pacing: ${blueprint.pacingCutRate}"
      )
      _activeProject.value = updated
      viewModelScope.launch { repository.updateProject(updated) }
    }
    navigateTo(StudioScreen.SCRIPT_STUDIO)
  }

  fun selectActiveProject(project: ProjectItem) {
    _activeProject.value = project
    scriptPremise.value = project.title
    scriptDuration.value = project.targetDuration
    scriptTone.value = project.tone
    viewModelScope.launch {
      repository.getScriptsForProject(project.id).collect { scripts ->
        activeProjectScripts.value = scripts
      }
    }
    viewModelScope.launch {
      repository.getStoryboardsForProject(project.id).collect { storyboards ->
        activeProjectStoryboards.value = storyboards
      }
    }
  }

  fun createNewProject(
    title: String,
    topic: String,
    duration: String = "Standard 8 - 12 min",
    tone: String = "Cinematic",
    type: ProjectType = ProjectType.SCRIPT,
    channelTarget: String = "YouTube"
  ) {
    val newProj = ProjectItem(
      id = UUID.randomUUID().toString(),
      title = title.ifBlank { topic.take(40) },
      type = type,
      description = "$duration * $tone * Channel: $channelTarget",
      timestamp = System.currentTimeMillis(),
      targetDuration = duration,
      tone = tone,
      content = topic,
      isEncrypted = true,
      channelTarget = channelTarget,
      status = "In Production"
    )
    _activeProject.value = newProj
    scriptPremise.value = topic
    scriptTone.value = tone
    scriptDuration.value = duration
    viewModelScope.launch {
      repository.saveProject(newProj)
      selectActiveProject(newProj)
    }
  }

  fun updateActiveProject(project: ProjectItem) {
    viewModelScope.launch {
      repository.updateProject(project)
      if (_activeProject.value?.id == project.id) {
        _activeProject.value = project
      }
    }
  }

  fun draftScript() {
    viewModelScope.launch {
      isDraftingScript.value = true
      updateDepartmentStatus(DepartmentType.SCRIPT, ProductionStatus.WORKING)
      val bible = productionBible.value
      val bibleContext = "Tone: ${bible.tone}, Visuals: ${bible.visualLanguage}, Optics: ${bible.cameraLighting}, Editing: ${bible.editingStyle}, Character Style: ${bible.characterStyle}"
      
      val key = getEffectiveGeminiKey()
      val scenes = YouTubeScriptService.generateYouTubeScript(
        topic = scriptPremise.value,
        tone = scriptTone.value,
        duration = scriptDuration.value,
        architecture = scriptArchitecture.value,
        apiKey = key,
        productionBibleContext = bibleContext
      )
      scriptScenes.value = scenes
      isDraftingScript.value = false
      updateDepartmentStatus(DepartmentType.SCRIPT, ProductionStatus.APPROVED)

      val targetProjectId = _activeProject.value?.id ?: UUID.randomUUID().toString()
      val scriptEntity = ScriptEntity(
        id = UUID.randomUUID().toString(),
        projectId = targetProjectId,
        title = scriptPremise.value.take(40),
        topic = scriptPremise.value,
        tone = scriptTone.value,
        duration = scriptDuration.value,
        architecture = scriptArchitecture.value,
        scenesJson = scenes.joinToString("\n") { "${it.timestamp}|${it.actTitle}|${it.narration}" },
        fullScriptText = scenes.joinToString("\n\n") { "${it.timestamp} [${it.actTitle}]\n${it.narration}\n(B-Roll: ${it.bRollCue})\n(Retention: ${it.retentionAnchor})" },
        timestamp = System.currentTimeMillis()
      )
      repository.saveScript(scriptEntity)

      val updatedProject = _activeProject.value?.copy(
        content = scriptEntity.fullScriptText,
        description = "${scenes.size} Scenes * ${scriptDuration.value} * ${scriptTone.value}",
        status = "Script Approved"
      ) ?: ProjectItem(
        id = targetProjectId,
        title = scriptPremise.value.take(40),
        type = ProjectType.SCRIPT,
        description = "${scenes.size} Scenes * ${scriptDuration.value} * ${scriptTone.value}",
        content = scriptEntity.fullScriptText,
        targetDuration = scriptDuration.value,
        tone = scriptTone.value,
        channelTarget = "YouTube",
        status = "Script Approved"
      )
      repository.saveProject(updatedProject)
      _activeProject.value = updatedProject
    }
  }

  fun generateStoryboard() {
    viewModelScope.launch {
      isGeneratingStoryboard.value = true
      updateDepartmentStatus(DepartmentType.CINEMATOGRAPHY, ProductionStatus.WORKING)
      val bible = productionBible.value
      val bibleContext = "Visuals: ${bible.visualLanguage}, Optics: ${bible.cameraLighting}, Environment: ${bible.environmentStyle}, Color: ${bible.colorLanguage}"
      val shots = GeminiClient.generateStoryboard(
        premise = storyboardPremise.value,
        optics = storyboardOptics.value,
        aspectRatio = storyboardAspect.value,
        userKey = getEffectiveGeminiKey(),
        productionBibleContext = bibleContext
      )
      storyboardShots.value = shots
      isGeneratingStoryboard.value = false
      updateDepartmentStatus(DepartmentType.CINEMATOGRAPHY, ProductionStatus.APPROVED)

      repository.saveProject(
        ProjectItem(
          id = UUID.randomUUID().toString(),
          title = "Storyboard: ${storyboardOptics.value}",
          type = ProjectType.STORYBOARD,
          description = "${shots.size} Frames * ${storyboardAspect.value} * Photorealistic 8K",
          content = shots.joinToString("\n") { "Shot ${it.shotNumber}: ${it.focalLength}" }
        )
      )
    }
  }

  fun generateStoryboardFromCurrentScript(context: Context? = null) {
    viewModelScope.launch {
      isGeneratingStoryboard.value = true
      updateDepartmentStatus(DepartmentType.CINEMATOGRAPHY, ProductionStatus.WORKING)
      val bible = productionBible.value
      val bibleContext = "Visuals: ${bible.visualLanguage}, Optics: ${bible.cameraLighting}, Environment: ${bible.environmentStyle}, Color: ${bible.colorLanguage}"
      val scenes = scriptScenes.value
      val shots = GeminiClient.generateStoryboardFromScriptSegments(
        scriptScenes = scenes,
        optics = storyboardOptics.value,
        aspectRatio = storyboardAspect.value,
        userKey = getEffectiveGeminiKey(),
        productionBibleContext = bibleContext
      )
      storyboardShots.value = shots
      isGeneratingStoryboard.value = false
      updateDepartmentStatus(DepartmentType.CINEMATOGRAPHY, ProductionStatus.APPROVED)

      // Persist to Room
      val targetProjectId = _activeProject.value?.id ?: UUID.randomUUID().toString()
      val entities = shots.map { shot ->
        StoryboardEntity(
          id = shot.id,
          projectId = targetProjectId,
          shotNumber = shot.shotNumber,
          focalLength = shot.focalLength,
          cameraMovement = shot.movement,
          visualPrompt = shot.visualPrompt,
          lighting = shot.lighting,
          aspectRatio = shot.aspectRatio,
          timestamp = System.currentTimeMillis()
        )
      }
      repository.saveStoryboards(entities)

      // Update project metadata
      _activeProject.value?.let { curr ->
        val updated = curr.copy(
          storyboardCount = shots.size,
          storyboardMetadata = "${shots.size} Frames from Script (${storyboardOptics.value})"
        )
        _activeProject.value = updated
        repository.updateProject(updated)
      }

      navigateTo(StudioScreen.STORYBOARD)
    }
  }

  fun updateScriptScene(index: Int, updatedScene: ScriptScene) {
    val current = scriptScenes.value.toMutableList()
    if (index in current.indices) {
      current[index] = updatedScene
      scriptScenes.value = current
      _lastAutoSaved.value = System.currentTimeMillis()
    }
  }

  fun addNewScriptScene() {
    val current = scriptScenes.value.toMutableList()
    val nextIndex = current.size + 1
    val startMin = (nextIndex - 1) * 2
    val endMin = startMin + 2
    val newScene = ScriptScene(
      timestamp = String.format("%02d:00 - %02d:00", startMin, endMin),
      actTitle = "Act $nextIndex: Scene Escalation",
      narration = "Enter voiceover narration here...",
      bRollCue = "35mm anamorphic close-up, dynamic studio lighting",
      retentionAnchor = "Curiosity Hook / Visual Contrast"
    )
    current.add(newScene)
    scriptScenes.value = current
    _lastAutoSaved.value = System.currentTimeMillis()
  }

  fun removeScriptScene(index: Int) {
    val current = scriptScenes.value.toMutableList()
    if (index in current.indices && current.size > 1) {
      current.removeAt(index)
      scriptScenes.value = current
      _lastAutoSaved.value = System.currentTimeMillis()
    }
  }

  fun assistScript(
    action: String,
    contextText: String,
    instruction: String = "",
    onResult: (String) -> Unit
  ) {
    viewModelScope.launch {
      isAssistingScript.value = true
      scriptAssistantFeedback.value = "AI Script Assistant drafting $action..."
      val key = getEffectiveGeminiKey()
      val result = GeminiClient.assistScriptText(
        action = action,
        currentText = contextText,
        instruction = instruction,
        tone = scriptTone.value,
        userKey = key
      )
      isAssistingScript.value = false
      scriptAssistantFeedback.value = null
      onResult(result)
    }
  }

  fun exportScriptAsPlainText(context: Context) {
    val title = _activeProject.value?.title ?: scriptPremise.value.take(30)
    val scenes = scriptScenes.value
    ScriptExporter.exportAsPlainText(
      context = context,
      title = title,
      channelTarget = _activeProject.value?.channelTarget ?: "YouTube",
      tone = scriptTone.value,
      duration = scriptDuration.value,
      scenes = scenes
    )
  }

  fun exportScriptAsPdf(context: Context) {
    val title = _activeProject.value?.title ?: scriptPremise.value.take(30)
    val scenes = scriptScenes.value
    ScriptExporter.exportAsPdf(
      context = context,
      title = title,
      channelTarget = _activeProject.value?.channelTarget ?: "YouTube",
      tone = scriptTone.value,
      duration = scriptDuration.value,
      scenes = scenes
    )
  }

  fun quickShareScriptText(context: Context) {
    val title = _activeProject.value?.title ?: scriptPremise.value.take(30)
    val scenes = scriptScenes.value
    ScriptExporter.shareAsQuickText(
      context = context,
      title = title,
      channelTarget = _activeProject.value?.channelTarget ?: "YouTube",
      scenes = scenes
    )
  }

  fun getEffectiveGeminiKey(): String {
    val app = getApplication<Application>()
    val localPrefs = app.getSharedPreferences("localStorage", android.content.Context.MODE_PRIVATE)
    val k1 = localPrefs.getString("samlyt_gemini_key", "")
    if (!k1.isNullOrBlank() && k1 != "MY_GEMINI_API_KEY" && k1 != "YOUR_GEMINI_API_KEY") return k1.trim()

    val settingsPrefs = app.getSharedPreferences("samlyt_settings", android.content.Context.MODE_PRIVATE)
    val k2 = settingsPrefs.getString("samlyt_gemini_key", "")
    if (!k2.isNullOrBlank() && k2 != "MY_GEMINI_API_KEY" && k2 != "YOUR_GEMINI_API_KEY") return k2.trim()

    val k3 = _settings.value.geminiApiKey
    if (k3.isNotBlank() && k3 != "MY_GEMINI_API_KEY" && k3 != "YOUR_GEMINI_API_KEY") return k3.trim()

    return GeminiClient.resolveApiKey("")
  }

  fun getApiAspectRatio(): String {
    val asp = thumbnailAspect.value
    return when {
      asp.contains("9:16") -> "9:16"
      asp.contains("1:1") -> "1:1"
      else -> "16:9"
    }
  }

  fun clearThumbnailToast() {
    thumbnailToastMessage.value = null
  }

  fun generateThumbnailMatrix() {
    viewModelScope.launch {
      isGeneratingThumbnails.value = true
      val key = getEffectiveGeminiKey()
      val apiAspect = getApiAspectRatio()

      // 1. Generate text concepts
      val baseConcepts = GeminiClient.generateThumbnailConcepts(
        premise = thumbnailPremise.value,
        title = thumbnailTitle.value,
        archetype = thumbnailArchetype.value,
        userKey = key
      )

      // 2. Prepare prompt for Imagen 3
      val promptForImagen = baseConcepts.firstOrNull()?.midjourneyPrompt?.ifBlank { null }
        ?: "${thumbnailPremise.value}, ${thumbnailArchetype.value} archetype, YouTube thumbnail packaging, high contrast, photorealistic 8k, cinematic color grade"

      // 3. Call Imagen 3 with sampleCount: 3, and aspectRatio
      val imagenResult = GeminiClient.generateImagen3Thumbnails(
        prompt = promptForImagen,
        sampleCount = 3,
        aspectRatio = apiAspect,
        userKey = key
      )

      if (imagenResult.isSuccess && imagenResult.getOrNull()?.isNotEmpty() == true) {
        val images = imagenResult.getOrNull().orEmpty()
        val populated = baseConcepts.mapIndexed { index, concept ->
          val img = images.getOrNull(index) ?: images.firstOrNull()
          concept.copy(
            imageBase64 = img,
            generationFailed = img == null
          )
        }
        thumbnailConcepts.value = populated
      } else {
        // Imagen 3 & fallback failed or returned error
        val failedConcepts = baseConcepts.map { concept ->
          concept.copy(
            imageBase64 = null,
            generationFailed = true
          )
        }
        thumbnailConcepts.value = failedConcepts
        thumbnailToastMessage.value = "Image generation failed — showing prompt instead"
      }

      isGeneratingThumbnails.value = false

      repository.saveProject(
        ProjectItem(
          id = UUID.randomUUID().toString(),
          title = "Thumbnails: ${thumbnailTitle.value}",
          type = ProjectType.THUMBNAIL,
          description = "3 Packaging Variants * Archetype: ${thumbnailArchetype.value}",
          content = thumbnailConcepts.value.joinToString("\n\n") { "${it.title} (CTR: ${it.ctrScore}%)\n${it.midjourneyPrompt}" }
        )
      )
    }
  }

  fun regenerateThumbnailVariant(conceptId: String) {
    viewModelScope.launch {
      val currentList = thumbnailConcepts.value.toMutableList()
      val index = currentList.indexOfFirst { it.id == conceptId }
      if (index == -1) return@launch

      val target = currentList[index]
      currentList[index] = target.copy(isGeneratingImage = true, generationFailed = false)
      thumbnailConcepts.value = currentList.toList()

      val key = getEffectiveGeminiKey()
      val apiAspect = getApiAspectRatio()
      val prompt = target.midjourneyPrompt.ifBlank {
        "${thumbnailPremise.value}, ${target.archetype} composition, 8k YouTube thumbnail, cinematic lighting"
      }

      val result = GeminiClient.generateImagen3Thumbnails(
        prompt = prompt,
        sampleCount = 1,
        aspectRatio = apiAspect,
        userKey = key
      )

      val updatedList = thumbnailConcepts.value.toMutableList()
      val newIndex = updatedList.indexOfFirst { it.id == conceptId }
      if (newIndex != -1) {
        if (result.isSuccess && result.getOrNull()?.isNotEmpty() == true) {
          val newImage = result.getOrNull()!!.first()
          updatedList[newIndex] = updatedList[newIndex].copy(
            imageBase64 = newImage,
            isGeneratingImage = false,
            generationFailed = false
          )
        } else {
          updatedList[newIndex] = updatedList[newIndex].copy(
            isGeneratingImage = false,
            generationFailed = true
          )
          thumbnailToastMessage.value = "Image generation failed — showing prompt instead"
        }
        thumbnailConcepts.value = updatedList.toList()
      }
    }
  }

  fun saveThumbnailToVault(concept: ThumbnailConcept) {
    val base64 = concept.imageBase64 ?: return
    val app = getApplication<Application>()
    try {
      // 1. Store in localStorage SharedPreferences under key "vault_thumbnails"
      val localPrefs = app.getSharedPreferences("localStorage", android.content.Context.MODE_PRIVATE)
      val existingJson = localPrefs.getString("vault_thumbnails", "[]") ?: "[]"
      val array = try { org.json.JSONArray(existingJson) } catch (e: Exception) { org.json.JSONArray() }

      val itemObj = org.json.JSONObject().apply {
        put("id", concept.id)
        put("conceptName", concept.title)
        put("suggestedTitle", concept.suggestedTitle)
        put("ctrScore", concept.ctrScore)
        put("base64", base64)
        put("timestamp", System.currentTimeMillis())
      }
      array.put(itemObj)
      localPrefs.edit().putString("vault_thumbnails", array.toString()).apply()

      // Also in samlyt_settings
      val settingsPrefs = app.getSharedPreferences("samlyt_settings", android.content.Context.MODE_PRIVATE)
      settingsPrefs.edit().putString("vault_thumbnails", array.toString()).apply()

      loadVaultThumbnails()

      // Also save to repository project so it appears in Projects & Vault
      viewModelScope.launch {
        repository.saveProject(
          ProjectItem(
            id = UUID.randomUUID().toString(),
            title = "Vault: ${concept.title}",
            type = ProjectType.THUMBNAIL,
            description = "Saved Thumbnail * CTR: ${concept.ctrScore}%",
            content = "Concept: ${concept.title}\nSuggested Title: ${concept.suggestedTitle}\nPrompt: ${concept.midjourneyPrompt}\n[Base64 Image Cached in Vault]"
          )
        )
      }
    } catch (e: Exception) {
      // ignore
    }
  }

  fun loadVaultThumbnails() {
    val app = getApplication<Application>()
    try {
      val localPrefs = app.getSharedPreferences("localStorage", android.content.Context.MODE_PRIVATE)
      val json = localPrefs.getString("vault_thumbnails", null)
        ?: app.getSharedPreferences("samlyt_settings", android.content.Context.MODE_PRIVATE).getString("vault_thumbnails", "[]")
        ?: "[]"
      val array = org.json.JSONArray(json)
      val list = mutableListOf<SavedVaultThumbnail>()
      for (i in 0 until array.length()) {
        val obj = array.optJSONObject(i) ?: continue
        list.add(
          SavedVaultThumbnail(
            id = obj.optString("id", UUID.randomUUID().toString()),
            conceptName = obj.optString("conceptName", "Thumbnail"),
            suggestedTitle = obj.optString("suggestedTitle", ""),
            ctrScore = obj.optInt("ctrScore", 85),
            base64 = obj.optString("base64", ""),
            timestamp = obj.optLong("timestamp", System.currentTimeMillis())
          )
        )
      }
      vaultThumbnails.value = list.reversed()
    } catch (e: Exception) {
      vaultThumbnails.value = emptyList()
    }
  }

  fun deleteVaultThumbnail(id: String) {
    val app = getApplication<Application>()
    try {
      val localPrefs = app.getSharedPreferences("localStorage", android.content.Context.MODE_PRIVATE)
      val settingsPrefs = app.getSharedPreferences("samlyt_settings", android.content.Context.MODE_PRIVATE)
      val json = localPrefs.getString("vault_thumbnails", "[]") ?: "[]"
      val array = org.json.JSONArray(json)
      val newArray = org.json.JSONArray()
      for (i in 0 until array.length()) {
        val obj = array.optJSONObject(i) ?: continue
        if (obj.optString("id") != id) {
          newArray.put(obj)
        }
      }
      localPrefs.edit().putString("vault_thumbnails", newArray.toString()).apply()
      settingsPrefs.edit().putString("vault_thumbnails", newArray.toString()).apply()
      loadVaultThumbnails()
    } catch (e: Exception) {
      // ignore
    }
  }

  fun sendVoiceMessage(userText: String) {
    if (userText.isBlank()) return
    val userTurn = VoiceThought(
      id = UUID.randomUUID().toString(),
      speaker = "Director (You)",
      timeAgo = "Just now",
      text = userText
    )
    val updated = voiceThoughtStream.value.toMutableList().apply { add(userTurn) }
    voiceThoughtStream.value = updated

    viewModelScope.launch {
      val aiResponse = GeminiClient.streamVoiceDirector(
        userMessage = userText,
        history = emptyList(),
        mode = voiceDirectorMode.value,
        userKey = getEffectiveGeminiKey()
      )
      val aiTurn = VoiceThought(
        id = UUID.randomUUID().toString(),
        speaker = "SAMLYT AI (Female Director)",
        timeAgo = "Just now",
        text = aiResponse,
        isSynthesizing = true,
        actionPills = listOf("Add B-Roll Cue", "Script Anchor")
      )
      voiceThoughtStream.value = voiceThoughtStream.value + aiTurn
      // Female voice output triggered whenever voice agent responds
      voiceAgentSpeaker.speak(aiResponse)
    }
  }

  fun startVoiceListening() {
    speechRecognizerManager.startListening()
  }

  fun stopVoiceListening() {
    speechRecognizerManager.stopListening()
  }

  fun cancelVoiceListening() {
    speechRecognizerManager.cancel()
  }

  fun toggleVoiceMute(): Boolean {
    return voiceAgentSpeaker.toggleMute()
  }

  fun setVoiceSpeechRate(rate: Float) {
    voiceAgentSpeaker.setSpeechRate(rate)
  }

  fun setVoicePitch(pitch: Float) {
    voiceAgentSpeaker.setPitch(pitch)
  }

  fun clearVoiceThoughts() {
    voiceThoughtStream.value = emptyList()
  }

  fun handleRecognizedVoiceCommand(spokenText: String) {
    if (spokenText.isBlank()) return

    val commandResult = VoiceCommandProcessor.process(spokenText)

    when (commandResult) {
      is VoiceCommandResult.Navigation -> {
        val userTurn = VoiceThought(
          id = UUID.randomUUID().toString(),
          speaker = "Director (You)",
          timeAgo = "Just now",
          text = spokenText
        )
        val aiTurn = VoiceThought(
          id = UUID.randomUUID().toString(),
          speaker = "SAMLYT AI (Female Director)",
          timeAgo = "Just now",
          text = commandResult.verbalFeedback,
          isSynthesizing = true,
          actionPills = listOf("Navigation", commandResult.destination.name)
        )
        voiceThoughtStream.value = voiceThoughtStream.value + listOf(userTurn, aiTurn)

        // Provide verbal feedback aloud through TextToSpeech, then navigate
        voiceAgentSpeaker.speak(commandResult.verbalFeedback) {
          viewModelScope.launch {
            delay(350)
            navigateTo(commandResult.destination)
          }
        }
      }

      is VoiceCommandResult.ModeChange -> {
        val userTurn = VoiceThought(
          id = UUID.randomUUID().toString(),
          speaker = "Director (You)",
          timeAgo = "Just now",
          text = spokenText
        )
        val aiTurn = VoiceThought(
          id = UUID.randomUUID().toString(),
          speaker = "SAMLYT AI (Female Director)",
          timeAgo = "Just now",
          text = commandResult.verbalFeedback,
          isSynthesizing = true,
          actionPills = listOf("Mode Switch", commandResult.newMode)
        )
        voiceDirectorMode.value = commandResult.newMode
        voiceThoughtStream.value = voiceThoughtStream.value + listOf(userTurn, aiTurn)
        voiceAgentSpeaker.speak(commandResult.verbalFeedback)
      }

      is VoiceCommandResult.DirectAction -> {
        when (commandResult.actionType) {
          ActionType.CLEAR_STREAM -> {
            clearVoiceThoughts()
            voiceAgentSpeaker.speak(commandResult.verbalFeedback)
          }
          ActionType.COMMIT_TO_SCRIPT -> {
            commitVoiceToScript()
            voiceAgentSpeaker.speak(commandResult.verbalFeedback)
          }
          ActionType.STOP_SPEECH -> {
            stopVoiceAgentSpeech()
          }
          ActionType.TOGGLE_MUTE -> {
            val isNowMuted = voiceAgentSpeaker.toggleMute()
            if (!isNowMuted) {
              voiceAgentSpeaker.speak("Voice unmuted.")
            }
          }
          ActionType.TEST_VOICE -> {
            voiceAgentSpeaker.speak(commandResult.verbalFeedback)
          }
        }
      }

      is VoiceCommandResult.CreativeQuery -> {
        sendVoiceMessage(spokenText)
      }
    }
  }

  fun speakTextWithVoiceAgent(text: String) {
    voiceAgentSpeaker.speak(text)
  }

  fun stopVoiceAgentSpeech() {
    voiceAgentSpeaker.stop()
  }

  fun commitVoiceToScript() {
    val lastDirectorThought = voiceThoughtStream.value.lastOrNull { it.speaker.contains("Director") }?.text
    if (!lastDirectorThought.isNullOrBlank()) {
      scriptPremise.value = lastDirectorThought
    }
    navigateTo(StudioScreen.SCRIPT_STUDIO)
  }

  fun updateApiKey(newKey: String) {
    val updated = _settings.value.copy(geminiApiKey = newKey.trim())
    _settings.value = updated
    repository.saveSettings(updated)
    testGeminiApi()
  }

  fun testGeminiApi() {
    viewModelScope.launch {
      isTestingApi.value = true
      apiTestMessage.value = "Testing Gemini API connection..."
      val key = getEffectiveGeminiKey()
      val (success, message) = YouTubeScriptService.testConnection(key)
      apiTestMessage.value = message
      isTestingApi.value = false
      isApiConnected.value = success
      apiConnectionStatus.value = if (success) "Connected (Gemini 3.5 Flash / Retrofit Active)" else "API Offline: $message"
    }
  }

  fun updateSettings(newSettings: UserSettings) {
    _settings.value = newSettings
    viewModelScope.launch {
      repository.saveSettingsToRoomAndPrefs(newSettings)
    }
  }

  fun deleteProject(id: String) {
    viewModelScope.launch {
      repository.deleteProject(id)
      if (_activeProject.value?.id == id) {
        _activeProject.value = projects.value.firstOrNull { it.id != id }
      }
    }
  }

  override fun onCleared() {
    super.onCleared()
    speechRecognizerManager.destroy()
    voiceAgentSpeaker.shutdown()
  }

  fun purgeCache() {
    val current = _settings.value
    val updated = current.copy(vaultStorageUsedMb = 0.0)
    _settings.value = updated
    repository.saveSettings(updated)
  }

  fun updateProductionBible(bible: ProductionBible) {
    productionBible.value = bible
    _lastAutoSaved.value = System.currentTimeMillis()
  }

  fun addCastMember(name: String, tokens: String, voiceProfile: String) {
    val newMember = CastMember(
      id = UUID.randomUUID().toString(),
      name = name,
      continuityTokens = tokens,
      voiceProfile = voiceProfile,
      appearances = 1
    )
    castMembers.value = castMembers.value + newMember
    _lastAutoSaved.value = System.currentTimeMillis()
  }

  fun addLocationItem(name: String, style: String, continuity: String) {
    val newLoc = LocationItem(
      id = UUID.randomUUID().toString(),
      name = name,
      environmentStyle = style,
      continuityNotes = continuity,
      shotHistoryCount = 1
    )
    locationItems.value = locationItems.value + newLoc
    _lastAutoSaved.value = System.currentTimeMillis()
  }

  fun updateDepartmentStatus(department: DepartmentType, status: ProductionStatus) {
    val map = departmentStatuses.value.toMutableMap()
    map[department] = status
    departmentStatuses.value = map
  }

  fun parseProductionDocument(documentText: String) {
    rawDocumentText.value = documentText
    if (documentText.isBlank()) return

    val beats = mutableListOf<ParsedBeat>()
    // Split text into scene blocks
    val sceneBlocks = documentText.split(Regex("(?i)(?=Scene\\s*\\d+)"))
      .filter { it.isNotBlank() }

    var sceneIdx = 1
    for (block in sceneBlocks) {
      val lines = block.lines().map { it.trim() }.filter { it.isNotEmpty() }
      var title = "Scene $sceneIdx"
      var visualLine = ""
      var animationLine: String? = null

      for (line in lines) {
        if (line.startsWith("Scene", ignoreCase = true)) {
          val colonIdx = line.indexOf(':')
          if (colonIdx != -1 && colonIdx < line.length - 1) {
            title = line.substring(colonIdx + 1).trim()
          } else {
            title = line
          }
        } else if (line.startsWith("Visual:", ignoreCase = true)) {
          visualLine = line.substring(7).trim()
        } else if (line.startsWith("Animation:", ignoreCase = true) || line.startsWith("Animation ", ignoreCase = true)) {
          val colonIdx = line.indexOf(':')
          animationLine = if (colonIdx != -1) line.substring(colonIdx + 1).trim() else line.substring(9).trim()
        } else if (visualLine.isEmpty() && !line.startsWith("Scene", ignoreCase = true)) {
          visualLine = line
        }
      }

      if (visualLine.isBlank()) {
        visualLine = block.take(120).replace("\n", " ")
      }

      // Check for cast continuity matches
      val matchedCast = castMembers.value.filter { cast ->
        block.contains(cast.name, ignoreCase = true) || visualLine.contains(cast.name, ignoreCase = true)
      }
      val continuityNotes = matchedCast.joinToString(" | ") { "${it.name}: ${it.continuityTokens}" }

      // CRITICAL RULE: Only scenes with an explicit Animation line are flagged for video generation (Veo)
      // Every other scene defaults to a still image
      val genType = if (!animationLine.isNullOrBlank()) {
        GenerationType.VIDEO_VEO
      } else {
        GenerationType.STILL_IMAGE
      }

      beats.add(
        ParsedBeat(
          sceneNumber = sceneIdx,
          rawText = block,
          visualPrompt = visualLine,
          animationPrompt = animationLine,
          generationType = genType,
          matchedCastNames = matchedCast.map { it.name },
          appliedContinuityNotes = continuityNotes,
          status = ProductionStatus.REVIEW
        )
      )
      sceneIdx++
    }

    parsedBeats.value = beats
    isReviewCheckpointVisible.value = true
    updateDepartmentStatus(DepartmentType.BREAKDOWN, ProductionStatus.REVIEW)
  }

  fun approveParsedBeats() {
    val beats = parsedBeats.value
    if (beats.isEmpty()) return

    // Convert approved beats into storyboard shots & script scenes
    val newShots = beats.map { beat ->
      StoryboardShot(
        id = UUID.randomUUID().toString(),
        shotNumber = beat.sceneNumber,
        focalLength = if (beat.generationType == GenerationType.VIDEO_VEO) "Veo 4s Gen-Video" else "35mm Anamorphic Still",
        movement = beat.animationPrompt ?: "Locked Static Framing",
        visualPrompt = beat.visualPrompt + if (beat.appliedContinuityNotes.isNotBlank()) " [Continuity: ${beat.appliedContinuityNotes}]" else "",
        lighting = "Production Bible 5600K Key + Rim Haze",
        aspectRatio = storyboardAspect.value
      )
    }
    storyboardShots.value = newShots

    val newScenes = beats.map { beat ->
      val timestampMinutes = (beat.sceneNumber - 1) * 15 / 60
      val timestampSeconds = ((beat.sceneNumber - 1) * 15) % 60
      val timeFormatted = String.format("0%d:%02d", timestampMinutes, timestampSeconds)
      ScriptScene(
        timestamp = timeFormatted,
        actTitle = "Scene ${beat.sceneNumber}",
        narration = beat.visualPrompt,
        bRollCue = if (beat.generationType == GenerationType.VIDEO_VEO) "VEO VIDEO: ${beat.animationPrompt}" else "STILL PLATE: ${beat.visualPrompt.take(50)}...",
        retentionAnchor = if (beat.generationType == GenerationType.VIDEO_VEO) "Dynamic Motion Retention Spike" else "Curiosity Gap Anchor"
      )
    }
    scriptScenes.value = newScenes

    // Update statuses
    updateDepartmentStatus(DepartmentType.BREAKDOWN, ProductionStatus.APPROVED)
    updateDepartmentStatus(DepartmentType.SCRIPT, ProductionStatus.APPROVED)
    updateDepartmentStatus(DepartmentType.CINEMATOGRAPHY, ProductionStatus.APPROVED)
    updateDepartmentStatus(DepartmentType.VFX, ProductionStatus.APPROVED)

    // Record new Version proposal
    val newVersion = ProductionProposal(
      id = UUID.randomUUID().toString(),
      department = DepartmentType.BREAKDOWN,
      title = "Applied Document Import (${beats.size} beats approved)",
      analysis = "Explicit document beat breakdown confirmed by creator. Cast continuity bound.",
      proposedChange = "Populated Storyboard and CineScript workspaces with approved beat sequence.",
      previewContent = "Beats approved: ${beats.size} scenes (${beats.count { it.generationType == GenerationType.VIDEO_VEO }} Veo motion shots, ${beats.count { it.generationType == GenerationType.STILL_IMAGE }} still frames).",
      versionTag = "V2.4.${System.currentTimeMillis() % 1000}",
      status = ProductionStatus.APPROVED
    )
    productionProposals.value = listOf(newVersion) + productionProposals.value
    isReviewCheckpointVisible.value = false
    _lastAutoSaved.value = System.currentTimeMillis()
  }

  fun rejectParsedBeats() {
    isReviewCheckpointVisible.value = false
    updateDepartmentStatus(DepartmentType.BREAKDOWN, ProductionStatus.FAILED)
  }

  fun approveProposal(proposalId: String) {
    val list = productionProposals.value.map { prop ->
      if (prop.id == proposalId) {
        prop.copy(status = ProductionStatus.APPROVED)
      } else {
        prop
      }
    }
    productionProposals.value = list
    _lastAutoSaved.value = System.currentTimeMillis()
  }

  fun rejectProposal(proposalId: String) {
    val list = productionProposals.value.map { prop ->
      if (prop.id == proposalId) {
        prop.copy(status = ProductionStatus.FAILED)
      } else {
        prop
      }
    }
    productionProposals.value = list
    _lastAutoSaved.value = System.currentTimeMillis()
  }
}

