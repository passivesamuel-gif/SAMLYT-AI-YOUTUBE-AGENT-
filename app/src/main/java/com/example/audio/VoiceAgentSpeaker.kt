package com.example.audio

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import java.util.UUID

/**
 * VoiceAgentSpeaker handles text-to-speech output using a dedicated female voice profile.
 * Ensures the voice agent speaks audibly whenever the user interacts with it.
 */
class VoiceAgentSpeaker(private val context: Context) : TextToSpeech.OnInitListener {
  private val tag = "VoiceAgentSpeaker"
  private var tts: TextToSpeech? = null
  private val _isReady = MutableStateFlow(false)
  val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

  private val _isSpeaking = MutableStateFlow(false)
  val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

  private val _isMuted = MutableStateFlow(false)
  val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

  private val _speechRate = MutableStateFlow(1.05f)
  val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

  private val _pitch = MutableStateFlow(1.22f)
  val pitch: StateFlow<Float> = _pitch.asStateFlow()

  private val _activeVoiceName = MutableStateFlow("Female Director Voice")
  val activeVoiceName: StateFlow<String> = _activeVoiceName.asStateFlow()

  private val _currentSpokenText = MutableStateFlow<String?>(null)
  val currentSpokenText: StateFlow<String?> = _currentSpokenText.asStateFlow()

  private val utteranceCallbacks = java.util.concurrent.ConcurrentHashMap<String, () -> Unit>()

  init {
    initTts()
  }

  fun initTts() {
    try {
      tts = TextToSpeech(context.applicationContext, this)
    } catch (e: Exception) {
      Log.e(tag, "Failed to initialize TextToSpeech engine: ${e.message}")
    }
  }

  override fun onInit(status: Int) {
    if (status == TextToSpeech.SUCCESS) {
      _isReady.value = true
      tts?.language = Locale.US

      // Configure female voice profile and parameters
      configureFemaleVoice()
      tts?.setSpeechRate(_speechRate.value)
      tts?.setPitch(_pitch.value)

      tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
        override fun onStart(utteranceId: String?) {
          _isSpeaking.value = true
        }

        override fun onDone(utteranceId: String?) {
          _isSpeaking.value = false
          _currentSpokenText.value = null
          if (utteranceId != null) {
            utteranceCallbacks.remove(utteranceId)?.invoke()
          }
        }

        @Deprecated("Deprecated in Java")
        override fun onError(utteranceId: String?) {
          _isSpeaking.value = false
          _currentSpokenText.value = null
          if (utteranceId != null) {
            utteranceCallbacks.remove(utteranceId)?.invoke()
          }
        }

        override fun onError(utteranceId: String?, errorCode: Int) {
          _isSpeaking.value = false
          _currentSpokenText.value = null
          if (utteranceId != null) {
            utteranceCallbacks.remove(utteranceId)?.invoke()
          }
        }
      })
      Log.d(tag, "TTS initialized with voice: ${_activeVoiceName.value}")
    } else {
      _isReady.value = false
      Log.e(tag, "TTS initialization failed with status $status")
    }
  }

  private fun configureFemaleVoice() {
    try {
      val voices = tts?.voices
      if (!voices.isNullOrEmpty()) {
        val candidate = voices.firstOrNull { voice ->
          val n = voice.name.lowercase(Locale.ROOT)
          (n.contains("female") ||
            n.contains("en-us-x-sfg") ||
            n.contains("en-us-x-iol") ||
            n.contains("en-us-x-tpd") ||
            n.contains("en-us-language") ||
            n.contains("kore")) &&
            voice.locale.language == "en"
        } ?: voices.firstOrNull { voice ->
          voice.locale.language == "en" && voice.name.lowercase(Locale.ROOT).contains("female")
        }

        if (candidate != null) {
          tts?.voice = candidate
          _activeVoiceName.value = "Female Director (${candidate.name.substringAfterLast("-")})"
        }
      }

      tts?.setPitch(_pitch.value)
      tts?.setSpeechRate(_speechRate.value)
    } catch (e: Exception) {
      Log.w(tag, "Exception configuring female voice: ${e.message}")
    }
  }

  fun setSpeechRate(rate: Float) {
    val clamped = rate.coerceIn(0.5f, 2.0f)
    _speechRate.value = clamped
    tts?.setSpeechRate(clamped)
  }

  fun setPitch(newPitch: Float) {
    val clamped = newPitch.coerceIn(0.5f, 2.0f)
    _pitch.value = clamped
    tts?.setPitch(clamped)
  }

  fun toggleMute(): Boolean {
    val newMuted = !_isMuted.value
    _isMuted.value = newMuted
    if (newMuted) {
      stop()
    }
    return newMuted
  }

  /**
   * Cleans input formatting (markdown, bracketed cues, timestamps)
   * and speaks out loud with the configured female voice.
   */
  fun speak(rawText: String, onComplete: () -> Unit = {}) {
    if (_isMuted.value) {
      onComplete()
      return
    }

    if (tts == null || !_isReady.value) {
      Log.w(tag, "TTS not ready, attempting re-initialization...")
      initTts()
      return
    }

    val cleanText = sanitizeForSpeech(rawText)
    if (cleanText.isBlank()) {
      onComplete()
      return
    }

    val utteranceId = UUID.randomUUID().toString()
    utteranceCallbacks[utteranceId] = onComplete

    val params = Bundle().apply {
      putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
    }

    _isSpeaking.value = true
    _currentSpokenText.value = cleanText
    tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
  }

  fun stop() {
    try {
      tts?.stop()
      _isSpeaking.value = false
      _currentSpokenText.value = null
      utteranceCallbacks.clear()
    } catch (e: Exception) {
      Log.e(tag, "Error stopping TTS: ${e.message}")
    }
  }

  fun shutdown() {
    try {
      tts?.stop()
      tts?.shutdown()
      tts = null
      _isReady.value = false
      _isSpeaking.value = false
      _currentSpokenText.value = null
      utteranceCallbacks.clear()
    } catch (e: Exception) {
      Log.e(tag, "Error shutting down TTS: ${e.message}")
    }
  }

  companion object {
    fun sanitizeForSpeech(text: String): String {
      val cleaned = text
        .replace(Regex("\\[([^\\]]+)\\]\\(([^)]+)\\)"), "$1") // markdown links [text](url) -> text
        .replace(Regex("\\[([^\\]]+)\\]"), "") // remove [bracketed cues] like [Camera Dolly]
        .replace(Regex("\\*\\*([^*]+)\\*\\*"), "$1") // bold
        .replace(Regex("\\*([^*]+)\\*"), "$1") // italic
        .replace(Regex("`([^`]+)`"), "$1") // code
        .replace(Regex("#+\\s*"), "") // headers
        .replace(Regex("^[\\s*-]+\\s+", RegexOption.MULTILINE), "") // bullet points
        .replace(Regex("\\b\\d{1,2}:\\d{2}\\s*(-\\s*\\d{1,2}:\\d{2})?\\b"), "") // timestamps 00:00 - 00:45
        .replace(Regex("[*#_~`>]"), " ") // residual markdown tokens
        .replace(Regex("\\s+"), " ") // collapse multiple spaces
        .trim()

      val words = cleaned.split("\\s+".toRegex()).filter { it.isNotBlank() }
      return if (words.size > 35) {
        words.take(35).joinToString(" ").trimEnd('.', ',', ';', ':', ' ') + "."
      } else {
        cleaned
      }
    }
  }
}
