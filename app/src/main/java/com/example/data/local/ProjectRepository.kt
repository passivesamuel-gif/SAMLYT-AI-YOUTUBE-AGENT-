package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.ProjectItem
import com.example.data.model.ProjectType
import com.example.data.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProjectRepository(
  private val projectDao: ProjectDao,
  private val scriptDao: ScriptDao? = null,
  private val storyboardDao: StoryboardDao? = null,
  private val settingsDao: SettingsDao? = null,
  context: Context
) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences("samlyt_settings", Context.MODE_PRIVATE)

  private val localPrefs: SharedPreferences =
    context.getSharedPreferences("localStorage", Context.MODE_PRIVATE)

  val allProjects: Flow<List<ProjectItem>> = projectDao.getAllProjects().map { entities ->
    entities.map { entity ->
      ProjectItem(
        id = entity.id,
        title = entity.title,
        type = try { ProjectType.valueOf(entity.type) } catch (e: Exception) { ProjectType.SCRIPT },
        description = entity.description,
        timestamp = entity.timestamp,
        creationDate = entity.creationDate,
        targetDuration = entity.targetDuration,
        tone = entity.tone,
        content = entity.content,
        isEncrypted = entity.isEncrypted,
        channelTarget = entity.channelTarget,
        status = entity.status,
        scriptMetadata = entity.scriptMetadata,
        storyboardMetadata = entity.storyboardMetadata,
        scriptCount = entity.scriptCount,
        storyboardCount = entity.storyboardCount
      )
    }
  }

  suspend fun getProjectById(id: String): ProjectItem? {
    val entity = projectDao.getProjectById(id) ?: return null
    return ProjectItem(
      id = entity.id,
      title = entity.title,
      type = try { ProjectType.valueOf(entity.type) } catch (e: Exception) { ProjectType.SCRIPT },
      description = entity.description,
      timestamp = entity.timestamp,
      creationDate = entity.creationDate,
      targetDuration = entity.targetDuration,
      tone = entity.tone,
      content = entity.content,
      isEncrypted = entity.isEncrypted,
      channelTarget = entity.channelTarget,
      status = entity.status,
      scriptMetadata = entity.scriptMetadata,
      storyboardMetadata = entity.storyboardMetadata,
      scriptCount = entity.scriptCount,
      storyboardCount = entity.storyboardCount
    )
  }

  suspend fun saveProject(project: ProjectItem) {
    projectDao.insertProject(
      ProjectEntity(
        id = project.id,
        title = project.title,
        type = project.type.name,
        description = project.description,
        timestamp = project.timestamp,
        creationDate = project.creationDate,
        targetDuration = project.targetDuration,
        tone = project.tone,
        content = project.content,
        isEncrypted = project.isEncrypted,
        channelTarget = project.channelTarget,
        status = project.status,
        scriptMetadata = project.scriptMetadata,
        storyboardMetadata = project.storyboardMetadata,
        scriptCount = project.scriptCount,
        storyboardCount = project.storyboardCount
      )
    )
  }

  suspend fun updateProject(project: ProjectItem) {
    projectDao.updateProject(
      ProjectEntity(
        id = project.id,
        title = project.title,
        type = project.type.name,
        description = project.description,
        timestamp = project.timestamp,
        creationDate = project.creationDate,
        targetDuration = project.targetDuration,
        tone = project.tone,
        content = project.content,
        isEncrypted = project.isEncrypted,
        channelTarget = project.channelTarget,
        status = project.status,
        scriptMetadata = project.scriptMetadata,
        storyboardMetadata = project.storyboardMetadata,
        scriptCount = project.scriptCount,
        storyboardCount = project.storyboardCount
      )
    )
  }

  suspend fun updateProjectStatus(id: String, status: String) {
    projectDao.updateProjectStatus(id, status)
  }

  suspend fun deleteProject(id: String) {
    projectDao.deleteProjectById(id)
    scriptDao?.deleteScriptsByProjectId(id)
    storyboardDao?.deleteStoryboardsByProjectId(id)
  }

  // Scripts DAO operations
  fun getScriptsForProject(projectId: String): Flow<List<ScriptEntity>> {
    return scriptDao?.getScriptsByProjectId(projectId) ?: kotlinx.coroutines.flow.flowOf(emptyList())
  }

  fun getAllScripts(): Flow<List<ScriptEntity>> {
    return scriptDao?.getAllScripts() ?: kotlinx.coroutines.flow.flowOf(emptyList())
  }

  suspend fun saveScript(script: ScriptEntity) {
    scriptDao?.insertScript(script)
  }

  suspend fun deleteScript(id: String) {
    scriptDao?.deleteScriptById(id)
  }

  // Storyboards DAO operations
  fun getStoryboardsForProject(projectId: String): Flow<List<StoryboardEntity>> {
    return storyboardDao?.getStoryboardsByProjectId(projectId) ?: kotlinx.coroutines.flow.flowOf(emptyList())
  }

  fun getAllStoryboards(): Flow<List<StoryboardEntity>> {
    return storyboardDao?.getAllStoryboards() ?: kotlinx.coroutines.flow.flowOf(emptyList())
  }

  suspend fun saveStoryboard(shot: StoryboardEntity) {
    storyboardDao?.insertStoryboard(shot)
  }

  suspend fun saveStoryboards(shots: List<StoryboardEntity>) {
    storyboardDao?.insertStoryboards(shots)
  }

  suspend fun deleteStoryboard(id: String) {
    storyboardDao?.deleteStoryboardById(id)
  }

  // Settings operations
  fun loadSettings(): UserSettings {
    val rawKey = localPrefs.getString("samlyt_gemini_key", null)
      ?: prefs.getString("samlyt_gemini_key", null)
      ?: prefs.getString("gemini_api_key", "")
      ?: ""
    val resolvedKey = com.example.data.api.GeminiClient.resolveApiKey(rawKey)
    return UserSettings(
      geminiApiKey = if (resolvedKey.isNotBlank()) resolvedKey else rawKey,
      email = prefs.getString("user_email", "alex.creator@studio.samlyt.ai") ?: "alex.creator@studio.samlyt.ai",
      productionFocus = prefs.getString("production_focus", "Cinematic Longform") ?: "Cinematic Longform",
      nleTarget = prefs.getString("nle_target", "Final Cut Pro (FCPXML)") ?: "Final Cut Pro (FCPXML)",
      baseFramerate = prefs.getString("base_framerate", "24 fps") ?: "24 fps",
      scriptVoice = prefs.getString("script_voice", "Aperture Studio (Female)") ?: "Aperture Studio (Female)",
      lowLatencyVoice = prefs.getBoolean("low_latency_voice", true),
      zeroTelemetry = prefs.getBoolean("zero_telemetry", true),
      biometricLock = prefs.getBoolean("biometric_lock", true),
      vaultStorageUsedMb = prefs.getFloat("vault_used_mb", 1.2f).toDouble(),
      totalVaultMb = 50000.0,
      airGapArmed = prefs.getBoolean("air_gap_armed", true)
    )
  }

  suspend fun saveSettingsToRoomAndPrefs(settings: UserSettings) {
    saveSettings(settings)
    settingsDao?.insertSettings(
      ProjectSettingsEntity(
        key = "default_settings",
        geminiApiKey = settings.geminiApiKey,
        email = settings.email,
        voiceName = "Kore (Female Studio Voice)",
        voiceGender = "Female",
        productionFocus = settings.productionFocus,
        nleTarget = settings.nleTarget,
        baseFramerate = settings.baseFramerate,
        scriptVoice = settings.scriptVoice,
        lowLatencyVoice = settings.lowLatencyVoice,
        zeroTelemetry = settings.zeroTelemetry,
        biometricLock = settings.biometricLock,
        vaultStorageUsedMb = settings.vaultStorageUsedMb,
        airGapArmed = settings.airGapArmed,
        updatedAt = System.currentTimeMillis()
      )
    )
  }

  fun saveSettings(settings: UserSettings) {
    prefs.edit()
      .putString("gemini_api_key", settings.geminiApiKey)
      .putString("samlyt_gemini_key", settings.geminiApiKey)
      .putString("user_email", settings.email)
      .putString("production_focus", settings.productionFocus)
      .putString("nle_target", settings.nleTarget)
      .putString("base_framerate", settings.baseFramerate)
      .putString("script_voice", settings.scriptVoice)
      .putBoolean("low_latency_voice", settings.lowLatencyVoice)
      .putBoolean("zero_telemetry", settings.zeroTelemetry)
      .putBoolean("biometric_lock", settings.biometricLock)
      .putFloat("vault_used_mb", settings.vaultStorageUsedMb.toFloat())
      .putBoolean("air_gap_armed", settings.airGapArmed)
      .apply()

    localPrefs.edit()
      .putString("samlyt_gemini_key", settings.geminiApiKey)
      .apply()
  }
}
