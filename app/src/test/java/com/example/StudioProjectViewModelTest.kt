package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.api.GeminiScriptApiRequest
import com.example.data.api.PartDto
import com.example.data.api.ContentDto
import com.example.data.local.ProjectEntity
import com.example.data.local.ProjectRepository
import com.example.data.local.SamlytDatabase
import com.example.data.local.ScriptEntity
import com.example.data.local.StoryboardEntity
import com.example.data.model.ProjectItem
import com.example.data.model.ProjectType
import com.example.viewmodel.StudioViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class StudioProjectViewModelTest {

  private lateinit var application: Application
  private lateinit var database: SamlytDatabase
  private lateinit var repository: ProjectRepository

  @Before
  fun setup() {
    application = ApplicationProvider.getApplicationContext()
    database = SamlytDatabase.getDatabase(application)
    repository = ProjectRepository(
      projectDao = database.projectDao(),
      scriptDao = database.scriptDao(),
      storyboardDao = database.storyboardDao(),
      settingsDao = database.settingsDao(),
      context = application
    )
  }

  @Test
  fun testRoomProjectPersistenceAndRetrieval() = runBlocking {
    val projId = UUID.randomUUID().toString()
    val project = ProjectItem(
      id = projId,
      title = "Why 8K Digital Ruined Cinema",
      type = ProjectType.SCRIPT,
      description = "10 min * Cinematic",
      content = "Act I Hook: The illusion of resolution...",
      channelTarget = "YouTube",
      status = "In Production"
    )

    repository.saveProject(project)

    val allProjects = repository.allProjects.first()
    val saved = allProjects.firstOrNull { it.id == projId }
    assertNotNull(saved)
    assertEquals("Why 8K Digital Ruined Cinema", saved?.title)
    assertEquals("YouTube", saved?.channelTarget)
  }

  @Test
  fun testScriptAndStoryboardRoomDaos() = runBlocking {
    val projId = UUID.randomUUID().toString()
    val script = ScriptEntity(
      id = UUID.randomUUID().toString(),
      projectId = projId,
      title = "Lens Distortion Psychology",
      topic = "Why lenses bend reality",
      tone = "Cinematic",
      duration = "8 - 12 min",
      architecture = "3-Act Essay",
      scenesJson = "0:00|Hook|Narration",
      fullScriptText = "Scene 1: The Hitchcock Zoom",
      timestamp = System.currentTimeMillis()
    )
    repository.saveScript(script)

    val loadedScripts = repository.getScriptsForProject(projId).first()
    assertEquals(1, loadedScripts.size)
    assertEquals("Lens Distortion Psychology", loadedScripts[0].title)

    val storyboard = StoryboardEntity(
      id = UUID.randomUUID().toString(),
      projectId = projId,
      shotNumber = 1,
      focalLength = "35mm Anamorphic",
      cameraMovement = "Slow Push-In",
      visualPrompt = "Close-up anamorphic lens flare across dark studio table",
      lighting = "Key Rembrandt 5600K",
      aspectRatio = "16:9",
      timestamp = System.currentTimeMillis()
    )
    repository.saveStoryboard(storyboard)

    val loadedStoryboards = repository.getStoryboardsForProject(projId).first()
    assertEquals(1, loadedStoryboards.size)
    assertEquals("35mm Anamorphic", loadedStoryboards[0].focalLength)
    assertEquals("Slow Push-In", loadedStoryboards[0].cameraMovement)
  }

  @Test
  fun testRetrofitRequestPayload() {
    val request = GeminiScriptApiRequest(
      contents = listOf(
        ContentDto(
          parts = listOf(PartDto(text = "Generate a YouTube hook on cinema lenses"))
        )
      )
    )
    assertEquals(1, request.contents.size)
    assertEquals("Generate a YouTube hook on cinema lenses", request.contents[0].parts[0].text)
  }

  @Test
  fun testViewModelActiveProjectManagement() = runBlocking {
    val viewModel = StudioViewModel(application)

    val testTitle = "The Psychology of Hitchcock's Vertigo Shot"
    viewModel.createNewProject(
      title = testTitle,
      topic = "A psychological breakdown of Hitchcock's camera moves",
      duration = "Standard 8 - 12 min",
      tone = "Cinematic",
      type = ProjectType.SCRIPT,
      channelTarget = "YouTube"
    )

    val active = viewModel.activeProject.value
    assertNotNull(active)
    assertEquals(testTitle, active?.title)
    assertEquals("YouTube", active?.channelTarget)
    assertNotNull(viewModel.voiceAgentName.value)

    viewModel.updateApiKey("test-key-12345")
    assertEquals("test-key-12345", viewModel.getEffectiveGeminiKey())
  }

  @Test
  fun testGeminiApiKeyResolution() {
    // Valid key
    val valid = com.example.data.api.GeminiClient.resolveApiKey("  AIzaSyValidKey123  ")
    assertEquals("AIzaSyValidKey123", valid)

    // Blank key
    val blank = com.example.data.api.GeminiClient.resolveApiKey("   ")
    assertTrue(blank.isEmpty() || blank != "MY_GEMINI_API_KEY")

    // Placeholders
    val placeholder1 = com.example.data.api.GeminiClient.resolveApiKey("MY_GEMINI_API_KEY")
    assertTrue(placeholder1.isEmpty() || placeholder1 != "MY_GEMINI_API_KEY")

    val placeholder2 = com.example.data.api.GeminiClient.resolveApiKey("YOUR_GEMINI_API_KEY")
    assertTrue(placeholder2.isEmpty() || placeholder2 != "YOUR_GEMINI_API_KEY")
  }

  @Test
  fun testAspectRatioMapping() {
    val viewModel = StudioViewModel(application)

    viewModel.thumbnailAspect.value = "16:9 Cinematic Landscape"
    assertEquals("16:9", viewModel.getApiAspectRatio())

    viewModel.thumbnailAspect.value = "9:16 TikTok / Shorts Vertical"
    assertEquals("9:16", viewModel.getApiAspectRatio())

    viewModel.thumbnailAspect.value = "1:1 Square Feed"
    assertEquals("1:1", viewModel.getApiAspectRatio())
  }

  @Test
  fun testYouTubeFormulaBlueprintFallback() = runBlocking {
    val blueprint = com.example.data.api.GeminiClient.reverseEngineerYouTubeFormula(
      urlOrChannel = "https://youtube.com/@JohnnyHarris",
      userKey = ""
    )
    assertNotNull(blueprint)
    assertTrue(blueprint.creatorStyle.isNotBlank())
    assertTrue(blueprint.hookFormula.isNotBlank())
    assertTrue(blueprint.workflowSteps.size >= 5)
  }

  @Test
  fun testScriptGenerationIntegrity() = runBlocking {
    val scenes = com.example.data.api.YouTubeScriptService.generateYouTubeScript(
      topic = "The Secret Chemistry of 35mm Technicolor",
      tone = "Cinematic",
      duration = "Standard 8 - 12 min",
      architecture = "3-Act Essay",
      apiKey = ""
    )
    assertTrue("Script should have at least 3 scenes", scenes.size >= 3)
    assertTrue("First scene should be hook", scenes[0].actTitle.contains("Hook", ignoreCase = true) || scenes[0].timestamp.contains("0:00"))
    assertTrue("First scene narration should not be blank", scenes[0].narration.isNotBlank())
  }
}
