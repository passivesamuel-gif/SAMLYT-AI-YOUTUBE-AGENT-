package com.example.data.model

enum class StudioScreen {
  LANDING,
  AUTH,
  ONBOARDING,
  HOME,
  RESEARCH,
  SCRIPT_STUDIO,
  STORYBOARD,
  THUMBNAIL_STUDIO,
  VOICE_DIRECTOR,
  PROJECTS,
  VAULT,
  SETTINGS
}

enum class ProjectType {
  SCRIPT,
  STORYBOARD,
  THUMBNAIL,
  VOICE,
  RESEARCH
}

data class ProjectItem(
  val id: String,
  val title: String,
  val type: ProjectType,
  val description: String,
  val timestamp: Long = System.currentTimeMillis(),
  val creationDate: Long = timestamp,
  val targetDuration: String = "8 - 12 min",
  val tone: String = "Cinematic",
  val content: String = "",
  val isEncrypted: Boolean = true,
  val channelTarget: String = "YouTube",
  val status: String = "In Production",
  val scriptMetadata: String = "",
  val storyboardMetadata: String = "",
  val scriptCount: Int = 0,
  val storyboardCount: Int = 0
)

data class ResearchResult(
  val videoUrl: String,
  val channelTitle: String,
  val videoTitle: String,
  val hookScore: Int,
  val retentionCurveNotes: String,
  val viralAngles: List<String>,
  val keyHooks: List<String>,
  val audienceGaps: List<String>,
  val recommendedStructure: String,
  val formulaBlueprint: YouTubeFormulaBlueprint? = null
)

data class YouTubeFormulaBlueprint(
  val channelOrUrl: String,
  val creatorStyle: String,
  val hookFormula: String,
  val pacingCutRate: String,
  val narrativeFramework: String,
  val visualAesthetic: String,
  val soundDesignArchitecture: String,
  val workflowSteps: List<String>,
  val sampleScriptArchitecture: String = "3-Act Viral Essay"
) {
  val channelName: String get() = channelOrUrl
  val creatorArchetype: String get() = creatorStyle
  val signatureHookFormula: String get() = hookFormula
  val videoTempoAndCutCadence: String get() = pacingCutRate
  val visualStyleAndCameraPackage: String get() = visualAesthetic
  val audioAndSoundDesignFormula: String get() = soundDesignArchitecture
  val repetitiveSystemWorkflow: List<String> get() = workflowSteps
}

data class ScriptScene(
  val timestamp: String,
  val actTitle: String,
  val narration: String,
  val bRollCue: String,
  val retentionAnchor: String
)

data class StoryboardShot(
  val id: String,
  val shotNumber: Int,
  val focalLength: String,
  val movement: String,
  val visualPrompt: String,
  val lighting: String,
  val aspectRatio: String = "16:9"
)

data class ThumbnailConcept(
  val id: String,
  val title: String,
  val archetype: String,
  val midjourneyPrompt: String,
  val focalSubject: String,
  val lightingColor: String,
  val ctrScore: Int,
  val suggestedTitle: String,
  val imageBase64: String? = null,
  val isGeneratingImage: Boolean = false,
  val generationFailed: Boolean = false,
  val errorMessage: String? = null
)

data class SavedVaultThumbnail(
  val id: String,
  val conceptName: String,
  val suggestedTitle: String,
  val ctrScore: Int,
  val base64: String,
  val timestamp: Long
)

data class VoiceThought(
  val id: String,
  val speaker: String,
  val timeAgo: String,
  val text: String,
  val isSynthesizing: Boolean = false,
  val actionPills: List<String> = emptyList()
)

data class UserSettings(
  val geminiApiKey: String = "",
  val email: String = "alex.creator@studio.samlyt.ai",
  val productionFocus: String = "Cinematic Longform",
  val nleTarget: String = "Final Cut Pro (FCPXML)",
  val baseFramerate: String = "24 fps",
  val scriptVoice: String = "Aperture Studio (Neutral)",
  val lowLatencyVoice: Boolean = true,
  val zeroTelemetry: Boolean = true,
  val biometricLock: Boolean = true,
  val autoSaveIntervalSeconds: Int = 30,
  val vaultStorageUsedMb: Double = 0.0,
  val totalVaultMb: Double = 50000.0,
  val airGapArmed: Boolean = true
)

enum class DepartmentType(val displayName: String, val roleSummary: String) {
  SCRIPT("Script", "Narrative thesis, screenplay & beat dialogue"),
  BREAKDOWN("Breakdown", "Scene extraction & asset schedule"),
  DIRECTOR("Director", "Vision, dramatic pacing & hook arc"),
  CINEMATOGRAPHY("Cinematography", "Camera packages, lens selection & lighting"),
  PERFORMANCE("Performance", "Voice cadence, delivery & scratch audio"),
  LOCATION("Location", "World continuity & environmental style"),
  VFX("VFX / Veo", "Generative video & motion dynamics"),
  SOUND("Sound", "Score, foley, ambience & soundscapes"),
  EDITOR("Editor", "Retention anchors, cut cadence & NLE xml"),
  DISTRIBUTION("Distribution", "Packaging, CTR testing & thumbnail hooks")
}

enum class ProductionStatus(val label: String) {
  QUEUED("Queued"),
  ANALYZING("Analyzing"),
  WORKING("Working"),
  REVIEW("Review"),
  APPROVED("Approved"),
  FAILED("Failed")
}

enum class GenerationType {
  STILL_IMAGE,
  VIDEO_VEO
}

data class ProductionBible(
  val title: String = "The Silicon Anomaly",
  val visualLanguage: String = "35mm anamorphic, heavy rim lighting, desaturated shadows, warm skin tones",
  val cameraLighting: String = "Arri Alexa Mini, Master Anamorphic primes, 5600K key + 3200K practicals",
  val editingStyle: String = "Fast-cut hooks, 4.2s average shot length, match cuts on subject motion",
  val tone: String = "Investigative, tense, intellectual, cinematic documentary",
  val characterStyle: String = "Faceless silhouettes, low-angle dominance, recurring investigator figure",
  val environmentStyle: String = "Brutalist architecture, neon rain-slicked streets, subterranean archives",
  val colorLanguage: String = "Cyan-orange dual tone, deep carbon blacks, amber warning accents"
)

data class CastMember(
  val id: String,
  val name: String,
  val continuityTokens: String,
  val voiceProfile: String,
  val appearances: Int = 1
)

data class LocationItem(
  val id: String,
  val name: String,
  val environmentStyle: String,
  val continuityNotes: String,
  val shotHistoryCount: Int = 1
)

data class ParsedBeat(
  val sceneNumber: Int,
  val rawText: String,
  val visualPrompt: String,
  val animationPrompt: String?,
  val generationType: GenerationType,
  val matchedCastNames: List<String> = emptyList(),
  val appliedContinuityNotes: String = "",
  val status: ProductionStatus = ProductionStatus.REVIEW
)

data class ProductionProposal(
  val id: String,
  val department: DepartmentType,
  val title: String,
  val analysis: String,
  val proposedChange: String,
  val previewContent: String,
  val versionTag: String,
  val status: ProductionStatus = ProductionStatus.REVIEW,
  val timestamp: Long = System.currentTimeMillis()
)

