package com.example

import com.example.audio.ActionType
import com.example.audio.SpeechState
import com.example.audio.VoiceAgentSpeaker
import com.example.audio.VoiceCommandProcessor
import com.example.audio.VoiceCommandResult
import com.example.data.model.StudioScreen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VoiceIntegrationTest {

  @Test
  fun testSanitizeForSpeechStripsMarkdownAndCues() {
    val rawText = """
      ### Act I: The Hook (0:00 - 0:45)
      * **Visual Juxtaposition:** [Shot 1](cue) 35mm Technicolor bath vs modern digital.
      * Dialogue: "Why does early film feel alive?"
      `code_snippet_here`
    """.trimIndent()

    val sanitized = VoiceAgentSpeaker.sanitizeForSpeech(rawText)

    // Verify markdown asterisks, backticks, hashes, and brackets are removed
    assertFalse(sanitized.contains("###"))
    assertFalse(sanitized.contains("**"))
    assertFalse(sanitized.contains("`"))
    assertFalse(sanitized.contains("[Shot 1]"))
    assertFalse(sanitized.contains("(cue)"))
    assertTrue(sanitized.contains("Act I: The Hook"))
    assertTrue(sanitized.contains("Why does early film feel alive?"))
  }

  @Test
  fun testSanitizeForSpeechTruncatesToConciseDirectorLength() {
    val longDirectorSpeech = (1..20).joinToString(" ") { "This is sentence number $it about camera movement and pacing." }
    val sanitized = VoiceAgentSpeaker.sanitizeForSpeech(longDirectorSpeech)

    val wordCount = sanitized.split("\\s+".toRegex()).size
    assertTrue("Word count should be concise director length: $wordCount", wordCount <= 40)
  }

  @Test
  fun testVoiceCommandProcessorNavigation() {
    val scriptCmd = VoiceCommandProcessor.process("Open Script Studio")
    assertTrue(scriptCmd is VoiceCommandResult.Navigation)
    assertEquals(StudioScreen.SCRIPT_STUDIO, (scriptCmd as VoiceCommandResult.Navigation).destination)

    val storyboardCmd = VoiceCommandProcessor.process("Go to storyboard")
    assertTrue(storyboardCmd is VoiceCommandResult.Navigation)
    assertEquals(StudioScreen.STORYBOARD, (storyboardCmd as VoiceCommandResult.Navigation).destination)

    val researchCmd = VoiceCommandProcessor.process("Open research")
    assertTrue(researchCmd is VoiceCommandResult.Navigation)
    assertEquals(StudioScreen.RESEARCH, (researchCmd as VoiceCommandResult.Navigation).destination)

    val thumbnailsCmd = VoiceCommandProcessor.process("Open thumbnail studio")
    assertTrue(thumbnailsCmd is VoiceCommandResult.Navigation)
    assertEquals(StudioScreen.THUMBNAIL_STUDIO, (thumbnailsCmd as VoiceCommandResult.Navigation).destination)
  }

  @Test
  fun testVoiceCommandProcessorModeChanges() {
    val brainstormCmd = VoiceCommandProcessor.process("Switch to brainstorm mode")
    assertTrue(brainstormCmd is VoiceCommandResult.ModeChange)
    assertEquals("Brainstorm", (brainstormCmd as VoiceCommandResult.ModeChange).newMode)

    val directorCmd = VoiceCommandProcessor.process("Director mode")
    assertTrue(directorCmd is VoiceCommandResult.ModeChange)
    assertEquals("Director Mode", (directorCmd as VoiceCommandResult.ModeChange).newMode)

    val pacingCmd = VoiceCommandProcessor.process("Switch to script pacing")
    assertTrue(pacingCmd is VoiceCommandResult.ModeChange)
    assertEquals("Script Pacing", (pacingCmd as VoiceCommandResult.ModeChange).newMode)
  }

  @Test
  fun testVoiceCommandProcessorDirectActions() {
    val stopCmd = VoiceCommandProcessor.process("Stop talking")
    assertTrue(stopCmd is VoiceCommandResult.DirectAction)
    assertEquals(ActionType.STOP_SPEECH, (stopCmd as VoiceCommandResult.DirectAction).actionType)

    val clearCmd = VoiceCommandProcessor.process("Clear conversation")
    assertTrue(clearCmd is VoiceCommandResult.DirectAction)
    assertEquals(ActionType.CLEAR_STREAM, (clearCmd as VoiceCommandResult.DirectAction).actionType)

    val commitCmd = VoiceCommandProcessor.process("Commit to script")
    assertTrue(commitCmd is VoiceCommandResult.DirectAction)
    assertEquals(ActionType.COMMIT_TO_SCRIPT, (commitCmd as VoiceCommandResult.DirectAction).actionType)
  }

  @Test
  fun testVoiceCommandProcessorCreativePromptFallback() {
    val creativePrompt = "How should I structure the 3-second visual hook for high retention?"
    val result = VoiceCommandProcessor.process(creativePrompt)
    assertTrue(result is VoiceCommandResult.CreativeQuery)
    assertEquals(creativePrompt, (result as VoiceCommandResult.CreativeQuery).prompt)
  }

  @Test
  fun testSpeechStateDefinitions() {
    val idle = SpeechState.Idle
    val listening = SpeechState.Listening
    val processing = SpeechState.Processing
    val error = SpeechState.Error("Microphone permission required", 9)

    assertEquals("Microphone permission required", error.message)
    assertEquals(9, error.code)
    assertTrue(idle is SpeechState)
    assertTrue(listening is SpeechState)
    assertTrue(processing is SpeechState)
  }
}
