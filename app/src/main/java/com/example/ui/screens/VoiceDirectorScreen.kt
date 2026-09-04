package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.audio.SpeechState
import com.example.data.model.StudioScreen
import com.example.data.model.VoiceThought
import com.example.ui.components.BottomNavBar
import com.example.ui.components.SamlytLogo
import com.example.ui.components.StudioHeader
import com.example.ui.components.WaveformVisualizer
import com.example.ui.theme.SamlytBackground
import com.example.ui.theme.SamlytCard
import com.example.ui.theme.SamlytCardBorder
import com.example.ui.theme.SamlytCyan
import com.example.ui.theme.SamlytGreen
import com.example.ui.theme.SamlytOrange
import com.example.ui.theme.SamlytTextMuted
import com.example.ui.theme.SamlytTextPrimary
import com.example.ui.theme.SamlytTextSecondary
import com.example.viewmodel.StudioViewModel

@Composable
fun VoiceDirectorScreen(
  viewModel: StudioViewModel,
  onNavigate: (StudioScreen) -> Unit,
  onFabClick: () -> Unit
) {
  val isListening by viewModel.isVoiceListening.collectAsState()
  val speechState by viewModel.speechState.collectAsState()
  val partialText by viewModel.partialVoiceText.collectAsState()
  val rmsLevel by viewModel.voiceRmsLevel.collectAsState()
  val errorMessage by viewModel.voiceErrorMessage.collectAsState()

  val mode by viewModel.voiceDirectorMode.collectAsState()
  val thoughts by viewModel.voiceThoughtStream.collectAsState()
  val isSpeaking by viewModel.isVoiceAgentSpeaking.collectAsState()
  val voiceName by viewModel.voiceAgentName.collectAsState()
  val isMuted by viewModel.isVoiceMuted.collectAsState()
  val speechRate by viewModel.voiceSpeechRate.collectAsState()
  val pitch by viewModel.voicePitch.collectAsState()

  var inputMessage by remember { mutableStateOf("") }
  var showTextInput by remember { mutableStateOf(false) }
  var showVoiceSettings by remember { mutableStateOf(false) }

  val modes = listOf("Director Mode", "Script Pacing", "Brainstorm", "Dialogue")
  val context = LocalContext.current

  val micScale by animateFloatAsState(
    targetValue = if (isListening) (1.0f + rmsLevel * 0.35f).coerceIn(1.0f, 1.35f) else 1.0f,
    label = "micScale"
  )

  val permissionLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      viewModel.startVoiceListening()
    } else {
      Toast.makeText(context, "Microphone permission required for speech recognition", Toast.LENGTH_SHORT).show()
    }
  }

  fun handleMicClick() {
    if (isListening) {
      viewModel.stopVoiceListening()
    } else {
      val hasPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.RECORD_AUDIO
      ) == PackageManager.PERMISSION_GRANTED

      if (hasPermission) {
        viewModel.startVoiceListening()
      } else {
        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
      }
    }
  }

  Scaffold(
    containerColor = SamlytBackground,
    topBar = {
      StudioHeader(
        title = "Voice Director",
        subtitle = "SPEECH-TO-TEXT & TTS LOOP",
        versionBadge = "NATIVE ANDROID AI",
        canGoBack = true,
        onBackClick = { onNavigate(StudioScreen.HOME) },
        onProfileClick = { onNavigate(StudioScreen.SETTINGS) }
      )
    },
    bottomBar = {
      BottomNavBar(
        currentScreen = StudioScreen.VOICE_DIRECTOR,
        onNavigate = onNavigate,
        onFabClick = onFabClick
      )
    }
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Status Pill
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(999.dp))
              .background(if (isSpeaking) Color(0xFF2C1910) else if (isListening) Color(0xFF0F2618) else Color(0xFF161616))
              .border(
                1.dp,
                if (isSpeaking) SamlytOrange.copy(alpha = 0.6f) else if (isListening) SamlytGreen.copy(alpha = 0.6f) else SamlytCardBorder,
                RoundedCornerShape(999.dp)
              )
              .padding(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .clip(CircleShape)
                  .background(
                    if (isSpeaking) SamlytOrange else if (isListening) SamlytGreen else SamlytTextMuted
                  )
              )
              Text(
                text = when {
                  isSpeaking -> "TTS SPEAKING FEEDBACK"
                  isListening -> "STT LISTENING..."
                  speechState is SpeechState.Processing -> "PROCESSING SPEECH..."
                  isMuted -> "VOICE MUTED"
                  else -> "FEMALE VOICE ACTIVE"
                },
                color = when {
                  isSpeaking -> SamlytOrange
                  isListening -> SamlytGreen
                  isMuted -> Color(0xFFEF4444)
                  else -> SamlytGreen
                },
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Text(voiceName, color = SamlytOrange, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
      }

      item {
        // Glowing Logo & Visualizer
        SamlytLogo(size = 76.dp, isLiveAnimated = isListening || isSpeaking)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = when {
            isSpeaking -> "Female Director Speaking..."
            isListening -> "Listening to Director..."
            speechState is SpeechState.Processing -> "Processing Command..."
            speechState is SpeechState.Error -> "Microphone Notice"
            else -> "Voice Assistant Ready"
          },
          color = when {
            isSpeaking -> SamlytOrange
            isListening -> SamlytGreen
            speechState is SpeechState.Error -> Color(0xFFEF4444)
            else -> SamlytTextPrimary
          },
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Speak studio commands ('Open Script Studio', 'Switch to Brainstorm', 'Commit to Script') or ask directing questions. Verbal feedback is spoken aloud.",
          color = SamlytTextSecondary,
          fontSize = 12.sp,
          textAlign = TextAlign.Center,
          lineHeight = 16.sp,
          modifier = Modifier.padding(horizontal = 8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Direct Female Voice Trigger & Mute Controls
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          // Mute / Unmute Button
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(if (isMuted) Color(0xFF331610) else Color(0xFF1E1E1E))
              .border(1.dp, if (isMuted) Color(0xFFEF4444) else SamlytCardBorder, RoundedCornerShape(8.dp))
              .clickable { viewModel.toggleVoiceMute() }
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Icon(
                imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                contentDescription = null,
                tint = if (isMuted) Color(0xFFEF4444) else SamlytGreen,
                modifier = Modifier.size(14.dp)
              )
              Text(
                text = if (isMuted) "Unmute Voice" else "Voice Muted: Off",
                color = if (isMuted) Color(0xFFEF4444) else SamlytGreen,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          // Test Voice Button
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFF1E1E1E))
              .border(1.dp, SamlytCardBorder, RoundedCornerShape(8.dp))
              .clickable {
                viewModel.handleRecognizedVoiceCommand("introduce yourself")
              }
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Icon(Icons.Default.Headphones, contentDescription = null, tint = SamlytOrange, modifier = Modifier.size(14.dp))
              Text("Test Director Voice", color = SamlytOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }

          // Settings Toggle Button
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(if (showVoiceSettings) Color(0xFF261D15) else Color(0xFF1E1E1E))
              .border(1.dp, if (showVoiceSettings) SamlytOrange else SamlytCardBorder, RoundedCornerShape(8.dp))
              .clickable { showVoiceSettings = !showVoiceSettings }
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Icon(Icons.Default.Tune, contentDescription = null, tint = SamlytCyan, modifier = Modifier.size(14.dp))
              Text("Voice Tuning", color = SamlytCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))
        WaveformVisualizer(
          isLive = isListening || isSpeaking,
          rmsLevel = rmsLevel,
          barCount = 17,
          maxHeight = 34.dp
        )
      }

      // Voice Tuning Panel (Expandable)
      if (showVoiceSettings) {
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .border(1.dp, SamlytOrange.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF171311))
          ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text("VOICE SYNTHESIS TUNING", color = SamlytOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Close",
                  tint = SamlytTextMuted,
                  modifier = Modifier
                    .size(16.dp)
                    .clickable { showVoiceSettings = false }
                )
              }

              // Speech Rate
              Text("Speech Rate (Pacing)", color = SamlytTextSecondary, fontSize = 11.sp)
              Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(0.9f to "0.9x Calm", 1.0f to "1.0x Normal", 1.08f to "1.08x Director", 1.25f to "1.25x Fast").forEach { (r, label) ->
                  val isSel = kotlin.math.abs(speechRate - r) < 0.04f
                  Box(
                    modifier = Modifier
                      .weight(1f)
                      .clip(RoundedCornerShape(6.dp))
                      .background(if (isSel) SamlytOrange else Color(0xFF222222))
                      .clickable { viewModel.setVoiceSpeechRate(r) }
                      .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(label, color = if (isSel) Color.Black else SamlytTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }

              // Pitch
              Text("Vocal Pitch (Timbre)", color = SamlytTextSecondary, fontSize = 11.sp)
              Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(1.0f to "Standard", 1.22f to "Female Director", 1.35f to "High Bright").forEach { (p, label) ->
                  val isSel = kotlin.math.abs(pitch - p) < 0.04f
                  Box(
                    modifier = Modifier
                      .weight(1f)
                      .clip(RoundedCornerShape(6.dp))
                      .background(if (isSel) SamlytCyan else Color(0xFF222222))
                      .clickable { viewModel.setVoicePitch(p) }
                      .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(label, color = if (isSel) Color.Black else SamlytTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }
        }
      }

      // Live Speech-to-Text Transcription Banner
      if (isListening || partialText.isNotBlank()) {
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .border(1.dp, SamlytGreen, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1E14))
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  Box(
                    modifier = Modifier
                      .size(8.dp)
                      .clip(CircleShape)
                      .background(SamlytGreen)
                  )
                  Text("LIVE TRANSCRIPTION (STT)", color = SamlytGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Text("Audio RMS: ${(rmsLevel * 100).toInt()}%", color = SamlytTextMuted, fontSize = 10.sp)
              }

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = if (partialText.isNotBlank()) "\"$partialText…\"" else "Listening for voice command or creative prompt...",
                color = if (partialText.isNotBlank()) Color.White else SamlytTextMuted,
                fontSize = 14.sp,
                fontWeight = if (partialText.isNotBlank()) FontWeight.SemiBold else FontWeight.Normal,
                lineHeight = 18.sp
              )

              Spacer(modifier = Modifier.height(8.dp))

              // Audio Level Progress Bar
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(4.dp)
                  .clip(RoundedCornerShape(2.dp))
                  .background(Color(0xFF1B3824))
              ) {
                Box(
                  modifier = Modifier
                    .fillMaxWidth(rmsLevel.coerceIn(0.05f, 1f))
                    .height(4.dp)
                    .background(SamlytGreen)
                )
              }
            }
          }
        }
      }

      // Speech Error Banner
      if (errorMessage != null) {
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.6f), RoundedCornerShape(10.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF261210))
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = errorMessage ?: "",
                color = Color(0xFFFF8888),
                fontSize = 11.sp,
                modifier = Modifier.weight(1f)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Button(
                onClick = { handleMicClick() },
                colors = ButtonDefaults.buttonColors(containerColor = SamlytOrange),
                shape = RoundedCornerShape(6.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
              ) {
                Text("Retry", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }

      // Mode Selection Pills
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          modes.forEach { m ->
            val isSel = m == mode
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(999.dp))
                .background(if (isSel) Color(0xFF261D15) else Color(0xFF191919))
                .border(1.dp, if (isSel) SamlytOrange else SamlytCardBorder, RoundedCornerShape(999.dp))
                .clickable { viewModel.voiceDirectorMode.value = m }
                .padding(vertical = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = m,
                color = if (isSel) SamlytOrange else SamlytTextSecondary,
                fontSize = 10.sp,
                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
              )
            }
          }
        }
      }

      // Quick Voice Starters & Commands
      item {
        Column(modifier = Modifier.fillMaxWidth()) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "VOICE COMMANDS & TOPIC STARTERS",
              color = SamlytTextMuted,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
            Text("TAP TO RUN", color = SamlytOrange, fontSize = 9.sp, fontWeight = FontWeight.Bold)
          }
          Spacer(modifier = Modifier.height(6.dp))

          val voiceStarters = listOf(
            "Open Script Studio" to "Navigation Command",
            "Switch to Brainstorm" to "Mode Switch",
            "Give me a high-retention 3-second hook for my intro" to "Creative Prompt",
            "What camera movement and B-roll creates maximum tension here?" to "Directing Prompt",
            "Clear conversation" to "Studio Action"
          )
          voiceStarters.forEach { (prompt, badge) ->
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF161616))
                .border(1.dp, SamlytCardBorder, RoundedCornerShape(8.dp))
                .clickable {
                  viewModel.handleRecognizedVoiceCommand(prompt)
                }
                .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "\"$prompt\"",
                    color = SamlytTextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                  )
                  Text(badge, color = SamlytCyan, fontSize = 9.sp)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                  imageVector = Icons.Default.VolumeUp,
                  contentDescription = "Run voice command",
                  tint = SamlytOrange,
                  modifier = Modifier.size(14.dp)
                )
              }
            }
          }
        }
      }

      // Live Thought Stream Header
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "LIVE THOUGHT STREAM",
            color = SamlytTextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("${thoughts.size} turns", color = SamlytTextMuted, fontSize = 11.sp)
            if (thoughts.isNotEmpty()) {
              Icon(
                imageVector = Icons.Default.DeleteSweep,
                contentDescription = "Clear",
                tint = SamlytTextMuted,
                modifier = Modifier
                  .size(16.dp)
                  .clickable { viewModel.clearVoiceThoughts() }
              )
            }
          }
        }
      }

      // Thoughts Stream
      items(thoughts) { thought ->
        VoiceThoughtCard(
          thought = thought,
          onSpeak = { text -> viewModel.speakTextWithVoiceAgent(text) }
        )
      }

      // Text input box if toggled
      if (showTextInput) {
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedTextField(
              value = inputMessage,
              onValueChange = { inputMessage = it },
              placeholder = { Text("Speak or type direction...", color = SamlytTextMuted, fontSize = 12.sp) },
              modifier = Modifier.weight(1f),
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SamlytCard,
                unfocusedContainerColor = SamlytCard,
                focusedBorderColor = SamlytOrange,
                unfocusedBorderColor = SamlytCardBorder,
                focusedTextColor = SamlytTextPrimary,
                unfocusedTextColor = SamlytTextPrimary
              ),
              shape = RoundedCornerShape(8.dp),
              singleLine = true
            )
            Button(
              onClick = {
                if (inputMessage.isNotBlank()) {
                  viewModel.handleRecognizedVoiceCommand(inputMessage)
                  inputMessage = ""
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = SamlytOrange),
              shape = RoundedCornerShape(8.dp)
            ) {
              Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.Black, modifier = Modifier.size(16.dp))
            }
          }
        }
      }

      // Voice Controls Bar
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SamlytCard)
            .border(1.dp, SamlytCardBorder, RoundedCornerShape(14.dp))
            .padding(vertical = 10.dp, horizontal = 20.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Keyboard toggle
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(if (showTextInput) SamlytOrange.copy(alpha = 0.2f) else Color(0xFF222222))
              .clickable { showTextInput = !showTextInput },
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Keyboard, contentDescription = "Type", tint = if (showTextInput) SamlytOrange else SamlytTextSecondary, modifier = Modifier.size(20.dp))
          }

          // Big Pulsing Mic Button (Speech-to-Text Input)
          Box(
            modifier = Modifier
              .size(64.dp)
              .scale(micScale)
              .clip(CircleShape)
              .background(if (isListening) SamlytGreen else SamlytOrange)
              .border(
                width = if (isListening) (3 + (rmsLevel * 4).toInt()).dp else 2.dp,
                color = if (isListening) Color.White else SamlytOrange.copy(alpha = 0.5f),
                shape = CircleShape
              )
              .clickable { handleMicClick() },
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (isListening) Icons.Default.GraphicEq else Icons.Default.Mic,
              contentDescription = if (isListening) "Stop speech recognition" else "Start speech recognition",
              tint = Color.Black,
              modifier = Modifier.size(30.dp)
            )
          }

          // Stop Speech / Pause Button
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(Color(0xFF222222))
              .clickable {
                viewModel.stopVoiceListening()
                viewModel.stopVoiceAgentSpeech()
              },
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Pause, contentDescription = "Stop voice", tint = SamlytTextSecondary, modifier = Modifier.size(20.dp))
          }
        }
      }

      // Commit to Script Studio
      item {
        Button(
          onClick = { viewModel.commitVoiceToScript() },
          colors = ButtonDefaults.buttonColors(containerColor = SamlytOrange),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
        ) {
          Text("Commit to Script Studio", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.width(6.dp))
          Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
        }
      }

      item {
        Spacer(modifier = Modifier.height(20.dp))
      }
    }
  }
}

