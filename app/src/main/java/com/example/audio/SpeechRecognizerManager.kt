package com.example.audio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * SpeechState encapsulates the lifecycle of speech recognition.
 */
sealed class SpeechState {
  data object Idle : SpeechState()
  data object Ready : SpeechState()
  data object Listening : SpeechState()
  data object Processing : SpeechState()
  data class Error(val message: String, val code: Int = -1) : SpeechState()
}

/**
 * SpeechRecognizerManager manages Speech-to-Text via Android's native SpeechRecognizer.
 * Exposes live transcription, audio input RMS level for visualizers, and state flows.
 */
class SpeechRecognizerManager(
  private val context: Context,
  private val onCommandRecognized: (String) -> Unit = {}
) {
  private val tag = "SpeechRecognizerMgr"
  private val mainHandler = Handler(Looper.getMainLooper())
  private var speechRecognizer: SpeechRecognizer? = null

  private val _speechState = MutableStateFlow<SpeechState>(SpeechState.Idle)
  val speechState: StateFlow<SpeechState> = _speechState.asStateFlow()

  private val _isListening = MutableStateFlow(false)
  val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

  private val _partialText = MutableStateFlow("")
  val partialText: StateFlow<String> = _partialText.asStateFlow()

  private val _lastRecognizedText = MutableStateFlow<String?>(null)
  val lastRecognizedText: StateFlow<String?> = _lastRecognizedText.asStateFlow()

  private val _rmsLevel = MutableStateFlow(0f)
  val rmsLevel: StateFlow<Float> = _rmsLevel.asStateFlow()

  private val _errorMessage = MutableStateFlow<String?>(null)
  val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

  val isRecognitionAvailable: Boolean
    get() = SpeechRecognizer.isRecognitionAvailable(context)

  init {
    ensureRecognizerInitialized()
  }

  private fun ensureRecognizerInitialized() {
    mainHandler.post {
      if (speechRecognizer == null && isRecognitionAvailable) {
        try {
          speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(createListener())
          }
        } catch (e: Exception) {
          Log.e(tag, "Failed to create SpeechRecognizer: ${e.message}")
        }
      }
    }
  }

  private fun createListener() = object : RecognitionListener {
    override fun onReadyForSpeech(params: Bundle?) {
      Log.d(tag, "onReadyForSpeech")
      _speechState.value = SpeechState.Ready
      _isListening.value = true
      _errorMessage.value = null
    }

    override fun onBeginningOfSpeech() {
      Log.d(tag, "onBeginningOfSpeech")
      _speechState.value = SpeechState.Listening
    }

    override fun onRmsChanged(rmsdB: Float) {
      // rmsdB ranges typically from -2.0 to ~10.0+ dB
      val normalized = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
      _rmsLevel.value = normalized
    }

    override fun onBufferReceived(buffer: ByteArray?) {}

    override fun onEndOfSpeech() {
      Log.d(tag, "onEndOfSpeech")
      _speechState.value = SpeechState.Processing
      _rmsLevel.value = 0f
    }

    override fun onError(error: Int) {
      _isListening.value = false
      _rmsLevel.value = 0f
      val errorMsg = mapErrorCode(error)
      Log.w(tag, "SpeechRecognizer onError: $error ($errorMsg)")
      _errorMessage.value = errorMsg
      _speechState.value = SpeechState.Error(errorMsg, error)
    }

    override fun onResults(results: Bundle?) {
      _isListening.value = false
      _speechState.value = SpeechState.Idle
      _rmsLevel.value = 0f

      val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
      val bestMatch = matches?.firstOrNull()?.trim().orEmpty()

      if (bestMatch.isNotBlank()) {
        _lastRecognizedText.value = bestMatch
        _partialText.value = ""
        onCommandRecognized(bestMatch)
      } else {
        _partialText.value = ""
      }
    }

    override fun onPartialResults(partialResults: Bundle?) {
      val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
      val partial = matches?.firstOrNull()?.trim().orEmpty()
      if (partial.isNotBlank()) {
        _partialText.value = partial
      }
    }

    override fun onEvent(eventType: Int, params: Bundle?) {}
  }

  fun startListening() {
    mainHandler.post {
      _errorMessage.value = null
      _partialText.value = ""

      if (!isRecognitionAvailable) {
        val msg = "Speech recognition is not supported on this device/system."
        _errorMessage.value = msg
        _speechState.value = SpeechState.Error(msg)
        return@post
      }

      if (speechRecognizer == null) {
        ensureRecognizerInitialized()
      }

      val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toString())
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
        putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to your AI Voice Director...")
      }

      try {
        speechRecognizer?.startListening(intent)
        _isListening.value = true
        _speechState.value = SpeechState.Ready
      } catch (e: Exception) {
        Log.e(tag, "Error starting speech recognition: ${e.message}")
        _isListening.value = false
        val msg = e.localizedMessage ?: "Failed to start microphone"
        _errorMessage.value = msg
        _speechState.value = SpeechState.Error(msg)
      }
    }
  }

  fun stopListening() {
    mainHandler.post {
      try {
        speechRecognizer?.stopListening()
      } catch (e: Exception) {
        Log.e(tag, "Error stopping speech recognition: ${e.message}")
      }
      _isListening.value = false
      _speechState.value = SpeechState.Idle
      _rmsLevel.value = 0f
    }
  }

  fun cancel() {
    mainHandler.post {
      try {
        speechRecognizer?.cancel()
      } catch (e: Exception) {
        Log.e(tag, "Error canceling speech recognition: ${e.message}")
      }
      _isListening.value = false
      _speechState.value = SpeechState.Idle
      _rmsLevel.value = 0f
      _partialText.value = ""
    }
  }

  fun destroy() {
    mainHandler.post {
      try {
        speechRecognizer?.destroy()
      } catch (e: Exception) {
        Log.e(tag, "Error destroying speech recognizer: ${e.message}")
      }
      speechRecognizer = null
      _isListening.value = false
      _speechState.value = SpeechState.Idle
    }
  }

  private fun mapErrorCode(errorCode: Int): String {
    return when (errorCode) {
      SpeechRecognizer.ERROR_AUDIO -> "Audio recording error. Check microphone."
      SpeechRecognizer.ERROR_CLIENT -> "Client error. Tap mic to retry."
      SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission required."
      SpeechRecognizer.ERROR_NETWORK -> "Network error during speech recognition."
      SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout. Please retry."
      SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized. Tap and speak clearly."
      SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Speech engine busy. Resetting..."
      SpeechRecognizer.ERROR_SERVER -> "Recognition server error."
      SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected before timeout."
      else -> "Speech recognition error ($errorCode)"
    }
  }
}
