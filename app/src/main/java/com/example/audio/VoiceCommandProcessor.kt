package com.example.audio

import com.example.data.model.StudioScreen

/**
 * Result of interpreting a voice command spoken by the creator.
 */
sealed class VoiceCommandResult {
  data class Navigation(
    val destination: StudioScreen,
    val verbalFeedback: String
  ) : VoiceCommandResult()

  data class ModeChange(
    val newMode: String,
    val verbalFeedback: String
  ) : VoiceCommandResult()

  data class DirectAction(
    val actionType: ActionType,
    val verbalFeedback: String
  ) : VoiceCommandResult()

  data class CreativeQuery(
    val prompt: String
  ) : VoiceCommandResult()
}

enum class ActionType {
  CLEAR_STREAM,
  COMMIT_TO_SCRIPT,
  STOP_SPEECH,
  TOGGLE_MUTE,
  TEST_VOICE
}

/**
 * VoiceCommandProcessor inspects recognized speech and determines whether
 * the creator gave a studio execution command or a creative directing prompt.
 */
object VoiceCommandProcessor {

  fun process(spokenText: String): VoiceCommandResult {
    val clean = spokenText.trim().lowercase()

    // 1. Navigation Commands
    if (clean.contains("script studio") || clean == "open script" || clean == "go to script" || clean.contains("show script")) {
      return VoiceCommandResult.Navigation(
        destination = StudioScreen.SCRIPT_STUDIO,
        verbalFeedback = "Opening Script Studio. Your scenes and beat breakdown are ready."
      )
    }
    if (clean.contains("storyboard") || clean == "open storyboard" || clean == "go to storyboard" || clean.contains("visual pre-vis")) {
      return VoiceCommandResult.Navigation(
        destination = StudioScreen.STORYBOARD,
        verbalFeedback = "Navigating to Storyboard. Reviewing camera packages and shot composition."
      )
    }
    if (clean.contains("research") || clean.contains("reverse engineer") || clean == "open research") {
      return VoiceCommandResult.Navigation(
        destination = StudioScreen.RESEARCH,
        verbalFeedback = "Opening Channel Research engine for viral formula deconstruction."
      )
    }
    if (clean.contains("thumbnail") || clean == "open packaging" || clean.contains("thumbnail studio")) {
      return VoiceCommandResult.Navigation(
        destination = StudioScreen.THUMBNAIL_STUDIO,
        verbalFeedback = "Opening Thumbnail Studio for high-CTR packaging."
      )
    }
    if (clean.contains("vault") || clean == "open vault" || clean.contains("show vault")) {
      return VoiceCommandResult.Navigation(
        destination = StudioScreen.VAULT,
        verbalFeedback = "Accessing Creator Vault."
      )
    }
    if (clean.contains("setting") || clean == "open settings") {
      return VoiceCommandResult.Navigation(
        destination = StudioScreen.SETTINGS,
        verbalFeedback = "Opening Studio Settings and API diagnostics."
      )
    }
    if (clean == "go home" || clean == "open home" || clean == "dashboard" || clean == "return home") {
      return VoiceCommandResult.Navigation(
        destination = StudioScreen.HOME,
        verbalFeedback = "Returning to Studio Dashboard."
      )
    }

    // 2. Mode Change Commands
    if (clean.contains("brainstorm")) {
      return VoiceCommandResult.ModeChange(
        newMode = "Brainstorm",
        verbalFeedback = "Switched to Brainstorm mode. Let's explore premise hooks and contrast."
      )
    }
    if (clean.contains("director mode") || clean == "switch to director") {
      return VoiceCommandResult.ModeChange(
        newMode = "Director Mode",
        verbalFeedback = "Director Mode active. Standing by for scene pacing and tension."
      )
    }
    if (clean.contains("script pacing") || clean.contains("pacing mode")) {
      return VoiceCommandResult.ModeChange(
        newMode = "Script Pacing",
        verbalFeedback = "Script Pacing calibrated for maximum viewer retention."
      )
    }
    if (clean.contains("dialogue mode") || clean == "dialogue") {
      return VoiceCommandResult.ModeChange(
        newMode = "Dialogue",
        verbalFeedback = "Dialogue mode engaged for natural voiceover rhythm."
      )
    }

    // 3. Direct Studio Actions
    if (clean.contains("clear conversation") || clean.contains("clear thoughts") || clean.contains("reset conversation") || clean == "clear stream") {
      return VoiceCommandResult.DirectAction(
        actionType = ActionType.CLEAR_STREAM,
        verbalFeedback = "Thought stream reset. Standing by for your next direction."
      )
    }
    if (clean.contains("commit to script") || clean.contains("save to script") || clean.contains("send to script")) {
      return VoiceCommandResult.DirectAction(
        actionType = ActionType.COMMIT_TO_SCRIPT,
        verbalFeedback = "Latest scene idea committed to Script Studio premise."
      )
    }
    if (clean == "stop talking" || clean == "be quiet" || clean == "shut up" || clean == "silence" || clean == "stop speech") {
      return VoiceCommandResult.DirectAction(
        actionType = ActionType.STOP_SPEECH,
        verbalFeedback = ""
      )
    }
    if (clean == "mute" || clean == "unmute" || clean == "toggle mute") {
      return VoiceCommandResult.DirectAction(
        actionType = ActionType.TOGGLE_MUTE,
        verbalFeedback = "Voice audio toggled."
      )
    }
    if (clean == "test voice" || clean == "who are you" || clean == "introduce yourself") {
      return VoiceCommandResult.DirectAction(
        actionType = ActionType.TEST_VOICE,
        verbalFeedback = "Hello! I am Sam Skytube, your female AI Voice Director. Speak to direct pacing, hooks, or camera shots."
      )
    }

    // 4. Fallback: Creative Directing Prompt (sent to Gemini AI Director)
    return VoiceCommandResult.CreativeQuery(spokenText)
  }
}