@Composable
fun VoiceThoughtCard(
  thought: VoiceThought,
  onSpeak: (String) -> Unit = {}
) {
  val isAi = thought.speaker.contains("SAMLYT")
  val bg = if (isAi) Color(0xFF131D18) else SamlytCard
  val border = if (isAi) SamlytGreen.copy(alpha = 0.3f) else SamlytCardBorder
  val speakerColor = if (isAi) SamlytGreen else SamlytOrange

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .border(1.dp, border, RoundedCornerShape(10.dp)),
    colors = CardDefaults.cardColors(containerColor = bg)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(speakerColor)
          )
          Text(thought.speaker, color = speakerColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(thought.timeAgo, color = SamlytTextMuted, fontSize = 10.sp)
          if (isAi) {
            Box(
              modifier = Modifier
                .clip(CircleShape)
                .background(Color(0xFF1F2E23))
                .clickable { onSpeak(thought.text) }
                .padding(4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = "Listen to thought",
                tint = SamlytGreen,
                modifier = Modifier.size(14.dp)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = thought.text,
        color = SamlytTextPrimary,
        fontSize = 12.sp,
        lineHeight = 17.sp
      )

      if (thought.actionPills.isNotEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          thought.actionPills.forEach { pill ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF222222))
                .border(1.dp, Color(0xFF333333), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(pill, color = SamlytCyan, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
            }
          }
        }
      }
    }
  }
}
